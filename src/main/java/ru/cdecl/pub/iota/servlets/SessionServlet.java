package ru.cdecl.pub.iota.servlets;

import co.paralleluniverse.fibers.Suspendable;
import org.json.JSONWriter;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.services.AccountService;
import ru.cdecl.pub.iota.servlets.base.JsonApiServlet;

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
public class SessionServlet extends JsonApiServlet {

    @Inject
    AccountService accountService;

    @Override
    @Suspendable
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final JSONWriter jsonWriter = getJsonWriterForHttpResponse(resp);
        jsonWriter.object();

        final Long userId = getUserIdFromHttpSession(req.getSession(false));
        if (userId != null && accountService.isUserExistent(userId)) {
            jsonWriter.key("id").value(userId);
        }

        jsonWriter.endObject();
    }

    @Override
    @Suspendable
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final JSONWriter jsonWriter = getJsonWriterForHttpResponse(resp);
        jsonWriter.object();

        final HttpSession httpSession = req.getSession();
        // todo
        jsonWriter.key("status").value(HttpServletResponse.SC_NOT_IMPLEMENTED);

        jsonWriter.endObject();
    }

    @Override
    @Suspendable
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final JSONWriter jsonWriter = getJsonWriterForHttpResponse(resp);
        jsonWriter.object();

        final HttpSession httpSession = req.getSession();
        httpSession.invalidate();

        jsonWriter.endObject();
    }

}
