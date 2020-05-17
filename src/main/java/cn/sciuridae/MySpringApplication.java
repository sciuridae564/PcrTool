package cn.sciuridae;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

/**
 * 启动器
 */
@SpringBootApplication
public class MySpringApplication {

    /**
     * 此处为main方法所在处，执行main方法，启动Springboot
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(MySpringApplication.class, args);
    }

    //开启springboot后自动启动一个提示网页
    @Component
    public class RunHomePage implements CommandLineRunner {
        public void run(String... args) throws Exception {
            try {
                Runtime.getRuntime().exec("cmd /c start http://localhost:8080/index.html");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
