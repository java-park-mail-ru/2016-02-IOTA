package su.iota.backend.frontend;

import co.paralleluniverse.actors.behaviors.ProxyServerActor;
import co.paralleluniverse.fibers.SuspendExecution;
import com.esotericsoftware.minlog.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.testng.annotations.*;
import su.iota.backend.accounts.AccountService;
import su.iota.backend.accounts.impl.AccountServiceMapImpl;
import su.iota.backend.main.ApplicationBootstrapper;
import su.iota.backend.misc.ServiceUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import su.iota.backend.settings.SettingsService;
import su.iota.backend.settings.impl.SettingsServiceFixedImpl;

import java.io.IOException;
import java.io.InputStreamReader;

import static org.apache.http.HttpStatus.*;
import static org.testng.Assert.*;

public class FunctionalTest extends ProxyServerActor {

    public static final int SOCKET_TIMEOUT = 5000;
    public static final int CONNECT_TIMEOUT = 5000;
    public static final int CONNECTION_REQUEST_TIMEOUT = 5000;

    private Server server;

    private CloseableHttpClient client;

    private String uriFormat;

    public FunctionalTest() {
        super(true);
    }

    @BeforeClass
    public void setUpClass() throws Exception, SuspendExecution, MultiException {
        final ServiceLocator serviceLocator = ApplicationBootstrapper.setupServiceLocator(
                new FunctionalTestDependencyBinder()
        );
        ServiceUtils.setupServiceUtils(serviceLocator);
        final SettingsService settingsService = serviceLocator.getService(SettingsService.class);
        uriFormat = String.format("http://127.0.0.1:%s/%s%%s",
                settingsService.getServerPortSetting(),
                settingsService.getServerContextPathSetting());
        server = serviceLocator.getService(ApplicationBootstrapper.class).setupServer();
        server.start();
    }

    @AfterClass
    public void tearDownClass() throws Exception, IOException {
        server.stop();
        ServiceUtils.teardownServiceUtils();
    }

    @BeforeMethod
    public void setUp() throws Exception {
        client = HttpClients.custom().setDefaultRequestConfig(
                RequestConfig.custom()
                        .setSocketTimeout(SOCKET_TIMEOUT)
                        .setConnectTimeout(CONNECT_TIMEOUT)
                        .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                        .build()
        ).build();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void testSession() throws Exception {
        final String uri = getResourceUri("/session");
        Log.info(uri);
        //noinspection TooBroadScope
        JsonElement response;

        response = client.execute(new HttpDelete(uri), httpResponse -> {
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

    private void assertHttpStatus(HttpResponse httpResponse, int statusCode) {
        assertEquals(httpResponse.getStatusLine().getStatusCode(), statusCode);
    }

    private String getResourceUri(String resourcePart) {
        return String.format(uriFormat, resourcePart);
    }

    private JsonElement jsonResponse(HttpResponse httpResponse) throws IOException {
        return new JsonParser().parse(new InputStreamReader(httpResponse.getEntity().getContent()));
    }

    private static class FunctionalTestDependencyBinder extends AbstractBinder {

        @Override
        protected void configure() {
            bind(AccountServiceMapImpl.class).to(AccountService.class).ranked(1000);
            bind(SettingsServiceFixedImpl.class).to(SettingsService.class).ranked(1000);
        }

    }

}