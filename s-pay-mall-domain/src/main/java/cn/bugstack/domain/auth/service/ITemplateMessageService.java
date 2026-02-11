package cn.bugstack.domain.auth.service;

import cn.bugstack.domain.auth.model.valobj.LoginInfoVO;

import java.io.IOException;

public interface ITemplateMessageService {

    void sendLoginSuccessMessage(String openid, LoginInfoVO loginInfo) throws IOException;

}
