package cn.sciuridae.dataBase.service;

import cn.sciuridae.dataBase.bean.KnifeList;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sciuridae
 * @since 2020-06-02
 */
public interface KnifeListService extends IService<KnifeList> {

    List<KnifeList> getKnifeList(long group);

    List<KnifeList> getKnifeList(long group, LocalDateTime date);

    List<KnifeList> getKnifeList(long group, LocalDateTime startdate, LocalDateTime endate);

    List<KnifeList> getKnife(long Qq, LocalDateTime date);

    List<KnifeList> getKnife(long Qq);

    List<KnifeList> getKnife(long Qq, LocalDateTime startdate, LocalDateTime endate);

    int getKnifeNum(long Qq, LocalDateTime date, boolean isNoReward);

    int getKnifeNum(long Qq, LocalDateTime startdate, LocalDateTime endate, boolean isNoReward);

    KnifeList getKnifeLast(long Qq, LocalDateTime startdate, LocalDateTime endate);

    List<KnifeList> getKnifeSumQq(long Qq, LocalDateTime startdate, LocalDateTime endate);

    List<KnifeList> getKnifeSumByGroup(long groupQq, LocalDateTime startdate, LocalDateTime endate);

    KnifeList getTopKnife(long groupQq);
}
