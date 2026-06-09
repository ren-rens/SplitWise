package bg.sofia.uni.fmi.mjt.splitwise.server.model.payments;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Notification implements Serializable {

    @Serial
    private static final long serialVersionUID = 4L;

    public Notification() {
        this.messages = new ArrayList<>();
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }

    public String getNotifications() {
        if (this.messages.isEmpty()) {
            return "No notifications to show.";
        }

        StringBuilder result = new StringBuilder("*** Notifications ***" + System.lineSeparator());
        for (String message : this.messages) {
            result.append(message).append(System.lineSeparator());
        }

        return result.toString();
    }

    public void clearNotifications() {
        this.messages.clear();
    }

    public boolean hasNotifications() {
        return !this.messages.isEmpty();
    }

    private final List<String> messages;

}
