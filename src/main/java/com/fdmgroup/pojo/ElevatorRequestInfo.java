package com.fdmgroup.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fdmgroup.display.AdditionalMessageDisplayUpdater;

public class ElevatorRequestInfo {
	private final int startFloor;
	private final int destinationFloor;
	private static int maxFloorCount;

	public ElevatorRequestInfo(int startFloor, int destinationFloor) {
		this.startFloor = startFloor;
		this.destinationFloor = destinationFloor;
	}

	public int getStartFloor() {
		return startFloor;
	}

	public int getDestinationFloor() {
		return destinationFloor;
	}

	public static void setMaxFloorCount(int maxFloorCount) {
		ElevatorRequestInfo.maxFloorCount = maxFloorCount;
	}
	
	public static int getMaxFloorCount() {
		return maxFloorCount;
	}

	@Override
	public String toString() {
		return "Request: floor " + startFloor + " to floor " + destinationFloor;
	}

	public static List<ElevatorRequestInfo> parseRawInput(String string) {
		List<ElevatorRequestInfo> parsedUserInput = new ArrayList<>();
		String[] userInputArray = string.split(";");
		for (String s : userInputArray) {
			ElevatorRequestInfo info = parseString((s.trim()));
			if (info != null && !parsedUserInput.contains(info)) {
				parsedUserInput.add(info);
			}
		}
		if (parsedUserInput.isEmpty()) {
			return null;
		}
		return parsedUserInput;
	}

	private static ElevatorRequestInfo parseString(String string) {
		if (string.isBlank()) {
			// ignore whitespace-only strings
			return null;
		}
		String[] inputs = string.split("\\s+");
		if (inputs.length != 2) {
			AdditionalMessageDisplayUpdater.getInstance().addAdditionalMessage("Bad number of params in input '" + string + "': had " + inputs.length + ", expected 2");
			return null;
		}
		try {
			int startFloor = Integer.parseInt(inputs[0]);
			int destinationFloor = Integer.parseInt(inputs[1]);
			if (startFloor > maxFloorCount || startFloor < 1) {
				AdditionalMessageDisplayUpdater.getInstance()
						.addAdditionalMessage("Invalid input. Starting floor must be between 1 and " + maxFloorCount);
				return null;
			}
			if (destinationFloor > maxFloorCount || destinationFloor < 1) {
				AdditionalMessageDisplayUpdater.getInstance().addAdditionalMessage(
						"Invalid input. Destination floor must be between 1 and " + maxFloorCount);
				return null;
			}
			if (startFloor == destinationFloor) {
				AdditionalMessageDisplayUpdater.getInstance()
						.addAdditionalMessage("Same current and destination floor inputted, skipping request");
				return null;
			}
			return new ElevatorRequestInfo(startFloor, destinationFloor);
		} catch (NumberFormatException e) {
			AdditionalMessageDisplayUpdater.getInstance()
					.addAdditionalMessage("Input not an integer");
			return null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElevatorRequestInfo other = (ElevatorRequestInfo) obj;
		return destinationFloor == other.destinationFloor && startFloor == other.startFloor;
	}
}
