package cn.sciuridae;


import cn.sciuridae.utils.InitDatabase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动器
 */
@SpringBootApplication
public class MySpringApplication {

    /**
     * 此处为main方法所在处，执行main方法，启动Springboot
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        //初始化数据库文件
        InitDatabase InitDatabase = new InitDatabase();
        InitDatabase.InitDB();

        //开启服务
        SpringApplication.run(MySpringApplication.class, args);
    }


}
