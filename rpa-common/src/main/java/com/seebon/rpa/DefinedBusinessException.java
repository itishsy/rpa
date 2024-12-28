package com.seebon.rpa;

public class DefinedBusinessException extends RuntimeException {

    private String content;

    public DefinedBusinessException() {
        super();
    }

    public DefinedBusinessException(String message) {
        super(message);
    }

    public DefinedBusinessException(String message, String content) {
        super(message);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
