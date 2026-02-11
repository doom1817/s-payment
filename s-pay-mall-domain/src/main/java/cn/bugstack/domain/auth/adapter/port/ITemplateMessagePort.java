package cn.bugstack.domain.auth.adapter.port;

import cn.bugstack.domain.auth.model.valobj.LoginInfoVO;

import java.io.IOException;

public interface ITemplateMessagePort {

    void sendLoginSuccessTemplateMessage(String openid, LoginInfoVO loginInfo) throws IOException;

}
