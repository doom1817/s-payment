package cn.bugstack.infrastructure.gateway.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IpLocationDTO {

    @JSONField(name = "status")
    private String status;
    
    @JSONField(name = "country")
    private String country;
    
    @JSONField(name = "countryCode")
    private String countryCode;
    
    @JSONField(name = "region")
    private String region;
    
    @JSONField(name = "regionName")
    private String regionName;
    
    @JSONField(name = "city")
    private String city;
    
    @JSONField(name = "zip")
    private String zip;
    
    @JSONField(name = "lat")
    private Double lat;
    
    @JSONField(name = "lon")
    private Double lon;
    
    @JSONField(name = "timezone")
    private String timezone;
    
    @JSONField(name = "isp")
    private String isp;
    
    @JSONField(name = "org")
    private String org;
    
    @JSONField(name = "as")
    private String as;
    
    @JSONField(name = "query")
    private String query;

    public String getFullRegion() {
        if (country == null && regionName == null && city == null) {
            return "未知地区";
        }
        StringBuilder sb = new StringBuilder();
        if (country != null) {
            sb.append(country);
        }
        if (regionName != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(regionName);
        }
        if (city != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(city);
        }
        return sb.toString();
    }

}
