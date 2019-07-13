package GUI;


import simulator.Machine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class PostSimulatorPanel extends JPanel {
    private final static int BITS_TO_BORDER = 7;

    private int currentCarriagePos;
    private int savedCurrentCarriagePas;
    private int carriagePosBeforeStart;

    private String currentTape = "";
    private String savedTape = "";
    private String tapeBeforeStart = "";

    private Thread simulatorExec;

    private volatile int sleepDuration;

    private volatile Machine machine;

    private JButton[] tape = new JButton[15];

    private JButton moveTapeLeft, moveTapeRight, start, pause, reset, loadProgram, saveTape, revertTape, speed, doStep,
            chooseProgramFile, chooseTapeFile, saveTapeFile;

    private JFileChooser programFile, tapeFile;

    private JTextArea programText;

    private JTextField currentCommand;

    private void initListeners() {
        moveTapeLeft.addActionListener(e -> onMoveTapeLeftPressed());
        moveTapeRight.addActionListener(e -> onMoveTapeRightPressed());
        start.addActionListener(e -> onStartPressed());
        reset.addActionListener(e -> onResetPressed());
        pause.addActionListener(e -> onPausePressed());
        loadProgram.addActionListener(e -> onLoadProgramPressed());
        saveTape.addActionListener(e -> onSaveTapePressed());
        revertTape.addActionListener(e -> onRevertTapePressed());
        speed.addActionListener(e -> onSpeedPressed());
        doStep.addActionListener(e -> onDoStepPressed());
        chooseTapeFile.addActionListener(e -> onChooseTapeFilePressed());
        chooseProgramFile.addActionListener(e -> onChooseProgramFilePressed());
        saveTapeFile.addActionListener(e -> onSaveTapeFilePressed());
    }

    private void onMoveTapeLeftPressed() {
        if (currentCarriagePos - BITS_TO_BORDER - 1 < 0) {
            currentTape = "0" + currentTape;
        } else {
            currentCarriagePos--;
        }
        updateTape();
        updateCurrentTape();
    }

    private void onMoveTapeRightPressed() {
        if (currentCarriagePos + BITS_TO_BORDER + 1 >= currentTape.length()) {
            currentTape = currentTape + "0";
        }
        currentCarriagePos++;
        updateTape();
        updateCurrentTape();
    }

    private void onStartPressed() {
        for (JButton button : tape) {
            button.setEnabled(false);
        }
        machine.setTape(currentTape, currentCarriagePos);
        tapeBeforeStart = currentTape;
        carriagePosBeforeStart = currentCarriagePos;
        simulatorExec = new Thread(new Runnable() {
            @Override
            public void run() {
                while (machine.executeStep()) {
                    try {
                        Thread.sleep(sleepDuration);
                    } catch (InterruptedException e) {
                        return;
                    }
                    currentTape = Machine.byteToBinaryString(machine.getTape());
                    currentCarriagePos = machine.getCurrentCarriageNumber();
                    updateTape();
                    currentCommand.setText(machine.getCurrentCommand().toString());
                }
                start.setEnabled(true);
                machine.resetCommands();
                pause.setEnabled(false);
                for (JButton button : tape) {
                    button.setEnabled(true);
                }
            }
        });
        simulatorExec.start();
        start.setEnabled(false);
        pause.setEnabled(true);
    }

    private void onResetPressed() {
        machine.resetCommands();
        currentTape = tapeBeforeStart;
        currentCarriagePos = carriagePosBeforeStart;
        updateTape();
        start.setEnabled(true);
        reset.setEnabled(false);
        pause.setText("pause");
        pause.setEnabled(false);
        for (JButton button : tape) {
            button.setEnabled(true);
        }
    }

    private void onPausePressed() {
        if (pause.getText().equals("pause")) {
            pause.setText("resume");
            simulatorExec.interrupt();
            reset.setEnabled(true);
        } else {
            pause.setText("pause");
            onStartPressed();
            reset.setEnabled(false);
        }
    }

    private void onSaveTapePressed() {
        savedTape = currentTape;
        savedCurrentCarriagePas = currentCarriagePos;
        if (!revertTape.isEnabled()) {
            revertTape.setEnabled(true);
        }
    }

    private void onRevertTapePressed() {
        currentTape = savedTape;
        currentCarriagePos = savedCurrentCarriagePas;
        updateTape();
    }


    private void onLoadProgramPressed() {
        machine.setProgram(programText.getText().split("\n"));
        start.setEnabled(true);
        doStep.setEnabled(true);
    }

    private void onSpeedPressed() {
        if (sleepDuration != 100) {
            sleepDuration -= 100;
        } else {
            sleepDuration = 500;
        }
        switch (sleepDuration) {
            case 100:
                speed.setText("Speed:\nVery fast");
                break;
            case 200:
                speed.setText("Speed:\nFast");
                break;
            case 300:
                speed.setText("Speed:\nNormal");
                break;
            case 400:
                speed.setText("Speed:\nSlow");
                break;
            case 500:
                speed.setText("Speed:\nVery slow");
                break;
        }
    }

    private void onDoStepPressed() {
        if (tape[0].isEnabled()) {
            for (JButton button : tape) {
                button.setEnabled(false);
            }
        }
        machine.setTape(currentTape, currentCarriagePos);
        if (!machine.executeStep()) {
            start.setEnabled(true);
            pause.setEnabled(false);
            reset.setEnabled(false);
        }
        currentTape = Machine.byteToBinaryString(machine.getTape());
        currentCarriagePos = machine.getCurrentCarriageNumber();
        updateTape();
        currentCommand.setText(machine.getCurrentCommand().toString());
    }

    private void onChooseTapeFilePressed() {

    }

    private void onChooseProgramFilePressed() {

    }

    private void onSaveTapeFilePressed() {

    }

    private void updateTape() {
        for (int i = currentCarriagePos - BITS_TO_BORDER; i < currentCarriagePos + BITS_TO_BORDER + 1; i++) {
            if (i >= currentTape.length()) {
                currentTape = currentTape + "0";
            }
            tape[i - (currentCarriagePos - BITS_TO_BORDER)].setText(Character.toString(currentTape.charAt(i)));
        }
    }

    private void updateCurrentTape() {
        StringBuilder newCurrentTape = new StringBuilder("");
        for (int i = 0; i < currentCarriagePos - BITS_TO_BORDER; i++) {
            newCurrentTape.append(currentTape.charAt(i));
        }
        for (int i = 0; i < BITS_TO_BORDER * 2 + 1; i++) {
            newCurrentTape.append(tape[i].getText());
        }
        for (int i = currentCarriagePos + BITS_TO_BORDER + 1; i < currentTape.length(); i++) {
            newCurrentTape.append(currentTape.charAt(i));
        }
        currentTape = newCurrentTape.toString();
    }

    public PostSimulatorPanel() {
        super();
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        setLayout(gridBagLayout);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.ipady = 40;
        start = new JButton("start");
        start.setEnabled(false);
        pause = new JButton("pause");
        pause.setEnabled(false);
        reset = new JButton("reset");
        reset.setEnabled(false);
        speed = new JButton("Speed:\nNormal");
        doStep = new JButton("Execute one command");
        doStep.setEnabled(false);
        loadProgram = new JButton("load Program");
        add(start, gridBagConstraints);
        gridBagConstraints.gridx = 4;
        add(pause, gridBagConstraints);
        gridBagConstraints.gridx = 7;
        add(reset, gridBagConstraints);
        gridBagConstraints.gridx = 10;
        add(loadProgram, gridBagConstraints);
        saveTape = new JButton("Save tape");
        gridBagConstraints.gridx = 13;
        add(saveTape, gridBagConstraints);
        revertTape = new JButton("revert tape");
        revertTape.setEnabled(false);
        gridBagConstraints.gridy = 0;
        add(revertTape, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        add(doStep, gridBagConstraints);
        gridBagConstraints.gridx = 4;
        add(speed, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 0;
        moveTapeLeft = new JButton("Left");
        moveTapeRight = new JButton("Right");
        gridBagConstraints.gridy = 2;
        add(moveTapeLeft, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        for (int i = 0; i < BITS_TO_BORDER * 2 + 1; i++) {
            tape[i] = new JButton("0");
            tape[i].setHorizontalAlignment(SwingConstants.CENTER);
            tape[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (button.getText().equals("0")) {
                        button.setText("1");
                    } else {
                        button.setText("0");
                    }
                    updateCurrentTape();
                }
            });
            add(tape[i], gridBagConstraints);
            gridBagConstraints.gridx++;
        }
        add(moveTapeRight, gridBagConstraints);

        programText = new JTextArea();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 13;
        gridBagConstraints.gridheight = 1;
        add(programText, gridBagConstraints);
        gridBagConstraints.gridheight = 1;
        currentCommand = new JTextField("");
        currentCommand.setFocusable(false);
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 15;
        add(currentCommand, gridBagConstraints);

        chooseProgramFile = new JButton("Choose program from file");
        chooseTapeFile = new JButton("Choose tape from file");
        saveTapeFile = new JButton("Save tape to file");

        gridBagConstraints.gridx = 0;
        add(chooseProgramFile, gridBagConstraints);
        gridBagConstraints.gridy = 4;
        add(chooseTapeFile, gridBagConstraints);
        gridBagConstraints.gridx = 15;
        add(saveTapeFile, gridBagConstraints);


        initListeners();

        updateCurrentTape();
        currentCarriagePos = 7;

        machine = new Machine();

        sleepDuration = 300;
    }
}
