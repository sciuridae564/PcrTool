package cn.sciuridae.utils.bean;

import java.util.ArrayList;
import java.util.List;

public class HoreEvent {
    public String help = "在下面俩添加事件，?是几号马的占位符，？需要是英文状态";
    public List<String> bedHorseEvent = new ArrayList<>();
    public List<String> goodHorseEvent = new ArrayList<>();


    public HoreEvent() {
        bedHorseEvent.add("?号马滑倒了！");
        bedHorseEvent.add("?号马自由了!");
        bedHorseEvent.add("?号马踩到了xcw");
        bedHorseEvent.add("?号马突然想上天摘星星");
        bedHorseEvent.add("?号马掉入了时辰的陷阱");
        goodHorseEvent.add("?号马发现了前方的母马，加速加速！");
        goodHorseEvent.add("?号马使用了私藏的超级棒棒糖，加速加速！");
        goodHorseEvent.add("?号马已经没什么所谓了！");
        goodHorseEvent.add("?号马发现，赛道岂是如此不便之物！");
    }

    public List<String> getBedHorseEvent() {
        return bedHorseEvent;
    }

    public void setBedHorseEvent(List<String> bedHorseEvent) {
        this.bedHorseEvent = bedHorseEvent;
    }

    public List<String> getGoodHorseEvent() {
        return goodHorseEvent;
    }

    public void setGoodHorseEvent(List<String> goodHorseEvent) {
        this.goodHorseEvent = goodHorseEvent;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    @Override
    public String toString() {
        return "HoreEvent{" +
                "help='" + help + '\'' +
                ", bedHorseEvent=" + bedHorseEvent +
                ", goodHorseEvent=" + goodHorseEvent +
                '}';
    }
}
