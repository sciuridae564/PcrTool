package cn.sciuridae;

import org.junit.Test;


public class test {

    @Test
    public void tes() {
        String string = "[CQ:at,qq=215123515]";

        System.out.println(Long.parseLong(string.substring(10, string.length() - 1)));
    }
}
