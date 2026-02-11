package cn.bugstack.infrastructure.adapter.port;

import cn.bugstack.domain.auth.adapter.port.ITemplateMessagePort;
import cn.bugstack.domain.auth.model.valobj.LoginInfoVO;
import cn.bugstack.infrastructure.gateway.IIpLocationService;
import cn.bugstack.infrastructure.gateway.IWeixinApiService;
import cn.bugstack.infrastructure.gateway.dto.IpLocationDTO;
import cn.bugstack.infrastructure.gateway.dto.WeixinTemplateMessageDTO;
import cn.bugstack.infrastructure.gateway.dto.WeixinTokenResponseDTO;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TemplateMessagePort implements ITemplateMessagePort {

    @Value("${weixin.config.app-id}")
    private String appid;
    @Value("${weixin.config.app-secret}")
    private String appSecret;
    @Value("${weixin.config.template_id_2}")
    private String template_id;
    @Resource
    private Cache<String, String> weixinAccessToken;
    @Resource
    private IWeixinApiService weixinApiService;
    @Resource
    private IIpLocationService ipLocationService;

    @Override
    public void sendLoginSuccessTemplateMessage(String openid, LoginInfoVO loginInfo) throws IOException {
        String accessToken = getAccessToken();
        
        String ip = loginInfo.getIp();
        String region = loginInfo.getRegion();
        
        if (region == null || region.isEmpty()) {
            region = queryIpRegion(ip);
            if ("未知地区".equals(region)) {
                throw new IOException("无法获取IP地区信息，不发送模板消息");
            }
        }
        
        String loginTime = loginInfo.getLoginTime();
        if (loginTime == null || loginTime.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            loginTime = sdf.format(new Date());
        }

        Map<String, Map<String, String>> data = new HashMap<>();
        WeixinTemplateMessageDTO.put(data, WeixinTemplateMessageDTO.TemplateKey.USER, openid);
        WeixinTemplateMessageDTO.put(data, WeixinTemplateMessageDTO.TemplateKey.IP, ip);
        WeixinTemplateMessageDTO.put(data, WeixinTemplateMessageDTO.TemplateKey.REGION, region);
        WeixinTemplateMessageDTO.put(data, WeixinTemplateMessageDTO.TemplateKey.LOGIN_TIME, loginTime);

        WeixinTemplateMessageDTO templateMessageDTO = new WeixinTemplateMessageDTO(openid, template_id);
        templateMessageDTO.setUrl("https://gaga.plus");
        templateMessageDTO.setData(data);

        try {
            Call<Void> call = weixinApiService.sendMessage(accessToken, templateMessageDTO);
            call.execute();
            log.info("发送登录成功模板消息成功 openid:{} ip:{} region:{}", openid, ip, region);
        } catch (Exception e) {
            log.error("发送登录成功模板消息失败 openid:{} ip:{} region:{}", openid, ip, region, e);
            throw e;
        }
    }

    private String queryIpRegionWithRetry(String ip) {
        int maxRetries = 2;
        int retryCount = 0;
        String lastError = null;
        
        while (retryCount < maxRetries) {
            try {
                String region = queryIpRegion(ip);
                if (!"未知地区".equals(region)) {
                    return region;
                }
                retryCount++;
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "未知地区";
            } catch (Exception e) {
                lastError = e.getMessage();
                retryCount++;
                log.warn("IP地区查询重试 {}/{} ip:{}", retryCount, maxRetries, ip);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return "未知地区";
                }
            }
        }
        
        log.error("IP地区查询失败，已重试{}次 ip:{} 最后错误:{}", maxRetries, ip, lastError);
        return "未知地区";
    }

    private String getAccessToken() throws IOException {
        String accessToken = weixinAccessToken.getIfPresent(appid);
        if (null == accessToken){
            Call<WeixinTokenResponseDTO> call = weixinApiService.getToken("client_credential", appid, appSecret);
            WeixinTokenResponseDTO weixinTokenResponseDTO = call.execute().body();
            assert weixinTokenResponseDTO != null;
            accessToken = weixinTokenResponseDTO.getAccess_token();
            weixinAccessToken.put(appid, accessToken);
        }
        return accessToken;
    }

    private String queryIpRegion(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "未知地区";
        }
        
        return queryIpFromIpApi(ip);
    }

    private String queryIpFromIpApi(String ip) {
        try {
            log.info("使用ip-api查询IP地区信息 ip:{}", ip);
            
            Call<IpLocationDTO> call = ipLocationService.getIpLocation("zh-CN");
            retrofit2.Response<IpLocationDTO> response = call.execute();
            
            if (!response.isSuccessful()) {
                log.error("ip-api查询失败，HTTP状态码:{} ip:{}", response.code(), ip);
                return "未知地区";
            }
            
            String rawResponse = response.raw() != null ? response.raw().body().string() : "null";
            log.info("ip-api原始响应 ip:{} 响应:{}", ip, rawResponse);
            
            IpLocationDTO ipLocationDTO = response.body();
            if (ipLocationDTO != null) {
                log.info("ip-api查询结果 ip:{} status:{} region:{}", ip, ipLocationDTO.getStatus(), ipLocationDTO.getFullRegion());
                
                if ("success".equals(ipLocationDTO.getStatus())) {
                    return ipLocationDTO.getFullRegion();
                } else if ("fail".equals(ipLocationDTO.getStatus())) {
                    log.warn("ip-api查询失败，API返回fail状态 ip:{}", ip);
                    return "未知地区";
                }
            }
        } catch (com.fasterxml.jackson.core.JsonParseException e) {
            log.error("ip-api JSON解析失败，API可能返回HTML而非JSON ip:{} 错误:{}", ip, e.getMessage());
            log.debug("JSON解析异常详情: {}", e.getOriginalMessage());
        } catch (Exception e) {
            log.error("ip-api查询异常 ip:{} 错误类型:{} 错误信息:{}", ip, e.getClass().getSimpleName(), e.getMessage());
        }
        return "未知地区";
    }

}
