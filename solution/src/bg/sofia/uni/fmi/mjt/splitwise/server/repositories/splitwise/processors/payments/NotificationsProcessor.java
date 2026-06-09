package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors.payments;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;

public class NotificationsProcessor {

    public static void updateNotificationsWhenSplitting(User friend, String groupName, double amount,
                                                         String username, String reasonForPayment) {
        String notification = getNotificationMessagesForSplitting(groupName, username, amount, reasonForPayment);
        friend.addMessageToNotifications(notification);
    }

    private static String getNotificationMessagesForSplitting(String groupName, String username,
                                                              double amount, String reasonForPayment) {
        if (groupName == null) {
            return String.format("%s split %.2f LV with you [%s]",
                username, amount, reasonForPayment);
        } else {
            return String.format("In group %s friend %s split %.2f LV with you [%s]",
                groupName, username, amount, reasonForPayment);
        }
    }

    public static void updateNotificationWhenPaying(User friend,
                                                     String groupName, double amount, String username) {
        String notification = createPaymentNotification(groupName, amount, username);
        friend.addMessageToNotifications(notification);
    }

    private static String createPaymentNotification(String groupName, double amount, String username) {
        if (groupName == null) {
            return String.format("%s accepted your payment %.2f LV", username, amount);
        } else {
            return String.format("In group %s user %s accepted your payment %.2f LV",
                groupName, username, amount);
        }
    }

}
