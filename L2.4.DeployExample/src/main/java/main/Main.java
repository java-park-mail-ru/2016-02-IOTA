package main;

import base.AuthService;
import base.GameMechanics;
import base.WebSocketService;
import frontend.AuthServiceImpl;
import frontend.GameServlet;
import frontend.WebSocketGameServlet;
import frontend.WebSocketServiceImpl;
import mechanics.GameMechanicsImpl;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {
        int port = DEFAULT_PORT;

        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }

        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        WebSocketService webSocketService = new WebSocketServiceImpl();
        GameMechanics gameMechanics = new GameMechanicsImpl(webSocketService);
        AuthService authService = new AuthServiceImpl();

        //for game example
        context.addServlet(new ServletHolder(new WebSocketGameServlet(authService, gameMechanics, webSocketService)), "/gameplay");
        context.addServlet(new ServletHolder(new GameServlet(authService)), "/game.html");

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase("static");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, context});
        server.setHandler(handlers);

        server.setHandler(handlers);

        server.start();

        //run GM in main thread
        gameMechanics.run();
    }
}
