//Zachary Lee 55104210
//Jason Kandu 23434725

import java.util.ArrayList;

public class Elevator implements Runnable {
	private int elevatorID;
	private int currentFloor;
	private int numPassengers;
	private int totalLoadedPassengers;
	private int totalUnloadedPassengers;
	private int[] unloadedPassengers; //Keeps track of unloadedPassengers for each floor (necessary for printBuildingState())
	private ArrayList<ElevatorEvent> moveQueue;
	private int[] passengerDestinations;
	private BuildingManager manager;
	private int previousFloor;		//Floor that all the passengers came from 
	private int lastArrivalMessage; // int that tracks when the last arrival for this elevator was.
	
	public Elevator(int elevatorID, BuildingManager manager) {
		this.elevatorID = elevatorID;
		this.currentFloor = 0;
		this.numPassengers = 0;
		this.totalLoadedPassengers = 0;
		this.totalUnloadedPassengers = 0;
		this.unloadedPassengers = new int[Constants.MAX_FLOORS];
		this.moveQueue = new ArrayList<ElevatorEvent>();
		this.passengerDestinations = new int[Constants.MAX_FLOORS];
		this.manager = manager;
	}
	
	//Runs a continuous loop that constantly checks the state of moveQueue
	//If moveQueue is empty, then Elevator asks BuildingManager for requests 
	//Else, Elevator processes the Elevator events in the moveQueue 
	public void run() {
		while(!Thread.interrupted()) {
			if (!moveQueue.isEmpty()){ // elevator is in the middle of an action
				//Grabs the first ElevatorEvent in the moveQueue because it is highest priority
				ElevatorEvent eventToHandle = moveQueue.get(0);
				
				//Checks the clock to see if it is the correct time to load/unload passengers
				if (SimClock.getTime() == eventToHandle.getExpectedArrival()) {
					currentFloor = eventToHandle.getDestination();
					
					//Checks to see how many passengers are currently in the elevator
					//If there are passengers, then elevator is unloading them
					if (numPassengers != 0 ) {
						unload();
					}
					
					//If there are no passengers, then elevator is loading them
					else {
						load();
						manager.getFloor(currentFloor).clearApproachingElevator();
					}
					
					//Finished processing ElevatorEvent, so remove it from moveQueue
					moveQueue.remove(0);
					
					// if there is another move in the moveQueue, the elevator must have more people to unload
					if (Constants.VERBOSE) {
						if (!moveQueue.isEmpty()) { // Elevator is now traveling to unload people
							printTravelMessage(elevatorID, moveQueue.get(0).getDestination(), "unload");
						}
						else {
							// do nothing. Elevator has just dropped off its last passengers. Elevator is now idle
						}
					}
				}
				
				// Elevator has arrived on a floor to load or unload
				if (Constants.VERBOSE) {
					if (SimClock.getTime() == eventToHandle.getExpectedArrival() - Constants.LOAD_UNLOAD_TIME) {
						// Floor that the elevator just arrived at
						int destinationFloor = moveQueue.get(0).getDestination();
						if (numPassengers == 0) { // elevator is empty, so arrived to load
							if (SimClock.getTime() == lastArrivalMessage) {
								// do nothing because you already made a message for arrival.
							}
							else {
								printArrivalMessage(elevatorID, destinationFloor, "pick up");
								lastArrivalMessage = SimClock.getTime();
							}
						}
						else { // elevator has people, so arrived to unload
							if (SimClock.getTime() == lastArrivalMessage) {
								// do nothing because you already made a message for arrival.
							}
							else {
								printArrivalMessage(elevatorID, destinationFloor, "unload");
								lastArrivalMessage = SimClock.getTime();
							}
						}
					}
				}
			} else { // elevator is idle
				int pickupFloor = manager.assignPickup(elevatorID);
				if (pickupFloor == -1) {
					// do nothing since no one needs to be picked up
				} else {
					if (Constants.VERBOSE) {
						printTravelMessage(elevatorID, pickupFloor, "pick up");
					}
					ElevatorEvent nextMove = new ElevatorEvent(pickupFloor, SimClock.getTime() + moveTime(currentFloor, pickupFloor));
					moveQueue.add(nextMove);
				}
			}
				
		}
	}

	
	//Creates an ElevatorEvent for the passengers who want to go to the destination floor
	public void processRequest(int destinationFloor, int numOfPassengers, int currentSimTime) {
		// This will be used if there are no moves in the moveQueue
		int expectedArrivalTime = currentSimTime;
		// floor that the ElevatorEvent begins on. uses the current floor if there are no moves in moveQueue
		int previous = currentFloor;
		
		// calculate the expected time after the previous ElevatorEvents are completed.
		for (int i = 0; i < moveQueue.size(); i++) {
			expectedArrivalTime = moveQueue.get(i).getExpectedArrival();
			previous = moveQueue.get(i).getDestination();
		}
		
		// add the time from the end of the last move to the predicted end of this move
		expectedArrivalTime += moveTime(previous, destinationFloor);
		
		// create an ElevatorEvent and add it to the moveQueue
		ElevatorEvent nextMove = new ElevatorEvent(destinationFloor, expectedArrivalTime);
		moveQueue.add(nextMove);
		
		// update passengerDestinations
		passengerDestinations[destinationFloor] += numOfPassengers;
	}

	
	// loads passengers on the current floor
	private void load() {
		int newPassengers = 0;
		// allow BuildingFloor to properly handle loading on its side and then calculate 
		//      the number of people getting onto this elevator
		newPassengers = manager.getFloor(currentFloor).loadPassengers(this, SimClock.getTime());
		totalLoadedPassengers += newPassengers;
		numPassengers += newPassengers;
		System.out.println("    Elevator " + elevatorID + " now has " + newPassengers + " passengers.");
	}

	// Returns the amount of time for traversal and dropoff or load from one floor to another.
	// Assumes you WILL drop off or load passengers.
	private int moveTime(int originFloor, int destinationFloor) {
		return (Math.abs(destinationFloor - originFloor) * Constants.FLOOR_TRAVERSAL_TIME) + Constants.LOAD_UNLOAD_TIME; 
	}
	
	// unloads all the passengers at the current floor.
	private void unload() {
		manager.getFloor(currentFloor).unloadPassengers(this, passengerDestinations[currentFloor], previousFloor);
		totalUnloadedPassengers += passengerDestinations[currentFloor];
		unloadedPassengers[currentFloor] += passengerDestinations[currentFloor];
		numPassengers -= passengerDestinations[currentFloor];
		passengerDestinations[currentFloor] = 0;
	}
	
	private void printTravelMessage(int elevator, int destinationFloor, String action) {
		String formatString = ElevatorSimulation.getTimestamp(); 
		formatString += "Elevator %d is traveling to floor %d to %s passengers.\n";
		System.out.printf(formatString, elevator, destinationFloor, action);
	}
	
	private void printArrivalMessage(int elevator, int floor, String action) {
		String formatString = ElevatorSimulation.getTimestamp(); 
		formatString += "Elevator %d has arrived on floor %d to %s passengers.\n";
		System.out.printf(formatString, elevator, floor, action);
	}
	
	//prints the state of this elevator
	public void printState() {
		String indent = "    ";
		System.out.println("----------------");
		System.out.println("Elevator " + elevatorID + ":");
		System.out.println(indent + "Total passengers that entered the elevator: " + totalLoadedPassengers);
		for (int i = 0; i < unloadedPassengers.length; i++){
			System.out.println(indent + indent + "Total passengers that exited the elevator on floor "
					+ i + ": "+ unloadedPassengers[i]);
		}

		System.out.println(indent + "Current number of passengers heading to any floor: " + numPassengers);
		System.out.println(indent + "Total passengers that exited the elevator: " + totalUnloadedPassengers);
		System.out.println();	
	}
	
	public int getID() { return this.elevatorID;}
}
