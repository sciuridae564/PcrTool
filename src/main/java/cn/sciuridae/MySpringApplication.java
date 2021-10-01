package cn.sciuridae;

import cn.sciuridae.utils.InitDatabase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动器
 */

@EnableSimbot
@SpringBootApplication
@EnableScheduling
public class MySpringApplication {

    public static void main(String[] args) {
        //初始化数据库文件
        InitDatabase InitDatabase = new InitDatabase();
        InitDatabase.InitDB();

        //开启服务
        SpringApplication.run(MySpringApplication.class, args);
    }


}
