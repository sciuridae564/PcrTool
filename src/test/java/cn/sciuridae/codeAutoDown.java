package cn.sciuridae;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;

public class codeAutoDown {
    public static void main(String[] args) {
        AutoGenerator mpg = new AutoGenerator();
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("sciuridae");
        gc.setOpen(false);
        gc.setFileOverride(false);//不覆盖原来文件
        gc.setServiceName("%sService");

        gc.setSwagger2(true);
        mpg.setGlobalConfig(gc);

        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:sqlite:root.db");
        dsc.setDriverName("org.sqlite.JDBC");
        dsc.setUsername("");
        dsc.setPassword("");
        dsc.setDbType(DbType.SQLITE);
        mpg.setDataSource(dsc);

        PackageConfig pc = new PackageConfig();
        pc.setModuleName("dataBase");
        pc.setParent("cn.sciuridae");
        pc.setEntity("bean");
        pc.setMapper("dao");
        pc.setService("service");
        pc.setController("controller");
        mpg.setPackageInfo(pc);

        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        strategy.setLogicDeleteFieldName("deleted");

        TableFill createDate = new TableFill("createDate", FieldFill.INSERT);
        ArrayList<TableFill> tableFills = new ArrayList<>();
        tableFills.add(createDate);
        strategy.setTableFillList(tableFills);

        strategy.setRestControllerStyle(true);

        mpg.execute();
    }
}
