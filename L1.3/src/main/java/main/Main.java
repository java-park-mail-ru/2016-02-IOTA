package main;

import frontend.SignInServlet;
import frontend.SignUpServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.Servlet;

/**
 * @author esin88
 */
public class Main {
    @SuppressWarnings("OverlyBroadThrowsClause")
    public static void main(String[] args) throws Exception {
        int port = -1;
        if (args.length == 1) {
            port = Integer.valueOf(args[0]);
        } else {
            System.err.println("Specify port");
            System.exit(1);
        }

        System.out.append("Starting at port: ").append(String.valueOf(port)).append('\n');

        final AccountService accountService = new AccountService();

        final Servlet signin = new SignInServlet(accountService);
        final Servlet signUp = new SignUpServlet(accountService);

        final ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.addServlet(new ServletHolder(signin), "/api/v1/auth/signin");
        contextHandler.addServlet(new ServletHolder(signUp), "/api/v1/auth/signup");

        final Server server = new Server(port);
        server.setHandler(contextHandler);

        server.start();
        server.join();
    }
}