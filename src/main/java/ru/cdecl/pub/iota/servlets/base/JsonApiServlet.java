package ru.cdecl.pub.iota.servlets.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public abstract class JsonApiServlet extends HttpServlet {

    @Nullable
    protected static Long getUserIdFromHttpSession(@Nullable HttpSession sess) {
        if (sess == null) {
            return null;
        }
        final Object userIdAttribute = sess.getAttribute("user_id");
        return (userIdAttribute instanceof Long)
                ? (Long) userIdAttribute
                : null;
    }

    protected static JSONObject getJsonObjectFromHttpRequest(HttpServletRequest req) throws IOException {
        return new JSONObject(new JSONTokener(req.getInputStream()));
    }

    protected static JSONWriter getJsonWriterForHttpResponse(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        return new JSONWriter(resp.getWriter());
    }

}
