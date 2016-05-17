package su.iota.backend.services;

import org.jvnet.hk2.annotations.Service;
import su.iota.backend.common.SettingsService;

import javax.inject.Singleton;

@Service
@Singleton
public class FixedSettingsServiceImpl implements SettingsService {

    @Override
    public int getServerPortSetting() {
        //noinspection MagicNumber
        return 8080;
    }

    @Override
    public String getServerContextPathSetting() {
        return "/api";
    }

}
