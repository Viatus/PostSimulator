package simulator;

import org.junit.Test;

import static org.junit.Assert.*;

public class MachineTest {

    @Test
    public void parseCommandsList() {
        String[] test = {"1.l", "2.b1;3", "3.u", "4.r", "5.b4;6", "6.u", "7.r", "8.b9;1", "9.s"};
        Machine machine = new Machine();
        Command[] testres = Machine.parseCommandsList(test);
        Command[] expectedRes = {new Command(Command.CommandName.left,2), new Command(Command.CommandName.branch, 1,3),
        new Command(Command.CommandName.unmark,4), new Command(Command.CommandName.right, 5),
        new Command(Command.CommandName.branch, 4, 6), new Command(Command.CommandName.unmark, 7),
        new Command(Command.CommandName.right, 8), new Command(Command.CommandName.branch, 9, 1),
        new Command(Command.CommandName.stop)};
        assertArrayEquals(expectedRes, testres);

        test = new String[]{"1.    l2", "2. b      3 ; 1", "3.u4", "4.s"};
        testres = Machine.parseCommandsList(test);
        expectedRes = new Command[]{new Command(Command.CommandName.left, 2), new Command(Command.CommandName.branch, 3, 1),
        new Command(Command.CommandName.unmark, 4), new Command(Command.CommandName.stop)};
        assertArrayEquals(expectedRes, testres);
    }

    @Test
    public void executeProgram() {
        //Вычитание из левого числа правого
        String[] testCommands = {"1.l", "2.b1;3", "3.u", "4.r", "5.b4;6", "6.u", "7.r", "8.b9;1", "9.s"};
        Machine machine = new Machine();
        machine.setProgram(testCommands);
        byte[] testTape = {0b1111011};
        machine.setTape(testTape, 5);
        assertTrue(machine.executeProgram());
        String expectedTape = "11";
        String resultTape = Machine.byteToBinaryString(machine.getTape());
        resultTape = resultTape.replace('0', ' ');
        resultTape = resultTape.trim();
        resultTape = resultTape.replace(' ', '0');
        assertEquals(expectedTape, resultTape);

        //То же задание, но лента задана в виде строки
        expectedTape = "111";
        assertTrue(machine.setTape("0000000011111011", 14));
        assertTrue(machine.executeProgram());
        resultTape = Machine.byteToBinaryString(machine.getTape());
        resultTape = resultTape.replace('0', ' ');
        resultTape = resultTape.trim();
        resultTape = resultTape.replace(' ', '0');
        assertEquals(expectedTape, resultTape);

        //Прибавление единиы к числу
        testCommands = new String[]{"1.r", "2.b1;3", "3.l", "4.m", "5.s"};
        machine.setProgram(testCommands);
        testTape = new byte[]{0b0001111};
        machine.setTape(testTape, 0);
        assertTrue(machine.executeProgram());
        expectedTape = "11111";
        resultTape = Machine.byteToBinaryString(machine.getTape());
        resultTape = resultTape.replace('0', ' ');
        resultTape = resultTape.trim();
        resultTape = resultTape.replace(' ', '0');
        assertEquals(expectedTape, resultTape);

        //На ленте задана последовательность массивов, включающая в себя один и более массивов.
        // При этом два соседних массива отделены друг от друга одной пустой ячейкой.
        // Необходимо на ленте оставить один массив длиной равной сумме длин массивов, присутствовавших изначально.
        // Каретка находится над крайней левой меткой первого (левого) массива.
        testCommands = new String[]{"1.u", "2.r", "3.b4;2", "4.m", "5.r", "6.b10;7", "7.l", "8.b9;7", "9.r1", "10.s"};
        machine.setProgram(testCommands);
        testTape = new byte[]{0b1010101};
        machine.setTape(testTape, 0);
        assertTrue(machine.executeProgram());
        expectedTape = "1111";
        resultTape = Machine.byteToBinaryString(machine.getTape());
        resultTape = resultTape.replace('0', ' ');
        resultTape = resultTape.trim();
        resultTape = resultTape.replace(' ', '0');
        assertEquals(expectedTape, resultTape);
    }

    @Test
    public void executeStep() {
    }

    @Test
    public void byteToBinaryString() {
        byte[] testArray = {0b1110111, 0b1111111};
        String expectedResult = "11101111111111";
        assertEquals(expectedResult, Machine.byteToBinaryString(testArray));
        testArray[0] = 0b0000000;
        testArray[1] = 0b1010101;
        expectedResult = "00000001010101";
        assertEquals(expectedResult,Machine.byteToBinaryString(testArray));
    }
}