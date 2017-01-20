//Zachary Lee 55104210
//Jason Kandu 23434725

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ElevatorSimulation
{
	private BuildingManager manager;
	private int totalSimulationTime;
	private int simulatedSecondRate;
	private ArrayList<PassengerArrival> passengerArrivalList; //keeps track of the passenger arrival behavior throughout simulation
	private ArrayList<Thread> elevatorThreads;
	private ArrayList<Elevator> elevators;
	private static int timeFormatLen;
	
	public ElevatorSimulation(){
		this.manager = new BuildingManager();
		this.totalSimulationTime = 0;
		this.passengerArrivalList = new ArrayList<PassengerArrival>();
		this.elevatorThreads = new ArrayList<Thread>();
		this.elevators = new ArrayList<Elevator>();
		for (int i = 0; i < Constants.NUM_OF_ELEVATORS; i++){
			// create each elevator
			Elevator newElevator = new Elevator(i, manager);
			this.elevators.add(newElevator);
			
			//Instantiates a thread for each elevator
			this.elevatorThreads.add(new Thread(newElevator)); 
		}
		// calculates length of max time for 0-padding
		timeFormatLen = String.valueOf(totalSimulationTime).length();
		
		System.out.println("************************************************************************");
		System.out.println("-----------------------BEGIN SIMULATION---------------------------------");
		System.out.println("************************************************************************");
	}
	
	//Main simulated loop to increment SimClock and manage passenger arrival behavior 
	//Simulation ends when the current simulation time is greater than the total simulation time defined in config file
	public void start(){
		//Updates totalSimulationTime and passengerArrivalList
		readFile();
		startThreads(); //Starts all Elevator threads
		while (SimClock.getTime() < totalSimulationTime){
			manageAllSpawns();
			//Sleep for however long it's supposed to sleep
			try
			{
				Thread.sleep(simulatedSecondRate);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			// Simulation carries out everything that happens in the final second, but it does NOT tick at the end of that final second.
			if (SimClock.getTime() == totalSimulationTime - 1) {
				break;
			}
			SimClock.tick();
		}
		endThreads(); //Interrupts all Elevator threads
	}
	
	//Prints the state of the Elevator and Building floors in Simulation State
	public void printBuildingState() {
		System.out.println("\n************************************************************************");
		System.out.println("Simulation State Statistics");
		System.out.println("Total Simulated Time: " + totalSimulationTime);
		System.out.println("Simulation Rate: " + simulatedSecondRate);
		for (Elevator elevator: elevators) {
			elevator.printState();
		}
		for (BuildingFloor buildingFloor: manager.getFloors()) {
			buildingFloor.printState();
		}		
		System.out.println("************************************************************************");
	}

	//reads ElevatorConfig.txt file to define the passenger arrivals
	public void readFile()
	{
		String s;
		try
		{
			Scanner input = new Scanner(new File("ElevatorConfig.txt"));	//Grabs ElevatorConfig.txt file
			String[] elevatorConfig;
			int lineCounter = 1;
			while (input.hasNextLine()){
				s = input.nextLine();
				elevatorConfig = s.split(";");
				if (lineCounter == 1){		//Line 1 of text file so this sets the 
					totalSimulationTime = Integer.parseInt(s);
					lineCounter++;
					continue;
				} else if (lineCounter == 2) {
					simulatedSecondRate = Integer.parseInt(s);
					lineCounter++;
					continue;
				} else{
					for (int i = 0; i < elevatorConfig.length; i++){
						String[] configNumbers = elevatorConfig[i].split(" "); 
						int numPassengers = Integer.parseInt(configNumbers[0]);
						int destinationFloor = Integer.parseInt(configNumbers[1]);
						int sourceFloor = lineCounter - 3; //Source floor starts on the 3rd line of config file
						int timePeriod = Integer.parseInt(configNumbers[2]);
						PassengerArrival newPassenger = new PassengerArrival(numPassengers, destinationFloor, sourceFloor, timePeriod);
						passengerArrivalList.add(newPassenger);
					}	
					lineCounter++;
				}
			}
			input.close();
			// fix the time format length now that a new format length has been found.
			timeFormatLen = String.valueOf(totalSimulationTime).length();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void manageSpawn(PassengerArrival arrivalObject){
		int timeToSpawn = arrivalObject.getExpectedTimeOfArrival(SimClock.getTime());
		if (SimClock.getTime() == timeToSpawn) {
			int currentFloor = arrivalObject.getSourceFloor();
			int numPeople = arrivalObject.getNumPassengers();
			int destination = arrivalObject.getDestinationFloor();
			if (Constants.VERBOSE) {
				System.out.printf(getTimestamp() + "%d people have arrived on floor %d and are requesting to go to floor %d\n", 
					numPeople, currentFloor, destination);
			}
			manager.getFloor(currentFloor).spawnPeople(numPeople, destination);
		}
	}
	
	private void manageAllSpawns(){
		for (int i = 0; i < passengerArrivalList.size(); i++){
			PassengerArrival arrivalObject = passengerArrivalList.get(i);
			manageSpawn(arrivalObject);
		}
	}
	
	private void startThreads(){
		for (int i = 0; i < Constants.NUM_OF_ELEVATORS; i++){
			elevatorThreads.get(i).start();
		}
	}
	
	private void endThreads(){
		for (int i = 0; i < Constants.NUM_OF_ELEVATORS; i++){
			elevatorThreads.get(i).interrupt();
		}
	}
	
	public static String getTimestamp() {
		String formatString = "%0" + timeFormatLen + "d: ";
		return String.format(formatString, SimClock.getTime());
	}
}
