import AESHelper.AES;
import AESHelper.word;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Demo {


    static word[] toWordArr(byte[] b) {
        int len = b.length / 4;
        if (b.length % 4 != 0) {
            len++;
        }
        word[] w = new word[len];
        for (int i = 0; i < len; i++) {
            byte[] c = new byte[4];
            if (i * 4 < b.length) {
                for (int j = 0; j < 4; j++) {
                    c[j] = b[(i * 4) + j];
                }
            }
            w[i] = new word(c);
        }
        return w;
    }

    static String wordArrStr(word[] w) {
        String str = "";
        for (int i = 0; i < w.length; i++) {
            str = String.valueOf(str) + w[i];
        }
        return str;
    }


    static String aa(byte[] plain, byte[] key) {
        try {
            byte[] tmp = new byte[16];
            int rest = plain.length % 16;
            int begin = 16 - rest;
            System.arraycopy(plain, 0, tmp, 0, plain.length);
            if (plain.length < 16) {
                int index = 0;
                for (int i = rest; i < 16; i++) {
                    tmp[i] = (byte) begin;
                    index++;
                    tmp[i] = 0;
                }
            }
            word[] plaintext = toWordArr(tmp);
           // System.out.println("明文2：" + wordArrStr(plaintext));
            return wordArrStr(AES.encrypt(plaintext, toWordArr(key)));
        } catch (Exception e) {
            return "";
        }
    }
    static String bb(byte[] plain, byte[] key) {

        try {

            word[] plaintext = toWordArr(plain);
            //System.out.println("密文2：" + wordArrStr(plaintext));
            return wordArrStr(AES.decrypt(plaintext, toWordArr(key)));
        } catch (Exception e) {
            return "";
        }
    }

    public String decrypt(byte[] plain, byte[] key) {
        byte[] pBuf;
        try {
            ByteBuffer bb = ByteBuffer.wrap(plain);
            String content = "";
            while (true) {
                byte[] bArr = new byte[16];
                int restCount = bb.remaining();
                if (restCount == 0) {
                    return content;
                }
                if (restCount > 16) {
                    pBuf = new byte[16];
                } else {
                    pBuf = new byte[restCount];
                }
                bb.get(pBuf);
                System.out.println(content);
                System.out.println(pBuf);
                content = String.valueOf(content) + bb(pBuf, key);
            }
        } catch (Exception e) {
            return "content1content2";
        }

    }
    public String Encrypt(byte[] plain, byte[] key) {
        byte[] pBuf;
        try {
            ByteBuffer bb = ByteBuffer.wrap(plain);
            String content = "";
            while (true) {
                byte[] bArr = new byte[16];
                int restCount = bb.remaining();
                if (restCount == 0) {
                    return content;
                }
                if (restCount > 16) {
                    pBuf = new byte[16];
                } else {
                    pBuf = new byte[restCount];
                }
                bb.get(pBuf);
//                System.out.println(String.valueOf(content));
//                System.out.println(pBuf);
                content = String.valueOf(content) + aa(pBuf, key);
            }
        } catch (Exception e) {
            return "content1content2";
        }
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] hexString2Bytes(String hex) {
        byte[] b = null;
        if (hex != null && !hex.equals("") && hex.length() % 2 == 0) {
            String hex2 = hex.toUpperCase();
            int len = hex2.length() / 2;
            b = new byte[len];
            char[] hc = hex2.toCharArray();
            for (int i = 0; i < len; i++) {
                int p = i * 2;
                b[i] = (byte) ((charToByte(hc[p]) << 4) | charToByte(hc[p + 1]));
            }
        }
        return b;
    }

    public static byte[] String2Bytes(String hex) {
        byte[] b = null;
        if (hex != null && !hex.equals("") && hex.length() % 2 == 0) {
            String hex2 = hex.toUpperCase();
            int len = hex2.length() / 2;
            b = new byte[len];
            char[] hc = hex2.toCharArray();
            for (int i = 0; i < len; i++) {
                int p = i * 2;
                b[i] = (byte) ((charToByte(hc[p]) << 4) | charToByte(hc[p + 1]));
            }
        }
        return b;
    }




    public static  void main(String [] args)
    {
        Demo demo = new Demo();


        String strText="hello world00000hello world00000@^*7";
        String strKey="MMProtocalJnipac";

        word[] plaintext = toWordArr(strText.getBytes());
        System.out.println("明文：" + wordArrStr(plaintext));
        word[] CipherKey = toWordArr(strKey.getBytes());
        System.out.println("密钥：" + wordArrStr(CipherKey));

        String hexEnText= demo.Encrypt(strText.getBytes(),strKey.getBytes());

        System.out.println("密文    ：" + hexEnText);
        hexEnText="f2267388ca6c1cdb0e8b7613b34d1139f2267388ca6c1cdb0e8b7613b34d113913cd6e25a596db5daa1f27759c97c8e6";
        word[] cipherText1=toWordArr(hexString2Bytes(hexEnText));
        System.out.println("密文1111：" + wordArrStr(cipherText1));



        String hexDeText= demo.decrypt(String2Bytes(hexEnText),strKey.getBytes());
        System.out.println("解密明文：" +hexDeText);









    }
}
