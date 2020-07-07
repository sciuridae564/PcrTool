package cn.sciuridae.listener;

import cn.sciuridae.dataBase.bean.Scores;
import cn.sciuridae.dataBase.service.ScoresService;
import cn.sciuridae.utils.bilibili.BilibiliLive;
import cn.sciuridae.utils.bilibili.BilibiliVideo;
import cn.sciuridae.utils.bilibili.BvAndAv;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.sender.MsgSender;
import com.simplerobot.modules.utils.KQCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class bilibiliListener {

    @Autowired
    ScoresService ScoresServiceImpl;
    public static HashMap<String, BilibiliLive> liveHashMap = new HashMap<>();

    //视频封面 av114514
    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = "视频封面", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void videoFace(PrivateMsg msg, MsgSender sender) {
        String av = msg.getMsg().substring(4).trim();
        try {
            BilibiliVideo bilibiliVideo = null;
            if (av.substring(0, 2).equals("av") || av.substring(0, 2).equals("AV")) {
                bilibiliVideo = new BilibiliVideo(av.substring(2), false);
            } else if (av.substring(0, 2).equals("bv") || av.substring(0, 2).equals("BV")) {
                bilibiliVideo = new BilibiliVideo(av.substring(2), true);
            }
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "av号：" + bilibiliVideo.getAv() + "\nbv号：" + bilibiliVideo.getBv() + "\n视频标题:" + bilibiliVideo.getTitle());

            sender.SENDER.sendPrivateMsg(msg, KQCodeUtils.getInstance().toCq("image", "file="+ bilibiliVideo.getPic().getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //avbv转换
    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"av", "AV", "bv", "BV"}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void avtobv(PrivateMsg msg, MsgSender sender) {
        String substring = msg.getMsg();
        String av;
        String bv;
        if ((substring.charAt(0) == 'a' || substring.charAt(0) == 'A')) {
            av = substring;
            bv = "\nBV" + BvAndAv.v2b(substring.substring(2));
        } else {
            av = "AV" + BvAndAv.b2v(substring.substring(2));
            bv = "\n" + substring;
        }

        sender.SENDER.sendPrivateMsg(msg, av + bv);
    }

    //查询直播状态
    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"直播"}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void searchlive(PrivateMsg msg, MsgSender sender) {
        String mid = msg.getMsg().substring(2).trim();
        BilibiliLive bilibiliLive = liveHashMap.get(mid);
        if (bilibiliLive == null) {
            try {
                bilibiliLive = new BilibiliLive(mid);
            } catch (IOException e) {
                sender.SENDER.sendPrivateMsg(msg, "网络链接错误，请稍后再试");
                return;
            }
        }

        if (bilibiliLive.getRoomStatus() == 0) {
            sender.SENDER.sendPrivateMsg(msg, "该用户还未开通直播间");
            return;
        }

        if (bilibiliLive.getLiveStatus() == 0) {
            if (bilibiliLive.getRoundStatus() == 1) {
                sender.SENDER.sendPrivateMsg(msg, "在轮播中");
            } else {
                sender.SENDER.sendPrivateMsg(msg, "还未开播");
            }
        } else {
            sender.SENDER.sendPrivateMsg(msg, "标题:" + bilibiliLive.getTitle() + "人气值:" + bilibiliLive.getOnline() + "链接:" + bilibiliLive.getUrl());
            sender.SENDER.sendPrivateMsg(msg, KQCodeUtils.getInstance().toCq("image", "file="+ bilibiliLive.getCover().getAbsolutePath()));
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"设置开播提示"}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void setlive(PrivateMsg msg, MsgSender sender) {
        Pattern pattern = Pattern.compile("[0-9.]");
        Matcher matcher = pattern.matcher(msg.getMsg());
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(matcher.group(0));
        }
        int i = ScoresServiceImpl.setLive(msg.getCodeNumber(), sb.toString());
        if (i == -1) {
            sender.SENDER.sendPrivateMsg(msg, "槽位已满请去掉一个");
        } else {
            sender.SENDER.sendPrivateMsg(msg, "已添加，记录在" + i + "号槽上");
        }
        //开始监听直播间

    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"查看开播提示"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void getlive(PrivateMsg msg, MsgSender sender) {
        Scores byId = ScoresServiceImpl.getById(msg.getCodeNumber());
        if (byId == null) {
            sender.SENDER.sendPrivateMsg(msg, "还没有关注的主播哦");
        }
        sender.SENDER.sendPrivateMsg(msg, "开启状态:" + byId.getLiveON() + "\n一号槽：uid" + byId.getLive1() + "\n二号槽：uid" + byId.getLive2() + "\n三号槽：uid" + byId.getLive3() + "\nuid为0的槽未则为为使用的槽");

    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"清除开播提示 "}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void clearlive(PrivateMsg msg, MsgSender sender) {
        ScoresServiceImpl.clearLive(msg.getCodeNumber(), "live" + msg.getMsg().substring(6).trim());

    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"开启开播提示 "}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void openlive(PrivateMsg msg, MsgSender sender) {
        int i = ScoresServiceImpl.updateLiveOn(msg.getCodeNumber(), true);
        if (i < 1) {
            sender.SENDER.sendPrivateMsg(msg, "还没有直播关注记录");
        } else {
            sender.SENDER.sendPrivateMsg(msg, "已开启开播提示功能");
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"关闭开播提示 "}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void closelive(PrivateMsg msg, MsgSender sender) {
        int i = ScoresServiceImpl.updateLiveOn(msg.getCodeNumber(), false);
        if (i < 1) {
            sender.SENDER.sendPrivateMsg(msg, "还没有直播关注记录");
        } else {
            sender.SENDER.sendPrivateMsg(msg, "已关闭开播提示功能");
        }
    }

    private void addLive(String mid) {
        new AddLive(mid).start();
    }

    class AddLive extends Thread {
        private String mid;

        public AddLive(String mid) {
            this.mid = mid;
        }

        @Override
        public void run() {
            if (liveHashMap.get(mid) == null) {
                //没有则加入一个
                boolean flag = true;
                do {
                    try {
                        BilibiliLive bilibiliLive = new BilibiliLive(mid);
                        liveHashMap.put(mid, bilibiliLive);
                        flag = false;
                    } catch (IOException e) {
                        //出现了问题则等一会再加上去
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                } while (flag);
            }
        }
    }
}
