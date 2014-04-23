package beans;
/**
 * Performing checking condition for entering or exiting passengers in  elevator, and manages the process awakening of threads  
 */
import java.lang.Thread.State;

import org.apache.log4j.Logger;

import stateEnums.ElevatorTrasportationState;

public class Controller {

	private final String THREAD_ERROR  = "- thread ending with error  - ";
	private Object [] locks;
	private  Logger logger = Logger.getLogger(Controller.class);
		
	public Controller() {
		super();
	
	}
	
	public Controller(int syncNumbers) {
		locks = new Object[syncNumbers];
		for (int i = 0; i < syncNumbers; i++){
			locks[i] = new Object();
		}
	}

	
	public Object[] getLocks() {
		return locks;
	}
	
	/**
	 * Checking request from threads for entering in elevator
	 * @param passenger instance of Passenger
	 * @param building instance of Building
	 * @return boolean value
	 */
	public  boolean canMoveIn(Passenger passenger, Building building){
			
		boolean isBoadingState = building.getElevator().getElevatorTrasportationState().equals(ElevatorTrasportationState.BOARDING_OF_PASSENGER);
		
		boolean isNotFull = building.getElevator().getElevatorCapacity() > building.getElevator().getElevatorContainer().size();
		
		boolean isMyStorey = building.getElevator().getCurrentStorey() == passenger.getStartStorey();
		
		boolean isMyDirection = building.getElevator().getElevatorMovingState().equals(passenger.getMovingOperation());
		
		if (isBoadingState & isNotFull & isMyStorey & isMyDirection){
			return true;
		}else {
				return false;
		}
		
	}
	
	/**
	 * Checking request from threads for exiting from elevator
	 * @param passenger instance of Passenger
	 * @param building instance of Building
	 * @return boolean value
	 */
	public  boolean canMoveOut(Passenger passenger, Building building ){

		Elevator elevator = building.getElevator(); 
		
		boolean isBoadingState = elevator.getElevatorTrasportationState().equals(ElevatorTrasportationState.DEBOARDING_OF_PASSENGER);
		boolean isMyStorey = elevator.getCurrentStorey() == passenger.getDestinationStorey();

		if (isBoadingState & isMyStorey ){
			return true;
		}else {
				return false;
		}
	}
	
	/**
	 * Waits until all the transferred threads  not go into a state of waiting or terminated 
	 * @param threads array of Treads
	 * @return boolean value
	 */
	public boolean isAllThreadsReady(Thread [] threads){
		
		boolean isReady = true;
		Object sync = new Object();
		 
		do	{
			isReady = true;
			try{
				for (int i = 0 ; i < threads.length; i++) {
							
					if(threads[i].getState().equals(State.WAITING) 
							| threads[i].getState().equals(State.TERMINATED)){
						
						isReady &= true;
									
					}else{
						isReady &= false;
					}
				
				}
			
			}catch(NullPointerException e){
					isReady &= false;
					
			}
				
			
			synchronized (sync) {
			
				try {
					sync.wait(100);
				} catch (InterruptedException e) {
					logger.error(Thread.currentThread().getName() + THREAD_ERROR + e.getMessage());
				}
			}
			
		}while (!isReady);
		
		return true;
	} 
	
	
	/**
	 * Release monitor linked with current floor
	 * @param building instance of Building
	 * @param threads array of Threads
	 */
	
	 public  void releaseThreadsFromCurrentStorey(Building building, Thread [] threads){
		
		 isAllThreadsReady(threads);
		 Object sync = locks[building.getElevator().getCurrentStorey() - 1];
		
		
		 synchronized (sync) {
				
			 sync.notifyAll();
		}
		 isAllThreadsReady(threads);
		
	 }
	
	 /**
	  * Release all transferred threads
	  * @param threads array of Threads
	  */
	public  void releaseAllPassengerThreads (Thread [] threads){

		for (Object sync : locks) {
			isAllThreadsReady(threads);

			synchronized (sync) {
				sync.notifyAll();					
				
			}
		}
	}
	
}
