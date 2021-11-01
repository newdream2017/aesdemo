package AESHelper;

import java.lang.reflect.Array;

public class AES {
    static word[][] InvRoundKey;
    static int Nb;
    static int Nk;
    static int Nr;
    static word[][] RoundKey;

    public static word[] encrypt(word[] plaintext, word[] CipherKey) {
        Nb = 4;
        Nk = CipherKey.length;
        Nr = Nk + 6;
        RoundKey = KeyExpansion(CipherKey);
        word[] ciphertext = new word[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            ciphertext[i] = new word(plaintext[i]);
        }
        word[] ciphertext2 = AddRoundKey(ciphertext, RoundKey[0]);
        for (int i2 = 1; i2 < Nr + 1; i2++) {
            word[] ciphertext3 = ShiftRow(ByteSub(ciphertext2));
            if (i2 != Nr) {
                ciphertext3 = MixColumn(ciphertext3);
            }
            ciphertext2 = AddRoundKey(ciphertext3, RoundKey[i2]);
        }
        return ciphertext2;
    }

    public static word[] decrypt(word[] ciphertext, word[] CipherKey) {
        Nb = 4;
        Nk = CipherKey.length;
        Nr = Nk + 6;
        InvRoundKey = InvKeyExpansion(CipherKey);
        word[] plaintext = new word[ciphertext.length];
        for (int i = 0; i < ciphertext.length; i++) {
            plaintext[i] = new word(ciphertext[i]);
        }
        word[] plaintext2 = AddRoundKey(plaintext, InvRoundKey[Nr]);
        for (int i2 = Nr - 1; i2 >= 0; i2--) {
            word[] plaintext3 = InvShiftRow(InvByteSub(plaintext2));
            if (i2 != 0) {
                plaintext3 = InvMixColumn(plaintext3);
            }
            plaintext2 = AddRoundKey(plaintext3, InvRoundKey[i2]);
        }
        return plaintext2;
    }

    static word[] ByteSub(word[] state) {
        for (int i = 0; i < Nb; i++) {
            for (int j = 0; j < 4; j++) {
                state[i].word[j] = word.inverse(state[i].word[j]);
                state[i].word[j] = AffineTransformation(state[i].word[j], 'C');
            }
        }
        return state;
    }

    static word[] ShiftRow(word[] state) {
        byte[][] b = (byte[][]) Array.newInstance(Byte.TYPE, 4, Nb);
        for (int j = 0; j < Nb; j++) {
            for (int i = 0; i < 4; i++) {
                b[i][j] = state[j].word[i];
            }
        }
        for (int i2 = 1; i2 < 4; i2++) {
            for (int k = 0; k < i2; k++) {
                byte t = b[i2][0];
                for (int j2 = 0; j2 < Nb - 1; j2++) {
                    b[i2][j2] = b[i2][j2 + 1];
                }
                b[i2][Nb - 1] = t;
            }
        }
        for (int j3 = 0; j3 < Nb; j3++) {
            for (int i3 = 0; i3 < 4; i3++) {
                state[j3].word[i3] = b[i3][j3];
            }
        }
        return state;
    }

    static word[] MixColumn(word[] state) {
        word a = new word(new byte[]{2, 1, 1, 3});
        for (int i = 0; i < Nb; i++) {
            state[i] = word.multiply(a, state[i]);
        }
        return state;
    }

    static word[] AddRoundKey(word[] state, word[] key) {
        for (int i = 0; i < Nb; i++) {
            state[i] = word.add(state[i], key[i]);
        }
        return state;
    }

    static word[][] KeyExpansion(word[] CipherKey) {
        word[] W = new word[(Nb * (Nr + 1))];
        if (Nk <= 6) {
            for (int i = 0; i < Nk; i++) {
                W[i] = CipherKey[i];
            }
            for (int i2 = Nk; i2 < W.length; i2++) {
                word Temp = new word(W[i2 - 1]);
                if (i2 % Nk == 0) {
                    Temp = word.add(SubByte(Rotl(Temp)), Rcon(i2 / Nk));
                }
                W[i2] = word.add(W[i2 - Nk], Temp);
            }
        } else {
            for (int i3 = 0; i3 < Nk; i3++) {
                W[i3] = CipherKey[i3];
            }
            for (int i4 = Nk; i4 < W.length; i4++) {
                word Temp2 = new word(W[i4 - 1]);
                if (i4 % Nk == 0) {
                    Temp2 = word.add(SubByte(Rotl(Temp2)), Rcon(i4 / Nk));
                } else if (i4 % Nk == 4) {
                    Temp2 = SubByte(Temp2);
                }
                W[i4] = word.add(W[i4 - Nk], Temp2);
            }
        }
        word[][] RoundKey2 = (word[][]) Array.newInstance(word.class, Nr + 1, Nb);
        for (int i5 = 0; i5 < Nr + 1; i5++) {
            for (int j = 0; j < Nb; j++) {
                RoundKey2[i5][j] = W[(Nb * i5) + j];
            }
        }
        return RoundKey2;
    }

    static word[] InvByteSub(word[] state) {
        for (int i = 0; i < Nb; i++) {
            for (int j = 0; j < 4; j++) {
                state[i].word[j] = AffineTransformation(state[i].word[j], 'D');
                state[i].word[j] = word.inverse(state[i].word[j]);
            }
        }
        return state;
    }

    static word[] InvShiftRow(word[] state) {
        byte[][] b = (byte[][]) Array.newInstance(Byte.TYPE, 4, Nb);
        for (int j = 0; j < Nb; j++) {
            for (int i = 0; i < 4; i++) {
                b[i][j] = state[j].word[i];
            }
        }
        for (int i2 = 1; i2 < 4; i2++) {
            for (int k = 0; k < Nb - i2; k++) {
                byte t = b[i2][0];
                for (int j2 = 0; j2 < Nb - 1; j2++) {
                    b[i2][j2] = b[i2][j2 + 1];
                }
                b[i2][Nb - 1] = t;
            }
        }
        for (int j3 = 0; j3 < Nb; j3++) {
            for (int i3 = 0; i3 < 4; i3++) {
                state[j3].word[i3] = b[i3][j3];
            }
        }
        return state;
    }

    static word[] InvMixColumn(word[] state) {
        word a = new word(new byte[]{14, 9, 13, 11});
        for (int i = 0; i < Nb; i++) {
            state[i] = word.multiply(a, state[i]);
        }
        return state;
    }

    static word[][] InvKeyExpansion(word[] CipherKey) {
        word[][] InvRoundKey2 = KeyExpansion(CipherKey);
        for (int i = 1; i < Nr; i++) {
            InvRoundKey2[i] = InvMixColumn(InvRoundKey2[i]);
        }
        return InvRoundKey2;
    }

    static word SubByte(word a) {
        word w = new word(a);
        for (int i = 0; i < 4; i++) {
            w.word[i] = word.inverse(w.word[i]);
            w.word[i] = AffineTransformation(w.word[i], 'C');
        }
        return w;
    }

    static word Rotl(word a) {
        word w = new word(a);
        byte b = w.word[0];
        for (int i = 0; i < 3; i++) {
            w.word[i] = w.word[i + 1];
        }
        w.word[3] = b;
        return w;
    }

    static word Rcon(int n) {
        word Rcon = new word(new byte[4]);
        byte RC = 1;
        for (int i = 1; i < n; i++) {
            RC = word.xtime(RC);
        }
        Rcon.word[0] = RC;
        return Rcon;
    }

    static byte AffineTransformation(byte b, char sign) {
        byte[] x = Integer.toBinaryString((b & 255) + 256).substring(1).getBytes();
        for (int i = 0; i < x.length; i++) {
            x[i] = (byte) (x[i] - 48);
        }
        if (sign == 'C') {
            byte[] x_ = new byte[8];
            byte b_ = 0;
            for (int i2 = 0; i2 < 8; i2++) {
                x_[i2] = (byte) ((((x[i2] ^ x[(i2 + 1) % 8]) ^ x[(i2 + 2) % 8]) ^ x[(i2 + 3) % 8]) ^ x[(i2 + 4) % 8]);
                b_ = (byte) ((int) (((double) b_) + (((double) x_[i2]) * Math.pow(2.0d, (double) (7 - i2)))));
            }
            return (byte) (b_ ^ 99);
        }
        byte[] x_2 = new byte[8];
        byte b_2 = 0;
        for (int i3 = 0; i3 < 8; i3++) {
            x_2[i3] = (byte) ((x[(i3 + 1) % 8] ^ x[(i3 + 3) % 8]) ^ x[(i3 + 6) % 8]);
            b_2 = (byte) ((int) (((double) b_2) + (((double) x_2[i3]) * Math.pow(2.0d, (double) (7 - i3)))));
        }
        return (byte) (b_2 ^ 5);
    }
}
