package elevatorProject.GUI;

import org.apache.log4j.Logger;
import beans.Building;
/**
 * Changes the coordinates for outgoing  passengers   
 */
public class MoveOutPass implements Runnable {
	private  final String THREAD_ERROR  = " - thread ending with error  - ";
	private int      offset = 50;
	private GUI      gui;
	private GUIBuild guiBuild ;
	private Object   sync;
	private Logger   logger = Logger.getLogger(MoveOutPass.class);
	
	
	public MoveOutPass(GUI gui) {
		this.gui = gui;
		this.guiBuild = gui.getGuiBuild();
		sync = guiBuild.getSync();

	}
	
	/**
	 * Launches changed coordinates
	 */
	@Override
	 public void run() {
		synchronized ( sync ) {
				
			if( !gui.isAborted() ){
							
				if(guiBuild.getLoadElevator() > 0){	
					
					do{
						guiBuild.setHasMoved(true);
						guiBuild.exitingPositionByX += offset;
						
						try {
							sync.wait();
						} catch (InterruptedException e) {
							logger.error(Thread.currentThread().getName() + THREAD_ERROR + e.getMessage());
						}
					}
					while(guiBuild.exitingPositionByX <= 680);
				}
			}else{
				guiBuild.getTimer().stop();
			}
			
			if ((guiBuild.exitingPositionByX >= 680) | (guiBuild.getLoadElevator() < 0 )){
				//reset coordinate for moving passenger
				guiBuild.exitingPositionByX = 100;
				guiBuild.setHasMoved( false ); 
						
			}
		}
	}
}