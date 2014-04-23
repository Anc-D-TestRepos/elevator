package elevatorProject.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.apache.log4j.Logger;

import beans.Building;

/**
 *Created main frame who contain three panels, 
 *first panel contain drawing build with passenger and stories
 *second panel contain text area for action messages
 *third panel content button of manage ,
 *also created frame for display log-information   
 */
public class GUI extends JFrame implements Runnable {

	private final String FILE_PATH = "elevator.log";
	private final String LOG_FRAME_TITLE = "LOG FILE";
	private final String NEW_LINE = "\r\n";
	private final String CLOSE_SCANNER_ERR = "Can`t finish reading log file ";
	private final String FILE_NOT_FOUND_ERR = "file elevator.log - not found";
	private final String ERR = " error";
	private final String THREAD_ERROR = " - thread ending with error  - ";
	private int       numberStoreys ;
	private int       animationBoost;
	private int       imgHeight = 221;
	private int       imgWidth = 835;
	private boolean   startState = false;
	private boolean   isAborted=false;
	private GUIBuild  guiBuild;
	private Building  building;
	private JFrame    mainFrame;
	private JFrame    logFrame;
	private JScrollPane jsp ;
	private JPanel    buildPanel;
	private JPanel    buttonPanel;
	private JPanel 	  messagePanel;
	private JTextArea area ;
	private JButton   start  = new JButton("start");
	private JButton   abort  = new JButton("abort");
	private JButton   view   = new JButton("VIEW LOG FILE");
	private Logger    logger = Logger.getLogger(GUI.class);
	
	
	
	public GUI() throws HeadlessException {
		super();
		
	}
	public GUI( Building building, int animationBoost) throws HeadlessException {
		super();
		this.animationBoost = animationBoost;
		this.building = building;
		if(animationBoost == 0){
			setStartState(true);
		}
		
		

		this.numberStoreys = this.building.getStoreys().length;
	}
	/**
	 * @return instance of JTextArea
	 */
	public JTextArea getArea() {
		return area;
	}
	
	/**
	 * Set state for button abort
	 * @param isAborted boolean value
	 */
	public void setAborted(boolean isAborted) {
		this.isAborted = isAborted;
	}
	
	/**
	 * Set state for button start
	 * @param isAborted boolean value
	 */
	void setStartState(boolean state){
		startState = state;
	}
	
	/**
	 * @return boolean value of start button
	 */
	public boolean getStartState(){
		return startState ;
	}

	/**
	 * @return instance GUIBuild
	 */
	public GUIBuild getGuiBuild() {
		return guiBuild;
	}

	/**
	 * @return  boolean value of abort button
	 */
	public boolean isAborted() {
		return isAborted;
	}
	
	/**
	 * @return instance JButton for button start
	 */
	public JButton getStart() {
		return start;
	}
	
	/**
	 * @return instance JButton for button abort
	 */
	public JButton getAbort() {
		return abort;
	}
	
	/**
	 * @return instance JButton for button view
	 */
	public JButton getView() {
		return view;
	}
	
	
	/**
	 * Launches process drawing main frame and connects the action listeners for buttons
	 */
	@Override
	public void run() {
	
	
		guiBuild = new GUIBuild(building, animationBoost);
		guiBuild.setPreferredSize(new Dimension(imgWidth, imgHeight * numberStoreys));
		Thread thread = new Thread(guiBuild);
	
		
		mainFrame = new JFrame("Elevator");
		BorderLayout layout = new BorderLayout();
		mainFrame.setLayout(layout);
		mainFrame.setBounds(300, 10, imgWidth, imgHeight*4);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		buildPanel = new JPanel();
		buildPanel.add(guiBuild);
		
		buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(835, 50));
		abort.setVisible(false);
		view.setVisible(false);
				
		buttonPanel.add(start);
		buttonPanel.add(abort);
		buttonPanel.add(view);
		
		messagePanel = new JPanel();
		messagePanel.setPreferredSize(new Dimension(835, 50));
		
		area = new JTextArea(4, 1);
		area.setPreferredSize(new Dimension(820, 50));
		area.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		area.setEditable(false);
		
		messagePanel.add(area);
		
		
		jsp = new JScrollPane(buildPanel);
		jsp.setPreferredSize(new Dimension(imgWidth, imgHeight*3));
		jsp.setVisible(true);
		
		mainFrame.add(buttonPanel, BorderLayout.SOUTH);
		mainFrame.add(messagePanel, BorderLayout.CENTER);
		mainFrame.add(jsp, BorderLayout.NORTH);			
			
				
		thread.start();
		
		if (animationBoost > 0){
			mainFrame.setVisible(true);
		}
		
		while (!thread.getState().equals(Thread.State.TERMINATED)){
			
			try {
				
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.error(Thread.currentThread().getName() + THREAD_ERROR + e.getMessage());
			}
		}
		
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				start.setVisible(false);
				abort.setVisible(true);
				guiBuild.getTimer().start();
				setStartState(true);
			
				
			}
		});
		
		
		abort.addActionListener( new ActionListener() {
			
			@Override
			 public void actionPerformed(ActionEvent e) {
				
				abort.setVisible(false);
				view.setVisible(true);
				guiBuild.getTimer().stop();
				isAborted = true;
				
			}
		});
		
		view.addActionListener( new ActionListener() {
			
			@Override
			 public void actionPerformed(ActionEvent e) {
				
				logFrame = new JFrame(LOG_FRAME_TITLE);
				JTextArea jta;
			    JScrollPane jsp ;
			    Scanner sc = null;
			    StringBuffer sb = new StringBuffer();
				try {
					sc = new Scanner(new FileReader(FILE_PATH));
			       
				while(sc.hasNextLine())
				{
				sb.append(sc.nextLine());
				sb.append(NEW_LINE);
				}
					
				
                    } catch (FileNotFoundException exc) {
                    	
                    	JFrame errorFarame = new JFrame();
                    	JOptionPane.showMessageDialog(errorFarame, FILE_NOT_FOUND_ERR,
                    										ERR, JOptionPane.ERROR_MESSAGE);
                    	errorFarame.setVisible(true);
				}  
				
			   
				if(sc != null){
					try{
						sc.close();
					}catch(IllegalStateException exc){
						logger.error(CLOSE_SCANNER_ERR + exc.getMessage());
					}
				}
				
				jta = new JTextArea(sb.toString());
				jsp = new JScrollPane(jta);
						 
				logFrame.add(jsp);
				logFrame.setBounds(300, 50, 500, 700);
				logFrame.setVisible(true);
					
			}
		});
		
	}
	
}
 