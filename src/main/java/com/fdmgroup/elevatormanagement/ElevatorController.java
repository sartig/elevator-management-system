package com.fdmgroup.elevatormanagement;

import com.fdmgroup.display.AdditionalMessageDisplayUpdater;
import com.fdmgroup.pojo.*;

public class ElevatorController {
    private final Elevator[] elevators;

    public ElevatorController(Elevator... elevators) {
        this.elevators = elevators;
    }

    public void generalChooseElevatorLogic(ElevatorRequestInfo info) {
        if (info == null) {
            return;
        }
        int minTime = Integer.MAX_VALUE;
        Elevator nearestElevator = null;
        for (Elevator elevator : elevators) {
            int elevatorTime = elevator.getEstimatedTime(info);
            if (elevatorTime < minTime) {
                minTime = elevatorTime;
                nearestElevator = elevator;
            }
        }
        AdditionalMessageDisplayUpdater.getInstance().addAdditionalMessage
                (info + " assigned to " + nearestElevator.getName().orElse("Unnamed Elevator"));
        nearestElevator.handleRequest(info);
        if (!nearestElevator.getIsBusy()) {
            // only start thread if it isn't already running
            nearestElevator.setIsBusy(true);
            new Thread(nearestElevator).start();
        }
    }

	public Elevator[] getElevators() {
		return elevators;
	}
}