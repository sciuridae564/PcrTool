package cn.sciuridae.Tools;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static cn.sciuridae.constant.*;

public class stringTool {

    public static int searchAtNumber(String str){
        int n=0;
        int length=coolQAt.length();
        int index=str.indexOf(coolQAt);
        while(index!=-1){
            n++;
            index=str.indexOf(coolQAt,index+length);
        }
        return n;
    }
    //语句中存在好几个个at，其中一个必为机器人的特殊方法,返回不是robot 的第一个qq
    public static String getAtNumber(String str ,String robotQQ){
        String[] src=str.split("\\[CQ:at,qq=");
        String s=null;
        if(src[0].substring(0, robotQQ.length()).equals(robotQQ)){

        }else {

        }
        return s;
    }

    public static long strToLong(String str){
        long p=0;
        for (int i=0;i<str.length();i++){
            if(str.charAt(i)>47&&str.charAt(i)<58){
                p*=10;
                p+=str.charAt(i)-48;
                continue;
            }
            break;
        }
        return p;
    }

    //存在这种命令  #命令名 @机器人 参数1
    //返回参数1并忽略所有空格
    //只是找到第一个】之后截断
    public static String getVar(String msg){
        String work=msg.replace(" ","");
        return work.substring(2);
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
                    System.out.println();
                    i = Integer.parseInt(work.substring(3, work.length() - 1)) * 10000;
                }
                break;
            case 2:
                if (s.charAt(s.length() - 1) != 'w' && s.charAt(s.length() - 1) != 'W') {
                    i = Integer.parseInt(work.substring(work.indexOf("]") + 1));
                } else {
                    System.out.println();
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

    public static List<Date> getBetweenDates(Date start, Date end) {
        List<Date> result = new ArrayList<Date>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);
        tempStart.add(Calendar.DAY_OF_YEAR, 1);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);
        while (tempStart.before(tempEnd)) {
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }

    //单日表
    public static String getExcelFileName(String GroupQQ, Date date) {
        return (ExcelDir + GroupQQ + "/" + dfForFile.format(date) + ".xls");
    }

    //总结表
    public static String getExcelFileName(String GroupQQ, Date stratDate, Date endDate) {
        return ExcelDir + GroupQQ + "/" + dfForFile.format(stratDate) + "到" + dfForFile.format(endDate) + ".xls";
    }

    //获取公主连接时间---指每天5点才算明天
    public static String getDate(){
        LocalDateTime  localDateTime=LocalDateTime.now();
        if(localDateTime.getHour()<5){      //若为陵城5点之前则为上一天
            localDateTime.plusDays(-1);
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
        return localDateTime.format(dateTimeFormatter);
    }

}
