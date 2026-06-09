package bg.sofia.uni.fmi.mjt.splitwise.commands.creator;

import bg.sofia.uni.fmi.mjt.splitwise.commands.Command;
import bg.sofia.uni.fmi.mjt.splitwise.commands.constants.CommandConstants;
import bg.sofia.uni.fmi.mjt.splitwise.commands.constants.ExecutorConstants;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandCreator {

    public static Command newCommand(String clientInput) {
        String[] splitStrs = clientInput.trim().split("\\s+");

        if (splitStrs.length == 0) {
            return new Command("", new String[0]);
        }

        String commandName = splitStrs[0];
        String[] commandArgs = getCommandArgs(commandName, splitStrs);

        return new Command(commandName, commandArgs);
    }

    private static String[] getCommandArgs(String commandName, String[] splitStrs) {
        String[] commandArgs;

        if (commandName.equals(CommandConstants.SPLIT) ||
            commandName.equals(CommandConstants.SPLIT_GROUP)) {
            commandArgs = new String[ExecutorConstants.SPLIT_ARGUMENTS_COUNT];
            String reasonForPayment = Arrays
                .stream(splitStrs)
                .skip(ExecutorConstants.THREE_IDX)
                .collect(Collectors.joining(" "));

            commandArgs[ExecutorConstants.ZERO_IDX] = splitStrs[ExecutorConstants.ONE_IDX];
            commandArgs[ExecutorConstants.ONE_IDX] = splitStrs[ExecutorConstants.TWO_IDX];
            commandArgs[ExecutorConstants.TWO_IDX] = reasonForPayment;
        } else {
            commandArgs = new String[splitStrs.length - 1];
            System.arraycopy(splitStrs, 1, commandArgs, 0, commandArgs.length);
        }

        return commandArgs;
    }

}
