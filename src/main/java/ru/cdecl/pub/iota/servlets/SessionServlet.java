package ru.cdecl.pub.iota.servlets;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.fibers.servlet.FiberHttpServlet;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.services.AccountService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Service
@Singleton
@WebServlet(asyncSupported = true)
public class SessionServlet extends FiberHttpServlet {

    @Inject
    AccountService accountService;

    @Override
    @Suspendable
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession(false);
        //
        try {
            Fiber.sleep(1000);
        } catch (InterruptedException | SuspendExecution ignored) {
        }
        resp.getWriter().println(this.getClass().getCanonicalName());
    }

    @Override
    @Suspendable
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        //
        super.doPut(req, resp);
    }

    @Override
    @Suspendable
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        httpSession.invalidate();
        //
        super.doDelete(req, resp);
    }

}
