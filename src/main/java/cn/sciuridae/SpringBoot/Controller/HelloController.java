package cn.sciuridae.SpringBoot.Controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;


@Controller
public class HelloController {
    @ResponseBody
    @RequestMapping("/hello")
    public String Hello() {
        return "hello";
    }

    @RequestMapping("/test")
    public String success(Map<String, Object> map) {
        map.put("hello", "nihao");
        map.put("numberlist", Arrays.asList("one", "two", "three"));
        map.put("tips", "hahaha");
        return "test";
    }

    @RequestMapping("/dosome")
    public String dosome(String username, String password) {

        return "test";
    }


}