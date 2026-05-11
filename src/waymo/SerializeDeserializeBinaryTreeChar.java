package waymo;
/*
Serialize and Deserialize Binary Tree with Character Values

Design serialize / deserialize for a binary tree whose nodes hold a single
character.  Before each value goes into the serialized string it must be
Base64-encoded.  The serialized form has no line breaks.  Round-tripping
serialize → deserialize must reproduce the exact tree.

Constraints
  - Node values are limited to printable ASCII characters.
  - Base64 must not contain line breaks (use the standard, not MIME, encoder).
  - The tree can have up to 10,000 nodes — that's deep enough to stress
    the default JVM stack if the tree is skewed, so the implementation is
    iterative (no recursion).

Example
  Tree:
          a
         / \
        b   c
       /
      d

  Pre-order serialized form (the Base64 of "a","b","d","null","null","null","c","null","null"):
    YQ==,Yg==,ZA==,#,#,#,Yw==,#,#
 */

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Base64;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;

/*
Algorithm — preorder with null markers + iterative traversal.

  serialize:
    DFS preorder.  At each visited node write its Base64-encoded value;
    at each null child write a sentinel "#".  Separator is comma.

    Iterative implementation with an explicit stack: push the root, then
    repeatedly pop a node, emit its token, and push its right + left
    children (right first so left is popped first — preorder).  Null
    children are pushed verbatim and emitted as "#".

  deserialize:
    Split on comma; walk the tokens left-to-right while maintaining an
    explicit stack of (in-progress node, slot ∈ {LEFT, RIGHT}).  Each token
    is assigned to the current "needs-this-slot" position, then if the
    token was a real (non-null) node it becomes the new top of the stack
    with slot = LEFT.  When a node has both children filled it pops.

  Why iterative instead of the textbook recursion:
    Worst case is a 10,000-node left-skewed tree → 10K-deep recursion.
    HotSpot's default thread stack is ~512 KB on macOS / Linux; each frame
    here is ~50 bytes, so 10K levels overflows on small stacks.  The
    iterative form is unconditionally safe.

  Why Base64 even though values are ASCII:
    The spec asks for it.  In practice it also lets the comma separator
    work unambiguously — Base64 alphabet is [A-Za-z0-9+/=], no commas.

  Encoding choice:
    java.util.Base64.getEncoder() — RFC 4648 standard, no line breaks by
    default (only the MIME encoder wraps at 76 chars).  Padding is kept
    so the encoded form for 1 byte is always 4 chars ("a" → "YQ==").

Complexity
  Time   : O(n) for both serialize and deserialize.
  Memory : O(n) for the output string and the explicit traversal stack.
*/
public class SerializeDeserializeBinaryTreeChar {

    /** Simple binary tree node carrying a single character. */
    public static final class TreeNode {
        public char val;
        public TreeNode left;
        public TreeNode right;

        public TreeNode(char val) { this.val = val; }
        public TreeNode(char val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    private static final String NULL_MARKER = "#";
    private static final char DELIM = ',';
    private static final Base64.Encoder ENC = Base64.getEncoder();           // no line breaks
    private static final Base64.Decoder DEC = Base64.getDecoder();

    /* --------------------------- Serialize --------------------------- */

    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        // LinkedList allows null elements (ArrayDeque does not), which lets us push
        // null child placeholders without inventing a sentinel TreeNode.
        Deque<TreeNode> stack = new LinkedList<>();
        stack.push(root);
        boolean first = true;
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            if (!first) sb.append(DELIM);
            first = false;
            if (node == null) {
                sb.append(NULL_MARKER);
            } else {
                sb.append(encode(node.val));
                // push right first so left is processed first → preorder
                stack.push(node.right);
                stack.push(node.left);
            }
        }
        return sb.toString();
    }

    /* --------------------------- Deserialize --------------------------- */

    public TreeNode deserialize(String data) {
        if (data == null || data.isEmpty()) return null;
        String[] tokens = data.split(",", -1);
        if (tokens.length == 0 || isNullToken(tokens[0])) return null;

        TreeNode root = newNode(tokens[0]);
        // State per stack entry: 0 = needs LEFT next, 1 = needs RIGHT next.
        Deque<TreeNode> stack = new ArrayDeque<>();
        Deque<Integer> state = new ArrayDeque<>();
        stack.push(root);
        state.push(0);

        int idx = 1;
        while (!stack.isEmpty() && idx < tokens.length) {
            TreeNode parent = stack.peek();
            int slot = state.peek();
            String tok = tokens[idx++];
            TreeNode child = isNullToken(tok) ? null : newNode(tok);

            if (slot == 0) {
                parent.left = child;
                state.pop();
                state.push(1);          // next we need parent's RIGHT child
            } else {
                parent.right = child;
                state.pop();
                stack.pop();            // parent now fully built — drop it
            }
            if (child != null) {
                stack.push(child);
                state.push(0);
            }
        }
        return root;
    }

    /* --------------------------- Helpers --------------------------- */

    private static String encode(char c) {
        // Encode the character as its single-byte ASCII representation.
        // For printable ASCII (the stated constraint) this is 1 byte → 4 chars Base64.
        return ENC.encodeToString(new byte[]{(byte) c});
    }

    private static char decode(String s) {
        byte[] bytes = DEC.decode(s);
        // bytes.length should always be 1 for our use case; mask & 0xFF preserves the byte value.
        return (char) (bytes[0] & 0xFF);
    }

    private static TreeNode newNode(String token) {
        return new TreeNode(decode(token));
    }

    private static boolean isNullToken(String tok) {
        return tok.equals(NULL_MARKER) || tok.isEmpty();
    }

    /* --------------------------- Tree comparison (for tests) --------------------------- */

    public static boolean equalTrees(TreeNode a, TreeNode b) {
        Deque<TreeNode> sa = new LinkedList<>();
        Deque<TreeNode> sb = new LinkedList<>();
        sa.push(a);
        sb.push(b);
        while (!sa.isEmpty() && !sb.isEmpty()) {
            TreeNode x = sa.pop();
            TreeNode y = sb.pop();
            if (x == null && y == null) continue;
            if (x == null || y == null) return false;
            if (x.val != y.val) return false;
            sa.push(x.right); sa.push(x.left);
            sb.push(y.right); sb.push(y.left);
        }
        return sa.isEmpty() && sb.isEmpty();
    }

    /** Renders the tree as preorder with explicit null markers — just for pretty-printing in tests. */
    public static String preorderString(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        Deque<TreeNode> stack = new LinkedList<>();
        stack.push(root);
        boolean first = true;
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            if (!first) sb.append(',');
            first = false;
            if (node == null) {
                sb.append('#');
            } else {
                sb.append(node.val);
                stack.push(node.right);
                stack.push(node.left);
            }
        }
        return sb.toString();
    }

    /* --------------------------- Demo + tests --------------------------- */

    public static void main(String[] args) {
        SerializeDeserializeBinaryTreeChar solver = new SerializeDeserializeBinaryTreeChar();

        // ---------- Spec example ----------
        //         a
        //        / \
        //       b   c
        //      /
        //     d
        TreeNode spec = new TreeNode('a',
                new TreeNode('b', new TreeNode('d'), null),
                new TreeNode('c'));
        runRoundTrip(solver, "spec example", spec, "YQ==,Yg==,ZA==,#,#,#,Yw==,#,#");

        // ---------- Empty tree ----------
        runRoundTrip(solver, "empty tree", null, "#");

        // ---------- Single node ----------
        // Base64('z' = 0x7A) = "eg==", so the full form is "eg==,#,#".
        runRoundTrip(solver, "single node", new TreeNode('z'), "eg==,#,#");

        // ---------- Left-skewed tree (a -> a -> a -> ...) ----------
        TreeNode skew = null;
        for (int i = 0; i < 10; i++) {
            TreeNode n = new TreeNode((char) ('a' + i));
            n.left = skew;
            skew = n;
        }
        runRoundTrip(solver, "left-skewed 10 nodes (no expected string)", skew, null);

        // ---------- Special printable chars ----------
        TreeNode chars = new TreeNode('!',
                new TreeNode('~',
                        new TreeNode(' '),
                        new TreeNode('/')),
                new TreeNode(','));   // comma in a value — Base64 escapes it cleanly
        runRoundTrip(solver, "punctuation incl. comma value", chars, null);

        // ---------- 10,000-node skewed tree (the spec's max) — stresses iterative path ----------
        TreeNode big = null;
        Random rnd = new Random(7);
        for (int i = 0; i < 10_000; i++) {
            char ch = (char) (32 + rnd.nextInt(95));  // printable ASCII 32..126
            TreeNode n = new TreeNode(ch);
            // alternate left / right skew so the structure is varied
            if (i % 2 == 0) n.left = big; else n.right = big;
            big = n;
        }
        long t0 = System.nanoTime();
        String bigSer = solver.serialize(big);
        long sMs = (System.nanoTime() - t0) / 1_000_000;
        t0 = System.nanoTime();
        TreeNode bigBack = solver.deserialize(bigSer);
        long dMs = (System.nanoTime() - t0) / 1_000_000;
        boolean bigOk = equalTrees(big, bigBack);
        System.out.printf("%s 10,000-node alternating-skew tree: serialized %d chars in %d ms, deserialized in %d ms%n",
                bigOk ? "OK   " : "FAIL ", bigSer.length(), sMs, dMs);

        // ---------- Random trees fuzz test ----------
        int mismatches = 0;
        Random fuzz = new Random(31);
        for (int t = 0; t < 200; t++) {
            int n = fuzz.nextInt(40);
            TreeNode tree = randomTree(fuzz, n);
            String s1 = solver.serialize(tree);
            TreeNode back = solver.deserialize(s1);
            String s2 = solver.serialize(back);
            if (!s1.equals(s2) || !equalTrees(tree, back)) {
                mismatches++;
                System.out.println("MISMATCH on random tree (n=" + n + "): " + preorderString(tree));
                System.out.println("  s1=" + s1);
                System.out.println("  s2=" + s2);
            }
        }
        System.out.println("Random fuzz: " + (200 - mismatches) + "/200 ok");

        // ---------- Serialized output must contain no line breaks (Base64 hygiene) ----------
        boolean noNewlines = !bigSer.contains("\n") && !bigSer.contains("\r");
        System.out.println((noNewlines ? "OK   " : "FAIL ")
                + "no line breaks in Base64 output for 10K-node tree");

        // ---------- Round-tripping an idempotent operation: deserialize(serialize(x)) == x ----------
        String oneMore = solver.serialize(spec);
        TreeNode back = solver.deserialize(oneMore);
        String oneMore2 = solver.serialize(back);
        boolean idem = oneMore.equals(oneMore2);
        System.out.println((idem ? "OK   " : "FAIL ") + "serialize(deserialize(serialize(x))) == serialize(x)");
    }

    /* --------------------------- Test plumbing --------------------------- */

    private static void runRoundTrip(SerializeDeserializeBinaryTreeChar solver, String name,
                                     TreeNode original, String expectedSerialized) {
        String got = solver.serialize(original);
        boolean serializedMatches = expectedSerialized == null || got.equals(expectedSerialized);
        TreeNode back = solver.deserialize(got);
        boolean treesMatch = equalTrees(original, back);
        boolean ok = serializedMatches && treesMatch;
        System.out.printf("%s %s%n", ok ? "OK   " : "FAIL ", name);
        System.out.println("  serialized = " + got);
        if (expectedSerialized != null && !got.equals(expectedSerialized)) {
            System.out.println("  expected   = " + expectedSerialized);
        }
        if (!treesMatch) {
            System.out.println("  original   = " + preorderString(original));
            System.out.println("  rebuilt    = " + preorderString(back));
        }
    }

    private static TreeNode randomTree(Random rnd, int n) {
        if (n == 0) return null;
        // Build a random binary tree by repeatedly inserting at a random open slot.
        TreeNode root = new TreeNode(randPrintable(rnd));
        for (int i = 1; i < n; i++) insertRandom(root, randPrintable(rnd), rnd);
        return root;
    }

    private static void insertRandom(TreeNode root, char val, Random rnd) {
        TreeNode cur = root;
        while (true) {
            boolean goLeft = rnd.nextBoolean();
            if (goLeft) {
                if (cur.left == null) { cur.left = new TreeNode(val); return; }
                cur = cur.left;
            } else {
                if (cur.right == null) { cur.right = new TreeNode(val); return; }
                cur = cur.right;
            }
        }
    }

    private static char randPrintable(Random rnd) {
        return (char) (32 + rnd.nextInt(95));   // ASCII 32..126
    }

    /** Sanity check that Base64 encode/decode of a single char round-trips. */
    @SuppressWarnings("unused")
    private static boolean charRoundTrip(char c) {
        return decode(encode(c)) == c;
    }

    /** Compute the spec example's expected Base64 string programmatically (useful comment). */
    @SuppressWarnings("unused")
    private static String specExpected() {
        // a, b, d, null, null, null, c, null, null
        StringBuilder sb = new StringBuilder();
        sb.append(ENC.encodeToString("a".getBytes(StandardCharsets.US_ASCII))).append(',');
        sb.append(ENC.encodeToString("b".getBytes(StandardCharsets.US_ASCII))).append(',');
        sb.append(ENC.encodeToString("d".getBytes(StandardCharsets.US_ASCII))).append(',');
        sb.append("#,#,#,");
        sb.append(ENC.encodeToString("c".getBytes(StandardCharsets.US_ASCII))).append(',');
        sb.append("#,#");
        return sb.toString();
    }
}
