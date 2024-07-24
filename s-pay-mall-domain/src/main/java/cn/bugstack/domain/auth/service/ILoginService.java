package cn.bugstack.domain.auth.service;

import java.io.IOException;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 微信服务
 * @create 2024-02-25 11:59
 */
public interface ILoginService {

    String createQrCodeTicket();

    String checkLogin(String ticket);

    void saveLoginState(String ticket, String openid) throws IOException;

}
