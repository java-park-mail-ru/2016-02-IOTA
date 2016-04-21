package ru.cdecl.pub.iota.services;

import org.glassfish.hk2.api.Immediate;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.exceptions.InitializationException;

import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@Service
@Immediate
public class ConfigurationService extends Properties {

    public ConfigurationService() {
        super();
        //noinspection OverlyBroadCatchBlock
        try(FileInputStream fileInputStream = new FileInputStream("server.properties")) {
            load(fileInputStream);
        } catch (IOException e) {
            throw new InitializationException(e);
        }
    }

}
