package cn.bugstack.infrastructure.gateway;

import cn.bugstack.infrastructure.gateway.dto.IpLocationDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IIpLocationService {

    @GET("/")
    Call<IpLocationDTO> getIpLocation(@Query("lang") String lang);

    @GET("/{ip}")
    Call<IpLocationDTO> getIpLocationByIp(@Query("lang") String lang, @Query("ip") String ip);

}
