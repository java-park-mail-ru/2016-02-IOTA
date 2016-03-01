package ru.cdecl.pub.iota.main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

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