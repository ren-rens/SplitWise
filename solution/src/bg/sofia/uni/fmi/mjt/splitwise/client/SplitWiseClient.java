package bg.sofia.uni.fmi.mjt.splitwise.client;

import bg.sofia.uni.fmi.mjt.splitwise.commands.constants.CommandConstants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SplitWiseClient {

    public SplitWiseClient(int port) {
        this.port = port;
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, this.port));
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);

            enterCommand(scanner, socketChannel);
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }

    private void enterCommand(Scanner scanner, SocketChannel socketChannel) throws IOException {
        showCommands();

        while (true) {
            System.out.print("Enter command: ");

            String message = scanner.nextLine();
            if (message.isBlank()) {
                continue;
            }

            if (message.equals(CommandConstants.LOGOUT)) {
                sendMessage(socketChannel, message);
                System.out.println("Disconnecting from server...");
                break;
            }

            sendMessage(socketChannel, message);
            String reply = receiveMessage(socketChannel);

            System.out.println("Server response: " + System.lineSeparator() + reply);
        }
    }

    private void showCommands() {
        System.out.println("Connected to the server.");
        System.out.println("Too see available commands write:");
        System.out.println("  help");
        System.out.println();
    }

    private void sendMessage(SocketChannel channel, String message) throws IOException {
        buffer.clear();
        buffer.put(message.getBytes(StandardCharsets.UTF_8));
        buffer.flip();

        channel.write(buffer);
    }

    private String receiveMessage(SocketChannel channel) throws IOException {
        buffer.clear();

        int bytesRead = channel.read(buffer);
        if (bytesRead < 0) {
            throw new IOException("Connection closed by server");
        }

        buffer.flip();

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        return new String(bytes, StandardCharsets.UTF_8);
    }

    private final int port;
    private static final String SERVER_HOST = "localhost";

    private static final int BUFFER_SIZE = 1024;
    private ByteBuffer buffer;

}
