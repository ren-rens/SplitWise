package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.IncorrectPasswordForUserNameException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;

public class LoginProcessor {

    public static String loginUser(UserRepository userRepository, String username,
                                   String password, Session session) throws
        UserWithThisUsernameDoesNotExistException, IncorrectPasswordForUserNameException {
        User user = userRepository.findUserByUsername(username);
        SplitWiseValidator.validateUserExists(user);

        SplitWiseValidator.validatePasswordCorrectness(user, password);

        session.login(username);

        /**
         * Every time a user logs into the system, they receive notifications
         * if their friends have added new amounts or "repaid" debts while the user was not logged in.
         * But after seeing those notifications they should be cleared from the users notifications
         * because we print only new notifications
         */
        String notifications = printNotifications(user);

        user.clearNotifications();
        userRepository.updateUsers(user);

        return notifications;
    }

    private static String printNotifications(User user) {
        Notification notification = user.getNotifications();

        return (notification == null || !notification.hasNotifications()) ?
            ("No notifications to show." + System.lineSeparator()) : notification.getNotifications();
    }

}
