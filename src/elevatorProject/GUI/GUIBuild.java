package elevatorProject.GUI;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;
import beans.Building;
import elevatorProject.GUI.beans.ElevClose;
import elevatorProject.GUI.beans.ElevOpen;
import elevatorProject.GUI.beans.Pass;

/**
 * Drawing  all stories, passengers status and elevator status
 */
public class GUIBuild 	 extends JPanel implements ActionListener, Runnable {
	
	private int storey;
	private int currentStorey = 0; 
	private int loadArrivalStorey ;
	private int loadStartStorey;
	private int loadElevator;
	private int loadArrivalStoreys [];
	private int loadStartStoreys [];
	private int animationSpeed = 500;
	private int verticalOffsetEnterPassenger = 0;
	private int verticalOffsetExitPassenger = 0;
	private boolean isInit   = true;
	private boolean hasMoved = false;
	private Timer     timer;
	private ElevClose elevClose = new ElevClose();
	private ElevOpen  elevOpen  = new ElevOpen();
	private Pass      pass      = new Pass();
	private Building  building;
	private Object    sync = new Object();
	public int enteringPositionByX = 835;
	public int exitingPositionByX  = 100;
	

	
		public GUIBuild() {
		super();
		
	}
		
		public GUIBuild(Building building, int animationBoost) {
			
			this.building = building;
			storey = building.getStoreys().length;
			
			if(animationBoost > 0){
				animationSpeed /= animationBoost;
			}
	
			loadArrivalStoreys = new int[storey];
			loadStartStoreys=new int[storey];
			
		}
		
		/**
		 * @return array contains number of passenger in arrival stories
		 */
		public int[] getLoadArrivalStoreys() {
			return loadArrivalStoreys;
		}
		
		/**
		 * @return array contains number of passenger in start stories
		 */
		public int[] getLoadStartStoreys() {
			return loadStartStoreys;
		}
		
		/**
		 * @return number of passenger in current arrival floor
		 */
		public int getLoadArrivalStorey() {
			return loadArrivalStorey;
		}
		
		/**
		 * @return number of passenger in current start floor
		 */
		public int getLoadStartStorey() {
			return loadStartStorey;
		}
		/**
		 * @return monitor for synchronous drawing and change passenger status on the floor  
		 */
		public Object getSync() {
			return sync;
		}

		/**
		 * @return boolean value
		 */
		public boolean isHasMoved() {
			return hasMoved;
		}
		
		/**
		 * Set permission for drawing exiting passenger 
		 * @param hasMoved boolean value
		 */
		public void setHasMoved(boolean hasMoved) {
			this.hasMoved = hasMoved;
		}
		
		/**
		 * @return number of passenger in elevator
		 */
		public int getLoadElevator() {
			return loadElevator;
		}

		/**
		 * @return instance of Timer
		 */
		public Timer getTimer() {
			return timer;
		}

		/**
		 * Launches timer for drawing building
		 */
		@Override
		public void run() {	
		 timer = new Timer(animationSpeed, this);
			
			
		}
		
		
		/**
		 * Launches  drawing  all stories, passengers status and elevator status
		 */
		public void paint (Graphics g){
			//create buffer for store number of people
			for (int i = 0 ; i < storey; i++){
				loadArrivalStoreys [i] = building.getStoreys()[i].getArrivalStoryContainer().size();
				loadStartStoreys [i] = building.getStoreys()[i].getDispatchStoryContainer().size();;
				loadElevator = building.getElevator().getElevatorContainer().size();
				
			}
			
			//" ((i - storey) * (-1)) - 1 " it's need for correctly drawing directions of stories with passenger 
			for (int i = 0; i < storey; i++){
				
				loadArrivalStorey = loadArrivalStoreys[((i - storey) * (-1)) - 1];
		
				loadStartStorey = loadStartStoreys[((i - storey) * (-1)) - 1];
				
				
				if (i > 0){
					verticalOffsetEnterPassenger = 81; verticalOffsetExitPassenger = 146;
				}
				
				//drawing floor with elevator
				if((((i - storey) * (-1) ) == currentStorey) & ( !isInit)){
					
					g.drawImage(elevOpen.elevOpen, 0, i*221, null);
					
					
					if(hasMoved){
						g.drawImage(pass.pass, exitingPositionByX - (45 * loadArrivalStorey), (i + 1) * 75 + verticalOffsetExitPassenger * i, null);
						
					}
				
					if(loadArrivalStorey >= 1){ 
						for(int x = 1; x <= loadArrivalStorey; x++){
							g.drawImage(pass.pass, 790 - (45 * x), (i + 1) * 75 +verticalOffsetExitPassenger* i, null);
						}
					}
					
					
					if(loadStartStorey > 0){ 

						g.drawImage(pass.pass, enteringPositionByX - (45 * (loadStartStorey + 1)), (i + 1) * 140 + verticalOffsetEnterPassenger * i, null); 
						 
							

						if(loadStartStorey > 1){
						
							for(int x = 2; x <= loadStartStorey; x++){
								
								g.drawImage(pass.pass, 835 - (45 * (x)), (i + 1) * 140 + verticalOffsetEnterPassenger * i, null);
							}
						}
					}
					
				}else { 
					//drawing floor without elevator or first drawing until not pressed button "start"
					g.drawImage(elevClose.elevClose, 0, i * 221, null);
					
					if(loadArrivalStorey > 0){
						for(int x = 1; x <= loadArrivalStorey; x++){
							g.drawImage(pass.pass, 790 - (45 * x), (i + 1)*  75 + verticalOffsetExitPassenger * i, null);
							
						}
					}
				
					
					if(loadStartStorey > 0){ 
						for(int x = 1; x <= loadStartStorey; x++){
														
						g.drawImage(pass.pass, 790 - ( 45 * x), (i + 1) * 140 + verticalOffsetEnterPassenger * i, null);
						}
					}	
				}
			}
		}
			
		
		/**
		 * Launches the rendering time specified in variable animationSpeed
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			isInit = false;
			synchronized (sync) {

				currentStorey = building.getElevator().getCurrentStorey();
					
				repaint();
				sync.notify();
			}
		}
	}


