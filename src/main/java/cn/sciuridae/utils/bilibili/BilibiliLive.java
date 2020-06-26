package cn.sciuridae.utils.bilibili;

import cn.sciuridae.utils.ApiConnect;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BilibiliLive {
    private static final String TEMP = "./temp/bili/Live/";
    private String mid;//主播uid
    private int roomStatus;
    private int roundStatus;
    private int liveStatus;
    private String url;
    private String title;
    private File cover;
    private int online;
    private int roomid;

    public BilibiliLive(String mid) throws IOException {
        this.mid = mid;
        frash();
    }

    /**
     * 通过传入的up主uid返回一个json数组，其中包含直播间房间号，封面，人气，标题
     *
     * @param uid up的uid
     * @return json 示例如下
     * {"code":0,"message":"0","ttl":1,
     * "data":{
     * "roomStatus":1,   //0：无房间 1：有房间
     * "roundStatus":0,   //0：未轮播 1：轮播
     * "liveStatus":0,    //0：未开播 1：直播中
     * "url":"https://live.bilibili.com/92613",
     * "title":"万能的普瑞斯特",  //直播间标题
     * "cover":"http://i0.hdslb.com/bfs/live/bef09ae4739d7005332c10dbb91d55e6a8241275.jpg",  //直播间封面
     * "online":449411, //直播间人气
     * "roomid":92613,  //直播间ID
     * "broadcast_type":0,
     * "online_hidden":0}
     * }
     */
    public static String getLive(String uid) {
        String url = "http://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=" + uid;
        return get(url);
    }

    public static String get(String getUrl) {
        return ApiConnect.httpRequest(getUrl);
    }


    public void frash() throws IOException {
        String live = getLive(mid);
        JSONObject jsonObject = JSONObject.parseObject(live);
        JSONObject data = jsonObject.getJSONObject("data");
        roomStatus = data.getInteger("roomStatus");
        roundStatus = data.getInteger("roundStatus");
        liveStatus = data.getInteger("liveStatus");
        url = data.getString("url");
        title = data.getString("title");
        online = data.getInteger("online");
        roomid = data.getInteger("roomid");

        String fileName = getImageName(data.getString("cover"));
        if (cover == null || cover.getName().equals(fileName)) {
            cover = new File(TEMP + fileName);
            cover.getParentFile().mkdirs();
            cover.delete();
            cover.createNewFile();
            URL imageUrl = new URL(data.getString("cover"));
            FileUtils.copyURLToFile(imageUrl, cover);
        }

    }

    private String getImageName(String url) {
        String regex = "http://i0.hdslb.com/bfs/live/(.*)";
        String result = null;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            result = matcher.group(1);
        }
        return result;

    }

    public String getMid() {
        return mid;
    }

    public int getRoomStatus() {
        return roomStatus;
    }

    public int getRoundStatus() {
        return roundStatus;
    }

    public int getLiveStatus() {
        return liveStatus;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public File getCover() {
        return cover;
    }

    public int getOnline() {
        return online;
    }

    public int getRoomid() {
        return roomid;
    }
}
