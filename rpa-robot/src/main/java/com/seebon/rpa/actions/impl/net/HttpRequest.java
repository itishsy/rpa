package com.seebon.rpa.actions.impl.net;

import com.alibaba.fastjson.JSONObject;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ResponseTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.HttpDataType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@RobotAction(name = "HTTP请求", targetType = ResponseTarget.class, comment = "将Http请求响应结果存放到指定变量")
public class HttpRequest extends AbstractAction {

    @ActionArgs(value = "结果变量", required = true)
    private String dataKey;

    @ActionArgs(value = "响应数据类型", required = true, dict = HttpDataType.class)
    private String responseType;

    @Override
    public void run() {
        try {
            Object object;
            Object response = getTarget();
            String result = this.getResult(response);
            log.info("result=" + result);
            switch (HttpDataType.valueOf(responseType)) {
                case map:
                    object = JSONObject.parseObject(result, Map.class);
                    break;
                case list: {
                    object = (List<Map<String, Object>>) JSONObject.parseObject(result, List.class);
                    break;
                }
                case str:
                default:
                    object = result;
                    break;
            }
            ctx.setVariable(dataKey, object);
        } catch (Exception e) {
            log.error("【Exception】", e);
        }
    }

    private String getResult(Object response) throws Exception {
        if (response instanceof CloseableHttpResponse) {
            HttpEntity entity = ((CloseableHttpResponse) response).getEntity();
            return EntityUtils.toString(entity, "utf-8");
        }
        if (response instanceof String) {
            return response.toString();
        }
        return null;
    }
}
