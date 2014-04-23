package elevatorProject.GUI;

import org.apache.log4j.Logger;
import beans.Building;

/**
 * Changes the coordinates for incoming passengers   
 */

public class MoveInPass implements Runnable {
	
	private final String THREAD_ERROR  = " - thread ending with error  - ";
	private	int offset = 50; 
	private Logger   logger = Logger.getLogger(MoveInPass.class);
	private	GUI      gui;
	private	GUIBuild guiBuild ;
	private	Object   sync ;
	
		
	public MoveInPass(GUI gui) {
		this.gui = gui;
		this.guiBuild = gui.getGuiBuild();
		sync = guiBuild.getSync();
			
	}
	
	/**
	 * Launches changed coordinates
	 */
		@Override
	public void run() {
		synchronized (sync) {
						
			if(!gui.isAborted()){
			
				do{
					guiBuild.enteringPositionByX -= offset;
					
					try {
						sync.wait();
					} catch (InterruptedException e) {
						logger.error(Thread.currentThread().getName() + THREAD_ERROR + e.getMessage());
					}
				}
				// " 200 + (45 *( guiBuild.getLoadStartStorey() + 1 )) " - it's needed for correctly display passenger in ending of moving 
				while(guiBuild.enteringPositionByX >= (200 + (45 *( guiBuild.getLoadStartStorey() + 1 ))));
	
			}else{
				guiBuild.getTimer().stop();
			}
			//reset coordinate for moving passenger
			guiBuild.enteringPositionByX = 835;
			sync.notify();
			
		}
	}
}
	
