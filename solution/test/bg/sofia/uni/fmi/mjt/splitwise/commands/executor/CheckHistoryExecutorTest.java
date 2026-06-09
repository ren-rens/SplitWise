package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWIthThisUsernameDoesNotHaveAnyPaymentHistory;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CheckHistoryExecutorTest {

    @BeforeEach
    void setUp() {
        this.mockRepository = mock(SplitWiseRepository.class);
        this.mockSession = mock(Session.class);
    }

    @Test
    void testCheckHistoryWithNotLoggedInUser()
        throws UserWithThisUsernameDoesNotExistException, UserWIthThisUsernameDoesNotHaveAnyPaymentHistory {
        when(this.mockSession.isLoggedIn()).thenReturn(false);

        String result = CheckHistoryExecutor.execute(this.mockRepository, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to check history of transactions but the user is not logged in" +
                "should return error message: " + result);

        verify(this.mockRepository, never()).checkHistory(any());
    }

    @Test
    void testExecuteWithEmptyHistory() throws Exception {
        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(USERNAME);
        when(this.mockRepository.checkHistory(USERNAME))
            .thenThrow(UserWIthThisUsernameDoesNotHaveAnyPaymentHistory.class);

        String result = CheckHistoryExecutor.execute(this.mockRepository, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to check history of transactions but it is empty" +
                "should return error message");
        verify(this.mockRepository).checkHistory(USERNAME);
    }

    @Test
    void testCheckHistoryWithUserDoesNotExist() throws Exception {
        String nonExistentUsername = "nonExistent";

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(nonExistentUsername);
        when(this.mockRepository.checkHistory(nonExistentUsername))
            .thenThrow(UserWithThisUsernameDoesNotExistException.class);

        String result = CheckHistoryExecutor.execute(this.mockRepository, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to check history of transactions of non-existent user" +
                "should return error message: " + result);

        verify(this.mockRepository).checkHistory(nonExistentUsername);
    }

    @Test
    void testCheckHistoryWithValidHistory() throws Exception {
        String validHistory = "You split 50.00 each with friend [dinner]";
        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(USERNAME);
        when(this.mockRepository.checkHistory(USERNAME)).thenReturn(validHistory);

        String result = CheckHistoryExecutor.execute(this.mockRepository, this.mockSession);

        assertEquals(validHistory, result,
            "When testing check history of transactions with some saved history should return it");
        verify(this.mockRepository).checkHistory(USERNAME);
    }

    private SplitWiseRepository mockRepository;
    private Session mockSession;

    private static final String USERNAME = "username";
    private static final String ERROR_MESSAGE = "ERROR";

}
