package cn.bugstack.xfg.dev.tech.test;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

@Slf4j
public class AliPayTest {

    // 「沙箱环境」应用ID - 您的APPID，收款账号既是你的APPID对应支付宝账号。获取地址；https://open.alipay.com/develop/sandbox/app
    public static String app_id = "9021000132689924";
    // 「沙箱环境」商户私钥，你的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCJN3l3DV6I2rGzMJo+fUjuyNWlneX6u2XPzLinviCtrmy2hLCipx0H37PzPVQbRWodiKThBaIhDbGK15mx0/UA+mLJOpK8fqAKXiuSgZ8J7zl4Gox7rySgHI0TA9aKNwMaeG2UovGUZiIL66Lx9u+Tn+X/hNFqrsEiDjHB80Z1AofRtboaI+4tCWq+/jsNgfJdOJf69qyqLXL1BtAo6UyXZpkGdZshDjO87V85Bmjl7ruVjIsmgtRHDO86HHnLHmZC3fZMG1eteI58MZWS8q5NoOQnkfw5l9m2+/rrAjpIqDIPxt6I289l/fVIG5CUX3N7+ws76YjBmN+LMy92/prFAgMBAAECggEAaT5NQYzVhy4FwaRziZ5aRBtyEncLH8aADmO89ZuUQOJZq5wWRorab2GbCeNuEQSqoUBfBeBmO+fe1tkKnyQs9LUW87SjO0P7yDf2P4Z/zwTXFVF3kp4A/3UmlwEImjYbgUAA/FIF7IZxcqcm8WMu2hF4PQVIAgVLSK2OaG4JClDoXE/R/HZrSv/AU3cMkuFsMHewuuDSHWYNdZb1Mqg6TrFt0+ryYciybkpfHZPvB6zqZG+c0gStZ6tTzviGi0T26zlHp3EdsLJ5Mmfw0z1TY2PDX/iICO/mCRJt1yT2E2h82QgFPszjS9wQ8OuxsT0eeUYA+4LA35juU4+GbpOhgQKBgQDI5TaVDzfLW5K0mw/1+6ZVq9Xgrg0p6kBUGyVfw2Ne47UCWsT1Q3eQTzMIhM3KPVeKgHPF4JT3tv7sCjbOspjcg9hw+zxAq/2jO1JZHffOd1id8rj7NmtLB0q9BQPNmtllmjYaJgPW45KY09vBtKWLxoVYp9qlxZIMIHZO58gZMQKBgQCu2sW9tMdoP2dvqzD5+vXN9lV/uUgCd+6dC4D3GIBAB3aRvnqZuGo3i7DBwdL2VwsTSs54CTlppQYKJHomPkhus68J69rWLI4cDJUblNHvNCtVHBDp8bnha7MfG99ppuDq/RK3X+jRTOqjk5uuVfBHR8GSBEwFGuvVdjfMslK11QKBgGl9Gq0owYZx/lYBul7TFmOUCImjO1xQGhlP688ePRa9gPXMjfGvzJ+lRsnpWlog3c/nZWN5ax+sIq31KxRcabIZNVUWR2w6I8RLyRDskIwE1S6THpki2TIbkR4x/O5AqnkUgeY3Ifr7oqXHUXSkk+0SgECuWzyCww7FGPqT8NlxAoGAVSOzWy72VZ1h2S7rNx1qqLIEaZ+nH4SUTCW40xg8AnFf25F7EvSHy6oaFA4zkb1KaEdph2s+aGFxMmkWjR6QmE6nsVYFhXnGbIP36qkpG6c0Xkm/Npp8HvYZDtG8lC4soF8/yMhLjfY83g9Wo6m2yI55A4dSGL5GDPd2Lha6TU0CgYEAlUwdoWap7Ws1M83pQyQaGHoDljRxrlk0He2OJ8icKLhDnTCJSdg4v+79W9+zLWrfcLtkQlNJRop1RZkns7RAWJq+N/9SppbFNhSxy5w3YzjTsSTKyLBPtFKpyqo9o5MzUelZFd6KWRbCY/ThEhQb+WCYXjP0U5Vx97WjWvxp6/s=";
    // 「沙箱环境」支付宝公钥
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmgtSO8GV5YSl01AWgGWe2xKJ9oDfjzf5vNtJS6SSc5klmGMToVdsi3gmLipN04yzmnEEchmFxsuxKLLhdQuC4d9V74I6CVIZPPHtaSuz/T3EZTHtQIGwF705Yrq1bd63l70iTfkrS0Ry9f72SDZEBBLllXfFo+otChwRRN+UXDd8X/bplV3/cbRncV5yWRnHHCgzQiwpH3ilS+sOmMfdfac0bi/xB7HIU6nUX04VCjAR7itSr0OmU8HC6p20Ubvjs45R6VuR7FMI+OahCd3LDe/ayelScfQ4zavruk4HGx3TDH4hLDA3N+xid5Cu5erLDPHtFXfnQHI4n/opQaXo5wIDAQAB";
    // 「沙箱环境」服务器异步通知回调地址
    public static String notify_url = "https://xfg.natapp.cn/api/v1/alipay/alipay_notify_url";
    // 「沙箱环境」页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "https://gaga.plus";
    // 「沙箱环境」
    public static String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    // 签名方式
    public static String sign_type = "RSA2";
    // 字符编码格式
    public static String charset = "utf-8";

    private AlipayClient alipayClient;

    @Before
    public void init() {
        this.alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id,
                merchant_private_key,
                "json",
                charset,
                alipay_public_key,
                sign_type);
    }

    @Test
    public void test_aliPay_pageExecute() throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();  // 发送请求的 Request类
        request.setNotifyUrl(notify_url);
        request.setReturnUrl(return_url);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "daniel82AAAA000032333361Y001");  // 我们自己生成的订单编号
        bizContent.put("total_amount", "0.01"); // 订单的总金额
        bizContent.put("subject", "测试商品");   // 支付的名称
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");  // 固定配置
        request.setBizContent(bizContent.toString());

        String form = alipayClient.pageExecute(request).getBody();
        log.info("测试结果：{}", form);

        /**
         * 会生成一个form表单；
         * <form name="punchout_form" method="post" action="https://openapi-sandbox.dl.alipaydev.com/gateway.do?charset=utf-8&method=alipay.trade.page.pay&sign=CAAYYDIbvUNRDvY%2B%2BF5vghx2dL9wovodww8CK0%2FferNP1KtyXdytBVLdZKssaFJV%2B8QksVuKlU3qneWhWUuI7atLDgzpussJlJhxTMYQ3GpAfOP4PEBYQFE%2FORemzA2XPjEn88HU7esdJdUxCs602kiFoZO8nMac9iqN6P8deoGWYO4UAwE0RCV65PKeJTcy8mzhOTgkz7V018N9yIL0%2BEBf5iQJaP9tGXM4ODWwFRxJ4l1Egx46FNfjLAMzysy7D14LvTwBi5uDXV4Y%2Bp4VCnkxh3Jhkp%2BDP9SXx6Ay7QaoerxHA09kwYyLQrZ%2FdMZgoQ%2BxSEOgklIZtYj%2FLbfx1A%3D%3D&return_url=https%3A%2F%2Fgaga.plus&notify_url=http%3A%2F%2Fngrok.sscai.club%2Falipay%2FaliPayNotify_url&version=1.0&app_id=9021000132689924&sign_type=RSA2&timestamp=2023-12-13+11%3A36%3A29&alipay_sdk=alipay-sdk-java-4.38.157.ALL&format=json">
         * <input type="hidden" name="biz_content" value="{&quot;out_trade_no&quot;:&quot;100001001&quot;,&quot;total_amount&quot;:&quot;1.00&quot;,&quot;subject&quot;:&quot;测试&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}">
         * <input type="submit" value="立即支付" style="display:none" >
         * </form>
         * <script>document.forms[0].submit();</script>
         */
    }

    /**
     * 查询订单
     */
    @Test
    public void test_alipay_certificateExecute() throws AlipayApiException {

        AlipayTradeQueryModel bizModel = new AlipayTradeQueryModel();
        bizModel.setOutTradeNo("daniel82AAAA000032333361Y001");

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizModel(bizModel);

        String body = alipayClient.execute(request).getBody();
        log.info("测试结果：{}", body);
    }

    /**
     * 退款接口
     */
    @Test
    public void test_alipay_refund() throws AlipayApiException {
        AlipayTradeRefundRequest request =new AlipayTradeRefundRequest();
        AlipayTradeRefundModel refundModel =new AlipayTradeRefundModel();
        refundModel.setOutTradeNo("daniel82AAAA000032333361X03");
        refundModel.setRefundAmount("1.00");
        refundModel.setRefundReason("退款说明");
        request.setBizModel(refundModel);

        AlipayTradeRefundResponse execute = alipayClient.execute(request);
        log.info("测试结果：{}", execute.isSuccess());
    }

    public static void main(String[] args) {
        System.out.println(new BigDecimal("9.99").doubleValue());
    }

}