package ru.cdecl.pub.iota.models.base;

import org.jetbrains.annotations.Nullable;

import javax.xml.bind.annotation.XmlElement;

public class BaseUserIdResponse {

    @Nullable
    private Long userId;

    public BaseUserIdResponse() {
        userId = -1L;
    }

    public BaseUserIdResponse(@Nullable Long userId) {
        this.userId = userId;
    }

    @Nullable
    @XmlElement(name="id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

}
