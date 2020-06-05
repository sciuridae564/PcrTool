package cn.sciuridae.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static cn.sciuridae.constant.*;

public class stringTool {

    //将cq码at 提取出qq号 long形式返回
    public static long cqAtoNumber(String string) {
        return Long.parseLong(string.substring(10, string.length() - 1));
    }

    //找这个字符串里at人的cq码出现了多少次
    public static int searchAtNumber(String str) {
        int n = 0;
        int length = coolQAt.length();
        int index = str.indexOf(coolQAt);
        while (index != -1) {
            n++;
            index = str.indexOf(coolQAt, index + length);
        }
        return n;
    }

    //将年-月-日字符串转换为locadate
    public static LocalDate strToLocaDate(String str) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(str, formatter);
    }

    //存在这种命令  #命令名 @机器人 参数1
    //返回参数1并忽略所有空格
    //只是找到第一个】之后截断
    public static String getVar(String msg) {
        String work = msg.replace(" ", "");
        return work.substring(work.indexOf("]") + 1);
    }

    /**
     * 将传入的字符转换成伤害值
     *
     * @param s
     * @param flag 1为自己出刀2为代刀
     * @return
     */
    public static int getHurt(String s, int flag) throws NumberFormatException {
        String work = s.replaceAll(" +", "");
        int i = 0;
        switch (flag) {
            case 1:
                if (s.charAt(s.length() - 1) != 'w' && s.charAt(s.length() - 1) != 'W') {
                    i = Integer.parseInt(work.substring(3));
                } else {
                    i = Integer.parseInt(work.substring(3, work.length() - 1)) * 10000;
                }
                break;
            case 2:
                if (s.charAt(s.length() - 1) != 'w' && s.charAt(s.length() - 1) != 'W') {
                    i = Integer.parseInt(work.substring(work.indexOf("]") + 1));
                } else {
                    i = Integer.parseInt(work.substring(work.indexOf("]" + 1), work.length() - 1)) * 10000;
                }
                break;
        }
        return i;
    }

    //将byte(8位)分割成俩4位
    public static int[] spiltByte(byte b) {
        int i[] = new int[2];
        i[1] = b / 16;
        i[0] = (b - i[1] * 16);
        return i;
    }

    //将byte(8位)分割成俩4位
    public static int[] spiltByte(int b) {
        int i[] = new int[2];
        i[1] = b / 16;
        i[0] = (b - i[1] * 16);
        return i;
    }

    //将byte(8位)分割成俩4位
    public static byte respiltByte(int b, int c) {
        return (byte) ((b * 16 + c) > 127 ? 127 - (b * 16 + c) : b * 16 + c);
    }

    public static char[] getChars(Byte[] bytes) {
        byte[] b = toPrimitives(bytes);
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(b).flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }

    static byte[] toPrimitives(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];
        for (int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }
        return bytes;
    }


    //单日表文件名
    public static String getExcelFileName(String GroupQQ, LocalDate date) {
        return (ExcelDir + GroupQQ + "/" + date.format(df) + ".xls");
    }

    //总结表
    public static String getExcelFileName(String GroupQQ, LocalDate stratDate, LocalDate endDate) {
        return ExcelDir + GroupQQ + "/" + stratDate.format(df) + "到" + endDate.format(df) + ".xls";
    }

    //整个工会excle文件路径
    public static String getExcelDirName(String GroupQQ) {
        return (ExcelDir + GroupQQ + "/");
    }

    //获取公主连接时间---指每天5点才算明天
    public static String getDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        if (localDateTime.getHour() < 5) {      //若为陵城5点之前则为上一天
            localDateTime.plusDays(-1);
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
        return localDateTime.format(dateTimeFormatter);
    }


    //获取字符集名字
    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {      //判断是不是GB2312
                String s = encode;
                return s;      //是的话，返回“GB2312“，以下代码同理
            }
        } catch (Exception exception) {
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {      //判断是不是ISO-8859-1
                String s1 = encode;
                return s1;
            }
        } catch (Exception exception1) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {   //判断是不是UTF-8
                String s2 = encode;
                return s2;
            }
        } catch (Exception exception2) {
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {      //判断是不是GBK
                String s3 = encode;
                return s3;
            }
        } catch (Exception exception3) {
        }
        return "";        //如果都不是，说明输入的内容不属于常见的编码格式。
    }


}
