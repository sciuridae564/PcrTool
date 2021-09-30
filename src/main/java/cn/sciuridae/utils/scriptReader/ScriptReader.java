package cn.sciuridae.utils.scriptReader;

import lombok.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
* 将文件读取成map的键值对形式
* */
@Data
public class ScriptReader {

    String temp ;
    private int cur = 0;
    private Map<String,Object> mainMap ;

    public ScriptReader(String fileName) throws IOException {
        File file=new File(fileName);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        StringBuffer stringBuffer=new StringBuffer();
        String strLine = null;
        while(null != (strLine = bufferedReader.readLine())){
            if(strLine.contains("#")){
                strLine=strLine.substring(0,strLine.indexOf("#"));
            }
            strLine=strLine.trim();
            if (!strLine.equals("")) {
                stringBuffer.append("\n" + strLine);
            }
        }

        temp=stringBuffer.toString();
        mainMap=new HashMap();
        jumpVoid();
        HashMap<String,Integer> sumMap =new HashMap<>();
        while (temp.length()>0){
            String title=getName();
            if(sumMap.get(title)!=null){
                sumMap.put(title,sumMap.get(title)+1);
                title=title+"$"+(sumMap.get(title))+"$";
            }else {
                sumMap.put(title,0);
                title=title+"$0$";
            }
            jumpVoid();
            jumpeq();
            if(temp.charAt(0)=='{'){
                //复杂值
                next();
                mainMap.put(title,getValues());
            }else {
                mainMap.put(title,getValue());
            }
            jumpVoid();
            if(temp.length()>0&&temp.charAt(0)=='}'){
                next(); break;
            }
        }

    }

    private Map getValues() {
        Map<String,Object> main=new HashMap();
        int i=0;
        //检查如果还没跳过左大括号就跳过
        jumpVoid();
        if (temp.charAt(0)=='{'){
            next();
        }
        jumpVoid();
        HashMap<String,Integer> sumMap =new HashMap<>();
        while (true){
            String title=getName();

            jumpVoid();
            if (temp.charAt(0)!='='){
                next();jumpVoid(); //是数组类型,循环读取直到结束
                List<String> list = new ArrayList();
                list.add(title);
                while (temp.charAt(0)!='}'){
                    jumpVoid();
                    list.add(getName());
                    jumpVoid();
                }
                break;
            } else {
                next();jumpVoid(); //不是数组类型
            }
            if(sumMap.get(title)!=null){
                sumMap.put(title,sumMap.get(title)+1);
                title=title+"$"+(sumMap.get(title))+"$";
            }else {
                sumMap.put(title,0);
                title=title+"$0$";
            }
            if(temp.charAt(0)=='{'){
                //复杂值
                next();
                main.put(title,getValues());
            }else {
                main.put(title,getValue());
            }
            jumpVoid();
            if(temp.charAt(0)=='}'){
                next(); break;
            }
        }
        return main;
    }

    private String getName() {
        StringBuffer stringBuffer=new StringBuffer();
        //首先，跳跃空白格和双引号这些不是值的部分
        jumpVoid();
        //然后读取值
        while (true){
            if (temp.charAt(0)==' '||temp.charAt(0)=='\n'||temp.charAt(0)=='\t'||temp.charAt(0)=='\"'||temp.charAt(0)=='='){
                next(); break;
            }else {
                stringBuffer.append(temp.charAt(0));next();
            }
        }
        return stringBuffer.toString();
    }

    private String getValue() {
        StringBuffer stringBuffer=new StringBuffer();
        //首先，跳跃空白格和双引号这些不是值的部分
        jumpVoid();
        //然后读取值
        while (true){
            if (temp.charAt(0)==' '||temp.charAt(0)=='\n'||temp.charAt(0)=='\t'||temp.charAt(0)=='\"'){
                next(); break;
            }else {
                stringBuffer.append(temp.charAt(0));next();
            }
        }
        return stringBuffer.toString();
    }

    //跳过空白
    private void jumpVoid(){
        while (true){
            if (temp.length()!=0&&(temp.charAt(0)==' '||temp.charAt(0)=='\n'||temp.charAt(0)=='\t'||temp.charAt(0)=='\"')){
                next();
            }else {
                break;
            }
        }
    }

    //跳过等于号
    private void jumpeq(){
        jumpVoid();
        while (true){
            if (temp.charAt(0)=='='){
                next();
            }else {
                break;
            }
        }
        jumpVoid();
    }
    private void next(){
        temp=temp.substring(1);
    }

}
