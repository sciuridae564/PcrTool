package cn.sciuridae.utils.bean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LocaldateUtils {
    /**
     * 从大到小
     */
    public List<LocalDate> getDescDateList(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> result = new ArrayList<>();
        LocalDate tempDate = null;//用户存储中间变量
        Long num = endDate.toEpochDay() - startDate.toEpochDay();
        //int num = endDate.compareTo(startDate);//不能使用这个方法进行比较，可以试一下7月30号 到8月30的比较结果
        for (int i = 0; i <= num; i++) {
            tempDate = endDate;
            if (tempDate.toEpochDay() - startDate.toEpochDay() >= 0) {
                result.add(endDate);
                endDate = tempDate.plusDays(-1);
            }
        }
        System.out.println(result);
        return result;
    }
}
