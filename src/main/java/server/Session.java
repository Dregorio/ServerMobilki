package server;

import java.io.Serializable;

public class Session implements Serializable {

    private boolean isSession = false;

    public boolean isSession() {
        return isSession;
    }

    public void setSession(boolean session) {
        isSession = session;
    }
}
