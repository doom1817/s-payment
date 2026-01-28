package cn.bugstack.config;

import cn.bugstack.infrastructure.gateway.IIpLocationService;
import cn.bugstack.infrastructure.gateway.ITaobaoIpService;
import cn.bugstack.infrastructure.gateway.IWeixinApiService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class Retrofit2Config {

    private static final String WEIXIN_BASE_URL = "https://api.weixin.qq.com/";
    private static final String IP_LOCATION_BASE_URL = "http://ip-api.com/json/";
    private static final String TAOBAO_IP_BASE_URL = "http://ip.taobao.com/outGetIpInfo/";

    @Bean
    public Retrofit weixinRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(WEIXIN_BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    @Bean
    public IWeixinApiService weixinApiService(Retrofit weixinRetrofit) {
        return weixinRetrofit.create(IWeixinApiService.class);
    }

    @Bean
    public Retrofit ipLocationRetrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request originalRequest = chain.request();
                    Request requestWithHeaders = originalRequest.newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                            .header("Accept", "application/json")
                            .method(originalRequest.method(), originalRequest.body())
                            .build();
                    return chain.proceed(requestWithHeaders);
                })
                .build();
        
        return new Retrofit.Builder()
                .baseUrl(IP_LOCATION_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    @Bean
    public IIpLocationService ipLocationService(Retrofit ipLocationRetrofit) {
        return ipLocationRetrofit.create(IIpLocationService.class);
    }

    @Bean
    public Retrofit taobaoIpRetrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request originalRequest = chain.request();
                    Request requestWithHeaders = originalRequest.newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                            .header("Accept", "application/json")
                            .method(originalRequest.method(), originalRequest.body())
                            .build();
                    return chain.proceed(requestWithHeaders);
                })
                .build();
        
        return new Retrofit.Builder()
                .baseUrl(TAOBAO_IP_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    @Bean
    public ITaobaoIpService taobaoIpService(Retrofit taobaoIpRetrofit) {
        return taobaoIpRetrofit.create(ITaobaoIpService.class);
    }

}
