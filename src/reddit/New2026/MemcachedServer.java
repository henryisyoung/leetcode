package reddit.New2026;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

/*
================================================================================
  MemcachedServer — a line-based TCP key-value server with byte-count framing
================================================================================

  GIVEN
  Clients connect over TCP and send ASCII, case-sensitive, \n-terminated command
  lines. Keys match [A-Za-z0-9_-]; VALUES MAY BE ARBITRARY BYTES.

      get key                 VALUE key n\n<n bytes>\nEND\n   (just END\n if absent)
      set key n               <n bytes>\n from the client, then STORED\n
      get key_a key_b ...     one VALUE block per EXISTING key, then a single END

  This is Reddit's "Backend Coding" round, which is deliberately not the
  algorithmic screen. Nothing here is harder than O(1); every point available is
  in the framing. The four bugs below are what the round is actually testing.

  --------------------------------------------------------------------------
  ⚠ 1. THE PROTOCOL IS BYTE-COUNT FRAMED, NOT DELIMITER FRAMED
  --------------------------------------------------------------------------
    After "set k n" you must read EXACTLY n bytes and only then consume the
    terminating newline. readLine() is wrong, because a value is allowed to
    contain newlines — and when it does, the remainder of the value is parsed
    as commands. Measured below on a 3-line value:

        line-framed reader   STORED\nERROR\nERROR\nVALUE k 5\nline1\nEND\n
        byte-framed reader   STORED\nVALUE k 17\nline1\nline2\nline3\nEND\n

    The line-framed one does not merely truncate. It stores the first line,
    then feeds "line2" and "line3" back through the command parser, which is
    why two spurious ERRORs appear — and they appear BEFORE the reply to the
    get, because they were executed before the parser ever reached it. A
    value beginning with "set" would have written a key nobody asked for.

  --------------------------------------------------------------------------
  ⚠ 2. DO NOT PUT A BufferedReader ON THE SOCKET
  --------------------------------------------------------------------------
    The obvious way to read a command line is new BufferedReader(new
    InputStreamReader(in)).readLine(). It works exactly once. A Reader owns a
    private char buffer and fills it greedily, so it swallows the payload that
    the following readFully() was going to read. Measured on a 21-byte
    request, after reading ONE 7-character command line:

        bytes left on the raw stream = 0 of 13

    The payload is gone, and it is gone into a buffer of a different type, so
    no amount of care on the InputStream can get it back. Read command lines
    yourself, byte at a time, from the SAME BufferedInputStream that readFully
    will use. That is what readLine(InputStream) below is for — it is not
    reinventing a wheel, it is the only way to keep one buffer.

  --------------------------------------------------------------------------
  ⚠ 3. VALUES ARE BYTES; DO NOT LET THEM BECOME A String
  --------------------------------------------------------------------------
    "values may be arbitrary bytes" rules out a Map<String,String>. A round
    trip through UTF-8 replaces every ill-formed sequence with U+FFFD, and it
    is not even length-preserving. Measured over the 256 single-byte values:

        Map<String,byte[]>   256 of 256 survive
        Map<String,String>   128 of 256 survive, 256 bytes in -> 512 bytes out

    ISO-8859-1 happens to round-trip all 256, so a test that only tries ASCII
    will pass on a broken server. Store byte[] and never decode.

  --------------------------------------------------------------------------
  ⚠ 4. read() IS ALLOWED TO RETURN FEWER BYTES THAN YOU ASKED FOR
  --------------------------------------------------------------------------
    in.read(buf) returning short is not an error and is not rare — it is the
    normal case for a value spanning TCP segments. It just never happens on
    localhost with small values, so it ships. Measured against a stream that
    delivers one byte per call, asking for 17:

        single in.read(buf)     got 1 byte,   "l"
        readFully loop          got 17 bytes, "line1\nline2\nline3"

    Either loop, or use DataInputStream.readFully, which exists for this.

  --------------------------------------------------------------------------
  Staying in sync when you reject something
  --------------------------------------------------------------------------
    A rejected "set" still has a payload in flight. Validate the key AFTER
    reading the n bytes, never before, or the payload lands in the command
    parser and every later command on that connection is garbage. Measured:

        validate first, skip payload   CLIENT_ERROR bad key\nERROR\nEND\n
        read payload, then validate    CLIENT_ERROR bad key\nEND\n

    The extra ERROR is the value being executed. There is one case with no
    honest recovery — an unparseable byte count, where the server cannot know
    how much to skip — and the only correct move is to close the connection
    rather than guess. This server does.

  --------------------------------------------------------------------------
  Questions worth asking out loud
  --------------------------------------------------------------------------
    1. \n or \r\n?  Real memcached is \r\n; the brief here says \n. This
       server accepts an optional \r and always emits bare \n.
    2. An invalid key in a get — CLIENT_ERROR, or just absent? Treated as
       absent here: set validates, so an invalid key cannot exist.
    3. Is there a size cap? Real memcached rejects above 1 MB. Without one,
       "set k 2000000000" is a remote OOM.
    4. Expiry, flags, cas, delete, incr? All out of scope, all one line to
       mention.

  --------------------------------------------------------------------------
  Design
  --------------------------------------------------------------------------
    serve(InputStream, OutputStream, Store) is the whole protocol and knows
    nothing about sockets, so every test below runs on byte arrays with no
    ports, no threads and no timing. Server is the thin TCP wrapper over it.
    One Store is shared by every connection, so it is a ConcurrentHashMap.

    Thread-per-connection is the right scope for the round; say the words
    "virtual threads or NIO if the connection count is the problem" and move
    on, because the framing is what is being marked.

  --------------------------------------------------------------------------
  Complexity
  --------------------------------------------------------------------------
    get   O(k) hash lookups + O(total bytes written)
    set   O(n) to read the payload, O(1) to store
    space O(total stored bytes); nothing is ever evicted (no LRU, no TTL)
================================================================================
*/
public class MemcachedServer {

    /** Real memcached's default item cap. Without a cap, byte_count is a remote OOM. */
    public static final int MAX_VALUE_BYTES = 1 << 20;

    /* ============================== the store ============================== */

    /** Shared by every connection, hence concurrent. Values are bytes, never Strings. */
    public static final class Store {
        private final ConcurrentMap<String, byte[]> data = new ConcurrentHashMap<>();

        public byte[] get(String key) { return data.get(key); }

        /** Takes ownership of value; serve() hands over a freshly read array. */
        public void set(String key, byte[] value) { data.put(key, value); }

        public int size() { return data.size(); }
    }

    /* ============================= the protocol ============================= */

    public static boolean isValidKey(String key) {
        if (key.isEmpty()) return false;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            boolean ok = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
                    || (c >= '0' && c <= '9') || c == '_' || c == '-';
            if (!ok) return false;
        }
        return true;
    }

    /**
     * The entire protocol, over plain streams. Returns when the client hangs up or
     * when the framing becomes unrecoverable.
     */
    public static void serve(InputStream rawIn, OutputStream rawOut, Store store) throws IOException {
        BufferedInputStream in = new BufferedInputStream(rawIn);
        OutputStream out = new BufferedOutputStream(rawOut);

        String line;
        while ((line = readLine(in)) != null) {
            String[] parts = line.trim().split("\\s+");
            String cmd = parts[0];

            if (cmd.equals("get") && parts.length >= 2) {
                for (int i = 1; i < parts.length; i++) {
                    byte[] value = isValidKey(parts[i]) ? store.get(parts[i]) : null;
                    if (value == null) continue;                 // absent: no VALUE line at all
                    write(out, "VALUE " + parts[i] + " " + value.length + "\n");
                    out.write(value);
                    write(out, "\n");
                }
                write(out, "END\n");                             // one END for the whole request

            } else if (cmd.equals("set") && parts.length == 3) {
                int count = parseCount(parts[2]);
                if (count < 0) {
                    // Unrecoverable: the payload length is unknown, so it cannot be skipped
                    // and would be parsed as commands. Closing is the only honest answer.
                    write(out, "CLIENT_ERROR bad data chunk\n");
                    out.flush();
                    return;
                }
                byte[] value = readFully(in, count);             // payload first, ALWAYS
                consumeTerminator(in);
                if (!isValidKey(parts[1])) {
                    write(out, "CLIENT_ERROR bad key\n");        // still in sync
                } else {
                    store.set(parts[1], value);
                    write(out, "STORED\n");
                }

            } else {
                write(out, "ERROR\n");
            }
            out.flush();
        }
        out.flush();
    }

    /** -1 for anything that is not a byte count this server will accept. */
    private static int parseCount(String s) {
        try {
            int n = Integer.parseInt(s);
            return (n < 0 || n > MAX_VALUE_BYTES) ? -1 : n;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * One command line, read a byte at a time so no bytes are buffered anywhere
     * readFully cannot see them. Returns null at end of stream.
     */
    static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int c;
        while ((c = in.read()) != -1) {
            if (c == '\n') {
                byte[] b = buf.toByteArray();
                int len = (b.length > 0 && b[b.length - 1] == '\r') ? b.length - 1 : b.length;
                return new String(b, 0, len, StandardCharsets.US_ASCII);
            }
            buf.write(c);
        }
        return buf.size() == 0 ? null : buf.toString(StandardCharsets.US_ASCII);
    }

    /** Exactly n bytes, however many reads that takes. */
    static byte[] readFully(InputStream in, int n) throws IOException {
        byte[] data = new byte[n];
        int off = 0;
        while (off < n) {
            int r = in.read(data, off, n - off);
            if (r < 0) throw new EOFException("wanted " + n + " bytes, got " + off);
            off += r;
        }
        return data;
    }

    /** The newline after a payload — put back if the client omitted it. */
    private static void consumeTerminator(BufferedInputStream in) throws IOException {
        in.mark(2);
        int c = in.read();
        if (c == '\r') c = in.read();
        if (c != '\n') in.reset();
    }

    private static void write(OutputStream out, String ascii) throws IOException {
        out.write(ascii.getBytes(StandardCharsets.US_ASCII));
    }

    /* ============================ the TCP wrapper ============================ */

    /** Thread per connection, one shared Store. Port 0 asks the OS for a free port. */
    public static final class Server implements Closeable {
        private final ServerSocket socket;
        private final Store store;
        private volatile boolean running = true;

        public Server(int port, Store store) throws IOException {
            this.socket = new ServerSocket(port);
            this.store = store;
            Thread acceptor = new Thread(this::acceptLoop, "memcached-accept");
            acceptor.setDaemon(true);
            acceptor.start();
        }

        public int port() { return socket.getLocalPort(); }

        private void acceptLoop() {
            while (running) {
                try {
                    Socket client = socket.accept();
                    Thread worker = new Thread(() -> {
                        try (Socket c = client) {
                            serve(c.getInputStream(), c.getOutputStream(), store);
                        } catch (IOException ignored) {
                            // a client hanging up mid-command is normal, not an error
                        }
                    }, "memcached-conn");
                    worker.setDaemon(true);
                    worker.start();
                } catch (IOException e) {
                    if (running) throw new UncheckedIOException(e);
                    return;
                }
            }
        }

        @Override public void close() throws IOException {
            running = false;
            socket.close();
        }
    }

    /* ================================ tests ================================ */

    public static void main(String[] args) throws Exception {

        /* ---------------------------- the brief ---------------------------- */
        expect("missing key is END and nothing else", run("get does_not_exist\n"), "END\\n");
        expect("set then get round trips",
                run("set foo 3\nbar\nget foo\n"), "STORED\\nVALUE foo 3\\nbar\\nEND\\n");
        expect("byte_count is the length in the VALUE line",
                run("set k 11\nhello world\nget k\n"),
                "STORED\\nVALUE k 11\\nhello world\\nEND\\n");
        expect("a zero-byte value is legal",
                run("set empty 0\n\nget empty\n"), "STORED\\nVALUE empty 0\\n\\nEND\\n");
        expect("overwrite replaces",
                run("set k 1\na\nset k 1\nb\nget k\n"), "STORED\\nSTORED\\nVALUE k 1\\nb\\nEND\\n");

        /* -------------------------- the bonus: multi-get -------------------------- */
        expect("multi-get returns a block per key, one END",
                run("set a 1\nx\nset b 1\ny\nget a b\n"),
                "STORED\\nSTORED\\nVALUE a 1\\nx\\nVALUE b 1\\ny\\nEND\\n");
        expect("multi-get silently skips the missing ones",
                run("set a 1\nx\nget a nope b\n"), "STORED\\nVALUE a 1\\nx\\nEND\\n");
        expect("multi-get where none exist is still one END",
                run("get p q r\n"), "END\\n");
        expect("a repeated key is returned twice",
                run("set a 1\nx\nget a a\n"), "STORED\\nVALUE a 1\\nx\\nVALUE a 1\\nx\\nEND\\n");

        /* ------------------------- framing and parsing ------------------------- */
        expect("a value containing newlines survives intact",
                run("set k 17\nline1\nline2\nline3\nget k\n"),
                "STORED\\nVALUE k 17\\nline1\\nline2\\nline3\\nEND\\n");
        expect("a value that looks like a command is not executed",
                run("set k 10\nset evil 5\nget k\nget evil\n"),
                "STORED\\nVALUE k 10\\nset evil 5\\nEND\\nEND\\n");
        expect("commands are case sensitive", run("GET foo\n"), "ERROR\\n");
        expect("unknown command", run("delete foo\n"), "ERROR\\n");
        expect("get with no key", run("get\n"), "ERROR\\n");
        expect("pipelined commands in one buffer",
                run("set a 1\nx\nget a\nget a\n"),
                "STORED\\nVALUE a 1\\nx\\nEND\\nVALUE a 1\\nx\\nEND\\n");
        expect("a command line with no trailing newline at EOF still runs",
                run("get nope"), "END\\n");

        /* ------------------------------ keys ------------------------------ */
        expect("valid key alphabet", isValidKey("Ab9_-"), true);
        expect("space is not a key character", isValidKey("a b"), false);
        expect("dot is not a key character", isValidKey("a.b"), false);
        expect("empty key", isValidKey(""), false);
        expect("a bad key is rejected AND the connection stays in sync",
                run("set bad.key 5\nhello\nget nope\n"), "CLIENT_ERROR bad key\\nEND\\n");
        expect("an unparseable count closes the connection",
                run("set k xyz\nhello\nget nope\n"), "CLIENT_ERROR bad data chunk\\n");
        expect("an oversized count is refused before allocating",
                run("set k 2000000000\n"), "CLIENT_ERROR bad data chunk\\n");

        /* ---------------------------- binary values ---------------------------- */
        binaryValuesSurvive();

        /* ------------- the four traps in the header, made runnable ------------- */
        lineFramingExecutesTheValue();
        bufferedReaderStealsThePayload();
        charsetCorruptsBinary();
        shortReadUnderReads();
        validatingBeforeReadingDesyncs();

        /* ----------------------------- over real TCP ----------------------------- */
        overRealTcp();
    }

    /* ----------------------------- test helpers ----------------------------- */

    private static String run(String request) throws IOException {
        return escape(run(new Store(), request.getBytes(StandardCharsets.ISO_8859_1)));
    }

    private static byte[] run(Store store, byte[] request) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serve(new ByteArrayInputStream(request), out, store);
        return out.toByteArray();
    }

    /** Printable form, so a failing expectation shows where the bytes diverge. */
    private static String escape(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            int v = b & 0xff;
            if (v == '\n') sb.append("\\n");
            else if (v == '\r') sb.append("\\r");
            else if (v >= 0x20 && v < 0x7f) sb.append((char) v);
            else sb.append(String.format("\\x%02x", v));
        }
        return sb.toString();
    }

    /* --------------------------- the runnable traps --------------------------- */

    /** Every byte value 0..255 has to come back unchanged. */
    private static void binaryValuesSurvive() throws IOException {
        Store store = new Store();
        byte[] all = new byte[256];
        for (int i = 0; i < 256; i++) all[i] = (byte) i;

        ByteArrayOutputStream req = new ByteArrayOutputStream();
        req.write("set bin 256\n".getBytes(StandardCharsets.US_ASCII));
        req.write(all);
        req.write('\n');
        req.write("get bin\n".getBytes(StandardCharsets.US_ASCII));

        byte[] response = run(store, req.toByteArray());
        ByteArrayOutputStream want = new ByteArrayOutputStream();
        want.write("STORED\nVALUE bin 256\n".getBytes(StandardCharsets.US_ASCII));
        want.write(all);
        want.write("\nEND\n".getBytes(StandardCharsets.US_ASCII));

        expect("all 256 byte values round trip through set/get",
                Arrays.equals(response, want.toByteArray()), true);
    }

    /** Trap 1: a line-framed reader feeds the rest of the value to the command parser. */
    private static void lineFramingExecutesTheValue() throws IOException {
        String request = "set k 17\nline1\nline2\nline3\nget k\n";
        System.out.println("\ntrap 1 -- byte-count framing vs line framing, value = \"line1\\nline2\\nline3\":");
        System.out.println("  line-framed reader   " + escape(serveLineFramed(request)));
        System.out.println("  byte-framed reader   " + run(request));
        System.out.println("  the extra ERRORs are line2 and line3 being parsed as commands");
    }

    /** The naive implementation: readLine() for the payload too. */
    private static byte[] serveLineFramed(String request) throws IOException {
        Map<String, String> store = new HashMap<>();
        BufferedReader in = new BufferedReader(new StringReader(request));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String line;
        while ((line = in.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");
            if (parts[0].equals("get") && parts.length >= 2) {
                for (int i = 1; i < parts.length; i++) {
                    String v = store.get(parts[i]);
                    if (v != null) out.write(("VALUE " + parts[i] + " " + v.length() + "\n" + v + "\n")
                            .getBytes(StandardCharsets.ISO_8859_1));
                }
                out.write("END\n".getBytes(StandardCharsets.US_ASCII));
            } else if (parts[0].equals("set") && parts.length == 3) {
                store.put(parts[1], in.readLine());              // <-- the bug
                out.write("STORED\n".getBytes(StandardCharsets.US_ASCII));
            } else {
                out.write("ERROR\n".getBytes(StandardCharsets.US_ASCII));
            }
        }
        return out.toByteArray();
    }

    /** Trap 2: a Reader's private buffer eats the payload before readFully sees it. */
    private static void bufferedReaderStealsThePayload() throws IOException {
        byte[] request = "set k 6\nabcdef\nget k\n".getBytes(StandardCharsets.US_ASCII);
        ByteArrayInputStream raw = new ByteArrayInputStream(request);

        BufferedReader reader = new BufferedReader(new InputStreamReader(raw, StandardCharsets.US_ASCII));
        String command = reader.readLine();

        int leftOnRawStream = raw.available();
        System.out.printf("%ntrap 2 -- BufferedReader on the socket, %d-byte request:%n", request.length);
        System.out.printf("  read one command line (%d chars: \"%s\")%n", command.length(), command);
        System.out.printf("  bytes left on the raw stream = %d of %d%n",
                leftOnRawStream, request.length - command.length() - 1);
        System.out.println("  the payload is inside the Reader's char buffer, unreachable from the InputStream");
    }

    /** Trap 3: a String-valued store is lossy for arbitrary bytes. */
    private static void charsetCorruptsBinary() {
        int utf8Survivors = 0, latin1Survivors = 0, utf8Bytes = 0;
        for (int i = 0; i < 256; i++) {
            byte[] one = {(byte) i};
            byte[] viaUtf8 = new String(one, StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8);
            byte[] viaLatin1 = new String(one, StandardCharsets.ISO_8859_1).getBytes(StandardCharsets.ISO_8859_1);
            if (Arrays.equals(one, viaUtf8)) utf8Survivors++;
            if (Arrays.equals(one, viaLatin1)) latin1Survivors++;
            utf8Bytes += viaUtf8.length;
        }
        System.out.println("\ntrap 3 -- storing values as String, over the 256 single-byte values:");
        System.out.printf("  Map<String,byte[]>            256 of 256 survive%n");
        System.out.printf("  Map<String,String> via UTF-8  %d of 256 survive, 256 bytes in -> %d bytes out%n",
                utf8Survivors, utf8Bytes);
        System.out.printf("  Map<String,String> via Latin-1 %d of 256 survive "
                + "-- which is why an ASCII-only test passes on a broken server%n", latin1Survivors);
    }

    /** Trap 4: one read() call is not a read of n bytes. */
    private static void shortReadUnderReads() throws IOException {
        byte[] payload = "line1\nline2\nline3".getBytes(StandardCharsets.US_ASCII);

        byte[] once = new byte[payload.length];
        int got = new DripInputStream(payload).read(once, 0, payload.length);

        byte[] looped = readFully(new DripInputStream(payload), payload.length);

        System.out.printf("%ntrap 4 -- a stream that delivers one byte per call, asking for %d:%n", payload.length);
        System.out.printf("  single in.read(buf)   got %2d bytes, \"%s\"%n", got, escape(Arrays.copyOf(once, got)));
        System.out.printf("  readFully loop        got %2d bytes, \"%s\"%n", looped.length, escape(looped));
    }

    /** A stream that always returns one byte, like a value split across TCP segments. */
    private static final class DripInputStream extends InputStream {
        private final byte[] data;
        private int pos;
        DripInputStream(byte[] data) { this.data = data; }
        @Override public int read() { return pos < data.length ? data[pos++] & 0xff : -1; }
        @Override public int read(byte[] b, int off, int len) {
            if (pos >= data.length) return -1;
            b[off] = data[pos++];
            return 1;
        }
    }

    /** Rejecting a set before reading its payload leaves the value in the command stream. */
    private static void validatingBeforeReadingDesyncs() throws IOException {
        String request = "set bad.key 5\nhello\nget nope\n";
        System.out.println("\nrejecting a set -- where the validation goes:");
        System.out.println("  validate first, skip payload   " + escape(serveValidateFirst(request)));
        System.out.println("  read payload, then validate    " + run(request));
        System.out.println("  the extra ERROR is \"hello\" being parsed as a command");
    }

    private static byte[] serveValidateFirst(String request) throws IOException {
        Store store = new Store();
        BufferedInputStream in = new BufferedInputStream(
                new ByteArrayInputStream(request.getBytes(StandardCharsets.ISO_8859_1)));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String line;
        while ((line = readLine(in)) != null) {
            String[] parts = line.trim().split("\\s+");
            if (parts[0].equals("get") && parts.length >= 2) {
                for (int i = 1; i < parts.length; i++) {
                    byte[] v = store.get(parts[i]);
                    if (v == null) continue;
                    write(out, "VALUE " + parts[i] + " " + v.length + "\n");
                    out.write(v);
                    write(out, "\n");
                }
                write(out, "END\n");
            } else if (parts[0].equals("set") && parts.length == 3) {
                if (!isValidKey(parts[1])) {                     // <-- the bug: bails out early
                    write(out, "CLIENT_ERROR bad key\n");
                    continue;
                }
                store.set(parts[1], readFully(in, Integer.parseInt(parts[2])));
                in.read();
                write(out, "STORED\n");
            } else {
                write(out, "ERROR\n");
            }
        }
        return out.toByteArray();
    }

    /** The same conversation over a real socket, to prove the wiring and not just the parser. */
    private static void overRealTcp() throws Exception {
        Store store = new Store();
        try (Server server = new Server(0, store)) {
            System.out.printf("%nover real TCP on port %d:%n", server.port());

            String reply = talk(server.port(), "set greeting 11\nhello\nworld\nget greeting nope\n");
            System.out.println("  " + escape(reply.getBytes(StandardCharsets.ISO_8859_1)));
            expect("TCP: set with an embedded newline, then multi-get", escape(reply.getBytes(StandardCharsets.ISO_8859_1)),
                    "STORED\\nVALUE greeting 11\\nhello\\nworld\\nEND\\n");

            // A second connection sees the first one's writes: the Store is shared.
            String second = talk(server.port(), "get greeting\n");
            expect("TCP: a second connection sees the shared store",
                    escape(second.getBytes(StandardCharsets.ISO_8859_1)),
                    "VALUE greeting 11\\nhello\\nworld\\nEND\\n");

            concurrentClientsAgree(server.port());
        }
    }

    /** Sends a request, half-closes so the server sees EOF, reads the whole reply. */
    private static String talk(int port, String request) throws IOException {
        try (Socket s = new Socket("127.0.0.1", port)) {
            s.getOutputStream().write(request.getBytes(StandardCharsets.ISO_8859_1));
            s.getOutputStream().flush();
            s.shutdownOutput();
            ByteArrayOutputStream reply = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int r;
            while ((r = s.getInputStream().read(buf)) > 0) reply.write(buf, 0, r);
            return reply.toString(StandardCharsets.ISO_8859_1);
        }
    }

    /** The store is shared mutable state; this is the test that a HashMap would fail. */
    private static void concurrentClientsAgree(int port) throws Exception {
        int clients = 32, perClient = 25;
        ExecutorService pool = Executors.newFixedThreadPool(8);
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int c = 0; c < clients; c++) {
            final int id = c;
            futures.add(pool.submit(() -> {
                for (int i = 0; i < perClient; i++) {
                    String key = "k" + id + "-" + i;
                    String value = "v" + id + "-" + i;
                    String reply = talk(port, "set " + key + " " + value.length() + "\n" + value
                            + "\nget " + key + "\n");
                    if (!reply.equals("STORED\nVALUE " + key + " " + value.length() + "\n" + value + "\nEND\n"))
                        return false;
                }
                return true;
            }));
        }
        boolean allOk = true;
        for (Future<Boolean> f : futures) allOk &= f.get(60, TimeUnit.SECONDS);
        pool.shutdown();
        expect(clients * perClient + " set/get pairs across " + clients + " concurrent connections", allOk, true);
    }

    private static <T> void expect(String label, T got, T expected) {
        boolean ok = Objects.equals(got, expected);
        System.out.println((ok ? "OK   " : "FAIL ") + label
                + (ok ? "" : "\n  got     =" + got + "\n  expected=" + expected));
    }
}
