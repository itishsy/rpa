package com.seebon.rpa.actions.impl.net;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ResponseTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.runtime.RobotRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RobotAction(name = "HTTP下载", targetType = ResponseTarget.class, comment = "Http请求下载文件，存放到指定路径")
public class HttpDownload extends AbstractAction {

    @ActionArgs(value = "存放路径", required = true)
    private String filePath;

    @Override
    public void run() {
        FileOutputStream fos = null;
        CloseableHttpResponse response = null;
        try {
            response = getTarget();
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            File file = new File(filePath);
            fos = new FileOutputStream(file);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            inputStream.close();
            fos.flush();
        } catch (Exception e) {
            if (e instanceof RobotRuntimeException) {
                throw (RobotRuntimeException) e;
            } else {
                log.error("【Exception】", e);
                throw new RobotRuntimeException("http download failed. info:" + e.getMessage());
            }
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                log.error("【Exception】", e);
            }
        }
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
