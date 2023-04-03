import java.util.Arrays;

public class Helpers {

    public static int bytesToInt(byte[] array, boolean flip) {
        if(array.length > 0 && array.length <= 4) { //Only 4 bytes can fit into an integer
            int i = 0x00000000;

            if(!flip) {
                for (byte b : array) {
                    i = (i << 8) | b & 0xFF;
                }
            }else{
                for(int bi=array.length-1;bi>=0;bi--) {
                    i = (i << 8) | array[bi] & 0xFF;
                }
            }

            return i;
        }

        return -1;
    }

    public static int bytesToInt(byte[] array, int newLength, boolean flip) {
        return bytesToInt(Arrays.copyOf(array, newLength), flip);
    }

    public static int bytesToInt(byte[] array, int from, int to, boolean flip) {
        return bytesToInt(Arrays.copyOfRange(array, from, to), flip);
    }

    public static String humanize(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for(byte b:array) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

}
