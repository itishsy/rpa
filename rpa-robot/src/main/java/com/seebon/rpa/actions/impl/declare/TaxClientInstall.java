package com.seebon.rpa.actions.impl.declare;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
@RobotAction(name = "社保费管理客户端安装", order = 51, targetType = NoneTarget.class, comment = "社保费管理客户端安装")
public class TaxClientInstall extends AbstractAction {
    @ActionArgs(value = "原始安装路径", comment = "原始安装路径（尚未选择城市/省份的安装包）如:D:/EPPortal_SB1.0", required = true, style = DynamicFieldType.text)
    private String basePath;

    @ActionArgs(value = "安装目录", comment = "实际要选择城市/省份的安装路径 如 D:/EPPortal_SB1.0/大连", required = true, style = DynamicFieldType.text)
    private String targetPath;

    @ActionArgs(value = "结果变量", required = true,comment = "新安装返回:true,反之false")
    private String resultKey;

    @Override
    public void run() {
        File directory = new File(targetPath);
        if (directory.exists() && directory.isDirectory()) {
            ctx.setVariable(resultKey, "false");
            return;
        } else {
            log.info("未安装:{}", targetPath);
            copyDir(basePath,targetPath);
            log.info("安装完毕:{}",targetPath);
            ctx.setVariable(resultKey, "true");
        }
    }


    public static void copyDir(String srcFile,String dstFile) {
        File srcfile = new File(srcFile);
        if(!srcfile.exists()){
            return ;
        }
        File dstfile = new File(dstFile);
        if(!dstfile.exists()){
            dstfile.mkdir();
        }
        File[] list = srcfile.listFiles();
        for(File file:list){
            if(file.isFile()){
                copyFile(srcFile+"/"+file.getName(), dstFile+"/"+file.getName());
            }
            if(file.isDirectory()){
                copyDir(srcFile+"/"+file.getName(), dstFile+"/"+file.getName());
            }
        }
    }
    public static void copyFile(String src,String dst){
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(new File(src)));
            bos = new BufferedOutputStream(new FileOutputStream(new File(dst)));
            byte[] b =new byte[1024];
            int n = 0;
            while((n =bis.read(b))!=-1){
                bos.write(b, 0, n);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }finally{
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }


}



