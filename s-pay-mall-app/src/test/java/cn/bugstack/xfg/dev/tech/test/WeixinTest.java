package cn.bugstack.xfg.dev.tech.test;

import cn.bugstack.infrastructure.gateway.IWeixinApiService;
import cn.bugstack.infrastructure.gateway.dto.WeixinTemplateMessageDTO;
import cn.bugstack.infrastructure.gateway.dto.WeixinTokenResponseDTO;
import com.alibaba.fastjson2.JSON;
import io.reactivex.Single;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 微信公众号服务测试
 * @create 2024-07-23 07:51
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class WeixinTest {

    @Value("${weixin.config.app-id}")
    private String appid;
    @Value("${weixin.config.app-secret}")
    private String appSecret;

    @Resource
    private IWeixinApiService weixinApiService;

    private String accessToken = "82_yOsOOitB88NgmAjnVeKyDQ5aXG5DTpenHF4MuKVpI4N4P2zcg7RYpQFItyUADbEfVFUQn_NNmhXdY1VYNa70jKKUamAXtqGj2cO3xzVjzsxJIG19dYp2zg_oFUwRWWaAJAWBB";

    @Before
    public void before() throws IOException {
        Call<WeixinTokenResponseDTO> call = weixinApiService.getToken("client_credential", appid, appSecret);
        WeixinTokenResponseDTO weixinTokenResponseDTO = call.execute().body();
        assert weixinTokenResponseDTO != null;
        accessToken = weixinTokenResponseDTO.getAccess_token();
        log.info("weixin accessToken:{}", accessToken);
    }

    @Test
    public void test_template_message() throws IOException {
        Map<String, Map<String, String>> data = new HashMap<>();
        WeixinTemplateMessageDTO.put(data, WeixinTemplateMessageDTO.TemplateKey.USER, "xiaofuge");

        WeixinTemplateMessageDTO templateMessageDTO = new WeixinTemplateMessageDTO("or0Ab6ivwmypESVp_bYuk92T6SvU", "RbEZ2jo47dQmF4A7_Ku7RsDy1x_5by6bk1Ox6rPCl4Y");
        templateMessageDTO.setUrl("https://gaga.plus");
        templateMessageDTO.setData(data);

        Call<Void> call = weixinApiService.sendMessage(accessToken, templateMessageDTO);
        call.execute();

        log.info("请求参数:{}",JSON.toJSONString(templateMessageDTO));
    }

    private static void sendPostRequest(String urlString, String jsonBody) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
                String response = scanner.useDelimiter("\\A").next();
                System.out.println(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
