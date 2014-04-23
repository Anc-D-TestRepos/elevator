

import java.util.Iterator;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import stateEnums.ElevatorTrasportationState;
import stateEnums.LogOperationState;
import stateEnums.MovingState;
import stateEnums.PassengerTransportationState;
import beans.Building;
import beans.Elevator;
import beans.Passenger;
import beans.Storey;
import elevatorProject.TransportationTask;
import elevatorProject.GUI.GUI;

/**
 * Perform reading input data, start moving elevator and check condition for stopped elevator  
 */

public class Runner  {
	
	private static final  int   MIN_STOREY_NUMB = 2;
	private static final  int   MIN_ELEVATOR_CAPACITY = 1;
	private static final  int   MIN_PASSENGER_NUMB = 1;
	private static final String CONFIG = "config";
	private static final String STOREYS_NUMBER = "storeysNumber";
	private static final String ELEVATOR_CAPACITY = "elevatorCapacity";
	private static final String PASSENGER_NUMBER = "passengerNumber";
	private static final String BOOST = "animationBoost";
	private static final String OPEN_BRACE = "(";
	private static final String CLOSE_BRACE = ")";
	private static final String FROM_STOREY = " from storey - ";
	private static final String TO_STOREY = " to storey - ";
	private static final String NEW_LINE = "\n";
	private static final String INPUT_ERROR = " Incorrect data from input file "; 
	private static final String THREAD_ERROR = "- thread ending with error  - ";
	private static       Logger logger ;
	
	{
	DOMConfigurator.configure(getClass().getClassLoader().getResource("resources/log4j.xml"));	
	}
	
	
	public static void main(String[] args) {
		
		logger= Logger.getLogger(Runner.class);
		int storiesNumber = 0;
		int elevatorCapacity;
		int passengerNumber;
		int animationBoost;
		
		Thread [] threads;
		Passenger [] initPassengerArray; 
		
		
		ResourceBundle resurce = ResourceBundle.getBundle(CONFIG);
		
		storiesNumber = Integer.parseInt(resurce.getString(STOREYS_NUMBER));
					
		elevatorCapacity = Integer.parseInt(resurce.getString(ELEVATOR_CAPACITY));
		
		passengerNumber = Integer.parseInt(resurce.getString(PASSENGER_NUMBER));
		
		animationBoost = Integer.parseInt(resurce.getString(BOOST));
		
		threads = new Thread[passengerNumber];
		
		initPassengerArray = new Passenger[passengerNumber];


	 
		if((storiesNumber < MIN_STOREY_NUMB)
							|( elevatorCapacity < MIN_ELEVATOR_CAPACITY ) 
							       				| (passengerNumber < MIN_PASSENGER_NUMB)){
			
			logger.error(INPUT_ERROR);

			System.exit(1);
		}
	
		Building building = new Building(storiesNumber, elevatorCapacity);
		
	
		GUI gui = new GUI(building,animationBoost);
		Thread guiThread = new Thread(gui);

		guiThread.start();
		
		while (!guiThread.getState().equals(Thread.State.TERMINATED)){
					
			threadSleep(100);
		}
						
		initPassengerArray = createPassengers(storiesNumber, passengerNumber, building, gui);
			
		placePassengersOnStartStorey(storiesNumber, initPassengerArray, building );
	
		threads = startPassengerThreads(building, gui, passengerNumber);

		building.getElevator().getController().isAllThreadsReady(threads);
			
		if(animationBoost > 0){
			while(!gui.getStartState()){
				threadSleep(100);
			}
		}else{
			
			gui.getGuiBuild().getTimer().start();
		}
		
		logger.info(LogOperationState.STARTING_TRANSPORTATION);
				
		do {
			
			building.getElevator().setElevatorMovingState(MovingState.MOVE_UP);
			
			for(int storey= 1; storey < storiesNumber; storey++ ){
				
				if(!canMoveElevator( building, passengerNumber, threads, gui )){
					
					break;
				} 
				
				clearPassengerRequest(initPassengerArray);
				building.getElevator().setCurrentStorey(storey);
			
				building.getElevator().setElevatorTrasportationState(ElevatorTrasportationState.DEBOARDING_OF_PASSENGER);
				building.getElevator().getController().releaseThreadsFromCurrentStorey(building,threads);
				
				clearPassengerRequest(initPassengerArray);
				
				building.getElevator().setElevatorTrasportationState(ElevatorTrasportationState.BOARDING_OF_PASSENGER);					
				building.getElevator().getController().releaseThreadsFromCurrentStorey(building,threads);
				
				logger.info(LogOperationState.MOVING_ELEVATOR + OPEN_BRACE + FROM_STOREY + storey + TO_STOREY + (storey+1)+ CLOSE_BRACE) ;
			
				
			}
			
			building.getElevator().setElevatorMovingState(MovingState.MOVE_DOWN);
			
			for(int x = storiesNumber; x > 1; x-- ){

				if(!canMoveElevator( building, passengerNumber, threads, gui )){
					break;
				} 
				clearPassengerRequest(initPassengerArray);
				
				building.getElevator().setCurrentStorey(x);
				
				building.getElevator().setElevatorTrasportationState(ElevatorTrasportationState.DEBOARDING_OF_PASSENGER);
				building.getElevator().getController().releaseThreadsFromCurrentStorey(building,threads);
				
				clearPassengerRequest(initPassengerArray);
				
				building.getElevator().setElevatorTrasportationState(ElevatorTrasportationState.BOARDING_OF_PASSENGER);
				building.getElevator().getController().releaseThreadsFromCurrentStorey(building,threads);
				
				logger.info(LogOperationState.MOVING_ELEVATOR + OPEN_BRACE + FROM_STOREY + x + TO_STOREY +( x - 1 )+ CLOSE_BRACE);
						
			}
		}		
		while ((isTaskNotComplit(building, passengerNumber)) & (!gui.isAborted()));
		
		if(!gui.isAborted()){
			gui.getArea().setText(LogOperationState.COMPLETION_TRANSPORTATION + NEW_LINE);
			logger.info(LogOperationState.COMPLETION_TRANSPORTATION);
		}else{
			gui.getArea().setText(LogOperationState.ABORTING_TRANSPORTATION + NEW_LINE);
			logger.info("ABORTING_TRANSPORTATION");
		}
		
		gui.getAbort().setVisible(false);
		gui.getView().setVisible(true);
		gui.getGuiBuild().getTimer().stop();
		
	}
	/**
	 * Create array of passenger
	 * @param storiesNumber number of stories
	 * @param passengerNumber number of passenger
	 * @param building instance of Building
	 * @param gui instance of GUI
	 * @return array of Passenger
	 */
	private static Passenger [] createPassengers(int storeysNumber, int passengerNumber, Building building, GUI gui){
		
		Passenger passengerArray[] = new Passenger[passengerNumber];
		for (int i = 0; i < passengerNumber; i++){
		
			passengerArray[i] = new Passenger(i + 100, storeysNumber, building, gui );
			
		}
		return passengerArray;
	}
	
	/**
	 * Perform placing passengers by stories of building
	 * @param storeysNumber number of stories
	 * @param initPassenger Array array of Passenger
	 * @param building instance of Building
	 */
	private static void placePassengersOnStartStorey(int storeysNumber, Passenger [] initPassengerArray, Building building){
	
	for( int i = 0; i < storeysNumber; i++){
			
			for (Passenger pass : initPassengerArray) {
				if(pass.getStartStorey() == ( i + 1 )){
					building.getStoreys()[i].getDispatchStoryContainer().add(pass);
				}
			}
		}
		
	}
				
	/**
	 * Create threads for passenger instance and  start him
	 * @param building instance of Building
	 * @param gui instance of GUI
	 * @param passengerNumber number of passenger
	 * @return array of Threads
	 */
	
	private static Thread [] startPassengerThreads(Building building, GUI gui, int passengerNumber){
		int countThreads = 0 ; 
		Thread [] threads  = new Thread [passengerNumber];
		
		for(int i = 0; i < building.getStoreys().length; i++){
			
			Passenger passenger;
		
			Iterator <Passenger> iterator = building.getStoreys()[i].getDispatchStoryContainer().iterator();
			
			while(iterator.hasNext()){
				passenger = iterator.next();
	
				Thread thread = new Thread(	new TransportationTask(passenger, building, gui));
				
				threads[countThreads] = thread;
				countThreads++;
	
				thread.start();
			}
		}
		return threads ;
	}
	
	/**
	 *  Stops Thread on the transferred  time
	 * @param time  int value of time 
	 */
	private static void threadSleep(int time){
		
		try {
			Thread.sleep(time);
			
		} catch (InterruptedException e) {
			logger.error(Thread.currentThread().getName() + THREAD_ERROR + e.getMessage());
		}
	}
	
	/**
	 * Set variable isAsked of passenger in false status 
	 * @param passengers array of Passenger
	 */
	private static void clearPassengerRequest(Passenger [] passengers){
		for (Passenger pass : passengers) {
			
			pass.setAsked(false);
			
		}
	}
	
	/**
	 *Check pressed abort button and condition of validation completing transportation 
	 * @param building instance of Building
	 * @param passengerNumber passengerNumber number of passenger
	 * @param threads array of Threads
	 * @param gui instance of GUI
	 * @return boolean value
	 */

	private static boolean canMoveElevator(Building building, int passengerNumber, Thread [] threads, GUI gui){
		
		if(gui.isAborted()){
			building.getElevator().getController().releaseAllPassengerThreads(threads);
			return false;
		}
		if(!isTaskNotComplit(building, passengerNumber)){
			return false;
		}
		return true;
	}
	
	/**
	 * Check condition of validation completing transportation 
	 * @param building instance of Building
	 * @param passengerNumber passengerNumber number of passenger
	 * @return boolean value
	 */
	private static boolean isTaskNotComplit (Building building, int passengerNumber){
		
		boolean isNotClearDispathContainer = false;
		boolean isNotClearElevatorContainer = false;
		boolean isNotArrivePassengers = false;
		boolean isMyStorey = false;
		boolean isCompletedState = false;
		int passengerCount = 0;
		Storey [] storeys = building.getStoreys();
		Elevator elevator = building.getElevator();
	
		for (Storey storey : storeys) {
			if(storey.getDispatchStoryContainer().size() > 0){
				isNotClearDispathContainer = true;
			}
		}
		
		if(elevator.getElevatorContainer().size() > 0){
			isNotClearElevatorContainer = true;
		}
		
		for (int i = 0; i <= storeys.length-1; i++) {
			for (Passenger  pass : storeys[i].getArrivalStoryContainer()) {
				
				isMyStorey = pass.getDestinationStorey() == i;
				isCompletedState = pass.getTransportationState().equals(PassengerTransportationState.COMPLETED);
				
				if (!(isMyStorey & isCompletedState)){
					isNotArrivePassengers = true;
				}
				
				passengerCount++;
			}  
			
		}
		
		if(passengerCount == passengerNumber){
			
			return isNotClearDispathContainer & isNotClearElevatorContainer & isNotArrivePassengers;
		}else{
			return true;
		}
	}
}
	
