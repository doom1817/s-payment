package cn.bugstack.domain.auth.service;

import cn.bugstack.domain.auth.adapter.port.ITemplateMessagePort;
import cn.bugstack.domain.auth.model.valobj.LoginInfoVO;
import cn.bugstack.types.exception.AppException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class TemplateMessageService implements ITemplateMessageService {

    @Resource
    private ITemplateMessagePort templateMessagePort;

    @Override
    public void sendLoginSuccessMessage(String openid, LoginInfoVO loginInfo) throws IOException {
        try {
            templateMessagePort.sendLoginSuccessTemplateMessage(openid, loginInfo);
        } catch (Exception e) {
            // 确保异常消息不为null
            String errorMessage = e.getMessage();
            if (errorMessage == null || "null".equals(errorMessage)) {
                errorMessage = e.getClass().getSimpleName() + ": " + e.toString();
            }
            throw new AppException("发送登录成功模板消息失败: " + errorMessage);
        }
    }

}
