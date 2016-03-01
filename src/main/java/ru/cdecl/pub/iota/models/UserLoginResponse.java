package ru.cdecl.pub.iota.models;

import ru.cdecl.pub.iota.models.base.BaseUserIdResponse;

public class UserLoginResponse extends BaseUserIdResponse {

    public UserLoginResponse() {
        super();
    }

    public UserLoginResponse(long userId) {
        super(userId);
    }
}
