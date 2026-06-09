package bg.sofia.uni.fmi.mjt.splitwise.server.model.requests;

public record FriendSplitRequest(String friendUsername, String group, double amount,
                                 String username, String reasonForPayment) {
}
