package su.iota.backend.frontend;

import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.comsat.webactors.HttpRequest;
import co.paralleluniverse.comsat.webactors.WebActor;
import co.paralleluniverse.comsat.webactors.WebMessage;
import co.paralleluniverse.fibers.SuspendExecution;
import com.esotericsoftware.minlog.Log;
import org.glassfish.hk2.api.ServiceLocator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.iota.backend.common.SettingsService;
import su.iota.backend.misc.ServiceUtils;

import static co.paralleluniverse.comsat.webactors.HttpResponse.ok;

@WebActor(httpUrlPatterns = {"/session", "/user/*"}, webSocketUrlPatterns = {"/ws"})
public class FrontendActor extends BasicActor<WebMessage, Void> {

    private boolean isInitialized = false;
    private String contextPath;

    private void init() throws InterruptedException, SuspendExecution {
        final ServiceLocator serviceLocator = ServiceUtils.getServiceLocator();
        final SettingsService settingsService = serviceLocator.getService(SettingsService.class);
        contextPath = settingsService.getServerContextPathSetting();
    }

    @Override
    protected Void doRun() throws InterruptedException, SuspendExecution {
        if (!isInitialized) {
            init();
            isInitialized = true;
        }
        //noinspection InfiniteLoopStatement
        while (true) {
            handleMessage(receive());
            checkCodeSwap();
        }
    }

    private void handleMessage(WebMessage message) throws SuspendExecution {
        if (message instanceof HttpRequest) {
            final HttpRequest httpRequest = (HttpRequest) message;
            final String resourceUri = getResourceUri(httpRequest);
            httpRequest.getFrom().send(ok(self(), httpRequest, httpRequest.toString()).build());
        }
    }

    private @NotNull String getResourceUri(HttpRequest httpRequest) {
        if (!isInitialized) {
            throw new IllegalStateException();
        }
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=433321
        return httpRequest.getRequestURI().substring(contextPath.length());
    }

    private @Nullable Long getUserIdFromHttpRequest(HttpRequest httpRequest) {
        final String resourceUri = getResourceUri(httpRequest);
        try {
            return Long.parseLong(resourceUri.substring(resourceUri.lastIndexOf('/') + 1));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return null;
        }
    }

}
