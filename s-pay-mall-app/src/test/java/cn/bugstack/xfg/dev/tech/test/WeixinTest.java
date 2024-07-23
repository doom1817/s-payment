package cn.bugstack.xfg.dev.tech.test;

import cn.bugstack.infrastructure.gateway.IWeixinApiService;
import cn.bugstack.infrastructure.gateway.dto.WeixinTemplateMessageDTO;
import cn.bugstack.infrastructure.gateway.dto.WeixinTokenResponseDTO;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import retrofit2.Call;

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

//    @Before
    public void before() throws IOException {
        Call<WeixinTokenResponseDTO> call = weixinApiService.getToken("client_credential", appid, appSecret);
        WeixinTokenResponseDTO weixinTokenResponseDTO = call.execute().body();
        assert weixinTokenResponseDTO != null;
        accessToken = weixinTokenResponseDTO.getAccess_token();
        log.info("weixin accessToken:{}", accessToken);
    }

    @Test
    public void test_template_message() {
        Map<String, Map<String, String>> data = new HashMap<>();
        WeixinTemplateMessageDTO.put(data, WeixinTemplateMessageDTO.TemplateKey.REPO_NAME, "big-market");
        WeixinTemplateMessageDTO.put(data, WeixinTemplateMessageDTO.TemplateKey.BRANCH_NAME, "240702-xfg-refactor");
        WeixinTemplateMessageDTO.put(data, WeixinTemplateMessageDTO.TemplateKey.COMMIT_AUTHOR, "fuzhengwei");
        WeixinTemplateMessageDTO.put(data, WeixinTemplateMessageDTO.TemplateKey.COMMIT_MESSAGE, "feat: 抽奖订单功能实现v1");

        WeixinTemplateMessageDTO templateMessageDTO = new WeixinTemplateMessageDTO("or0Ab6ivwmypESVp_bYuk92T6SvU", "l2HTkntHB71R4NQTW77UkcqvSOIFqE_bss1DAVQSybc");
        templateMessageDTO.setUrl("https://gaga.plus");
        templateMessageDTO.setData(data);

        Call<Void> call = weixinApiService.sendMessage(accessToken, templateMessageDTO);
        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("Message sent successfully!");
                } else {
                    System.out.println("Failed to send message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });

        log.info("请求参数:{}",JSON.toJSONString(templateMessageDTO));

//        String url = String.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s", accessToken);
//        sendPostRequest(url, JSON.toJSONString(templateMessageDTO));
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
