package cn.sciuridae.utils.scriptReader;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/*
* 将读取的map转化为实际对象
* */
//约定： 每个代码块的第一个 xxx = { 名字为 primary_key
public class Work_respoence {

    /**
     * 脚本文件格式为
     * xxx1 = { yyy }
     * xxx2 = { zzz }
     * xxx3 = { qqq }
     * 将xxx1/xxx2/xx3作为这个map的key，对应的{ yyy}即为这个map的data
     *
     * */
    public Map<String, Object> work(Class clazz, String filename) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, IOException, NoSuchFieldException {
        Map<String,Object> map =new HashMap<>();
        ScriptReader scriptReader=new ScriptReader(filename);

        Map<String, Object> mainMap = scriptReader.getMainMap();
        Set<String> strings = mainMap.keySet();
        for (String key :strings){
            Object o = mainMap.get(key);
            if (o instanceof List){
                map.put(key,o); //数组已经提前转好了，所以不需要再次操作
            }else if (o instanceof Map){
                Map submap= (Map) o;
                //map需要将其中的对应关系转换为字段与值的字段关系
                Object template=clazz.getConstructor().newInstance();
                Field[] fields = clazz.getDeclaredFields();
                //置入data
                map.put(key,template);
                //找到各个字段的值
                for (Field field:fields){
                    field.setAccessible(true);
                    Type fc = field.getGenericType();
                    if(field.getType().getTypeName().equals("java.lang.String")){
                        //一般字符字段，从map中找到对应的值
                        field.set(template,getvalue(submap,field.getName()));
                    }else if (field.getType().getTypeName().startsWith("java.util.List")){
                        //数组字段
                        ParameterizedType pt = (ParameterizedType) fc;
                        Class genericClazz = (Class)pt.getActualTypeArguments()[0];
                        if (genericClazz.isAssignableFrom(String.class)){
                            //字符串数组
                            field.set(template,getList(submap,field.getName()));
                        }else {
                            //类数组
                            ArrayList<Object> list=new ArrayList<>();
                            Object data=  getObjectvalue((Map) submap.get(field.getName()+"$0$"),genericClazz);list.add(data);
                            data=  getObjectvalue((Map) submap.get(field.getName()+"$1$"),genericClazz);
                            for (int i = 0; data !=null; i++) {
                                if (i != 0) { list.add(data); }
                                data= getObjectvalue((Map) submap.get(field.getName()+"$"+i+"$"),genericClazz);
                            }
                            field.set(template,list);
                        }
                    }else{
                        //其他的自定义类字段，递归
                        field.set(template,getObjectvalue((Map) submap.get(field.getName()+"$0$"),field.getType()));
                    }
                }
            }
        }

        return map;
    }

    /*
       与上面相同，这里解析具有很多字段的{ yyy}
        注意在上面因为已经对list做了处理，所以这里处理的一定是object
    * */
    private Object getObjectvalue(Map mainMap,Class clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (mainMap==null){
            return null ;
        }

        Object template=clazz.getConstructor().newInstance();

        //map需要将其中的对应关系转换为字段与值的字段关系
        Field[] fields = clazz.getDeclaredFields();
        //找到各个字段的值
        for (Field field:fields){
            field.setAccessible(true);
            Type fc = field.getGenericType();
            if(field.getType().getTypeName().equals("java.lang.String")){
                //一般字符字段，从map中找到对应的值
                field.set(template,getvalue(mainMap,field.getName()));
            }else if (field.getType().getTypeName().startsWith("java.util.List")){
                //数组字段
                ParameterizedType pt = (ParameterizedType) fc;
                Class genericClazz = (Class)pt.getActualTypeArguments()[0];
                if (genericClazz.isAssignableFrom(String.class)){
                    //字符串数组
                    field.set(template,getList(mainMap,field.getName()));
                }else {
                    //类数组
                    ArrayList<Object> list=new ArrayList<>();
                    Object data=  getObjectvalue((Map) mainMap.get(field.getName()+"$0$"),genericClazz);list.add(data);
                    data=  getObjectvalue((Map) mainMap.get(field.getName()+"$1$"),genericClazz);
                    for (int i = 0; data !=null; i++) {
                        if (i != 0) { list.add(data); }
                        data= getObjectvalue((Map) mainMap.get(field.getName()+"$"+i+"$"),genericClazz);
                    }
                    field.set(template,list);
                }
            }else{
                //其他的自定义类字段，递归
                field.set(template,getObjectvalue((Map) mainMap.get(field.getName()+"$0$"),field.getType()));
            }
        }

        return template;
    }

    private String getvalue(Map map,String key){
        return (String) map.get(key+"$0$");
    }

    private List<String> getList(Map map,String key){
        List<String> list =new ArrayList<>();
        String temp= (String) map.get(key+"$0$");
        for (int i=0;temp!=null;i++,temp= (String) map.get(key+"$"+i+"$")){
            list.add(temp);
        }
        return list;
    }

}
