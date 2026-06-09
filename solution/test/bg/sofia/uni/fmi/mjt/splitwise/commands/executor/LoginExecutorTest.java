package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.IncorrectPasswordForUserNameException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginExecutorTest {

    @BeforeEach
    void setUp() {
        this.mockRepository = mock(SplitWiseRepository.class);
        this.mockSession = mock(Session.class);
    }

    @Test
    void testLoginWithIncorrectAmountOfArguments() throws Exception {
        String[] wrongArguments = new String[0];

        String result = LoginExecutor.execute(this.mockRepository, wrongArguments, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to log in with incorrect data should return error message: " + result);

        verify(this.mockRepository, never()).loginUser(any(), any(), any());
    }

    @Test
    void testLoginUserSuccessfully() throws Exception {
        String[] args = {USERNAME, PASSWORD};
        String notifications = "No notifications to show.";

        when(this.mockSession.isLoggedIn()).thenReturn(false);
        when(this.mockRepository.loginUser(USERNAME, PASSWORD, this.mockSession))
            .thenReturn(notifications);

        String result = LoginExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(OK_MESSAGE),
            "When logging in successfully should return successful message: " + result);

        verify(this.mockRepository).loginUser(USERNAME, PASSWORD, this.mockSession);
    }

    @Test
    void testLoginUserWithUserDoesNotExist() throws Exception {
        String nonExistentUsername = "nonExistent";
        String[] args = {nonExistentUsername, PASSWORD};

        when(this.mockSession.isLoggedIn()).thenReturn(false);
        when(this.mockRepository.loginUser(nonExistentUsername, PASSWORD, this.mockSession))
            .thenThrow(UserWithThisUsernameDoesNotExistException.class);

        String result = LoginExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to login with non-existent username in thr system" +
            "should return error message: " + result);
    }

    @Test
    void testLoginUserWithWrongPassword() throws Exception {
        String wrongPassword = "wrongPass";
        String[] args = {USERNAME, wrongPassword};

        when(this.mockSession.isLoggedIn()).thenReturn(false);
        when(this.mockRepository.loginUser(USERNAME, wrongPassword, this.mockSession))
            .thenThrow(IncorrectPasswordForUserNameException.class);

        String result = LoginExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to login with wrong password should return error message: " + result);
    }

    @Test
    void testLoginWhenAlreadyLoggedIn() throws Exception {
        String[] args = {USERNAME, PASSWORD};

        when(this.mockSession.isLoggedIn()).thenReturn(true);

        String result = LoginExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to login but the user is already logged in" +
                "should return error message: " + result);
        verify(this.mockRepository, never()).loginUser(any(), any(), any());
    }

    private SplitWiseRepository mockRepository;
    private Session mockSession;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String ERROR_MESSAGE = "ERROR";
    private static final String OK_MESSAGE = "OK";

}