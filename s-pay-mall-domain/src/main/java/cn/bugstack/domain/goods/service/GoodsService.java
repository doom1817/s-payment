package cn.bugstack.domain.goods.service;

import cn.bugstack.domain.goods.adapter.repository.IGoodsRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: doom
 * @Date: 2026/02/12/14:06
 * @Description:
 */
@Service
public class GoodsService implements IGoodsService{

    @Resource
    private IGoodsRepository goodsRepository;


    /**
     * 订单处理完成
     * @param tradeNo 订单编号
     */
    @Override
    public void changeOrderDealDone(String tradeNo) {
        goodsRepository.changeOrderDealDone(tradeNo);
    }
}
