package cn.bugstack.xfg.dev.tech.test;

import cn.bugstack.types.sdk.weixin.MessageTextEntity;
import cn.bugstack.types.sdk.weixin.XmlUtil;
import com.alibaba.fastjson.JSON;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.security.AnyTypePermission;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringReader;

@Slf4j
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class ApiTest {

    @Test
    public void test() throws JAXBException {
        String xml = "<xml><ToUserName><![CDATA[gh_e067c267e056]]></ToUserName>\n" +
                "<FromUserName><![CDATA[or0Ab6ivwmypESVp_bYuk92T6SvU]]></FromUserName>\n" +
                "<CreateTime>1721779323</CreateTime>\n" +
                "<MsgType><![CDATA[event]]></MsgType>\n" +
                "<Event><![CDATA[TEMPLATESENDJOBFINISH]]></Event>\n" +
                "<MsgID>3559908001106460675</MsgID>\n" +
                "<Status><![CDATA[success]]></Status>\n" +
                "</xml>";

        XStream xStream = new XStream();
        xStream.processAnnotations(MessageTextEntity.class);

        xStream.addPermission(AnyTypePermission.ANY);

        MessageTextEntity message = (MessageTextEntity) xStream.fromXML(xml);

        System.out.println("ToUserName: " + message.getToUserName());
        System.out.println("FromUserName: " + message.getFromUserName());
        System.out.println("CreateTime: " + message.getCreateTime());
        System.out.println("MsgType: " + message.getMsgType());
        System.out.println("Event: " + message.getEvent());
        System.out.println("MsgId: " + message.getMsgId());
        System.out.println("Status: " + message.getStatus());

//        // 消息转换
        MessageTextEntity message1 = XmlUtil.xmlToBean(xml, MessageTextEntity.class);
        log.info("测试结果:{}", JSON.toJSONString(message1));
    }

    @XStreamAlias("xml")
    public static class XmlMessage {
        @XStreamAlias("ToUserName")
        private String toUserName;

        @XStreamAlias("FromUserName")
        private String fromUserName;

        @XStreamAlias("CreateTime")
        private String createTime;

        @XStreamAlias("MsgType")
        private String msgType;

        @XStreamAlias("Event")
        private String event;

        @XStreamAlias("EventKey")
        private String eventKey;

        @XStreamAlias("MsgID")
        private String msgId;

        @XStreamAlias("Status")
        private String status;

        @XStreamAlias("Ticket")
        private String ticket;

        @XStreamAlias("Content")
        private String content;

        // Getters and Setters
        public String getToUserName() {
            return toUserName;
        }

        public void setToUserName(String toUserName) {
            this.toUserName = toUserName;
        }

        public String getFromUserName() {
            return fromUserName;
        }

        public void setFromUserName(String fromUserName) {
            this.fromUserName = fromUserName;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getMsgType() {
            return msgType;
        }

        public void setMsgType(String msgType) {
            this.msgType = msgType;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getMsgId() {
            return msgId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getEventKey() {
            return eventKey;
        }

        public void setEventKey(String eventKey) {
            this.eventKey = eventKey;
        }

        public String getTicket() {
            return ticket;
        }

        public void setTicket(String ticket) {
            this.ticket = ticket;
        }
    }

}
