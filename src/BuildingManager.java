//Zachary Lee 55104210
//Jason Kandu 23434725

public class BuildingManager
{
	private BuildingFloor[] floors; //An array of BuildingFloors representing the state of all floors in the building
	
	public BuildingManager()
	{
		this.floors = new BuildingFloor[Constants.MAX_FLOORS];
		for (int i = 0; i < Constants.MAX_FLOORS; i++) {
			this.floors[i] = new BuildingFloor(i);
		}
	}
	
	// Returns a floor that an elevator can travel to for passenger pickup
	//	synchronized so that elevators won't ever receive the same passenger requests
	//  Returns -1 if there are no elevators that need a pickup.
	private synchronized int checkRequests() {
		for (int i = 0; i < Constants.MAX_FLOORS; i++) {
			if (floors[i].passengersWaiting()) {
				// if no elevator is approaching this floor
				if (floors[i].getApproachingElevator() == -1) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public synchronized int assignPickup(int elevatorID) {
		int pickupFloor = checkRequests();
		if (pickupFloor != -1) {
			floors[pickupFloor].setApproachingElevator(elevatorID);
		}
		return pickupFloor;
	}
	
	//Getters and Setters
	public synchronized BuildingFloor[] getFloors()	{return this.floors;}
	public synchronized BuildingFloor getFloor(int floorIndex) {return this.floors[floorIndex];}
	public synchronized void setFloors(BuildingFloor[] floors) {this.floors = floors;}
	public synchronized void setFloor(BuildingFloor floor, int floorIndex) {this.floors[floorIndex] = floor;}
}
