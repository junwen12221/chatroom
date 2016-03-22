package lightfish.im.kit;

import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;


/**
 * 
 * @author luoyizhu@gmail.com
 * @Description: 字节工具
 * @time:2014年12月8日 下午10:20:30
 */
public final class ByteKit {
    static final Charset DEFAULT_CHARSET = CharsetUtil.UTF_8;
    public static final byte SEPARATOR = (byte) '|';

    /**
     * @Description: 将字符串转换成byte数组
     * @author: luoyizhu
     * @time:2014年12月9日 上午9:35:08
     * @title toBytes
     * @param value
     * @return
     */
    public static byte[] toBytes(final String value) {
        return value.getBytes(DEFAULT_CHARSET);
    }

    /**
     * @Description: 将byte数组转换成字符串
     * @author: luoyizhu
     * @time:2014年12月9日 上午10:11:24
     * @title getStr
     * @param value
     * @return
     */
    public static String getStr(final byte[] value) {
        return new String(value, DEFAULT_CHARSET);
    }

    /**
     * @Description: 将double转换成byte数组
     * @author: luoyizhu
     * @time:2014年12月9日 上午10:05:53
     * @title toBytes
     * @param value
     * @return
     */
    public static byte[] toBytes(final double value) {
        byte[] b = new byte[8];
        long l = Double.doubleToLongBits(value);
        for (int i = 0; i < b.length; i++) {
            b[i] = new Long(l).byteValue();
            l = l >> 8;
        }
        return b;
    }
    /**
     * 将64位的long值放到8字节的byte数组
     * @param num
     * @return 返回转换后的byte数组
     */
    public static byte[] toBytes(long num) {
        byte[] result = new byte[8];
        result[0] = (byte) (num >>> 56);// 取最高8位放到0下标
        result[1] = (byte) (num >>> 48);// 取最高8位放到0下标
        result[2] = (byte) (num >>> 40);// 取最高8位放到0下标
        result[3] = (byte) (num >>> 32);// 取最高8位放到0下标
        result[4] = (byte) (num >>> 24);// 取最高8位放到0下标
        result[5] = (byte) (num >>> 16);// 取次高8为放到1下标
        result[6] = (byte) (num >>> 8); // 取次低8位放到2下标
        result[7] = (byte) (num); // 取最低8位放到3下标
        return result;
    }

    /**
     * @Description: 将byte数组转换成double
     * @author: luoyizhu
     * @time:2014年12月9日 上午10:06:21
     * @title getDouble
     * @param value
     * @return
     */
    public static double getDouble(final byte[] value) {
        long l;
        l = value[0];
        l &= 0xff;
        l |= ((long) value[1] << 8);
        l &= 0xffff;
        l |= ((long) value[2] << 16);
        l &= 0xffffff;
        l |= ((long) value[3] << 24);
        l &= 0xffffffffl;
        l |= ((long) value[4] << 32);
        l &= 0xffffffffffl;
        l |= ((long) value[5] << 40);
        l &= 0xffffffffffffl;
        l |= ((long) value[6] << 48);
        l &= 0xffffffffffffffl;
        l |= ((long) value[7] << 56);
        return Double.longBitsToDouble(l);
    }

    /**
     * @Description: 将int转换成byte数组
     * @author: luoyizhu
     * @time:2014年12月9日 上午9:36:00
     * @title toBytes
     * @param value
     * @return
     */
    public static byte[] toBytes(final int value) {
        return new byte[] {(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value};
    }

    /**
     * @Description: 将byte数组转换成int
     * @author: luoyizhu
     * @time:2014年12月9日 上午10:06:58
     * @title getInt
     * @param value
     * @return
     */
    public static int getInt(final byte[] value) {
        return ((value[0] & 0xFF) << 24) | ((value[1] & 0xFF) << 16) | ((value[2] & 0xFF) << 8) | (value[3] & 0xFF);
    }

    /**

     * 将8字节的byte数组转成一个long值

     * @param byteArray

     * @return 转换后的long型数值

     */

    public static long getLong(byte[] byteArray) {

        byte[] a = new byte[8];

        int i = a.length - 1, j = byteArray.length - 1;

        for (; i >= 0; i--, j--) {// 从b的尾部(即int值的低位)开始copy数据

            if (j >= 0)

                a[i] = byteArray[j];

            else

                a[i] = 0;// 如果b.length不足4,则将高位补0

        }

        // 注意此处和byte数组转换成int的区别在于，下面的转换中要将先将数组中的元素转换成long型再做移位操作，

        // 若直接做位移操作将得不到正确结果，因为Java默认操作数字时，若不加声明会将数字作为int型来对待，此处必须注意。

        long v0 = (long) (a[0] & 0xff) << 56;// &0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位

        long v1 = (long) (a[1] & 0xff) << 48;

        long v2 = (long) (a[2] & 0xff) << 40;

        long v3 = (long) (a[3] & 0xff) << 32;

        long v4 = (long) (a[4] & 0xff) << 24;

        long v5 = (long) (a[5] & 0xff) << 16;

        long v6 = (long) (a[6] & 0xff) << 8;

        long v7 = (long) (a[7] & 0xff);

        return v0 + v1 + v2 + v3 + v4 + v5 + v6 + v7;

    }

}
