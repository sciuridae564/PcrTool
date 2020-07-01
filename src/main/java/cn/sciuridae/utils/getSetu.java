package cn.sciuridae.utils;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class getSetu {
    private static final String TEMP = "./temp/";
    private static final String COS_URL = "https://www.zhaimoe.com/cosplay.html";

    public static File[] getFileCos(int id) {
        File file = new File(TEMP + "cosplay/" + id);
        if (!file.exists()) {
            return null;
        } else {
            File[] files = file.listFiles();
            return files;
        }
    }

    public static List<File> getCosImageById(int id) throws IOException {
        Document parse = Jsoup.parse(new URL("https://www.zhaimoe.com/cosplay/" + id + ".html"), 10000);
        ArrayList<File> arrayList = new ArrayList<>();
        Elements article_content = parse.getElementsByClass("pimg lazyload");
        int j = 1;
        for (Element element : article_content) {
            String imgaeSrc = element.attr("src");
            URL imgaeSrcUrl = new URL(imgaeSrc);
            File file = new File(TEMP, "cosplay/" + id + "/" + j + ".jpg");
            FileUtils.copyURLToFile(imgaeSrcUrl, file);
            arrayList.add(file);
            j++;
        }
        return arrayList;
    }


    public static int getCosImageMax() {
        String sum = "0";
        try {
            Document parse = Jsoup.parse(new URL(COS_URL), 10000);
            Elements elementsByClass = parse.getElementsByClass("item-image");
            int i = 2;
            for (Element e : elementsByClass) {
                String[] matcher = getMatcher(e.html());
                sum = matcher[0].substring(0, matcher[0].length() - 5);
                break;
            }
        } catch (IOException e) {

        }
        return Integer.parseInt(sum);
    }

    public static String[] getMatcher(String source) {
        String regex = "<a href=\"/cosplay/(.*)\" class=\"thumbnail\"> <img src=\"(.*)\" class=\"img-responsive2\" alt=\"\"> </a>";
        String[] result = new String[2];
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            result[0] = matcher.group(1);
            result[1] = matcher.group(2);
        }
        return result;
    }
}
