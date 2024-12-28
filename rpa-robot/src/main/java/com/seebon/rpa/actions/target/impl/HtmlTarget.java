package com.seebon.rpa.actions.target.impl;

import com.seebon.rpa.actions.target.AbstractTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionTarget;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

@ActionTarget
public class HtmlTarget extends AbstractTarget<File> {

    @ActionArgs("文件路径")
    private String filePath;

    @Override
    public File fetchTarget() {
        if (StringUtils.isNotBlank(filePath)) {
            File file = new File(filePath);
            return file;
        }
        return null;
    }
}
