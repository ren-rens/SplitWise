package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserDataMustContainAtLeastOneNonBlankSymbolException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUserNameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegistrationExecutorTest {

    @BeforeEach
    void setUp() {
        this.mockRepository = mock(SplitWiseRepository.class);
        this.mockSession = mock(Session.class);
    }

    @Test
    void testRegisterUserWithIncorrectAmountOfArguments()
        throws UserWithThisUserNameExistsException, UserDataMustContainAtLeastOneNonBlankSymbolException {
        String[] args = {};

        String result = RegistrationExecutor.execute(mockRepository, args, mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing register user with incorrect amount of arguments" +
                "should return error message:" + result);
        verify(mockRepository, never()).registerUser(any(), any());
    }

    @Test
    void testRegisterUserWhenUserAlreadyLoggedIn()
        throws UserWithThisUserNameExistsException, UserDataMustContainAtLeastOneNonBlankSymbolException {
        String[] arguments = {USERNAME, PASSWORD};
        when(mockSession.isLoggedIn()).thenReturn(true);

        String result = RegistrationExecutor.execute(mockRepository, arguments, mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing register user with already logged in user" +
                "should return error message:" + result);
        verify(mockRepository, never()).registerUser(any(), any());
    }

    @Test
    void testRegisterUserSuccessfulRegistration()
        throws UserWithThisUserNameExistsException, UserDataMustContainAtLeastOneNonBlankSymbolException {
        String[] arguments = {USERNAME, PASSWORD};
        when(mockSession.isLoggedIn()).thenReturn(false);

        String result = RegistrationExecutor.execute(mockRepository, arguments, mockSession);

        assertTrue(result.contains(OK_MESSAGE),
            "When testing register user with valid data" +
                "should return successful message:" + result);
        verify(mockRepository).registerUser(USERNAME, PASSWORD);
    }

    @Test
    void testRegisterUserWhenUserAlreadyExists()
        throws UserWithThisUserNameExistsException, UserDataMustContainAtLeastOneNonBlankSymbolException {
        String[] arguments = {USERNAME, PASSWORD};
        when(mockSession.isLoggedIn()).thenReturn(false);

        doThrow(UserWithThisUserNameExistsException.class)
            .when(mockRepository).registerUser(USERNAME, PASSWORD);

        String result = RegistrationExecutor.execute(mockRepository, arguments, mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing register user with username that is already saved in the system" +
                "should return error message:" + result);
        verify(mockRepository).registerUser(USERNAME, PASSWORD);
    }

    @Test
    void testRegisterUserWithBlankUsername()
        throws UserDataMustContainAtLeastOneNonBlankSymbolException, UserWithThisUserNameExistsException {
        String[] arguments = {"", PASSWORD};
        when(mockSession.isLoggedIn()).thenReturn(false);

        doThrow(UserDataMustContainAtLeastOneNonBlankSymbolException.class)
            .when(mockRepository).registerUser("", PASSWORD);

        String result = RegistrationExecutor.execute(mockRepository, arguments, mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing register user with empty username should return error message: " + result);
        verify(mockRepository).registerUser("", PASSWORD);
    }

    @Test
    void testRegisterUserWithBlankPassword()
        throws UserDataMustContainAtLeastOneNonBlankSymbolException, UserWithThisUserNameExistsException {
        String[] arguments = {USERNAME, ""};
        when(mockSession.isLoggedIn()).thenReturn(false);

        doThrow(UserDataMustContainAtLeastOneNonBlankSymbolException.class)
            .when(mockRepository).registerUser(USERNAME, "");

        String result = RegistrationExecutor.execute(mockRepository, arguments, mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing register user with empty password should return error message: " + result);
        verify(mockRepository).registerUser(USERNAME, "");
    }

    @Test
    void testRegisterUserWithNullUsername() throws Exception {
        String[] arguments = {null, PASSWORD};
        when(mockSession.isLoggedIn()).thenReturn(false);

        doThrow(UserDataMustContainAtLeastOneNonBlankSymbolException.class)
            .when(mockRepository).registerUser(null, PASSWORD);

        String result = RegistrationExecutor.execute(mockRepository, arguments, mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing register user with null password should return error message: " + result);
        verify(mockRepository).registerUser(null, PASSWORD);
    }

    @Test
    void testRegisterUserWithNullPassword() throws Exception {
        String[] arguments = {USERNAME, null};
        when(mockSession.isLoggedIn()).thenReturn(false);

        doThrow(UserDataMustContainAtLeastOneNonBlankSymbolException.class)
            .when(mockRepository).registerUser(USERNAME, null);

        String result = RegistrationExecutor.execute(mockRepository, arguments, mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing register user with null password should return error message: " + result);
        verify(mockRepository).registerUser(USERNAME, null);
    }

    private SplitWiseRepository mockRepository;
    private Session mockSession;

    private static final String ERROR_MESSAGE = "ERROR";
    private static final String OK_MESSAGE = "OK";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

}