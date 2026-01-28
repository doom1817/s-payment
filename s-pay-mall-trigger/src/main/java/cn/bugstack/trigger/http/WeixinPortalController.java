package cn.bugstack.trigger.http;

import cn.bugstack.domain.auth.model.valobj.LoginInfoVO;
import cn.bugstack.domain.auth.service.ILoginService;
import cn.bugstack.domain.auth.service.ITemplateMessageService;
import cn.bugstack.types.sdk.weixin.MessageTextEntity;
import cn.bugstack.types.sdk.weixin.SignatureUtil;
import cn.bugstack.types.sdk.weixin.XmlUtil;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 微信服务对接，对接地址：<a href="http://xfg-studio.natapp1.cc/api/v1/weixin/portal/receive">/api/v1/weixin/portal/receive</a>
 * @create 2024-02-25 10:16
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/weixin/portal/")
public class WeixinPortalController {

    @Value("${weixin.config.originalid}")
    private String originalid;
    @Value("${weixin.config.token}")
    private String token;
    @Resource
    private Cache<String, String> openidToken;
    @Resource
    private ILoginService loginService;
    @Resource
    private ITemplateMessageService templateMessageService;

    /**
     * 验签，硬编码 token b8b6 - 按需修改
     */
    @GetMapping(value = "receive", produces = "text/plain;charset=utf-8")
    public String validate(@RequestParam(value = "signature", required = false) String signature,
                           @RequestParam(value = "timestamp", required = false) String timestamp,
                           @RequestParam(value = "nonce", required = false) String nonce,
                           @RequestParam(value = "echostr", required = false) String echostr) {
        try {
            log.info("微信公众号验签信息开始 [{}, {}, {}, {}]", signature, timestamp, nonce, echostr);
            if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
                throw new IllegalArgumentException("请求参数非法，请核实!");
            }
            boolean check = SignatureUtil.check(token, signature, timestamp, nonce);
            log.info("微信公众号验签信息完成 check：{}", check);
            if (!check) {
                return null;
            }
            return echostr;
        } catch (Exception e) {
            log.error("微信公众号验签信息失败 [{}, {}, {}, {}]", signature, timestamp, nonce, echostr, e);
            return null;
        }
    }

    /**
     * 回调，接收公众号消息【扫描登录，会接收到消息】
     */
    @PostMapping(value = "receive", produces = "application/xml; charset=UTF-8")
    public String post(@RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam("openid") String openid,
                       @RequestParam(name = "encrypt_type", required = false) String encType,
                       @RequestParam(name = "msg_signature", required = false) String msgSignature,
                       HttpServletRequest request) {
        try {
            log.info("接收微信公众号信息请求{}开始 {}", openid, requestBody);
            String clientIp = getClientIp(request);
            log.info("客户端IP地址: {}", clientIp);
            
            MessageTextEntity message = XmlUtil.xmlToBean(requestBody, MessageTextEntity.class);

            if ("event".equals(message.getMsgType()) && "SCAN".equals(message.getEvent())) {
                loginService.saveLoginState(message.getTicket(), openid);
                
                LoginInfoVO loginInfo = LoginInfoVO.builder()
                        .ip(clientIp)
                        .region(null)
                        .loginTime(null)
                        .build();
                
                try {
                    templateMessageService.sendLoginSuccessMessage(openid, loginInfo);
                } catch (Exception e) {
                    log.error("发送登录成功模板消息失败 openid:{} ip:{}", openid, clientIp, e);
                }
                
                return buildMessageTextEntity(openid, "登录成功");
            }
            
            if ("event".equals(message.getMsgType()) && "TEMPLATESENDJOBFINISH".equals(message.getEvent())) {
                log.info("收到微信模板消息发送完成回调 openid:{}", openid);
                return "";
            }

            log.info("接收微信公众号信息请求{}完成 {}", openid, requestBody);
            return buildMessageTextEntity(openid, "测试本案例，需要请扫码登录！");
        } catch (Exception e) {
            log.error("接收微信公众号信息请求{}失败 {}", openid, requestBody, e);
            return "";
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String buildMessageTextEntity(String openid, String content) {
        MessageTextEntity res = new MessageTextEntity();
        // 公众号分配的ID
        res.setFromUserName(originalid);
        res.setToUserName(openid);
        res.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000L));
        res.setMsgType("text");
        res.setContent(content);
        return XmlUtil.beanToXml(res);
    }

}
