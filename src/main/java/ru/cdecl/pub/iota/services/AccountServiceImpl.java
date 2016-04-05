package ru.cdecl.pub.iota.services;

import org.glassfish.hk2.api.Immediate;
import org.jvnet.hk2.annotations.Service;

@Service
@Immediate
public class AccountServiceImpl implements AccountService {

    public AccountServiceImpl() {
        // throw new RuntimeException();
    }
}
