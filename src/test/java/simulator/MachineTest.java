package simulator;

import org.junit.Test;

import static org.junit.Assert.*;

public class MachineTest {

    @Test
    public void parseCommandsList() {
        String[] test = {"1.l", "2.b1;3", "3.u", "4.r", "5.b4;6", "6.u", "7.r", "8.b9;1", "9.s"};
        Machine machine = new Machine();
        Command[] testres = machine.parseCommandsList(test);
        Command[] expectedRes = {new Command(Command.CommandName.left,2), new Command(Command.CommandName.branch, 1,3),
        new Command(Command.CommandName.unmark,4), new Command(Command.CommandName.right, 5),
        new Command(Command.CommandName.branch, 4, 6), new Command(Command.CommandName.unmark, 7),
        new Command(Command.CommandName.right, 8), new Command(Command.CommandName.branch, 9, 1),
        new Command(Command.CommandName.stop)};
        assertArrayEquals(expectedRes, testres);
    }

    @Test
    public void executeProgram() {
        String[] test = {"1.l", "2.b1;3", "3.u", "4.r", "5.b4;6", "6.u", "7.r", "8.b9;1", "9.s"};
        Machine machine = new Machine();
        machine.setProgram(test);
        byte[] testTape = {-0b1111011};
        machine.setTape(testTape, 6);
        assertTrue(machine.executeProgram());
        String expectedTape = "1110000000000000";
        assertEquals(expectedTape, machine.byteToBinaryString(machine.getTape()));

        expectedTape = "00000000111000000000000000000000";
        assertTrue(machine.setTape("0000000011111011", 14));
        assertTrue(machine.executeProgram());
        assertEquals(expectedTape, machine.byteToBinaryString(machine.getTape()));
    }

    @Test
    public void executeStep() {
    }

    @Test
    public void byteToBinaryString() {
        byte[] testArray = {0b1110111, -0b1111111};
        String expectedResult = "0111011111111111";
        assertEquals(expectedResult, new Machine().byteToBinaryString(testArray));
        testArray[0] = 0b0000000;
        testArray[1] = 0b1010101;
        expectedResult = "0000000001010101";
        assertEquals(expectedResult, new Machine().byteToBinaryString(testArray));
    }
}