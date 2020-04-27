package pers.cs.videoandaudio.net;

/**
 * Base64编码解码
 */
public class BytesHandler {

    private static char[] chars;
    private static byte[] bytes;

    static {
        chars = new char[64];
        int j = 0;
        //大写字母26
        for (int i = 65; i <= 90; i++, j++) {
            chars[j] = (char) i;
        }
        //小写字母26
        for (int i = 97; i <= 122; i++, j++) {
            chars[j] = (char) i;
        }
        //数字10
        for (int i = 48; i <= 57; i++, j++) {
            chars[j] = (char) i;
        }
        //"+"
        chars[j] = 43;
        j++;
        //"/"
        chars[j] = 47;

        bytes = new byte[128];
        //byte一字节，初始化为-1
        for (int i = 0; i < 128; i++) {
            bytes[i] = -1;
        }
        //bytes[65]=0;……
        for (int i = 0; i < 64; i++) {
            bytes[chars[i]] = (byte) i;
        }

    }

    public static char[] getChars(byte[] bytes) {
        return getChars(bytes, 0, bytes.length);
    }

    public static char[] getChars(byte[] bytes, int start, int length) {
        //转换的字符不是3的倍数时,
        //原1个字符有2个有数，2个有3个，3个有4个
        //原4个字符有6个有数，5个有7个，6个有8个
        int num0 = (length * 4 + 2) / 3;
        //'='
        final char CHAR = 61;
        //原1、2、3个字符转为4个
        //原4、5、6转化为8个
        //3个ASCII字符（字节）用4个可打印字符（A-Za-z0-9）表示
        char[] result = new char[(length + 2) / 3 * 4];

        int max = start + length;
        int bytesIndex = start;
        int resultIndex = 0;

        for (; bytesIndex < max; ) {

            int n0 = bytes[bytesIndex++] & 0xFF;

            int n1 = 0;
            if (bytesIndex < max) {
                n1 = bytes[bytesIndex++] & 0xFF;
            }

            int n2 = 0;
            if (bytesIndex < max) {
                n2 = bytes[bytesIndex++] & 0xFF;
            }

            int i1 = n0 >>> 2;
            int i2 = ((n0 & 0x3) << 4) | (n1 >>> 4);
            int i3 = ((n1 & 0xF) << 2) | (n2 >>> 6);
            int i4 = n2 & 0x3F;

            //1、2、3个字符转换为4个后前两个原来必有值，补0在后边
            //4、5、6个字符转换为8个后第5、6字符原来也有值
            result[resultIndex++] = chars[i1];
            result[resultIndex++] = chars[i2];

            char c;
            c = resultIndex < num0 ? chars[i3] : CHAR;
            result[resultIndex++] = c;

            c = resultIndex < num0 ? chars[i4] : CHAR;
            result[resultIndex++] = c;
        }
        return result;
    }
}
