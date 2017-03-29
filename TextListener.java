import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

public class TextListener implements DocumentListener {
	
	private GUIClass gui;

	public TextListener(GUIClass gui) { 
		this.gui = gui;
	}

	public void changedUpdate(DocumentEvent e) {
		gui.applyNewFilter();
	}

	public void removeUpdate(DocumentEvent e) {
		gui.applyNewFilter();
	}

	public void insertUpdate(DocumentEvent e) {
		gui.applyNewFilter();
	}

}
