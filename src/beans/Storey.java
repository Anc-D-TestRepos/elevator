package beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Create Storey
 */
public class Storey {
		
	private int storeyNumber;

	private List <Passenger> dispatchStoryContainer = new ArrayList<>();
	private List <Passenger> arrivalStoryContainer = new ArrayList<>();
	
	
	
	
	public Storey() {
		super();
		
	}

	public Storey(int storeyNumber){
		this.storeyNumber = storeyNumber;
	}
	
	/**
	 * @return numbers of floor
	 */
	public int getStoreyNumber() {
		return storeyNumber;
	}
	/**
	 * Set number of floor
	 * @param storeyNumber number of floor 
	 */
	public void setStoreyNumber(int storeyNumber) {
		this.storeyNumber = storeyNumber;
	}
	
	/**
	 * @return List for dispatch container
	 */
	public List<Passenger> getDispatchStoryContainer() {
		return dispatchStoryContainer;
	}
	
	/**
	 * @return List for arrival container
	 */
	public List<Passenger> getArrivalStoryContainer() {
		return arrivalStoryContainer;
	}

	
	
	

}
