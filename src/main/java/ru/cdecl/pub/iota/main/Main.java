package ru.cdecl.pub.iota.main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import ru.cdecl.pub.iota.servlets.ConcreteUserServlet;
import ru.cdecl.pub.iota.servlets.SessionServlet;
import ru.cdecl.pub.iota.servlets.UserServlet;

import javax.servlet.Servlet;
import javax.sql.DataSource;

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

        final ServiceLocator serviceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        ServiceLocatorUtilities.bind(serviceLocator, new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(DataSourceFactory.class).to(DataSource.class).in(Immediate.class);
            }
        });
        ServiceLocatorUtilities.enableImmediateScope(serviceLocator);

        final Server server = new Server(port);
        final ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        server.setHandler(contextHandler);

        contextHandler.setContextPath("/api");
        contextHandler.addServlet(getServletHolder(serviceLocator, UserServlet.class), "/user");
        contextHandler.addServlet(getServletHolder(serviceLocator, ConcreteUserServlet.class), "/user/*");
        contextHandler.addServlet(getServletHolder(serviceLocator, SessionServlet.class), "/session");

        //noinspection OverlyBroadCatchBlock
        try {
            server.start();
            System.out.println(ASCII_LOGO);
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ServletHolder getServletHolder(ServiceLocator serviceLocator, Class<? extends Servlet> servletClass) {
        final Servlet servlet = serviceLocator.getService(servletClass);
        if (servlet == null) {
            throw new AssertionError(servletClass.getCanonicalName());
        }
        return new ServletHolder(servlet);
    }

    // -Dco.paralleluniverse.fibers.verifyInstrumentation=true

    private static final String ASCII_LOGO = "" +
            ".__           __                 ___    \n" +
            "|__|  ____  _/  |_ _____     /\\  \\  \\   \n" +
            "|  | /  _ \\ \\   __\\\\__  \\    \\/   \\  \\  \n" +
            "|  |(  <_> ) |  |   / __ \\_  /\\    )  ) \n" +
            "|__| \\____/  |__|  (____  /  \\/   /  /  \n" +
            "                        \\/       /__/   ";

}
