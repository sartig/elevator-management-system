package com.fdmgroup.display;

import javax.swing.JTextArea;

import com.fdmgroup.pojo.Elevator;
import com.fdmgroup.pojo.ElevatorState;
import com.fdmgroup.simulation.Simulation;

public class ElevatorStatusDisplayUpdater implements Runnable {
	private final Elevator[] elevators;
	private final JTextArea textArea;

	public ElevatorStatusDisplayUpdater(JTextArea textArea, Elevator... elevators) {
		this.textArea = textArea;
		this.elevators = elevators;
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			StringBuilder concat = new StringBuilder();
			for (Elevator e : elevators) {
				concat.append(String.format("%15s", "[" + e.getState().toString() + "]"));
				concat.append(" ").append(e.getName().orElse("Unnamed Elevator"));
				concat.append(" current floor: ").append(e.getCurrentFloor());
				if (e.getState() != ElevatorState.IDLE) {
					concat.append("\tHandling request: floor ").append(e.getRequestedInitialFloor())
							.append(" to floor ").append(e.getDestinationFloor());
				}
				concat.append("\n\n");
			}
			concat.append("=====================================================================\n");
			for (Elevator e : elevators) {
				concat.append(String.format(e.getName().orElse("Unnamed Elevator") + " request backlog:"));
				if (e.getRequestQueue().isEmpty())
					concat.append(("\n\tNone"));
				else {
					e.getRequestQueue().forEach(request -> concat.append("\n\t").append(request.toString()));
				}
				concat.append("\n");
			}
			textArea.setText(concat.toString());
			try {
				Thread.sleep((int) (500 / Simulation.getSpeedMultiplier()));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
