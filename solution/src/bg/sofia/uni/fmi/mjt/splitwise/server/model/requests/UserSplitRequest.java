package bg.sofia.uni.fmi.mjt.splitwise.server.model.requests;

public record UserSplitRequest(String loggedUsername,
                               double amount, String targetId, String reasonForPayment,
                               boolean isGroup) {
}
