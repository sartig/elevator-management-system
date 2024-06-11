package com.fdmgroup.display;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;

import com.fdmgroup.elevatormanagement.ElevatorController;
import com.fdmgroup.pojo.Elevator;
import com.fdmgroup.pojo.ElevatorRequestInfo;
import com.fdmgroup.simulation.Simulation;

public class AppDisplay extends JPanel implements ActionListener {
	private final JTextField inputTextField;
	private final JLabel speedLabel;
    private final ElevatorController elevatorController;
    private final AdditionalMessageDisplayUpdater additionalMessageDisplayUpdater = AdditionalMessageDisplayUpdater.getInstance();

	public AppDisplay(ElevatorController elevatorController, Elevator... elevators) {
		super(new GridBagLayout());
		this.elevatorController = elevatorController;
		JLabel textFieldLabel = new JLabel(
				"Enter request (format: [start] [end](;[start] [end]...) or type 'exit' to end");
		JLabel maxFloorLabel = new JLabel ("Floors in building: " + ElevatorRequestInfo.getMaxFloorCount());
		speedLabel = new JLabel("Current speed multiplier: " + Simulation.getSpeedMultiplier() + "x");
		inputTextField = new JTextField(1);
		inputTextField.addActionListener(this);
		inputTextField.setBackground(Color.black);
		inputTextField.setForeground(Color.white);

        JTextArea elevatorStatusTextArea = new JTextArea(20, 1);
		elevatorStatusTextArea.setAlignmentX(LEFT_ALIGNMENT);
		elevatorStatusTextArea.setEditable(false);
		elevatorStatusTextArea.setForeground(Color.white);
		elevatorStatusTextArea.setBackground(Color.black);

        JTextArea additionalMessageTextArea = new JTextArea(20, 1);
		additionalMessageTextArea.setBackground(Color.black);
		additionalMessageTextArea.setForeground(Color.white);

		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(textFieldLabel, c);
		add(maxFloorLabel, c);
		add(speedLabel, c);
		add(inputTextField, c);
		add(elevatorStatusTextArea, c);
		add(additionalMessageTextArea, c);
		new Thread(new ElevatorStatusDisplayUpdater(elevatorStatusTextArea, elevators)).start();
		additionalMessageDisplayUpdater.setTextArea(additionalMessageTextArea);
		new Thread(additionalMessageDisplayUpdater).start();
	}

	// runs when user types something and hits 'enter'
	@Override
	public void actionPerformed(ActionEvent e) {
		String text = inputTextField.getText().toLowerCase().trim();
		inputTextField.setText(null);
		if (text.equalsIgnoreCase("exit")) {
			// exit if user types exit
			System.exit(0);
		}
		// parse this text
		// send it to request system
		if (text.startsWith("speed ")) {
			String[] speedInput = text.split("\\s+");
			if (speedInput.length!=2) {
				additionalMessageDisplayUpdater.addAdditionalMessage("Speed command syntax incorrect: expected 1 argument, was: " + (speedInput.length-1));
				return;
			}
			try {
				double newSpeed = Double.parseDouble(speedInput[1]);
				Simulation.setSpeedMultiplier(newSpeed);
				if(newSpeed==0) {
					additionalMessageDisplayUpdater.addAdditionalMessage("Speed command: cannot set speed multiplier to " + newSpeed);
					return;
				}
				additionalMessageDisplayUpdater.addAdditionalMessage("Speed command: updated speed multiplier to " + newSpeed);
				speedLabel.setText("Current speed multiplier: " + newSpeed + "x");
			} catch (Exception ex) {
				additionalMessageDisplayUpdater.addAdditionalMessage("Speed command: could not parse " + speedInput[1] + " as double");
			}
			return;
		}
		List<ElevatorRequestInfo> requests = ElevatorRequestInfo.parseRawInput(text);
		if (requests == null || requests.isEmpty()) {
			return;
		}
		requests.forEach(elevatorController::generalChooseElevatorLogic);
	}
}
