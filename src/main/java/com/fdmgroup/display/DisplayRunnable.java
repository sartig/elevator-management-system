package com.fdmgroup.display;

import javax.swing.JFrame;

import com.fdmgroup.elevatormanagement.ElevatorController;
import com.fdmgroup.pojo.Elevator;

public class DisplayRunnable implements Runnable {

	private static Elevator[] elevators;
	private static ElevatorController elevatorController;

	public DisplayRunnable(ElevatorController elevatorController) {
		DisplayRunnable.elevatorController = elevatorController;
		DisplayRunnable.elevators = elevatorController.getElevators();
	}

	@Override
	public void run() {
		JFrame frame = new JFrame("Elevator Management System");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		AppDisplay textDemo = new AppDisplay(elevatorController, elevators);

		frame.add(textDemo);
		frame.pack();
		frame.setVisible(true);
	}

}
