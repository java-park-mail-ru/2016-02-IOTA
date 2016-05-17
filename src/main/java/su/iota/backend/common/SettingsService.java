package su.iota.backend.common;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface SettingsService {

    int getServerPortSetting();

    String getServerContextPathSetting();

}
