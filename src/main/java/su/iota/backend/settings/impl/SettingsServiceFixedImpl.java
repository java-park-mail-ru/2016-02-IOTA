package su.iota.backend.settings.impl;

import co.paralleluniverse.fibers.SuspendExecution;
import org.glassfish.hk2.api.Rank;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import su.iota.backend.settings.SettingsService;

import javax.inject.Named;
import javax.inject.Singleton;

@Rank(-100)
@Service
@Singleton
public class SettingsServiceFixedImpl implements SettingsService {

    @Override
    public int getServerPortSetting() throws SuspendExecution {
        //noinspection MagicNumber
        return 8089;
    }

    @NotNull
    @Override
    public String getServerContextPathSetting() throws SuspendExecution {
        return "/test/api";
    }

    @NotNull
    @Override
    public String getDatabaseUserID() {
        return "unused";
    }

    @NotNull
    @Override
    public String getDatabasePassword() {
        return "unused";
    }

    @NotNull
    @Override
    public String getDatabaseName() {
        return "unused";
    }

}
