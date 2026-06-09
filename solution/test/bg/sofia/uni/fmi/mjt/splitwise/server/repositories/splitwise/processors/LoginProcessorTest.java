package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.IncorrectPasswordForUserNameException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginProcessorTest {

    @BeforeEach
    void setUp() {
        this.mockUser = mock(User.class);
        this.mockSession = mock(Session.class);
        this.mockUserRepository = mock(UserRepository.class);
        this.splitWiseRepository = new SplitWiseRepository(this.mockUserRepository);
    }

    @Test
    void testLoginUserSuccessfullyWithNotifications() throws Exception {
        when(this.mockUserRepository.findUserByUsername(USERNAME)).thenReturn(mockUser);
        when(this.mockUser.getPassword()).thenReturn(PASSWORD);

        Notification notification = new Notification();
        notification.addMessage(NOTIFICATIONS);

        when(this.mockUser.getNotifications()).thenReturn(notification);

        String result = splitWiseRepository.loginUser(USERNAME, PASSWORD, this.mockSession);

        assertTrue(result.contains(NOTIFICATIONS));
        verify(this.mockSession).login(USERNAME);
        verify(this.mockUser).clearNotifications();
        verify(this.mockUser).getNotifications();
        verify(this.mockUserRepository).updateUsers(this.mockUser);
    }

    @Test
    void testLoginUserSuccessfullyWithNoNotifications() throws Exception {
        when(this.mockUserRepository.findUserByUsername(USERNAME)).thenReturn(mockUser);
        when(this.mockUser.getPassword()).thenReturn(PASSWORD);

        Notification notification = new Notification();
        when(this.mockUser.getNotifications()).thenReturn(notification);

        String result = splitWiseRepository.loginUser(USERNAME, PASSWORD, this.mockSession);

        assertTrue(result.contains(NOTIFICATIONS));
        verify(this.mockSession).login(USERNAME);
        verify(this.mockUser).clearNotifications();
        verify(this.mockUser).getNotifications();
        verify(this.mockUserRepository).updateUsers(this.mockUser);
    }

    @Test
    void testLoginUserWithUserDoesNotExist() {
        String username = "nonExistent";
        when(this.mockUserRepository.findUserByUsername(username)).thenReturn(null);

        assertThrows(UserWithThisUsernameDoesNotExistException.class,
            () -> this.splitWiseRepository.loginUser(username, PASSWORD, this.mockSession));
    }

    @Test
    void testLoginUserWithWrongPassword() {
        String wrongPassword = "wrongPass";

        when(this.mockUserRepository.findUserByUsername(USERNAME)).thenReturn(this.mockUser);
        when(this.mockUser.getPassword()).thenReturn(PASSWORD);

        assertThrows(IncorrectPasswordForUserNameException.class,
            () -> this.splitWiseRepository.loginUser(USERNAME, wrongPassword, this.mockSession));
    }

    private UserRepository mockUserRepository;

    private Session mockSession;

    private User mockUser;

    private SplitWiseRepository splitWiseRepository;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String NOTIFICATIONS = "No notifications to show.";

}
