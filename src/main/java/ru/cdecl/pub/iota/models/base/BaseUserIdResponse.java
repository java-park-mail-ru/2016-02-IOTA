package ru.cdecl.pub.iota.models.base;

import javax.xml.bind.annotation.XmlElement;

public class BaseUserIdResponse {

    private long userId;

    public BaseUserIdResponse() {
        userId = -1;
    }

    public BaseUserIdResponse(long userId) {
        this.userId = userId;
    }

    @XmlElement(name="id")
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

}
