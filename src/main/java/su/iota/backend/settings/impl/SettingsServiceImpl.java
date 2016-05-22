package su.iota.backend.settings.impl;

import co.paralleluniverse.fibers.SuspendExecution;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.glassfish.hk2.api.Immediate;
import org.jvnet.hk2.annotations.Service;
import su.iota.backend.settings.SettingsService;

import javax.inject.Singleton;

@Service
@Singleton
public class SettingsServiceImpl implements SettingsService {

    private Config config;

    public SettingsServiceImpl() {
        config = ConfigFactory.load();
        config.checkValid(ConfigFactory.defaultReference());
    }

    @Override
    public int getServerPortSetting() throws SuspendExecution {
        return config.getInt("port");
    }

    @Override
    public String getServerContextPathSetting() throws SuspendExecution {
        return config.getString("contextPath");
    }

}
