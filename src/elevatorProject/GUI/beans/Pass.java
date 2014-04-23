package elevatorProject.GUI.beans;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Creates panel with passenger image 
 */
public class Pass extends JPanel {
	
	public Image  pass = new ImageIcon(getClass().getClassLoader().getResource("resources/Pass.png")).getImage();
	
	public void paint (Graphics g){
		g = (Graphics2D)g;
		g.drawImage(pass, 0, 0, null);
	}
	
}

	
	

