package ru.cdecl.pub.iota.main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import ru.cdecl.pub.iota.services.ConfigurationService;
import ru.cdecl.pub.iota.servlets.ConcreteUserServlet;
import ru.cdecl.pub.iota.servlets.GameServlet;
import ru.cdecl.pub.iota.servlets.SessionServlet;
import ru.cdecl.pub.iota.servlets.UserServlet;

import javax.servlet.Servlet;
import javax.sql.DataSource;
import java.io.FileInputStream;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws Exception, InterruptedException, MultiException {
        final ServiceLocator serviceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        ServiceLocatorUtilities.bind(serviceLocator, new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(DataSourceFactory.class).to(DataSource.class).in(Immediate.class);
            }
        });
        ServiceLocatorUtilities.enableImmediateScope(serviceLocator);

        final ConfigurationService configurationService = serviceLocator.getService(ConfigurationService.class);

        int port = -1;
        try {
            port = Integer.valueOf(configurationService.getProperty("port"));
        } catch (NumberFormatException ex) {
            System.err.println("Cannot parse port");
            System.exit(2);
        }

        final Server server = new Server(port);
        final ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        server.setHandler(contextHandler);

        contextHandler.setContextPath("/api");
        contextHandler.addServlet(getServletHolder(serviceLocator, UserServlet.class), "/user");
        contextHandler.addServlet(getServletHolder(serviceLocator, ConcreteUserServlet.class), "/user/*");
        contextHandler.addServlet(getServletHolder(serviceLocator, SessionServlet.class), "/session");
        contextHandler.addServlet(getServletHolder(serviceLocator, GameServlet.class), "/game");

        server.start();
        System.out.println(ASCII_LOGO);
        server.join();
    }

    private static ServletHolder getServletHolder(ServiceLocator serviceLocator, Class<? extends Servlet> servletClass) {
        final Servlet servlet = serviceLocator.getService(servletClass);
        if (servlet == null) {
            throw new AssertionError(servletClass.getCanonicalName());
        }
        return new ServletHolder(servlet);
    }

    private static final String ASCII_LOGO = "" +
            ".__           __                 ___    \n" +
            "|__|  ____  _/  |_ _____     /\\  \\  \\   \n" +
            "|  | /  _ \\ \\   __\\\\__  \\    \\/   \\  \\  \n" +
            "|  |(  <_> ) |  |   / __ \\_  /\\    )  ) \n" +
            "|__| \\____/  |__|  (____  /  \\/   /  /  \n" +
            "                        \\/       /__/   ";

}
