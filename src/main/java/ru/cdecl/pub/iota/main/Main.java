package ru.cdecl.pub.iota.main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import ru.cdecl.pub.iota.servlets.ConcreteUserServlet;
import ru.cdecl.pub.iota.servlets.SessionServlet;
import ru.cdecl.pub.iota.servlets.UserServlet;

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
        ServiceLocatorUtilities.enableImmediateScope(serviceLocator);

        final Server server = new Server(port);
        final ServletContextHandler contextHandler = new ServletContextHandler();
        server.setHandler(contextHandler);

        contextHandler.setContextPath("/api");
        contextHandler.addServlet(new ServletHolder(serviceLocator.getService(UserServlet.class)), "/user");
        contextHandler.addServlet(new ServletHolder(serviceLocator.getService(ConcreteUserServlet.class)), "/user/*");
        contextHandler.addServlet(new ServletHolder(serviceLocator.getService(SessionServlet.class)), "/session");

        //noinspection OverlyBroadCatchBlock
        try {
            server.start();
            System.out.println(ASCII_LOGO);
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
