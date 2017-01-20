//Zachary Lee 55104210
//Jason Kandu 23434725

public class SimClock {
	private static int simulatedTime;
	
	public SimClock() {
		this.simulatedTime = 0;
	}
	
	public static void tick() {
		simulatedTime += 1;
	}
	
	public static int getTime() {return simulatedTime;}
}
