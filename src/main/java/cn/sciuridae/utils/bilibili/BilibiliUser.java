package cn.sciuridae.utils.bilibili;

import cn.sciuridae.utils.ApiConnect;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BilibiliUser {
    private static final String TEMP = "./temp/bili/User/";
    private String mid;
    private String uname;
    private int room_id;
    private File face;
    private String sign;

    public BilibiliUser(String mid) throws IOException {
        this.mid = mid;
        frash();
    }

    public static String get(String getUrl) {
        return ApiConnect.httpRequest(getUrl);
    }

    /**
     * @return {"code":0,
     * "data":{
     * "suggest_keyword":"",
     * "result":[
     * {"room_id":0,
     * "res":[
     * {"play":"317",
     * "bvid":"BV1hJ411q7MX",
     * "arcurl":"http://www.bilibili.com/video/av77916112",
     * "dm":0,
     * "pic":"//i0.hdslb.com/bfs/archive/93ce42c0020c5662ddb479232bc1488c81b681bf.jpg",
     * "title":"今天兑换104姆巴佩噜",
     * "duration":"0:41",
     * "is_union_video":0,
     * "fav":0,
     * "aid":77916112,
     * "is_pay":0,
     * "pubdate":1575357978,
     * "coin":0,
     * "desc":"-"},
     * {"play":"1226","bvid":"BV1uJ411Q7em","arcurl":"http://www.bilibili.com/video/av77399607","dm":0,"pic":"//i2.hdslb.com/bfs/archive/45d35928eff4f5219c72b6a738bfc4908803c144.jpg","title":"感恩节104来啦","duration":"0:34","is_union_video":0,"fav":1,"aid":77399607,"is_pay":0,"pubdate":1574999762,"coin":2,"desc":"-"},{"play":"290","bvid":"BV1HE411k7Ky","arcurl":"http://www.bilibili.com/video/av71706328","dm":0,"pic":"//i0.hdslb.com/bfs/archive/fb641bd8d9212e0e52beb656af431ddf33241d9e.jpg","title":"拿下传奇啦","duration":"0:50","is_union_video":0,"fav":2,"aid":71706328,"is_pay":0,"pubdate":1571455194,"coin":2,"desc":"-"}],
     * "upic":"//i1.hdslb.com/bfs/face/91e3d32f02821e992e5030ee237c8df9f73f6c81.jpg",  //用户头像
     * "uname":"luvi13046",                                                             //用户昵称
     * "gender":3,"hit_columns":["uname"],
     * "level":0,
     * "mid":356015997,                                                             //uid
     * "videos":3,                                                                     //应该是总视频数
     * "is_upuser":1,"type":"bili_user","official_verify":{"type":127,"desc":""},"verify_info":"","fans":2,"usign":"","is_live":0}],
     * "numPages":1, "show_column":0, "cost_time":{"params_check":"0.000257","deserialize_response":"0.000076","as_request_format":"0.000259","as_request":"0.004426","total":"0.010448","as_response_format":"0.000387","get upuser live status":"0.000002","main_handler":"0.005284","illegal_handler":"0.004839"},"seid":"1850065179708182403","pagesize":20,"numResults":1,"egg_hit":0,"page":1,"rqt_type":"search","exp_list":{"5599":true}},"message":"0","ttl":1}
     */
    public static String searchUser(String search) {
        String url = "https://api.bilibili.com/x/web-interface/search/type?search_type=bili_user&keyword=" + search;
        return get(url);
    }


    public void frash() throws IOException {
        String user = getUser(mid);
        JSONObject jsonObject = JSONObject.parseObject(user);
        JSONObject data = jsonObject.getJSONObject("data");

        uname = data.getString("uname");
        room_id = data.getInteger("uname");
        sign = data.getString("sign");

        String fileName = getImageName(data.getString("face"));
        if (face == null || face.getName().equals(fileName)) {
            face = new File(TEMP + fileName);
            face.getParentFile().mkdirs();
            face.delete();
            face.createNewFile();
            URL imageUrl = new URL(data.getString("face"));
            FileUtils.copyURLToFile(imageUrl, face);
        }
    }

    /**
     * @param mid
     * @return {"code":0,"message":"0","ttl":1,
     * "data":
     * {"mid":578227337,
     * "name":"华为",
     * "sex":"保密",
     * "face":"http://i0.hdslb.com/bfs/face/7dbc9f2993c7d829f816ebbcb87b9c51be304ce1.jpg",
     * "sign":"2020，一起向前！",
     * "rank":10000,"level":4,
     * "jointime":0,"moral":0,"silence":0,"birthday":"","coins":0,"fans_badge":true,
     * "official":
     * {"role":3,"title":"华为官方账号","desc":"","type":1},
     * "vip":{"type":2,"status":1,"theme_type":0,
     * "label":{"path":"","text":"年度大会员","label_theme":"annual_vip"},
     * "avatar_subscript":1,"nickname_color":"#FB7299"},
     * "pendant":{"pid":0,"name":"","image":"","expire":0,"image_enhance":""},
     * "nameplate":{"nid":0,"name":"","image":"","image_small":"","level":"","condition":""},
     * "is_followed":false,"top_photo":"http://i1.hdslb.com/bfs/space/cb1c3ef50e22b6096fde67febe863494caefebad.png","theme":{},"sys_notice":{}}}
     */
    public String getUser(String mid) {
        String url = "http://api.bilibili.com/x/space/acc/info?mid=" + mid;
        return get(url);
    }

    private String getImageName(String url) {
        String regex = "http://i0.hdslb.com/bfs/face/(.*)";
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

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public File getFace() {
        return face;
    }

    public void setFace(File face) {
        this.face = face;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
