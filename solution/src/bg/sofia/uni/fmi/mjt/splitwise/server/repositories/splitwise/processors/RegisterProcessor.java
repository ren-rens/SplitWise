package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserDataMustContainAtLeastOneNonBlankSymbolException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUserNameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;

public class RegisterProcessor {

    public static void registerUser(UserRepository userRepository, String username, String password)
        throws UserDataMustContainAtLeastOneNonBlankSymbolException, UserWithThisUserNameExistsException {
        SplitWiseValidator.validateUserNonExistenceByUsername(userRepository, username);
        SplitWiseValidator.validateUserDataCorrectness(username, password);

        User user = new User(username, password);
        userRepository.saveUser(user);
    }

}
