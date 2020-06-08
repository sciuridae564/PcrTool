package cn.sciuridae.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;


public class ImageUtil {
    private static final Integer HEIGHT_AND_WEIGHT = 128; //一个图片的长宽高为64
    private static final Integer LINE_SIZE_MAX = 4;//一行最多4个图片
    private static final String OUT = "./temp/";//输出图片的临时文件夹

    public static String composeImg(ArrayList<String> charas) throws IOException {
        if (charas.size() > 0) {

            int rows = charas.size() >= LINE_SIZE_MAX ? LINE_SIZE_MAX : charas.size();
            int cols = charas.size() / LINE_SIZE_MAX + (charas.size() % LINE_SIZE_MAX == 0 ? 0 : 1);
            BufferedImage thumbImage = new BufferedImage(rows * HEIGHT_AND_WEIGHT, cols * HEIGHT_AND_WEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = thumbImage.createGraphics();
            //设置白底
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, rows * HEIGHT_AND_WEIGHT, cols * HEIGHT_AND_WEIGHT);


            //画人物图片在上面
            for (int i = 0; i < charas.size(); i++) {
                File file = new File("./image/" + charas.get(i) + ".png");
                Image src;
                if (file.exists()) {  //如果外部图片包 有资源则使用外部资源
                    src = ImageIO.read(file);
                } else {              //否则使用内部资源
                    InputStream inputStream = ImageUtil.class.getResourceAsStream("/image/" + charas.get(i) + ".png");
                    if (inputStream != null) {
                        src = ImageIO.read(inputStream);
                    } else {         //内部资源也没有就显示问号占位图片
                        inputStream = ImageUtil.class.getResourceAsStream("/image/no.png");
                        src = ImageIO.read(inputStream);
                    }
                }
                //画图片
                g.drawImage(src.getScaledInstance(HEIGHT_AND_WEIGHT, HEIGHT_AND_WEIGHT, Image.SCALE_SMOOTH), (i % LINE_SIZE_MAX) * HEIGHT_AND_WEIGHT, (i / LINE_SIZE_MAX) * HEIGHT_AND_WEIGHT, null);
            }

            //生成uuid作为名字，防止图片相互覆盖
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");

            //输出图片
            String path = OUT + uuid + ".jpg";
            File exOut = new File(OUT);
            if (!exOut.exists()) {
                exOut.mkdir();
            }
            String formatName = path.substring(path.lastIndexOf(".") + 1);
            ImageIO.write(thumbImage, /*"GIF"*/ formatName /* format desired */, new File(path) /* target */);
            return path;
        } else {
            return null;
        }
    }
}
