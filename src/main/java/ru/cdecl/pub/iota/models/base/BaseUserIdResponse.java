package ru.cdecl.pub.iota.models.base;

import org.jetbrains.annotations.NotNull;
import ru.cdecl.pub.iota.models.base.BaseApiResponse;

import javax.xml.bind.annotation.XmlElement;

public class BaseUserIdResponse extends BaseApiResponse {

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
