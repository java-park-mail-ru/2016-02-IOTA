package ru.cdecl.pub.iota.servlets;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.fibers.servlet.FiberHttpServlet;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.services.AccountService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@Singleton
@WebServlet(asyncSupported = true)
public final class ConcreteUserServlet extends FiberHttpServlet {

    @Inject
    AccountService accountService;

    @Override
    @Suspendable
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Long userId = getUserIdFromHttpRequest(req);
        try {
            Fiber.sleep(1000);
        } catch (InterruptedException | SuspendExecution ignored) {
        }
        resp.getWriter().println(this.getClass().getCanonicalName() + " : " + String.valueOf(userId));
    }

    @Nullable
    private static Long getUserIdFromHttpRequest(HttpServletRequest req) {
        final String requestUri = req.getRequestURI();
        try {
            return Long.parseLong(requestUri.substring(requestUri.lastIndexOf('/') + 1));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return null;
        }
    }

}
