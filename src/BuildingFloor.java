//Zachary Lee 55104210
//Jason Kandu 23434725

public class BuildingFloor
{	
	//Private variables
	private int floorID;
	private int[] totalDestinationRequests; //Numbers of people who have requested to take the elevators to other floors
	private int[] arrivedPassengers; //total # of passengers that exited the elevator on this floor from another floor
	private int[] passengerRequests; //passengers waiting to take an elevator to floor i
	// -1 means no elevator is approaching
	private int approachingElevator; //elevator ID of elevator currently heading towards floor for passenger pickup
	
	public BuildingFloor(int floorID) {
		// tracks the floor number
		this.floorID = floorID;
		this.totalDestinationRequests = new int[Constants.MAX_FLOORS];
		this.arrivedPassengers = new int[Constants.MAX_FLOORS];
		this.passengerRequests = new int[Constants.MAX_FLOORS];
		this.approachingElevator = -1;
	}

	// Returns True if there are passengers waiting. False otherwise.
	public boolean passengersWaiting() {
		for (int otherFloor = 0; otherFloor < Constants.MAX_FLOORS; otherFloor++) {
			if (passengerRequests[otherFloor] > 0) {
				return true;
			}
		}
		return false;
	}
	
	//Changes the states in BuildingFloor when unloading passengers in Elevator
	public void unloadPassengers(Elevator elevator, int numberOfPassengers, int oldFloorIndex) {
		arrivedPassengers[oldFloorIndex] += numberOfPassengers;
		printUnloadMessage(elevator.getID(), numberOfPassengers, floorID);
	}
	
	//Changes the states in BuildingFloor when loading passengers in Elevator and then return the number of passengers loaded
	public int loadPassengers(Elevator elevator, int currentSimTime) {
		int loadedPassengers = 0;
		if (upRequests() > 0) {
			// for all floors being requested above this one, add ElevatorEvents for those
			for (int i = floorID; i < Constants.MAX_FLOORS; i++) {
				if (passengerRequests[i] > 0) {
					if (Constants.VERBOSE) {
						printLoadMessage(elevator.getID(), passengerRequests[i], i);
					}
					elevator.processRequest(i, passengerRequests[i], currentSimTime);
					loadedPassengers += passengerRequests[i];
					passengerRequests[i] = 0; // clear the passenger requests
				}
			}
		} else if (downRequests() > 0) {
			// for all floors being requested below this one, add ElevatorEvents for those
			for (int i = floorID; i >= 0; i--) {
				if (passengerRequests[i] > 0) {
					if (Constants.VERBOSE) {
						printLoadMessage(elevator.getID(), passengerRequests[i], i);
					}
					elevator.processRequest(i, passengerRequests[i], currentSimTime);
					loadedPassengers += passengerRequests[i];
					passengerRequests[i] = 0; // clear the passenger requests
				}
			}
		} else {
			System.out.println("****************");
			System.out.println("This shouldn't happen. An elevator traveled to a floor for pickup, but no passengers are requesting the elevator.");
			System.out.println("****************");
		}
		return loadedPassengers;
	}
	
	// Returns the number of requests for a floor higher than this floor.
	private int upRequests() {
		int numberOfRequests = 0;
		for (int i = 0; i < Constants.MAX_FLOORS; i++) {
			if (i > floorID) {
				numberOfRequests += passengerRequests[i];
			}
		}
		return numberOfRequests;
	}
	
	// Returns the number of requests for a floor lower than this floor.
	private int downRequests() {
		int numberOfRequests = 0;
		for (int i = 0; i < Constants.MAX_FLOORS; i++) {
			if (i < floorID) {
				numberOfRequests += passengerRequests[i];
			}
		}
		return numberOfRequests;
	}
	
	// Spawns people on this floor. People need to request other floors.
	public void spawnPeople(int numPeople, int destination) {
		if (destination == floorID) {
			// this should never happen
			System.out.println("****************");
			System.out.println("passengers are trying to go to their own floor!?!");
			System.out.println("****************");
		} else {
			passengerRequests[destination] += numPeople;
			totalDestinationRequests[destination] += numPeople;
		}
	}
	
	// prints every time people load onto the elevator for a specific destination
	private void printLoadMessage(int elevator, int numPassengers, int destination) {
		String formatString = ElevatorSimulation.getTimestamp(); 
		formatString += "%d passengers have boarded elevator %d on floor %d to go to floor %d.\n";
		System.out.printf(formatString, numPassengers, elevator, floorID, destination);
	}
	
	// prints every time people unload onto the elevator for a specific destination
	private void printUnloadMessage(int elevatorID, int numPassengers, int destinationFloor) {
		String formatString = ElevatorSimulation.getTimestamp();
		formatString += "%d passengers have exited elevator %d on floor %s.\n";
		System.out.printf(formatString, numPassengers, elevatorID, destinationFloor);
	}
	
	//prints the state of this building floor
	public void printState() {
		String indent = "    ";
		System.out.println("----------------");
		System.out.println("Floor " + floorID + ":");	
		System.out.println(indent + "Total number of passengers requesting access: " + sum(totalDestinationRequests));
		System.out.println(indent + "Current number of passengers waiting: " + sum(passengerRequests));
		System.out.println(indent + "Total number of passengers that arrived on this floor: " + sum(arrivedPassengers));
		if (approachingElevator == -1)
			System.out.println(indent + "Elevator currently heading towards floor for passenger pickup: None");
		else
			System.out.println(indent + "Elevator currently heading towards floor for passenger pickup: " + approachingElevator);
		
		System.out.println();	
	}
	
	//Calculates the sum of all the values in the tempArray
	private int sum(int[] tempArray){
		int sum = 0;
		for (int value: tempArray)
			sum += value;
		return sum;
	}	
	
	//Getters and Setters
	public int[] getTotalDestinationRequests() {return this.totalDestinationRequests;}
	public int[] getArrivedPassengers() {return this.arrivedPassengers;}
	public int[] getPassengerRequests() {return this.passengerRequests;}
	public int getApproachingElevator() {return this.approachingElevator;}
	public void setTotalDestationRequests(int[] totalDestinationRequests) {this.totalDestinationRequests = totalDestinationRequests;}
	public void setArrivedPassengers(int[] arrivedPassengers) {this.arrivedPassengers = arrivedPassengers;}
	public void setPassengerRequests(int[] passengerRequests) {this.passengerRequests = passengerRequests;}
	public void setApproachingElevator(int approachingElevator) {this.approachingElevator = approachingElevator;}
	public void clearApproachingElevator(){this.approachingElevator = -1;}
	
}
