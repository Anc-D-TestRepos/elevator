package elevatorProject.GUI.beans;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Creates panel with floor with close elevator image 
 */
public class ElevClose extends JPanel {
	
	public Image  elevClose = new ImageIcon(getClass().getClassLoader().getResource("resources/ElevClose.png")).getImage();
	
	public void paint (Graphics g){
		g = (Graphics2D)g;
		g.drawImage(elevClose, 0, 0, null);
	}
	
}
