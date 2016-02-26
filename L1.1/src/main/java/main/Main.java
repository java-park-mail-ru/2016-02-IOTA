package main;

import frontend.Frontend;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author esin88
 */
public class Main {
    public static final int PORT = 8080;

    @SuppressWarnings("OverlyBroadThrowsClause")
    public static void main(String[] args) throws Exception {
        final Frontend frontend = new Frontend();

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(frontend), "/login");

        final Server server = new Server(PORT);
        server.setHandler(context);

        server.start();
        server.join();
    }
}
