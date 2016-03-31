package ru.cdecl.pub.iota.main;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import javax.inject.Inject;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class ImmediateFeature implements Feature {

    @Inject
    public ImmediateFeature(ServiceLocator serviceLocator) {
        ServiceLocatorUtilities.enableImmediateScope(serviceLocator);
    }

    @Override
    public boolean configure(FeatureContext context) {
        return true;
    }

}
