package com.fdmgroup.simulation;

import com.fdmgroup.pojo.*;
import com.fdmgroup.display.DisplayRunnable;
import com.fdmgroup.elevatormanagement.*;

import javax.swing.SwingUtilities;

public class Simulation {
	private static final int floorCount = 20;
	private static final int elevatorCount = 4;
	private static double speedMultiplier = 1;

	public static void main(String[] args) {
		ElevatorRequestInfo.setMaxFloorCount(floorCount);
		Elevator[] elevators = new Elevator[elevatorCount];
		for (int i = 0; i < elevatorCount; i++) {
			elevators[i] = new Elevator("Elevator " + (i + 1));
		}
		ElevatorController elevatorController = new ElevatorController(elevators);

		SwingUtilities.invokeLater(new DisplayRunnable(elevatorController));

	}

	public static void setSpeedMultiplier(double multiplier) {
		// prevent divide-by-zero issues
		if (multiplier == 0) {
			multiplier = 1;
		}
		speedMultiplier = multiplier;
	}

	public static double getSpeedMultiplier() {
		return speedMultiplier;
	}
}
