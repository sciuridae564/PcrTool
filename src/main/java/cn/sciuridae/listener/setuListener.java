package cn.sciuridae.listener;

import cn.sciuridae.dataBase.bean.Scores;
import cn.sciuridae.dataBase.service.ScoresService;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.cqcode.CQCode;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static cn.sciuridae.constant.pricnessConfig;
import static cn.sciuridae.utils.getSetu.*;

@Service
public class setuListener {
    @Autowired
    ScoresService ScoresServiceImpl;


    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"我要看漂亮小姐姐"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void config(PrivateMsg msg, MsgSender sender) {
        Scores coin = ScoresServiceImpl.getById(msg.getCodeNumber());

        if (coin == null) {
            Scores scores = new Scores();
            scores.setiSign(false);
            scores.setQQ(msg.getCodeNumber());
            scores.setScore(0);
            ScoresServiceImpl.save(scores);
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "钱和漂亮小姐姐只能拥有一个");
        } else if (coin.getScore() >= pricnessConfig.getSetuCoin()) {
            coin.setScore(coin.getScore() - pricnessConfig.getSetuCoin());
            ScoresServiceImpl.updateById(coin);
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "正在搜索图片……………………");
        } else {
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "钱和漂亮小姐姐只能拥有一个");
            return;
        }

        sendSetu sendSetu = new sendSetu(msg.getQQ(), sender);
        sendSetu.start();
    }

    class sendSetu extends Thread {
        private String sendQQ;
        private MsgSender sender;

        public sendSetu(String sendQQ, MsgSender sender) {
            this.sendQQ = sendQQ;
            this.sender = sender;
        }

        @Override
        public void run() {
            Random random = new Random();
            int cosImageMax = 0;
            int cosImageTragtNum = random.nextInt(10) + 1;
            //请求cos图最高编号
            for (int i = 1; i < 5; i++) {
                cosImageMax = getCosImageMax();
                if (cosImageMax > 0)
                    break;
            }
            //如果没请求到则反馈
            if (cosImageMax < 1) {
                sender.SENDER.sendPrivateMsg(sendQQ, "网络条件不好，请稍后再试");
                return;
            }
            ArrayList<File> files = new ArrayList<>();
            while (files.size() < 1) {
                //随机出要爬的cos图编号
                int[] cosTraget = new int[cosImageTragtNum];
                for (int i = 0; i < cosImageTragtNum; i++) {
                    cosTraget[i] = random.nextInt(cosImageMax - 58) + 58;
                }
                //先试图从缓存文件夹中找有没有
                int[] cosTragetVoid = new int[cosImageTragtNum];
                int voidNum = 0;

                for (int i = 0; i < cosImageTragtNum; i++) {
                    File[] files1 = getFileCos(cosTraget[i]);
                    if (files1 != null) {
                        files.addAll(Arrays.asList());
                    } else {
                        cosTragetVoid[voidNum] = cosTraget[i];
                        voidNum++;
                    }
                }
                //没有再通过网络请求
                for (int i = 0; i < voidNum; i++) {
                    try {
                        files.addAll(getCosImageById(cosTragetVoid[i]));
                    } catch (IOException e) {
                    }
                }
            }

            //发送图片
            for (File f : files) {
                CQCode cqCode_image = CQCodeUtil.build().getCQCode_Image("file://" + f.getAbsolutePath());
                sender.SENDER.sendPrivateMsg(sendQQ, cqCode_image.toString());
            }
        }
    }
}
