package dropbox;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class FileCheckSum {
    public static long fileSize(String path) {
        File f = new File(path);
        return f.length();
    }

    public static String checkSum(String filename) {
//        MessageDigest md = MessageDigest.getInstance("MD5");
//        md.update(Files.readAllBytes(Paths.get(filename)));
//        byte[] digest = md.digest();
//        String myChecksum = DatatypeConverter.printHexBinary(digest).toUpperCase();
//        return myChecksum;
        String checksum = null;
        try {
            FileInputStream fis = new FileInputStream(filename);
            MessageDigest md = MessageDigest.getInstance("MD5");

            //Using MessageDigest update() method to provide input
            byte[] buffer = new byte[8192];
            int numOfBytesRead;
            while( (numOfBytesRead = fis.read(buffer)) > 0){
                md.update(buffer, 0, numOfBytesRead);
            }
            byte[] hash = md.digest();
            checksum = new BigInteger(1, hash).toString(16); //don't use this, truncates leading zero
        } catch (IOException ex) {
            System.out.println(ex.toString());
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.toString());
        }
        return checksum;
    }

    public static boolean compareFileByByte(String file1, String file2) throws IOException {
        InputStream file1InStream = null, file2InStream = null;
        BufferedInputStream file1Buffer = null, file2Buffer = null;

        try {
            file1InStream = new FileInputStream(file1);
            file2InStream = new FileInputStream(file2);

            // input stream is converted to buffered input stream
            file1Buffer = new BufferedInputStream(file1InStream);
            file2Buffer = new BufferedInputStream(file2InStream);

            // read until a single byte is available
            while(file1Buffer.available() > 0) {

                // read the byte and convert the integer to character
                char c1 = (char)file1Buffer.read();
                char c2 = (char)file2Buffer.read();
                if (c1 != c2) return false;
                // print the characters
//                System.out.println("Char: " + c1);;
            }
        } catch(Exception e) {
            // if any I/O error occurs
            e.printStackTrace();
        } finally {
            // releases any system resources associated with the stream
            if(file1InStream != null)
                file1InStream.close();
            if(file1Buffer != null)
                file1Buffer.close();
        }
        return true;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        String filename = "/Users/yunzo/Downloads/schedule.HEIC";
        String filename2 = "/Users/yunzo/Downloads/schedule copy.png";
        String filename3 = "/Users/yunzo/Downloads/Screen Shot 2019-07-07 at 13.42.57.png";
        System.out.println(checkSum(filename).equals(checkSum(filename2)));
        System.out.println(compareFileByByte(filename, filename2));
        System.out.println(compareFileByByte(filename3, filename2));
        System.out.println(fileSize(filename) == fileSize(filename3));
    }
}
