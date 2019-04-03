package airbnb;


import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class GuessNumber {

    public static void main(String args[]) {
        String[] words = {"4323", "1354", "2222", "1231", "5432", "1111", "3233"};
        for (String word : words) {
            System.out.println(client(word).equals(word));
        }
    }

    public static String client(String t){
        char[] result = {'6','6','6','6'};
        String base = "1111";
        int baseCount = check(base, t);
        if (baseCount == 4) {
            return base;
        }
        for (int i = 0; i < 4; i++) {
            for (char c = '2'; c < '6'; c++) {
                String newBase = replace(i, c, base);
                int newBaseCount = check(newBase, t);
                if (newBaseCount != baseCount) {
                    result[i] = baseCount > newBaseCount ? '1' : c;
                }
            }
        }
        return new String(result);
    }

    private static String replace(int i, char c, String base) {
        char[] arr = base.toCharArray();
        arr[i] = c;
        return new String(arr);
    }

    private static int check(String base, String t) {
        int count = 0;
        for (int i = 0; i < base.length(); i++) {
            if (base.charAt(i) == t.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    private int getAnswer(int nr, int port, String ip) throws IOException {
        Socket socket = new Socket(ip, port);
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        output.writeInt(nr);
        DataInputStream input = new DataInputStream(socket.getInputStream());
        int res = input.readInt();
        socket.close();
        return res;
    }

    private void socketExample(){
        try {
            Socket s = new Socket("127.0.0.1",8888);

            //构建IO
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            //向服务器端发送一条消息
            bw.write("测试客户端和服务器通信，服务器接收到消息返回到客户端\n");
            bw.flush();

            //读取服务器返回的消息
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String mess = br.readLine();
            System.out.println("服务器："+mess);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
