package cn.sciuridae.Tools;

import javax.swing.plaf.basic.BasicBorders;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import static cn.sciuridae.constant.coolQAt;

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
        return work.substring(work.indexOf(']')+1);
    }

    public static int getHurt(String s){
        String work = s.replaceAll(" +", "");
        int i;
        if (s.charAt(s.length() - 1) != 'w' || s.charAt(s.length() - 1) != 'W') {
            i = Integer.parseInt(work.substring(3));
        } else {
            i = Integer.parseInt(work.substring(3), work.length() - 1);
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
}
