package su.iota.backend.frontend;

import co.paralleluniverse.actors.behaviors.ProxyServerActor;
import co.paralleluniverse.fibers.SuspendExecution;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.testng.annotations.*;
import su.iota.backend.main.ApplicationBootstrapper;
import su.iota.backend.misc.ServiceUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import su.iota.backend.settings.SettingsService;

import java.io.IOException;
import java.io.InputStreamReader;

import static org.apache.http.HttpStatus.*;
import static org.testng.Assert.*;

public class FrontendActorTest extends ProxyServerActor {

    public static final int SOCKET_TIMEOUT = 5000;
    public static final int CONNECT_TIMEOUT = 5000;
    public static final int CONNECTION_REQUEST_TIMEOUT = 5000;

    private Server server;

    private CloseableHttpClient client;

    private String uriFormat;

    public FrontendActorTest() {
        super(true);
    }

    @BeforeClass
    public void setUp() throws Exception, SuspendExecution, MultiException {
        client = HttpClients.custom().setDefaultRequestConfig(
                RequestConfig.custom()
                        .setSocketTimeout(SOCKET_TIMEOUT)
                        .setConnectTimeout(CONNECT_TIMEOUT)
                        .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                        .build()
        ).build();
        final ServiceLocator serviceLocator = ApplicationBootstrapper.setupServiceLocator();
        ServiceUtils.setupServiceUtils(serviceLocator);
        final SettingsService settingsService = serviceLocator.getService(SettingsService.class);
        uriFormat = String.format("http://127.0.0.1:%s/%s%%s",
                settingsService.getServerPortSetting(),
                settingsService.getServerContextPathSetting());
        server = serviceLocator.getService(ApplicationBootstrapper.class).setupServer();
        server.start();
    }

    @Test
    public void testSession() throws Exception {
        final String uri = getResourceUri("/session");

        JsonElement response = client.execute(new HttpDelete(uri), httpResponse -> {
            assertHttpStatus(httpResponse, SC_OK);
            return jsonResponse(httpResponse);
        });
        assertTrue(response.getAsJsonObject().get("__ok").getAsBoolean());

        response = client.execute(new HttpGet(uri), httpResponse -> {
            assertHttpStatus(httpResponse, SC_OK);
            return jsonResponse(httpResponse);
        });
        assertFalse(response.getAsJsonObject().get("__ok").getAsBoolean());
    }

    @AfterClass
    public void tearDown() throws Exception, IOException {
        server.stop();
        ServiceUtils.teardownServiceUtils();
        client.close();
    }

    private void assertHttpStatus(HttpResponse httpResponse, int statusCode) {
        assertEquals(httpResponse.getStatusLine().getStatusCode(), statusCode);
    }

    private String getResourceUri(String resourcePart) {
        return String.format(uriFormat, resourcePart);
    }

    private JsonElement jsonResponse(HttpResponse httpResponse) throws IOException {
        return new JsonParser().parse(new InputStreamReader(httpResponse.getEntity().getContent()));
    }

}