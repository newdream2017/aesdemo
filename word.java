package AESHelper;

public class word {
    static int m = 283;
    byte[] word;

    public word(byte[] b) {
        this.word = new byte[4];
        for (int i = 0; i < 4; i++) {
            this.word[i] = b[i];
        }
    }

    public word(word w) {
        this.word = new byte[4];
        for (int i = 0; i < 4; i++) {
            this.word[i] = w.word[i];
        }
    }

    public String toString() {
        String str = "";
        byte[] bArr = this.word;
        for (int i = 0; i < bArr.length; i++) {
            str = String.valueOf(str) + Integer.toHexString((bArr[i] & 255) + 256).substring(1);
        }
        return str;
    }

    static word add(word a, word b) {
        word c = new word(new byte[4]);
        for (int i = 0; i < 4; i++) {
            c.word[i] = add(a.word[i], b.word[i]);
        }
        return c;
    }

    static word multiply(word a, word b) {
        word c = new word(new byte[4]);
        c.word[0] = add(add(add(multiply(a.word[0], b.word[0]), multiply(a.word[3], b.word[1])), multiply(a.word[2], b.word[2])), multiply(a.word[1], b.word[3]));
        c.word[1] = add(add(add(multiply(a.word[1], b.word[0]), multiply(a.word[0], b.word[1])), multiply(a.word[3], b.word[2])), multiply(a.word[2], b.word[3]));
        c.word[2] = add(add(add(multiply(a.word[2], b.word[0]), multiply(a.word[1], b.word[1])), multiply(a.word[0], b.word[2])), multiply(a.word[3], b.word[3]));
        c.word[3] = add(add(add(multiply(a.word[3], b.word[0]), multiply(a.word[2], b.word[1])), multiply(a.word[1], b.word[2])), multiply(a.word[0], b.word[3]));
        return c;
    }

    static word xtime(word a) {
        word b = new word(new byte[4]);
        for (int i = 0; i < 4; i++) {
            b.word[i] = a.word[(i + 1) % 4];
        }
        return b;
    }

    static byte add(byte a, byte b) {
        return (byte) (a ^ b);
    }

    static byte mod(int a, int b) {
        String str_a = Integer.toBinaryString(a);
        String str_b = Integer.toBinaryString(b);
        if (str_a.length() < str_b.length()) {
            return (byte) a;
        }
        return mod((b << (str_a.length() - str_b.length())) ^ a, b);
    }

    static byte multiply(byte a, byte b) {
        int op = a & 255;
        char[] c = Integer.toBinaryString((b & 255) + 256).substring(1).toCharArray();
        int r = 0;
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '1') {
                r ^= op << (7 - i);
            }
        }
        return mod(r, m);
    }

    static byte inverse(byte a) {
        if (a == 0) {
            return 0;
        }
        byte b = Byte.MIN_VALUE;
        while (mod(multiply(a, b), m) != 1) {
            b = (byte) (b + 1);
        }
        return b;
    }

    static byte xtime(byte a) {
        int r = (a & 255) << 1;
        if (r > 127) {
            return mod(r, m);
        }
        return (byte) r;
    }
}
