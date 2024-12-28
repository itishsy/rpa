package com.seebon.rpa.context.constant;

public enum AttributeType {
    id,
    name,
    xpath,
    className,
    tagName,
    cssSelector,
    linkText;

    @Override
    public String toString() {
        return this.name();
    }
}
