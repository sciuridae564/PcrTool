package cn.sciuridae.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class timeUtil {

    /**
     * 获取传入时间的出刀时间范围
     *
     * @return
     */
    public static LocalDateTime[] getTodayNoStart(LocalDateTime date, boolean start) {
        LocalDateTime[] dates = new LocalDateTime[2];
        LocalDateTime basedate = LocalDateTime.of(date.toLocalDate(), LocalTime.MIN);//当日零点
        if (start) {
            dates[0] = basedate;
        } else {
            dates[0] = basedate.withHour(5);
        }
        dates[1] = basedate.withHour(5).plusDays(1);//明日5点

        return dates;
    }

    /**
     * 从大到小
     */
    public static List<LocalDate> getDescDateList(LocalDate startDate, LocalDate endDate) {
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

    public static LocalDateTime getYesterday(LocalDateTime date) {
        if (date.getHour() < 5) {
            //若还没到五点，则这一天的会战还未结束
            return date.withHour(5).plusDays(-1).withMinute(0).withSecond(0).withNano(0);
        } else {
            //过了五点，上一天的会战结束在今日5点
            return date.withHour(5).withMinute(0).withSecond(0).withNano(0);
        }
    }

    //今日五点
    public static LocalDateTime getTodayFive(LocalDateTime date) {
        return date.withHour(5).withMinute(0).withSecond(0);
    }


    public static LocalDateTime[] localDateTolocalDateTimes(LocalDate date) {
        LocalDateTime[] localDateTimes = new LocalDateTime[2];
        localDateTimes[0] = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 5, 0);
        date = date.plusDays(1);
        localDateTimes[1] = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 5, 0);

        return localDateTimes;
    }

}
