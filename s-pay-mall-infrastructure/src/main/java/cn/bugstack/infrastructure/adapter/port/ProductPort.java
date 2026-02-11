package cn.bugstack.infrastructure.adapter.port;

import cn.bugstack.domain.order.adapter.port.IProductPort;
import cn.bugstack.domain.order.model.entity.MarketPayDiscountEntity;
import cn.bugstack.domain.order.model.entity.ProductEntity;
import cn.bugstack.infrastructure.gateway.IGroupBuyMarketService;
import cn.bugstack.infrastructure.gateway.ProductRPC;
import cn.bugstack.infrastructure.gateway.dto.LockMarketPayOrderRequestDTO;
import cn.bugstack.infrastructure.gateway.dto.LockMarketPayOrderResponseDTO;
import cn.bugstack.infrastructure.gateway.dto.ProductDTO;
import cn.bugstack.infrastructure.gateway.response.Response;
import cn.bugstack.types.exception.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Call;

import javax.annotation.Resource;

@Component
public class ProductPort implements IProductPort {

    private final ProductRPC productRPC;

    private final IGroupBuyMarketService groupBuyMarketService;

    @Value("${app.config.group-buy-market.source}")
    private String source;
    @Value("${app.config.group-buy-market.channel}")
    private String channel;
    @Value("${app.config.group-buy-market.api-url}")
    private String notifyUrl;

    public ProductPort(ProductRPC productRPC, IGroupBuyMarketService groupBuyMarketService) {
        this.productRPC = productRPC;
        this.groupBuyMarketService = groupBuyMarketService;
    }

    @Override
    public ProductEntity queryProductByProductId(String productId) {
        ProductDTO productDTO = productRPC.queryProductByProductId(productId);
        return ProductEntity.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getProductName())
                .productDesc(productDTO.getProductDesc())
                .price(productDTO.getPrice())
                .build();
    }

    @Override
    public MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId) {
        //请求参数
        LockMarketPayOrderRequestDTO request =new  LockMarketPayOrderRequestDTO();
        request.setUserId(userId);
        request.setTeamId(teamId);
        request.setGoodsId(productId);
        request.setActivityId(activityId);
        request.setSource(source);
        request.setChannel(channel);
        request.setOutTradeNo(orderId);
        request.setNotifyUrl(notifyUrl);
        try{
            Call<Response<LockMarketPayOrderResponseDTO>> call = groupBuyMarketService.lockMarketPayOrder(request);
            Response<LockMarketPayOrderResponseDTO> response = call.execute().body();
            if(null == response )return null;
            if (!"0000".equals(response.getCode())){
                throw new AppException(response.getCode(),response.getInfo());
            }
            LockMarketPayOrderResponseDTO responseDTO = response.getData();
            return MarketPayDiscountEntity.builder()
                    .originalPrice(responseDTO.getOriginalPrice())
                    .payPrice(responseDTO.getPayPrice())
                    .deductionPrice(responseDTO.getDeductionPrice())
                    .build();
        }
        catch (Exception e){
             return null;
        }
    }

}
