package simulator;

public class Command {

    public enum CommandName {
        mark, unmark, left, right, branch, stop;

        @Override
        public String toString() {
            switch(this) {
                case mark:
                    return "m";
                case unmark:
                    return "u";
                case left:
                    return "l";
                case right:
                    return "r";
                case branch:
                    return "b";
                default:
                    return "s";
            }
        }
    }

    private CommandName commandName;

    private int firstCommandNumber = 0;

    private int secondCommandNumber = 0;

    public int getSecondCommandNumber() {
        return secondCommandNumber;
    }

    public int getFirstCommandNumber() {
        return firstCommandNumber;
    }

    public CommandName getCommandName() {
        return commandName;
    }

    public Command(CommandName commandName, int firstCommandNumber, int secondCommandNumber) {
        if (commandName!= CommandName.branch) {
            throw new IllegalArgumentException("Wrong number of arguments for not branch command");
        }
        this.commandName = commandName;
        this.firstCommandNumber = firstCommandNumber;
        this.secondCommandNumber = secondCommandNumber;
    }

    public Command(CommandName commandName, int firstCommandNumber) {
        if (commandName == CommandName.branch || commandName == CommandName.stop) {
            throw new IllegalArgumentException("Wrong number of arguments for branch or stop command");
        }
        this.commandName = commandName;
        this.firstCommandNumber = firstCommandNumber;
    }

    public Command(CommandName commandName) {
        if (commandName!= CommandName.stop) {
            throw new IllegalArgumentException("Wrong number of arguments for not stop command");
        }
        this.commandName = commandName;
    }

    @Override
    public String toString() {
        return commandName.toString() + (firstCommandNumber != 0? firstCommandNumber:"") + (secondCommandNumber != 0? ";" + secondCommandNumber:"");
    }
}
