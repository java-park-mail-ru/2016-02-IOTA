package ru.cdecl.pub.iota.models;

import org.jetbrains.annotations.Nullable;
import ru.cdecl.pub.iota.models.base.BaseUserIdResponse;

public class UserLoginResponse extends BaseUserIdResponse {

    public UserLoginResponse() {
        super();
    }

    public UserLoginResponse(@Nullable Long userId) {
        super(userId);
    }

}
