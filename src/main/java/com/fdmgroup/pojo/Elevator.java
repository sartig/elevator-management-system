package com.fdmgroup.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fdmgroup.simulation.Simulation;

public class Elevator implements Runnable {
    private int currentFloor;
    private int requestedDestinationFloor;
    private int requestedInitialFloor;
    private ElevatorState state;
    private final String name;
    private boolean hasPassengers;
    private boolean isBusy;

    public List<ElevatorRequestInfo> getRequestQueue() {
        return requestQueue;
    }

    private final List<ElevatorRequestInfo> requestQueue;

    public Elevator(String name) {
        this.currentFloor = 1; // Elevator starts at floor 1
        this.state = ElevatorState.IDLE;
        this.name = name;
        this.hasPassengers = false;
        this.isBusy = false;
        this.requestQueue = new ArrayList<>();
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getDestinationFloor() {
        return requestedDestinationFloor;
    }

    public int getRequestedInitialFloor() {
        return requestedInitialFloor;
    }

    public ElevatorState getState() {
        return state;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public void setIsBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    public boolean getIsBusy() {
        return isBusy;
    }

    public int getEstimatedTime(ElevatorRequestInfo request) {
        int time = 0;
        time += timeToCompleteCurrentRequest(); // 0 if no current request
        time += timeToCompleteQueue(); // 0 if no queue
        if (isBusy) {
            // time to move from last request to new request start
            if (requestQueue.isEmpty()) {
                // if there is no queue
                time += Math.abs(requestedDestinationFloor - request.getStartFloor());
            } else {
                time += Math.abs(requestQueue.get(requestQueue.size() - 1).getDestinationFloor()
                        - request.getStartFloor());
            }
        } else {
            // time to move directly
            time += Math.abs(currentFloor - request.getStartFloor());
        }
        return time;
    }

    private int timeToCompleteQueue() {
        int time = 0;
        for (int i = 0; i < requestQueue.size(); i++) {
            ElevatorRequestInfo queueRequest = requestQueue.get(i);
            // travel time
            time += Math.abs(queueRequest.getDestinationFloor() - queueRequest.getStartFloor());
            // time to load and unload
            time += 10;
            if (i > 0) {
                // time to travel from end of one request to start of next
                time += Math.abs(requestQueue.get(i).getStartFloor() - requestQueue.get(i - 1).getDestinationFloor());
            }
        }
        return time;
    }

    private int timeToCompleteCurrentRequest() {
        if (!isBusy) {
            return 0;
        }
        int time = 0;
        if (hasPassengers) {
            // must be moving to request destination
            time += Math.abs(currentFloor - requestedDestinationFloor);
            // unloading time
            time += 5;
        } else {
            // time moving to request start
            time += Math.abs(currentFloor - requestedInitialFloor);
            // time moving to request end
            time += Math.abs(requestedDestinationFloor - requestedInitialFloor);
            // loading and unloading time
            time += 10;
        }
        return time;
    }

    public void handleRequest(ElevatorRequestInfo request) {
        requestQueue.add(request);
    }

    @Override
    public void run() {
        while (!requestQueue.isEmpty()) {
            ElevatorRequestInfo request = requestQueue.get(0);
            requestedInitialFloor = request.getStartFloor();
            requestedDestinationFloor = request.getDestinationFloor();
            requestQueue.remove(0);
            if (currentFloor == requestedInitialFloor) {
                loadPassengers();
            } else {
                moveToTargetFloor();
            }
        }
        isBusy = false;
    }

    private void offloadPassengers() {
        state = ElevatorState.OFFLOADING;
        try {
            Thread.sleep((int)(2500/Simulation.getSpeedMultiplier()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            hasPassengers = false;
            state = ElevatorState.IDLE;
        }
    }

    private void loadPassengers() {
        state = ElevatorState.LOADING;
        try {
            Thread.sleep((int)(2500/Simulation.getSpeedMultiplier()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            hasPassengers = true;
            moveToTargetFloor();
        }
    }

    private void moveToTargetFloor() {
        state = ElevatorState.MOVING;
        int targetFloor = hasPassengers ? requestedDestinationFloor : requestedInitialFloor;
        while (currentFloor != targetFloor) {
            try {
                Thread.sleep((int)(500/Simulation.getSpeedMultiplier()));
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (currentFloor < targetFloor) {
                    currentFloor++;
                } else {
                    currentFloor--;
                }
            }
        }

        // wouldn't move unless there was a reason
        if (hasPassengers) {
            offloadPassengers();
        } else {
            loadPassengers();
        }
    }

}
