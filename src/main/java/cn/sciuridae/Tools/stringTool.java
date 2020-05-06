package cn.sciuridae.Tools;

import javax.swing.plaf.basic.BasicBorders;

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
        String work=s.replace(" ","");
        int i=Integer.parseInt(work.substring(work.indexOf(']')+1));
        return i;
    }
}
