package cn.sciuridae;

import org.junit.Test;

import java.time.LocalDateTime;

import static cn.sciuridae.utils.timeUtil.getTodayFive;


public class test {

    @Test
    public void tes() {
        LocalDateTime startTime = getTodayFive(LocalDateTime.now());

        System.out.println(startTime);
        System.out.println(LocalDateTime.now());
    }
}
