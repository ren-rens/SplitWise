package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

class ClientRequestHandlerTest {

    @BeforeEach
    void setUp() {
        this.mockUserRepository = mock(UserRepository.class);
        this.mockSocketChannel = mock(SocketChannel.class);
        this.handler = new ClientRequestHandler(mockUserRepository);
    }

    @Test
    void testGetRepositoryForClientByIdThatIsAbsent() {
        String clientId = "client-1";

        SplitWiseRepository repo1 = this.handler.getRepositoryForClient(clientId);
        SplitWiseRepository repo2 = this.handler.getRepositoryForClient(clientId);

        assertSame(repo1, repo2,
            "When testing getRepositoryForClient that was added before" +
                "should just return it from the data");
    }

    @Test
    void testGetRepositoryForClientByIdThatWasSavedBefore() {
        String clientId = "client-1";

        SplitWiseRepository repo1 = this.handler.getRepositoryForClient(clientId);

        assertNotNull(repo1,
            "When testing getRepositoryForClient that was not add before" +
                "should add it with new repository");
    }

    @Test
    void testGetRepositoryForClientByIdDifferentIdsMustReturnDifferentRepositories() {
        String clientId1 = "client-1";
        String clientId2 = "client-2";

        SplitWiseRepository repo1 = this.handler.getRepositoryForClient(clientId1);
        SplitWiseRepository repo2 = this.handler.getRepositoryForClient(clientId2);

        assertNotSame(repo1, repo2,
            "When testing getRepositoryForClient for different clients" +
                "should return different repositories");
    }

    @Test
    void testGetRepositoryForClientBySocketChannelThatIsAbsent() {
        SplitWiseRepository repo = this.handler.getRepositoryForClient(this.mockSocketChannel);

        assertNotNull(repo,
            "When testing getRepositoryForClient that was not add before" +
                "should add it with new repository");
        assertNotNull(this.handler.getClientId(this.mockSocketChannel),
            "Should create client ID for socket channel");
    }

    @Test
    void testGetRepositoryForClientBySocketChannelThatIsNotAbsent() {
        SplitWiseRepository repo1 = this.handler.getRepositoryForClient(this.mockSocketChannel);
        SplitWiseRepository repo2 = this.handler.getRepositoryForClient(this.mockSocketChannel);

        assertSame(repo1, repo2,
            "When testing getRepositoryForClient that was added before" +
                "should just return it from the data");
    }

    @Test
    void testGetSessionForClientBySocketChannelThatIsNotAbsent() {
        Session session1 = this.handler.getSessionForClient(this.mockSocketChannel);
        Session session2 = this.handler.getSessionForClient(this.mockSocketChannel);

        assertSame(session1, session2,
            "When testing getSession by socket channel that was saved in the data" +
                "should just return it");
    }

    @Test
    void testGetSessionBySocketChannelThatIsAbsent() {
        Session session1 = this.handler.getSessionForClient(this.mockSocketChannel);

        assertNotNull(session1,
            "When testing get session by socket channel that was not add before" +
                "should add it with new repository");    }

    @Test
    void testRemoveClientSuccessfully() {
        this.handler.getClientId(this.mockSocketChannel);
        this.handler.getSessionForClient(this.mockSocketChannel);
        this.handler.getRepositoryForClient(this.mockSocketChannel);

        this.handler.removeClient(this.mockSocketChannel);

        assertNull(this.handler.getClientId(this.mockSocketChannel),
            "When testing remove client the client id at the end should be null");
    }

    private UserRepository mockUserRepository;

    private SocketChannel mockSocketChannel;
    private ClientRequestHandler handler;

}