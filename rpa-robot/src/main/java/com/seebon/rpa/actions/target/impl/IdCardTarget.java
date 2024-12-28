package com.seebon.rpa.actions.target.impl;

import com.seebon.rpa.actions.target.AbstractTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionTarget;

@ActionTarget
public class IdCardTarget extends AbstractTarget<String> {

    @ActionArgs("证件号码")
    private String idCard;

    @Override
    public String fetchTarget() {
        return idCard;
    }
}
