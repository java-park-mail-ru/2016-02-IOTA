package ru.cdecl.pub.iota.main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main {

    public static final int FALLBACK_PORT = 8080;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Not enough arguments: no port specified.");
            System.exit(1);
        }

        int port = FALLBACK_PORT;

        try {
            port = Integer.valueOf(args[0]);
        } catch (NumberFormatException ex) {
            String errorMessage = String.format("Cannot parse port, using fallback port %d.", port);
            System.err.println(errorMessage);
        }

        System.out.println(String.format("Starting server at port %d.", port));

        final Server server = new Server(port);
        final ServletContextHandler contextHandler = new ServletContextHandler(server, "/api/", ServletContextHandler.SESSIONS);

        final ServletHolder servletHolder = new ServletHolder(ServletContainer.class);
        servletHolder.setInitParameter("javax.ws.rs.Application", "ru.cdecl.pub.iota.main.RestApplication");

        contextHandler.addServlet(servletHolder, "/*");

        //noinspection OverlyBroadCatchBlock
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}