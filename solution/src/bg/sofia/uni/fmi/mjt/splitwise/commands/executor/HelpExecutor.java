package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

public class HelpExecutor {

    public static String execute() {
        return "registration <username> <password>" + System.lineSeparator() +
            "login <username> <password>" + System.lineSeparator() +
            "add-friend <username>" + System.lineSeparator() +
            "create-group <group_name> <username> <username> ... <username>" + System.lineSeparator() +
            "split <amount> <username> <reason_for_payment>" + System.lineSeparator() +
            "split-group <amount> <group_name> <reason_for_payment>" + System.lineSeparator() +
            "get-status" + System.lineSeparator() +
            "payed <amount> <username>" + System.lineSeparator() +
            "check-history" + System.lineSeparator() +
            "get-friends" + System.lineSeparator() +
            "get-groups" + System.lineSeparator();
    }

}
