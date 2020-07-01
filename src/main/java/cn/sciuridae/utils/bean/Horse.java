package cn.sciuridae.utils.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static cn.sciuridae.constant.emojis;

public class Horse {

    private List<Integer> position;//马的位置
    private List<Integer> type;//马的种类，在静态文件夹下

    public Horse(List<Integer> position, List<Integer> type) {
        this.position = position;
        this.type = type;
    }

    public Horse() {
        Random random = new Random();

        position = new ArrayList<>();
        position.add(0);
        position.add(0);
        position.add(0);
        position.add(0);
        position.add(0);
        type = new ArrayList<>();
        type.add(random.nextInt(emojis.length));
        type.add(random.nextInt(emojis.length));
        type.add(random.nextInt(emojis.length));
        type.add(random.nextInt(emojis.length));
        type.add(random.nextInt(emojis.length));
    }

    public List<Integer> getPosition() {
        return position;
    }

    public void setPosition(List<Integer> position) {
        this.position = position;
    }

    public List<Integer> getType() {
        return type;
    }

    public void setType(List<Integer> type) {
        this.type = type;
    }
}
