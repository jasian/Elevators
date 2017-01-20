//Zachary Lee 55104210
//Jason Kandu 23434725

// An ElevatorEvent consists of a sequence of:
// 		1. Floor traversal
// 		2. Passenger unloading (if exists)
// 		3. Passenger loading
public class ElevatorEvent {
	// destination floor
	private int destination;
	// Time that this event will be complete.
	private int expectedArrival;
	
	public ElevatorEvent(int destination, int expectedArrival) {
		this.destination = destination;
		this.expectedArrival = expectedArrival;
	}
	
	public int getDestination() { return this.destination;}
	public int getExpectedArrival() { return this.expectedArrival;}
}
