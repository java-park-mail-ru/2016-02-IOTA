package su.iota.backend.main;

import co.paralleluniverse.comsat.webactors.servlet.WebActorInitializer;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableRunnable;
import com.esotericsoftware.minlog.Log;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.jvnet.hk2.annotations.Service;
import su.iota.backend.settings.SettingsService;
import su.iota.backend.misc.ServiceUtils;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@Immediate
public final class ApplicationBootstrapper implements SuspendableRunnable {

    @Inject
    ServiceLocator serviceLocator;

    @Inject
    SettingsService settingsService;

    @Override
    public void run() throws SuspendExecution, InterruptedException {
        final Server server = new Server(settingsService.getServerPortSetting());
        final ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        ServiceUtils.setupServiceUtils(serviceLocator);
        WebActorInitializer.setUserClassLoader(ClassLoader.getSystemClassLoader());

        server.setHandler(contextHandler);
        contextHandler.setContextPath(settingsService.getServerContextPathSetting());
        contextHandler.addEventListener(new WebActorInitializer());

        try {
            WebSocketServerContainerInitializer.configureContext(contextHandler);
        } catch (ServletException e) {
            throw new AssertionError(e);
        }

        //noinspection AnonymousInnerClassMayBeStatic
        server.addLifeCycleListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStarted(LifeCycle event) {
                System.out.println(ASCII_LOGO);
            }
        });

        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        server.join();
    }

    public static void main(String[] args) throws SuspendExecution, InterruptedException {
        final ServiceLocator serviceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        ServiceLocatorUtilities.enableImmediateScope(serviceLocator);
        serviceLocator.getService(ApplicationBootstrapper.class).run();
    }

    private static final String ASCII_LOGO = "" +
            "\n.__        __                             ___    \n" +
            "|__| _____/  |______         ________ __  \\  \\   \n" +
            "|  |/  _ \\   __\\__  \\       /  ___/  |  \\  \\  \\  \n" +
            "|  (  <_> )  |  / __ \\_     \\___ \\|  |  /   )  ) \n" +
            "|__|\\____/|__| (____  / /\\ /____  >____/   /  /  \n" +
            "                    \\/  \\/      \\/        /__/   \n";

}
