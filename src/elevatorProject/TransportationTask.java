package elevatorProject;

import org.apache.log4j.Logger;
import stateEnums.PassengerTransportationState;
import stateEnums.PlaceState;
import beans.Building;
import beans.Passenger;
import elevatorProject.GUI.GUI;
/**
 *performs the full  moving  cycle of passenger from the start container  in the container arrival
  */

public class TransportationTask implements Runnable {


	private final String THREAD_ERROR  = "- thread ending with error  - ";
	private final String PASSENGER = "Passenger - ";
	private final String NEW_LINE = "\r\n";
	private final String SPACE = " ";
	private StringBuilder message;
	private Passenger 	 passenger;
	private	Object syncIn ;
	private Object syncOut ;
	private GUI gui ;
	private Logger logger = Logger.getLogger(TransportationTask.class);

	public TransportationTask() {
	
	}

	public TransportationTask(Passenger passenger, Building building, GUI gui) {
		
		this.passenger = passenger;
		this.gui = gui;
		
		syncIn = building.getElevator().getController().getLocks()[passenger.getStartStorey() - 1];
		syncOut = building.getElevator().getController().getLocks()[passenger.getDestinationStorey() - 1];
	}

	/**
	 * perform moving passenger between containers and check pressing "abort" button  
	 */
	@Override
	public void run() {
		
		synchronized (syncIn) {
		
	    	try {
				syncIn.wait();
				
			} catch (InterruptedException e) {
				logger.error(Thread.currentThread().getName() + THREAD_ERROR + e.getMessage());
			}
		}
			
		passenger.setTransportationState(PassengerTransportationState.IN_PROGRESS);
		gui.getArea().setText(getMessage());
			
		while(!passenger.getPlaceState().equals(PlaceState.ON_DISPATH_STOREY)  ){
				
			if(gui.isAborted()){
				break;
			}
			passenger.move(syncIn, syncOut);
		}
		
			
		if(gui.isAborted()){

			passenger.setTransportationState(PassengerTransportationState.ABORTED);
			gui.getArea().setText(getMessage());
		}else{
				passenger.setTransportationState(PassengerTransportationState.COMPLETED);
				gui.getArea().setText(getMessage());
		}
	}
	
	/**
	 * 
	 * @return message for text area in user interface 
	 */
	private String getMessage(){
		message = new StringBuilder();
		message.append(PASSENGER + passenger.getPassengerID());
		message.append(SPACE);
		message.append(passenger.getTransportationState());
		message.append(NEW_LINE);
		
		return message.toString();
		
	}
}