package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.commands.Command;
import bg.sofia.uni.fmi.mjt.splitwise.commands.creator.CommandCreator;
import bg.sofia.uni.fmi.mjt.splitwise.commands.executor.CommandExecutor;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplitWiseServer {

    public SplitWiseServer(int port) {
        this.port = port;
        this.clientRequestHandler = new ClientRequestHandler(this.userRepository);
        this.executorService = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            this.selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, this.selector);
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            this.isServerWorking = true;

            System.out.println("Server started on port " + port);

            while (this.isServerWorking) {
                try {
                    int readyChannels = this.selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }

                    processSelectedKeys();
                } catch (IOException e) {
                    System.out.println("Error occurred while processing client request: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to start server", e);
        } finally {
            stop();
        }
    }

    public void stop() {
        this.isServerWorking = false;
        if (this.executorService != null) {
            this.executorService.shutdown();
        }

        if (this.selector != null && this.selector.isOpen()) {
            this.selector.wakeup();
        }

        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                System.out.println("Error with closing selector occurred");
            }
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(SERVER_HOST, this.port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void processSelectedKeys() throws IOException {
        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();

            if (key.isReadable()) {
                handleReadableKey(key);
            } else if (key.isAcceptable()) {
                acceptConnection(key);
            }

            keyIterator.remove();
        }
    }

    private void handleReadableKey(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        String clientInput = getClientInput(clientChannel);

        if (clientInput == null) {
            this.clientRequestHandler.removeClient(clientChannel);
            clientChannel.close();
            return;
        }

        System.out.println("Received from: " + this.clientRequestHandler.getClientId(clientChannel) +
            ": " + clientInput);

        this.executorService.submit(() -> {
            try {
                processClientCommand(clientChannel, clientInput);
            } catch (IOException e) {
                System.out.println("Error processing command: " + e.getMessage());
            }
        });
    }

    private void processClientCommand(SocketChannel clientChannel, String clientInput)
        throws IOException {

        Session session = this.clientRequestHandler.getSessionForClient(clientChannel);
        Command command = CommandCreator.newCommand(clientInput);
        SplitWiseRepository clientRepository = this.clientRequestHandler.getRepositoryForClient(clientChannel);
        String output = CommandExecutor.execute(clientRepository, command, session);

        if (output.equals("disconnect")) {
            this.clientRequestHandler.removeClient(clientChannel);
            clientChannel.close();

            System.out.println("Client disconnected");
            return;
        }

        synchronized (clientChannel) {
            writeClientOutput(clientChannel, output);
        }
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        this.buffer.clear();

        int readBytes = clientChannel.read(this.buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        this.buffer.flip();

        byte[] clientInputBytes = new byte[this.buffer.remaining()];
        this.buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        this.buffer.clear();
        this.buffer.put(output.getBytes());
        this.buffer.flip();

        clientChannel.write(this.buffer);
    }

    private void acceptConnection(SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(this.selector, SelectionKey.OP_READ);

        String clientId = this.clientRequestHandler.getClientId(accept);
        System.out.println("New client connected: " + clientId);
    }

    private final int port;
    private final UserRepository userRepository = new UserRepository();
    private final ClientRequestHandler clientRequestHandler;
    private final ExecutorService executorService;

    private static final int BUFFER_SIZE = 1024;
    private static final int MAX_EXECUTOR_THREADS = 10;
    private static final String SERVER_HOST = "localhost";

    private boolean isServerWorking;
    private ByteBuffer buffer;
    private Selector selector;

}
