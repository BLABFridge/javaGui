import javax.swing.JButton;
import java.awt.event.*;

public class ButtonListener implements ActionListener{
	
	private GUIClass gui;

	public ButtonListener(GUIClass gui) {
		this.gui = gui;
	}

	public void actionPerformed(ActionEvent e) {
		gui.getItemsExpiringBefore(0);
	}

}
