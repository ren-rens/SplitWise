package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRequestHandler {

    public ClientRequestHandler(UserRepository userRepository) {
        this.clientRepositories = new ConcurrentHashMap<>();
        this.clients = new ConcurrentHashMap<>();
        this.userRepository = userRepository;
    }

    public SplitWiseRepository getRepositoryForClient(String clientId) {
        return this.clientRepositories.computeIfAbsent(clientId,
            repository -> new SplitWiseRepository(this.userRepository));
    }

    public SplitWiseRepository getRepositoryForClient(SocketChannel channel) {
        String clientId = this.clients.computeIfAbsent(channel, client ->
            "client-" + Thread.currentThread());

        return getRepositoryForClient(clientId);
    }

    public Session getSessionForClient(SocketChannel channel) {
        return this.sessions.computeIfAbsent(channel, socketChannel -> new Session());
    }

    public void removeClient(SocketChannel channel) {
        this.sessions.remove(channel);

        String clientId = this.clients.remove(channel);
        if (clientId != null) {
            this.clientRepositories.remove(clientId);
        }
    }

    public String getClientId(SocketChannel channel) {
        return this.clients.get(channel);
    }

    private final Map<String, SplitWiseRepository> clientRepositories; // <clientId, repository>
    private final Map<SocketChannel, String> clients; // <client, clientId>

    /**
     * we use ConcurrentHashMap in order to ensure the thread-safety of the data
     **/
    private final Map<SocketChannel, Session> sessions = new ConcurrentHashMap<>();
    private final UserRepository userRepository;

}
