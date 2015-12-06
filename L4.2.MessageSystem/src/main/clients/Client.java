package main.clients;

import frontEnd.FrontEndService;
import gameMechanics.GameRules;
import main.ThreadSettings;

import java.util.Random;

/**
 * Created by esin on 06.12.2014.
 */
public class Client extends Thread {
    private static final Random RANDOM = new Random();
    private FrontEndService service;
    private String name;
    private String password;
    private String sessionId = "";

    private State state = State.REGISTER;

    public Client(FrontEndService service, String name) {
        this.service = service;
        this.name = name;
        this.password = name;
        setName(name);
    }

    @Override
    public void run() {
        while (state != State.FINISHED) {
            switch (state) {
                case REGISTER:
                    register();
                    break;
                case CHECK_REGISTRATION:
                    checkRegistration();
                    break;
                case AUTH:
                    auth();
                    break;
                case CHECK_AUTH:
                    checkAuth();
                    break;
                case GET_SCORE:
                    getScore();
                    break;
                case INCREASE_SCORE:
                    increaseScore();
                    break;
                case FINISHED:
                    break;
            }

            try {
                Thread.sleep(ThreadSettings.CLIENT_SLEEP_TIME + RANDOM.nextInt(ThreadSettings.CLIENT_SLEEP_TIME * 10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void register() {
        System.out.println(name + " is registering...");
        service.register(name, password);
        state = State.CHECK_REGISTRATION;
    }

    private void checkRegistration() {
        final boolean registered = service.isRegistered(name);
        System.out.println(name + " registered: " + registered);
        if (registered) {
            state = State.AUTH;
        }
    }

    private void auth() {
        System.out.println(name + " is authenticating...");
        sessionId = service.authenticate(name, password);
        System.out.println(name + " got sessionId: " + sessionId);
        state = State.CHECK_AUTH;
    }

    private void checkAuth() {
        System.out.println(name + " is checking auth for session " + sessionId);
        final boolean authenticated = service.isAuthenticated(sessionId);
        System.out.println(name + " is authenticated: " + authenticated);
        if (authenticated) {
            state = State.GET_SCORE;
        }
    }

    private void getScore() {
        final int score = service.getScore(sessionId);
        System.out.println(name + " score is " + score);

        if (score == GameRules.MAX_SCORE || score == GameRules.MIN_SCORE) {
            System.out.println("==== " + name + " finished with score " + score + " ====");
            state = State.FINISHED;
        } else {
            state = State.INCREASE_SCORE;
        }
    }

    private void increaseScore() {
        final int delta = RANDOM.nextInt(GameRules.MAX_SCORE) - (GameRules.MAX_SCORE / 2);
        System.out.println(name + " increasing score by " + delta);
        service.updateScore(sessionId, delta);
        state = State.GET_SCORE;
    }

    private enum State {
        REGISTER,
        CHECK_REGISTRATION,
        AUTH,
        CHECK_AUTH,
        GET_SCORE,
        INCREASE_SCORE,
        FINISHED
    }
}
