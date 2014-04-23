package beans;

import java.util.Random;

import org.apache.log4j.Logger;

import stateEnums.ElevatorTrasportationState;
import stateEnums.MovingState;
import stateEnums.PassengerTransportationState;
import stateEnums.PlaceState;
import elevatorProject.GUI.GUI;
import elevatorProject.GUI.MoveInPass;
import elevatorProject.GUI.MoveOutPass;

/**
 *Created passenger and contain methods for entering in elevator or exiting from elevator  
 */
public class Passenger {
	private final int passengerID;
	private final int destinationStorey;
	private final int startStorey;
	private final String BOARDING_OF_PASSENGER   = "BOARDING_OF_PASSENGER "; 
	private final String DEBOARDING_OF_PASSENGER = "DEBOARDING_OF_PASSENGER "; 
	private final String PASS_ID    = " passangerID ";
	private final String ON_STOREY  = " on storey - ";
	private final String OPEN_BRACE = " ( ";
	private final String CLOSE_BRACE = " ) ";
	private final String THREAD_ERROR  = " - thread ending with error  - ";
	private boolean asked = false;
	private MovingState movingOperation;
	private PlaceState placeState = PlaceState.ON_START_STOREY;
	private Random random = new Random();
	private Logger passengerLogger ;
	private Building building;
	private Thread threadDrawMoveToElevator;
	private Thread threadDrawMoveFromElevator;
	private GUI gui;
	private StringBuilder message;
	private PassengerTransportationState transportationState = PassengerTransportationState.NOT_STARTED; 
		

	
	
	public Passenger(int id, int maxValue, Building building, GUI gui){
		
		passengerID = id;
		this.destinationStorey = setDestinationStorey(maxValue);
		this.startStorey = setStartStorey(maxValue, destinationStorey);
		{
			if (startStorey < destinationStorey){
				movingOperation = MovingState.MOVE_UP;
			}else{
				movingOperation = MovingState.MOVE_DOWN;
			}
		}
		this.building = building;
		this.gui = gui;
		
		
		threadDrawMoveFromElevator = new Thread(new MoveOutPass(gui ));
		threadDrawMoveToElevator =  new Thread(new MoveInPass(gui));
		
		passengerLogger = Logger.getLogger(Passenger.class);
	}


	/**
	 * Set isAsked state
	 * @param asked boolean  value
	 */
	public void setAsked(boolean asked) {
		this.asked = asked;
	}
	
	/**
	 * @return asked state boolean value
	 */
	public boolean isAsked() {
		return asked;
	}
	
	/**
	 * Set random destination floor for passenger 
	 * @param maxValue max stories value 
	 * @return value of floor
	 */
	private int setDestinationStorey(int maxValue ){
		return (random.nextInt(maxValue) + 1);
	}
	/**
	 * @return destination floor
	 */
	public int getDestinationStorey() {
		return destinationStorey;
	}

	
	/**
	 * Set random start floor for passenger exclude destination floor
	 * @param maxValue max stories value 
	 * @param excludeNumber destination floor
	 * @return value of start floor
	 */
	private int setStartStorey(int maxValue, int excludeNumber){
		
		int storey ;
		do{
			storey = (random.nextInt(maxValue) +1);
		}
		while(storey == excludeNumber);
		
		return storey;
	}
	
	/**
	 * @return start floor
	 */
	public int getStartStorey() {
		return startStorey;
	}

	/**
	 * @return PassengerTransportationState
	 */
	public PassengerTransportationState getTransportationState() {
		return transportationState;
	}
	/**
	 * Set transportation state for passenger
	 * @param transportationState instance of PassengerTransportationState
	 */
	public void setTransportationState(PassengerTransportationState transportationState) {
		this.transportationState = transportationState;
	}
	
	/**
	 * @return passenger ID
	 */
	public int getPassengerID() {
		return passengerID;
	}
	
	/**
	 * Sets the direction of movement for passenger
	 * @param movingOperation instance of MovingState
	 */
	public void setMovingOperation(MovingState movingOperation) {
		this.movingOperation = movingOperation;
	}
	
	/**
	 * @return instance of MovingState, the direction of movement for passenger
	 */
	public MovingState getMovingOperation() {
		return movingOperation;
	}
	
	/**
	 * Sets placed state for passenger
	 * @param placeState instance of  PlaceState
	 */
	public void setPlaceState(PlaceState placeState) {
		this.placeState = placeState;
	}
	
	/**
	 * @return instance of  PlaceState 
	 */
    public PlaceState getPlaceState() {
		return placeState;
	}

    /**
     *Decide which action should perform passenger (enter, exit or wait)
     * @param syncIn monitor for awaking or sleeping threads who current floor is starting floor
     * @param syncOut monitor for awaking or sleeping threads who current floor is destination floor
     */
	public void move(Object syncIn,Object syncOut){
		 
		boolean passOnStartStorey = this.getPlaceState().equals(PlaceState.ON_START_STOREY );
		boolean passOnElevator = this.getPlaceState().equals(PlaceState.ON_ELEVATOR );
		boolean elevatorBoadingState = building.getElevator().getElevatorTrasportationState().equals(ElevatorTrasportationState.BOARDING_OF_PASSENGER);
		boolean elevatorDeboadingState = building.getElevator().getElevatorTrasportationState().equals(ElevatorTrasportationState.DEBOARDING_OF_PASSENGER);
		
		boolean isEntered = passOnStartStorey & elevatorBoadingState ;
		boolean isExited = passOnElevator & elevatorDeboadingState;
		 
		//if the passenger is not suitable neither enter or leave his waiting 
		while((!isEntered) & (!isExited)){
			 
			if(gui.isAborted()){
				break;
			}
			try {
				if(passOnStartStorey){
			
					synchronized (syncIn) {
						syncIn.wait();
					}
				}
				 
				if(passOnElevator){
		
					synchronized (syncOut) {
						syncOut.wait();
					}
				}
			
			} catch (InterruptedException e) {
				passengerLogger.error(Thread.currentThread().getName() + THREAD_ERROR + e.getMessage());
				
			}
				
			passOnStartStorey = this.getPlaceState().equals(PlaceState.ON_START_STOREY );
			passOnElevator = this.getPlaceState().equals(PlaceState.ON_ELEVATOR );
			elevatorBoadingState = building.getElevator().getElevatorTrasportationState().equals(ElevatorTrasportationState.BOARDING_OF_PASSENGER);
			elevatorDeboadingState = building.getElevator().getElevatorTrasportationState().equals(ElevatorTrasportationState.DEBOARDING_OF_PASSENGER);
			
			isEntered = passOnStartStorey & elevatorBoadingState ;
			isExited = passOnElevator & elevatorDeboadingState;
		}
			 
		if (isEntered ){
			this.moveIn(syncIn,syncOut);
		}
	
		if(isExited){
			this.moveOut(syncOut);
		}
	}
	   
	/**
	 * Tries moving passenger from start container to elevator container
	 * @param syncIn monitor for awaking or sleeping threads who current floor is starting floor
     * @param syncOut monitor for awaking or sleeping threads who current floor is destination floor
	 */
	public void  moveIn(Object syncIn,Object syncOut){
    	
		synchronized (syncIn) {
				
			int storey = building.getElevator().getCurrentStorey();
			boolean hasMove = building.getElevator().getController().canMoveIn( this, building);
			
			// asked state will be true if passenger tries moving in this floor
	    	while(asked |(!hasMove)){
	    		try {
	    			
	    			if(gui.isAborted()){
		   				 break;
		   			}//registered attempt to move
	    			if(!asked){
	    				asked = true;
	    			}
	    			syncIn.wait();
	    		
	    		} catch (InterruptedException e) {
	    			
	    			passengerLogger.error(Thread.currentThread().getName() + THREAD_ERROR + e.getMessage());
	    		}
	    		hasMove = building.getElevator().getController().canMoveIn( this, building);
	    	}
    	
    	
    		passengerLogger.info(getMessage(BOARDING_OF_PASSENGER, storey));
    		
    		threadDrawMoveToElevator.start();
    		
	    
    		while(!threadDrawMoveToElevator.getState().equals(Thread.State.TERMINATED)){
    			
    			if(gui.isAborted()){
					 
					this.setTransportationState(PassengerTransportationState.ABORTED);
					break;
				}else{
					try {
						syncIn.wait(100);
					
					} catch (InterruptedException e) {
						passengerLogger.error(Thread.currentThread().getName() + THREAD_ERROR + e.getMessage());
						
					}
				}
    		}
    		
	    	building.getElevator().getElevatorContainer().add(this);
    		building.getStoreys()[storey - 1].getDispatchStoryContainer().remove(this);
    		this.setPlaceState(PlaceState.ON_ELEVATOR);
	    }
		//must sleeping on monitor of destination floor 
		synchronized (syncOut) {
			
	    	try {
	    		syncOut.wait();
    		} catch (InterruptedException e) {
    		
	    		passengerLogger.error(Thread.currentThread().getName() + THREAD_ERROR + e.getMessage());
    		}
	    }
	}
    
	/**
	 * Tries moving passenger from elevator container to destination container
	 * @param syncIn monitor for awaking or sleeping threads who current floor is starting floor
     * @param syncOut monitor for awaking or sleeping threads who current floor is destination floor
	 */
	public void moveOut(Object syncOut){
		
		 synchronized (syncOut) {
			 int storey = building.getElevator().getCurrentStorey();
    	
			 while(!building.getElevator().getController().canMoveOut(this, building)){
	    		try {
	    			if(gui.isAborted()){
	    				break;
	   			}	//registered attempt to move
	    			if(!asked){
	    				asked = true;
	    			}
	    			syncOut.wait();
	    		} catch (InterruptedException e) {
	    			passengerLogger.error(Thread.currentThread().getName() + THREAD_ERROR + e.getMessage());
	    			
	    		}
	    		
	    	}	
	    		passengerLogger.info( getMessage(DEBOARDING_OF_PASSENGER, storey));
	    		
	    		threadDrawMoveFromElevator.start();
				
				while(!threadDrawMoveFromElevator.getState().equals(Thread.State.TERMINATED)){
					
					if(gui.isAborted()){
						this.setTransportationState(PassengerTransportationState.ABORTED);
						break;
					}else{
						try {
						syncOut.wait(100);

						} catch (InterruptedException e) {
							passengerLogger.error(Thread.currentThread().getName() + THREAD_ERROR + e.getMessage());
							
						}
					}	
				}
				    		
	    		building.getStoreys()[storey - 1].getArrivalStoryContainer().add(this);
				building.getElevator().getElevatorContainer().remove(this);
	    		this.setPlaceState(PlaceState.ON_DISPATH_STOREY);
	    		
		 }
	}
	
	/**
	 * @return message for text area in user interface 
	 */
	private String getMessage(String elevatorState, int storey){
		
		message = new StringBuilder();
		message.append(elevatorState );
		message.append(OPEN_BRACE);
		message.append(PASS_ID);
		message.append(this.passengerID);
		message.append(ON_STOREY);
		message.append(storey);
		message.append(CLOSE_BRACE);
		
		
		return message.toString();
		
	}
}
