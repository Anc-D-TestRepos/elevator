package beans;

import java.util.ArrayList;
import java.util.List;

import stateEnums.ElevatorTrasportationState;
import stateEnums.MovingState;

/**
 * Create Elevator
 */
public class Elevator {
	private int elevatorCapacity;
	private int currentStorey;
	private ElevatorTrasportationState elevatorTrasportationState = ElevatorTrasportationState.BOARDING_OF_PASSENGER;
	private MovingState elevatorMovingState = MovingState.MOVE_UP;
	private List <Passenger> elevatorContainer = new ArrayList<Passenger>();;
	private Controller controller ;
	
	
	
	
	public Elevator(int elevatorCapacity,int storiesNumber){
		this.elevatorCapacity = elevatorCapacity;
		controller = new Controller(storiesNumber);
		
	}

	/**
	 * @return elevator capacity
	 */
	public int getElevatorCapacity() {
		return elevatorCapacity;
	}

	/**
	 * @return List for elevator container 
	 */
	public List<Passenger> getElevatorContainer() {
		return elevatorContainer;
	}

	/**
	 * @return current floor
	 */
	public int getCurrentStorey() {
		return currentStorey;
	}

	/**
	 * Set transferred value of current floor
	 * @param currentStorey current floor
	 */
	public void setCurrentStorey(int currentStorey) {
		this.currentStorey = currentStorey;
	}

	/**
	 * @return transportation state of elevator
	 */
	public ElevatorTrasportationState getElevatorTrasportationState() {
		return elevatorTrasportationState;
	}
	/**
	 * Set transportation state for elevator
	 * @param elevatorTrasportationState instance ElevatorTrasportationState
	 */

	public void setElevatorTrasportationState(
			ElevatorTrasportationState elevatorTrasportationState) {
		this.elevatorTrasportationState = elevatorTrasportationState;
	}

	/**
	 * @return moving state of elevator
	 */
	public MovingState getElevatorMovingState() {
		return elevatorMovingState;
	}
	
	/**
	 * Set moving state of elevator
	 * @param elevatorMovingState instance MovingState
	 */
	public void setElevatorMovingState(MovingState elevatorMovingState) {
		this.elevatorMovingState = elevatorMovingState;
	}
	
	/**
	 * @return instance of controller
	 */
	public Controller getController() {
		return controller;
	}



}
