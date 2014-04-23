package elevatorProject.GUI.beans;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
/**
 * Creates panel with floor with open elevator image 
 */
public class ElevOpen extends JPanel {
	
	public Image  elevOpen = new ImageIcon(getClass().getClassLoader().getResource("resources/ElevOpen.png")).getImage();
	
	public void paint (Graphics g){
		g = (Graphics2D)g;
		g.drawImage(elevOpen, 0, 0, null);
	}
	
}
