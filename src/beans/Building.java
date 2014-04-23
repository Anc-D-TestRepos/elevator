package beans;
/**
 * Create Building
 */
public class Building {
	
	private Storey [] storeys;
	private Elevator elevator;
	
	public Building() {
		super();
		
	}

	public Building( int storeysNumber, int elevatorCapacity ){
		storeys = new Storey [storeysNumber];
		
		for (int i = 0; i < storeysNumber ;i++){
			
			storeys[i]=new Storey(i + 1);
			
		}
		
		elevator = new Elevator(elevatorCapacity, storeysNumber);
	}
	
	/**
	 * @return array of Storeys
	 */
	public Storey[] getStoreys() {
		return storeys;
	}
	/**
	 * @return instance of Elevator
	 */
	public Elevator getElevator() {
		return elevator;
	}
}
