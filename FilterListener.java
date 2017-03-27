import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FilterListener implements ChangeListener{

	GUIClass gui;

	public FilterListener(GUIClass gui) {
		this.gui = gui;
	}

	public void stateChanged(ChangeEvent e) {
		gui.applyNewFilter();
	}

}
