package GUI;


import simulator.Machine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;


public class PostSimulatorPanel extends JPanel {
    private final static int BITS_TO_BORDER = 7;

    private int currentCarriagePos;
    private String currentTape = "";

    private Machine machine;

    private JButton[] tape = new JButton[15];

    private JButton moveTapeLeft, moveTapeRight, start, pause, reset, loadProgram;

    private JTextArea programText;

    private void initListeners() {
        moveTapeLeft.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onMoveTapeLeftPressed();
            }
        });
        moveTapeRight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onMoveTapeRightPressed();
            }
        });
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onStartPressed();
            }
        });
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onResetPressed();
            }
        });
        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onPausePressed();
            }
        });
        loadProgram.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLoadProgramPressed();
            }
        });
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
        machine.setTape(currentTape, currentCarriagePos);
        while(machine.executeStep()) {
            currentTape = Machine.byteToBinaryString(machine.getTape());
            updateTape();
        }
    }

    private void onResetPressed() {

    }

    private void onPausePressed() {

    }

    private void onLoadProgramPressed() {
        machine.setProgram(programText.getText().split("\n"));
    }

    private void updateTape() {
        for (int i = currentCarriagePos - BITS_TO_BORDER; i < currentCarriagePos + BITS_TO_BORDER + 1; i++) {
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
        programText.setText(currentTape);
    }

    public PostSimulatorPanel() {
        super();
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        setLayout(gridBagLayout);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.ipady = 40;
        start = new JButton("start");
        pause = new JButton("pause");
        reset = new JButton("reset");
        loadProgram = new JButton("load Programm");
        add(start, gridBagConstraints);
        gridBagConstraints.gridx = 4;
        add(pause, gridBagConstraints);
        gridBagConstraints.gridx = 7;
        add(reset, gridBagConstraints);
        gridBagConstraints.gridx = 10;
        add(loadProgram, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 0;
        moveTapeLeft = new JButton("Left");
        moveTapeRight = new JButton("Right");
        gridBagConstraints.gridy = 1;
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 15;
        gridBagConstraints.gridheight = 4;
        add(programText, gridBagConstraints);

        initListeners();

        updateCurrentTape();
        currentCarriagePos = 7;

        machine = new Machine();
    }
}
