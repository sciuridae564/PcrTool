package cn.sciuridae.listener;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.sender.MsgSender;
import org.springframework.stereotype.Service;

@Service
public class bilibiliListener {

    //视频封面 av114514
    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = "视频封面", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void videoFace(PrivateMsg msg, MsgSender sender) {

    }

    //
    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"a", "A", "b", "B"}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void avtobv(PrivateMsg msg, MsgSender sender) {

    }


}
