import bg.sofia.uni.fmi.mjt.splitwise.server.SplitWiseServer;

public class ServerMain {

    public static void main(String[] args) {
        SplitWiseServer server = new SplitWiseServer(SERVER_PORT);

        System.out.println("Starting Music Streaming Server on port " + SERVER_PORT + "...");
        server.start();
    }

    private static final int SERVER_PORT = 8080;

}