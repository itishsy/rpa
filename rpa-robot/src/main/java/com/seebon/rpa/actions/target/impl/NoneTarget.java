package com.seebon.rpa.actions.target.impl;

import com.seebon.rpa.actions.target.AbstractTarget;
import com.seebon.rpa.context.annotation.ActionTarget;

@ActionTarget
public class NoneTarget extends AbstractTarget<Object> {

    @Override
    public Object fetchTarget() {
        return null;
    }
}
