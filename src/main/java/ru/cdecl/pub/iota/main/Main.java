package ru.cdecl.pub.iota.main;

import org.eclipse.jetty.server.Server;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;
import ru.cdecl.pub.iota.services.AccountService;
import ru.cdecl.pub.iota.services.AccountServiceImpl;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Not enough arguments: no port specified.");
            System.exit(1);
        }

        int port = -1;
        try {
            port = Integer.valueOf(args[0]);
        } catch (NumberFormatException ex) {
            System.err.println("Cannot parse port");
            System.exit(2);
        }

        assert (port > 0);
        final URI baseUri = UriBuilder.fromUri("http://127.0.0.1/").port(port).build();
        System.out.println(String.format("Starting server at uri: %s.", baseUri));
        final Server server = setUpServer(baseUri);

        //noinspection OverlyBroadCatchBlock
        try {
            server.start();
            System.out.println(ASCII_LOGO);
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Server setUpServer(final URI baseUri) {
        final ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages("ru.cdecl.pub.iota.resources");
        resourceConfig.register(ImmediateFeature.class);
        resourceConfig.register(EntityFilteringFeature.class);
        resourceConfig.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(AccountServiceImpl.class).to(AccountService.class).in(Immediate.class);
            }
        });

        return JettyHttpContainerFactory.createServer(baseUri, resourceConfig);
    }

    private static final String ASCII_LOGO = '\n' +
            "\n" +
            ".__           __                 ___    \n" +
            "|__|  ____  _/  |_ _____     /\\  \\  \\   \n" +
            "|  | /  _ \\ \\   __\\\\__  \\    \\/   \\  \\  \n" +
            "|  |(  <_> ) |  |   / __ \\_  /\\    )  ) \n" +
            "|__| \\____/  |__|  (____  /  \\/   /  /  \n" +
            "                        \\/       /__/   \n" +
            '\n';

}