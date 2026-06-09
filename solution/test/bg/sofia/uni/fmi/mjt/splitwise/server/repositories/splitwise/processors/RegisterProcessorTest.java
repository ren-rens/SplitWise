package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserDataMustContainAtLeastOneNonBlankSymbolException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUserNameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegisterProcessorTest {

    @BeforeEach
    void setUp() {
        this.mockUserRepository = mock(UserRepository.class);
        this.splitWiseRepository = new SplitWiseRepository(this.mockUserRepository);
    }

    @Test
    void testRegisterUserWithExistentUser() {
        when(this.mockUserRepository.doesUserExist(USERNAME)).thenReturn(true);

        assertThrows(UserWithThisUserNameExistsException.class, () ->
                this.splitWiseRepository.registerUser(USERNAME, PASSWORD),
            "When trying to register a user with a username that is already saved in the system" +
                "should throw UserWithThisUserNameExistsException");

        verify(this.mockUserRepository).doesUserExist(USERNAME);
    }

    @Test
    void testRegisterUserWithValidData() {
        when(this.mockUserRepository.doesUserExist(USERNAME)).thenReturn(false);

        assertDoesNotThrow(() ->
                this.splitWiseRepository.registerUser(USERNAME, PASSWORD),
            "When trying to register a user with valid data" +
                "should just save the user in the system and return");

        verify(this.mockUserRepository).doesUserExist(USERNAME);
        verify(this.mockUserRepository).saveUser(any(User.class));
    }

    @Test
    void testRegisterUserWithNullUsername() {
        when(this.mockUserRepository.doesUserExist(USERNAME)).thenReturn(false);

        assertThrows(UserDataMustContainAtLeastOneNonBlankSymbolException.class, () ->
                this.splitWiseRepository.registerUser(null, PASSWORD),
            "When trying to register a user with NULL username" +
                "should throw UserDataMustContainAtLeastOneNonBlankSymbolException");

        verify(this.mockUserRepository).doesUserExist(null);
        verify(this.mockUserRepository, never()).saveUser(any(User.class));
    }

    @Test
    void testRegisterUserWithBlankUsername() {
        String blankUsername = "    ";
        when(this.mockUserRepository.doesUserExist(USERNAME)).thenReturn(false);

        assertThrows(UserDataMustContainAtLeastOneNonBlankSymbolException.class, () ->
                this.splitWiseRepository.registerUser(blankUsername, PASSWORD),
            "When trying to register a user with BLANK username" +
                "should throw UserDataMustContainAtLeastOneNonBlankSymbolException");

        verify(this.mockUserRepository).doesUserExist(blankUsername);
        verify(this.mockUserRepository, never()).saveUser(any(User.class));
    }

    private UserRepository mockUserRepository;

    private SplitWiseRepository splitWiseRepository;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

}
