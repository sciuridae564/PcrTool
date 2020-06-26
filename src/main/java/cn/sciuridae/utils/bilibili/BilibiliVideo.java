package cn.sciuridae.utils.bilibili;

import cn.sciuridae.utils.ApiConnect;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BilibiliVideo {
    private static final String TEMP = "./temp/bili/Video/";
    private String bv;
    private String av;
    private File pic;
    private String title;


    public BilibiliVideo(String avbv, boolean isbv) throws IOException {
        if (isbv) {
            this.bv = avbv;
        } else {
            this.bv = BvAndAv.v2b(avbv);
        }
        frash();
    }

    /**
     * @param av av号
     * @return json 示例
     * {"code":0,   //0：成功 -400：请求错误 -404：无视频
     * "message":"0","ttl":1,"data":{
     * "bvid":"BV17D4y1Q7vs","aid":711101099,
     * "videos":1,  //视频分P总数
     * "tid":17,     //分区ID
     * "tname":"单机游戏",  //子分区名称
     * "copyright":1,      //1：自制 2：转载
     * "pic":"http://i1.hdslb.com/bfs/archive/06438780c4dcebe6b4be9741891120fe9accd7f6.jpg",   //	视频封面图片url
     * "title":"helltaker——待遇这么好换我我也下地狱啊", //视频标题
     * "pubdate":1592742040,  //视频上传时间
     * "ctime":1592742040,   //视频审核通过时间
     * "desc":"直播的录像\n直击XP系统",  //视频简介
     * "state":0,
     * "attribute":16512,
     * "duration":2995,   //	视频总计持续时长（所有分P） (秒)
     * "rights":{"bp":0,"elec":0,"download":1,"movie":0,"pay":0,"hd5":0,"no_reprint":1,"autoplay":1,"ugc_pay":0,"is_cooperation":0,"ugc_pay_preview":0,"no_background":0},
     * "owner":{"mid":13046,"name":"少年Pi","face":"http://i0.hdslb.com/bfs/face/d851f48a579778b06249bf3debaa62d353694e91.jpg"},
     * "stat":{
     * "aid":711101099,
     * "view":81630  ,
     * "danmaku":2060,
     * "reply":311,
     * "favorite":1804,
     * "coin":3310,
     * "share":202,
     * "now_rank":0,
     * "his_rank":0,
     * "like":5526,
     * "dislike":0,
     * "evaluation":""},
     * "dynamic":"",
     * "cid":204310150,
     * "dimension":{"width":1920,"height":1080,"rotate":0}, //1p分辨率
     * "no_cache":false,
     * "pages":[{"cid":204310150,"page":1,"from":"vupload","part":"2020-06-20 23-01-21","duration":2995,"vid":"","weblink":"","dimension":{"width":1920,"height":1080,"rotate":0}}],
     * "subtitle":{"allow_submit":true,"list":[]}
     * }}
     */
    public static String getVideobyAV(String av) {
        String url = "http://api.bilibili.com/x/web-interface/view?aid=" + av;
        return get(url);
    }

    public static String getVideobyBV(String bv) {
        String url = "http://api.bilibili.com/x/web-interface/view?bvid=" + bv;
        return get(url);
    }

    public static String get(String getUrl) {
        return ApiConnect.httpRequest(getUrl);
    }


    public void frash() throws IOException {
        String videobyBV = getVideobyBV(bv);
        JSONObject jsonObject = JSONObject.parseObject(videobyBV);
        JSONObject data = jsonObject.getJSONObject("data");

        av = jsonObject.getString("data");
        title = jsonObject.getString("title");


        String fileName = getImageName(data.getString("pic"));
        if (pic == null || pic.getName().equals(fileName)) {
            pic = new File(TEMP + fileName);
            pic.getParentFile().mkdirs();
            pic.delete();
            pic.createNewFile();
            URL imageUrl = new URL(data.getString("pic"));
            FileUtils.copyURLToFile(imageUrl, pic);
        }
    }

    private String getImageName(String url) {
        String regex = "http://i1.hdslb.com/bfs/archive/(.*)";
        String result = null;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            result = matcher.group(1);
        }
        return result;

    }

    public String getBv() {
        return bv;
    }

    public String getAv() {
        return av;
    }

    public File getPic() {
        return pic;
    }

    public String getTitle() {
        return title;
    }
}
