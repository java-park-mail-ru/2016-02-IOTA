package ru.cdecl.pub.iota.servlets;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.fibers.servlet.FiberHttpServlet;
import jdk.nashorn.internal.ir.debug.JSONWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.exceptions.UserAlreadyExistsException;
import ru.cdecl.pub.iota.exceptions.UserNotFoundException;
import ru.cdecl.pub.iota.models.UserProfile;
import ru.cdecl.pub.iota.services.AccountService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.NullPointerException;

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
        if (userId != null) {
            final UserProfile userProfile = accountService.getUserProfile(userId);

            final String email;
            final String login;
            if (userProfile == null) {

            }
            try {
                email = userProfile.getEmail();
                login = userProfile.getLogin();
            } catch (NullPointerException npe) {
                System.out.println("No user with user id: " + userId);
                resp.setStatus(RESP_STATUS_NOT_AUTHORIZED);
                resp.getWriter().write(EMPTY_RESPONSE);
                return;
            }
            final JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("id", userId);
            jsonResponse.put("email", email);
            jsonResponse.put("login", login);

            resp.setStatus(RESP_STATUS_OK);
            resp.getWriter().write(jsonResponse.toString());
            return;
        }
        resp.setStatus(RESP_STATUS_NOT_AUTHORIZED);
        resp.getWriter().write(EMPTY_RESPONSE);
    }

    @Override
    @Suspendable
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession(false);
        final Long userIdFromHttpRequest = getUserIdFromHttpRequest(req);
        final Long userIdFromHttpSession = getUserIdFromHttpSession(httpSession);
        if (userIdFromHttpRequest != null && userIdFromHttpRequest.equals(userIdFromHttpSession)) {
            final JSONTokener tokener = new JSONTokener(req.getInputStream());
            final JSONObject jsonRequest = new JSONObject(tokener);

            final String email = jsonRequest.get("email").toString();
            final String login = jsonRequest.get("login").toString();
            final String password = jsonRequest.get("password").toString();
            //Long newId = Long.parseLong("2");
            //UserProfile profile = new UserProfile(newId, "vasya", "vasya@asd.ru");
            //try{
            //    accountService.createUser(profile, "vasya".toCharArray());
            //} catch (UserAlreadyExistsException ex) {
            //    System.out.println("User already exists");
            //} FIXME testinfo
            final UserProfile newProfile = accountService.getUserProfile(userIdFromHttpRequest);
            if (newProfile == null) {
                System.out.println("Can not get profile from database: no user with id `" + userIdFromHttpRequest + "`found");
                resp.setStatus(RESP_STATUS_NOT_AUTHORIZED);
                resp.getWriter().write(EMPTY_RESPONSE);
                return;
            }
            newProfile.setEmail(email);
            newProfile.setLogin(login);
            try {
                accountService.editUser(userIdFromHttpRequest, newProfile, password.toCharArray());
            } catch (UserNotFoundException exNotFound) {
                System.out.println("Can not edit user: no user with login `" + login + "` found");
                resp.setStatus(RESP_STATUS_NOT_AUTHORIZED);
                resp.getWriter().write(EMPTY_RESPONSE);
                return;
            } catch (UserAlreadyExistsException exAlreadyExists) {
                System.out.println("Can not edit user: user `" + login + "` already exists");
                resp.setStatus(RESP_STATUS_NOT_AUTHORIZED);
                resp.getWriter().write(EMPTY_RESPONSE);
                return;
            }

            final JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("id", userIdFromHttpRequest);
            resp.setStatus(RESP_STATUS_OK);
            resp.getWriter().write(jsonResponse.toString());

        } else {
            resp.setStatus(RESP_STATUS_FORBIDDEN);

            final JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", RESP_STATUS_FORBIDDEN);
            jsonResponse.put("message", "Чужой юзер");
            resp.getWriter().write(jsonResponse.toString());
        }
    }

    @Override
    @Suspendable
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession(false);
        final Long userIdFromHttpRequest = getUserIdFromHttpRequest(req);
        final Long userIdFromHttpSession = getUserIdFromHttpSession(httpSession);
        if (userIdFromHttpRequest != null && userIdFromHttpRequest.equals(userIdFromHttpSession)) {
            try {
                accountService.deleteUser(userIdFromHttpRequest);
            } catch (UserNotFoundException ignored) {
                System.out.println("Can not delete user: no user with id `" + userIdFromHttpRequest + "`found");
                resp.setStatus(RESP_STATUS_FORBIDDEN);

                final JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("status", RESP_STATUS_FORBIDDEN);
                jsonResponse.put("message", "Чужой юзер");
                resp.setStatus(RESP_STATUS_FORBIDDEN);
                resp.getWriter().write(jsonResponse.toString());
                return;
            }
            resp.setStatus(RESP_STATUS_OK);
            resp.getWriter().write(EMPTY_RESPONSE);
        } else {
            final JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", RESP_STATUS_FORBIDDEN);
            jsonResponse.put("message", "Чужой юзер");
            resp.setStatus(RESP_STATUS_FORBIDDEN);
            resp.getWriter().write(jsonResponse.toString());
        }
        //
    }

    @Nullable
    private static Long getUserIdFromHttpRequest(@NotNull HttpServletRequest req) {
        final String requestUri = req.getRequestURI();
        try {
            return Long.parseLong(requestUri.substring(requestUri.lastIndexOf('/') + 1));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Nullable
    private static Long getUserIdFromHttpSession(@Nullable HttpSession sess) {
        if (sess == null) {
            return null;
        }
        final Object userIdAttribute = sess.getAttribute("user_id");
        return (userIdAttribute instanceof Long)
                ? (Long) userIdAttribute
                : null;
    }

    private static final int RESP_STATUS_OK = 200;
    private static final int RESP_STATUS_NOT_AUTHORIZED = 401;
    private static final int RESP_STATUS_FORBIDDEN = 403;
    private static final String EMPTY_RESPONSE = "{}";
}
