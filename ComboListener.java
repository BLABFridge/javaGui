import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JComboBox;

public class ComboListener implements ItemListener{

	GUIClass gui;

	public ComboListener (GUIClass gui) {
		this.gui = gui;
	}

	public void itemStateChanged(ItemEvent e) {
		gui.applyNewFilter();
	}

}
