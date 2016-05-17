package su.iota.backend.settings;

import co.paralleluniverse.fibers.SuspendExecution;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface SettingsService {

    int getServerPortSetting() throws SuspendExecution;

    String getServerContextPathSetting() throws SuspendExecution;

}
