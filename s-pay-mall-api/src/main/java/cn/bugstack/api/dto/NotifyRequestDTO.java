package cn.bugstack.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: doom
 * @Date: 2026/02/07/11:47
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyRequestDTO {
    /**
     * 团队ID
     */
    private String teamId;
    /**
     * 外部订单号
     */
    private List<String> outTradeNoList;
}
