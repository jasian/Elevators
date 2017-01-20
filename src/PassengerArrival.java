//Zachary Lee 55104210
//Jason Kandu 23434725

// Class to handle passenger arrival behavior
public class PassengerArrival {
	// Number of passengers that will request an elevator for this specific behavior.
	private int numPassengers;
	private int destinationFloor;
	private int sourceFloor;	//Floor where the passengers spawn at
	// Represents how often these passengers will request elevator access.
	private int timePeriod;
	// Represents the simulated time where the next batch of passengers will enter the simulation.
	private int expectedTimeOfArrival;
	
	
	public PassengerArrival(int numPassengers, int destinationFloor, int sourceFloor, int timePeriod) {
		this.numPassengers = numPassengers;
		this.destinationFloor = destinationFloor;
		this.timePeriod = timePeriod;
		this.sourceFloor = sourceFloor;
		// Assumes that PassengerArrival objects are created at t=0
		this.expectedTimeOfArrival = timePeriod;
	}
	
	public int getNumPassengers() { return this.numPassengers;}
	public int getDestinationFloor() { return this.destinationFloor;}
	public int getSourceFloor() { return this.sourceFloor;}
	public int getTimePeriod() { return this.timePeriod;} 
	
	// Recursive function that updates the expected time of arrival until
	// 		it returns the next future time of arrival.
	public int getExpectedTimeOfArrival(int currentTime) {
		if (expectedTimeOfArrival >= currentTime) {
			return expectedTimeOfArrival;
		}
		else {
			expectedTimeOfArrival += timePeriod;
			return getExpectedTimeOfArrival(currentTime);
		}
	}
	
}
