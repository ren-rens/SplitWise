package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.processors;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;

import java.util.Map;

public class UserProcessor {

    public static void loadUsersFromFile(String usersFile, Map<String, User> registeredUsers) {
        RepositoryProcessor.loadFromFile(usersFile, registeredUsers);
    }

    public static void saveUser(String usersFile, Map<String, User> registeredUsers, User user) {
        RepositoryProcessor.updateInFile(usersFile, registeredUsers, user.getUsername(), user);
    }

    public static boolean doesUserExist(Map<String, User> registeredUsers, String username) {
        return RepositoryProcessor.existsInMap(registeredUsers, username);
    }

    public static User findUserByUsername(Map<String, User> registeredUsers, String username) {
        return RepositoryProcessor.getFromMap(registeredUsers, username);
    }

    public static void updateUsers(String usersFile, Map<String, User> registeredUsers, User user) {
        RepositoryProcessor.updateInFile(usersFile, registeredUsers, user.getUsername(), user);
    }

}