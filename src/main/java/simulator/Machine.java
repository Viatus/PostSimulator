package simulator;

public class Machine {

    private final static int ADDRESS_BITS_PER_WORD = 6;
    private final static int BITS_PER_WORD = 1 << ADDRESS_BITS_PER_WORD;

    private Command[] commands;

    private long[] tape;

    private int currentCommandNumber;
    private int currentCarriageNumber;

    public long[] getTape() {
        return tape;
    }

    public Machine() {
        tape = new long[1];
        currentCarriageNumber = 9;
    }

    public Command[] parseCommandsList(String[] listOfCommands) {
        Command[] parsedCommands = new Command[listOfCommands.length];
        for (String line : listOfCommands) {
            if (!line.matches("\\d+\\. *(([mulrMULR] *\\d*)|([Ss])|([bB] *\\d+ *; *\\d+))")) {
                throw new IllegalArgumentException("Wrong command structure");
            }

            Command command;

            int number = Integer.parseInt(line.substring(0, line.indexOf(".")));
            line = line.substring(line.indexOf(".") + 1);
            line = line.trim();

            char commandAlias = line.charAt(0);
            commandAlias = Character.toLowerCase(commandAlias);

            if (line.length() > 1) {
                line = line.substring(1);
                line = line.trim();
            } else {
                line = "";
            }
            switch (commandAlias) {
                case 'u':
                    if (line.isEmpty()) {
                        command = new Command(Command.CommandName.unmark, number + 1);
                    } else {
                        command = new Command(Command.CommandName.unmark, Integer.parseInt(line));
                    }
                    break;
                case 'm':
                    if (line.isEmpty()) {
                        command = new Command(Command.CommandName.mark, number + 1);
                    } else {
                        command = new Command(Command.CommandName.mark, Integer.parseInt(line));
                    }
                    break;
                case 'l':
                    if (line.isEmpty()) {
                        command = new Command(Command.CommandName.left, number + 1);
                    } else {
                        command = new Command(Command.CommandName.left, Integer.parseInt(line));
                    }
                    break;
                case 'r':
                    if (line.isEmpty()) {
                        command = new Command(Command.CommandName.right, number + 1);
                    } else {
                        command = new Command(Command.CommandName.right, Integer.parseInt(line));
                    }
                    break;
                case 'b':
                    String[] parts = line.split("[ ;]+");
                    command = new Command(Command.CommandName.branch, Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                    break;
                default:
                    command = new Command(Command.CommandName.stop);
                    break;
            }
            parsedCommands[number - 1] = command;
        }
        return parsedCommands;
    }

    public void setProgramm(String[] listOfCommands) {
        commands = parseCommandsList(listOfCommands);
        currentCommandNumber = 0;
    }

    private static int wordIndex(int bitIndex) {
        return bitIndex >> ADDRESS_BITS_PER_WORD;
    }

    public boolean executeProgram() {
        while (commands[currentCommandNumber].getCommandName() != Command.CommandName.stop) {
            if (!executeStep()) {
                return false;
            }
        }
        return true;
    }

    public boolean executeStep() {
        return true;
    }

    private boolean executeMark(Command command) {
        int wordIndex = wordIndex(currentCarriageNumber);
        tape[wordIndex] |= (1L << currentCarriageNumber);
        currentCommandNumber = command.getFirstCommandNumber();
        return true;
    }

    private boolean executeUnMark(Command command) {
        int wordIndex = wordIndex(currentCarriageNumber);
        tape[wordIndex] &= ~(1L << currentCarriageNumber);
        currentCommandNumber = command.getFirstCommandNumber();
        return true;
    }

    private boolean executeLeft(Command command) {
        if (currentCarriageNumber == 0) {
            long[] tapeCopy = tape;
            tape = new long[tape.length * 2];
            int i = tapeCopy.length;
            for (long word : tapeCopy) {
                tape[i++] = word;
            }
        }
        currentCarriageNumber--;
        currentCommandNumber = command.getFirstCommandNumber();
        return true;
    }

    private boolean executeRight(Command command) {
        if (currentCarriageNumber == tape.length) {
            long[] tapeCopy = tape;
            tape = new long[tape.length * 2];
            int i = 0;
            for (long word : tapeCopy) {
                tape[i++] = word;
            }
        }
        currentCarriageNumber--;
        currentCommandNumber = command.getFirstCommandNumber();
        return true;
    }

    private boolean executeBranch(Command command) {
        if (command.getFirstCommandNumber() > tape.length<<ADDRESS_BITS_PER_WORD || command.getFirstCommandNumber() < 1
                || command.getSecondCommandNumber() > tape.length<<ADDRESS_BITS_PER_WORD || command.getSecondCommandNumber() < 1) {
            return false;
        }
        long word = tape[wordIndex(currentCarriageNumber)] & (1L << currentCarriageNumber);
        if (word == 0) {
            currentCommandNumber = command.getSecondCommandNumber();
        } else {
            currentCommandNumber = command.getFirstCommandNumber();
        }
        return true;
    }


    public static void main(String[] args) {
        String[] test = {"1.l", "2.b1;3", "3.u", "4.r", "5.b4;6", "6.u", "7.r", "8.b9;1", "9.s"};
        Machine machine = new Machine();
        Command[] testres = machine.parseCommandsList(test);
        for (Command command : testres) {
            System.out.println(command);
        }

    }
}
