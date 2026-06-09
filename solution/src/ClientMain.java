import bg.sofia.uni.fmi.mjt.splitwise.client.SplitWiseClient;

public class ClientMain {

    public static void main(String[] args) {
        SplitWiseClient client = new SplitWiseClient(SERVER_PORT);
        client.start();
    }

    private static final int SERVER_PORT = 8080;
}