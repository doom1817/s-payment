package cn.bugstack.infrastructure.adapter.port;

import cn.bugstack.domain.auth.adapter.port.ILoginPort;
import cn.bugstack.infrastructure.gateway.IWeixinApiService;
import cn.bugstack.infrastructure.gateway.dto.WeixinQrCodeRequestDTO;
import cn.bugstack.infrastructure.gateway.dto.WeixinQrCodeResponseDTO;
import cn.bugstack.infrastructure.gateway.dto.WeixinTokenResponseDTO;
import com.google.common.cache.Cache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class LoginPort implements ILoginPort {

    @Value("${weixin.config.app-id}")
    private String appid;
    @Value("${weixin.config.app-secret}")
    private String appSecret;
    @Resource
    private Cache<String, String> weixinAccessToken;
    @Resource
    private IWeixinApiService weixinApiService;

    @Override
    public String createQrCodeTicket() throws IOException {
        String accessToken = getAccessToken();
        WeixinQrCodeRequestDTO request = WeixinQrCodeRequestDTO.builder()
                .expire_seconds(2592000)
                .action_name(WeixinQrCodeRequestDTO.ActionNameTypeVO.QR_SCENE.getCode())
                .action_info(WeixinQrCodeRequestDTO.ActionInfo.builder()
                        .scene(WeixinQrCodeRequestDTO.ActionInfo.Scene.builder()
                                .scene_id(100601)
                                .build())
                        .build())
                .build();

        Call<WeixinQrCodeResponseDTO> qrCodeCall = weixinApiService.createQrCode(accessToken, request);
        WeixinQrCodeResponseDTO weixinQrCodeResponseDTO = qrCodeCall.execute().body();
        assert weixinQrCodeResponseDTO != null;
        return weixinQrCodeResponseDTO.getTicket();
    }

    private String getAccessToken() throws IOException {
        String accessToken = weixinAccessToken.getIfPresent(appid);
        if (null == accessToken) {
            Call<WeixinTokenResponseDTO> call = weixinApiService.getToken("client_credential", appid, appSecret);
            WeixinTokenResponseDTO weixinTokenResponseDTO = call.execute().body();
            if (weixinTokenResponseDTO == null || weixinTokenResponseDTO.getAccess_token() == null) {
                throw new RuntimeException("Failed to get access token from WeChat API");
            }
            accessToken = weixinTokenResponseDTO.getAccess_token();
            weixinAccessToken.put(appid, accessToken);
        }
        return accessToken;
    }

}
