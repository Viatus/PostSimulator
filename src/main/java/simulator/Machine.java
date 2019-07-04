package simulator;

public class Machine {

    private final static int BITS_PER_WORD = 7;

    private Command[] commands;

    private byte[] tape;

    private int currentCommandNumber;
    private int currentCarriageNumber;

    public byte[] getTape() {
        return tape;
    }

    public Machine() {
        tape = new byte[2];
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

    public void setProgram(String[] listOfCommands) {
        commands = parseCommandsList(listOfCommands);
        currentCommandNumber = 0;
    }

    public void setTape(byte[] tape, int carriageNumber) {
        this.tape = tape;
        currentCarriageNumber = carriageNumber;
    }

    public boolean setTape(String tape, int currentCarriageNumber) {
        this.tape = new byte[wordIndex(tape.length() - 1) + 1];
        int index = 6;
        int tapeIndex = 0;
        for (int i = 0; i < tape.length(); i++) {
            char digit = tape.charAt(i);
            if (digit != '0' && digit != '1') {
                return false;
            }
            if (digit == '1') {
                this.tape[tapeIndex] += Math.pow(2, index);
            }
            if (index == 0) {
                index = 6;
                tapeIndex++;
            } else {
                index--;
            }
        }
        this.currentCarriageNumber = currentCarriageNumber;
        return true;
    }

    private static int wordIndex(int bitIndex) {
        return bitIndex / BITS_PER_WORD;
    }

    public boolean executeProgram() {
        currentCommandNumber = 0;
        while (commands[currentCommandNumber].getCommandName() != Command.CommandName.stop) {
            if (!executeStep()) {
                return false;
            }
        }
        return true;
    }

    public boolean executeStep() {
        Command command = commands[currentCommandNumber];
        //System.out.println(command + "    " + byteToBinaryString(tape));
        switch (command.getCommandName()) {
            case mark:
                return executeMark(command);
            case unmark:
                return executeUnMark(command);
            case left:
                return executeLeft(command);
            case right:
                return executeRight(command);
            case branch:
                return executeBranch(command);
            default:
                return false;
        }
    }

    private boolean executeMark(Command command) {
        int wordIndex = wordIndex(currentCarriageNumber);
        tape[wordIndex] |= (1 << BITS_PER_WORD - 1 - currentCarriageNumber % BITS_PER_WORD);
        currentCommandNumber = command.getFirstCommandNumber() - 1;
        return true;
    }

    private boolean executeUnMark(Command command) {
        int wordIndex = wordIndex(currentCarriageNumber);
        tape[wordIndex] &= ~(1 << BITS_PER_WORD - 1 - currentCarriageNumber % BITS_PER_WORD);
        currentCommandNumber = command.getFirstCommandNumber() - 1;
        return true;
    }

    private boolean executeLeft(Command command) {
        if (currentCarriageNumber == 0) {
            byte[] tapeCopy = tape;
            tape = new byte[tape.length * 2];
            int i = tapeCopy.length;
            for (byte word : tapeCopy) {
                tape[i++] = word;
            }
            currentCarriageNumber = tapeCopy.length - 1;
        } else {
            currentCarriageNumber--;
        }
        currentCommandNumber = command.getFirstCommandNumber() - 1;
        return true;
    }

    private boolean executeRight(Command command) {
        if (currentCarriageNumber + 1 == tape.length * BITS_PER_WORD) {
            byte[] tapeCopy = tape;
            tape = new byte[tape.length * 2];
            int i = 0;
            for (byte word : tapeCopy) {
                tape[i++] = word;
            }
        }
        currentCarriageNumber++;
        currentCommandNumber = command.getFirstCommandNumber() - 1;
        return true;
    }

    private boolean executeBranch(Command command) {
        if (command.getFirstCommandNumber() > commands.length || command.getFirstCommandNumber() < 1
                || command.getSecondCommandNumber() > commands.length || command.getSecondCommandNumber() < 1) {
            return false;
        }
        byte word = (byte) (tape[wordIndex(currentCarriageNumber)] & (1 << BITS_PER_WORD - 1 - currentCarriageNumber % BITS_PER_WORD));
        if (word == 0) {
            currentCommandNumber = command.getFirstCommandNumber() - 1;
        } else {
            currentCommandNumber = command.getSecondCommandNumber() - 1;
        }
        return true;
    }

    public String byteToBinaryString(byte[] tapeToConvert) {
        StringBuilder result = new StringBuilder();
        for (byte word : tapeToConvert) {
            StringBuilder temp = new StringBuilder();
            word = (byte) Math.abs(word);
            while (word > 1) {
                temp.append(word % 2);
                word /= 2;
            }
            temp.append(word);
            int zerosLeft = BITS_PER_WORD - temp.length();
            for (int i = 0; i < zerosLeft; i++) {
                temp.append("0");
            }
            result.append(temp.reverse());
        }
        return result.toString();
    }


    public static void main(String[] args) {
        String[] test = {"1.u", "2.r", "3.b4;2", "4.m", "5.r", "6.b10;7", "7.l", "8.b9;7", "9.r1", "10.s"};
        Machine machine = new Machine();
        Command[] testres = machine.parseCommandsList(test);
        for (Command command : testres) {
            System.out.println(command);
        }
        machine.setProgram(test);
        byte[] testTape = {0b1010101};
        machine.setTape(testTape, 0);
        System.out.println(machine.byteToBinaryString(machine.getTape()));
        System.out.println(machine.executeProgram());
        System.out.println(machine.byteToBinaryString(machine.getTape()));

    }
}
