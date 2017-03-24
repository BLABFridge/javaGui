import javax.swing.JButton;
import java.awt.event.*;

public class ButtonListener implements ActionListener{
    
    private GUIClass gui;

    public ButtonListener(GUIClass gui) {
        this.gui = gui;
    }

    public void actionPerformed(ActionEvent e) {
        String source = (String) e.getActionCommand();
        switch(source){
            case "Get List": 
                System.out.print("Get List pressed");
                gui.getItemsExpiringBefore(0);
                break;
            case "Get Expiring": 
                System.out.print("Get Expiring pressed");
                gui.editText1 = gui.expiringTextArea.getText().toString();
                int dayInput = Integer.valueOf(gui.editText1);
                gui.expiringTextArea.setText(gui.defaultExpiringAreaText);
                gui.getItemsExpiringBefore(dayInput);
                break;
            case "Set Timeout":
                System.out.print("Set Timeout pressed");
                gui.editText1 = gui.setTimeoutTextArea.getText().toString();
                int timeoutInput;
                if(gui.editText1 == ""){
                    timeoutInput = 0;
                }
                timeoutInput = Integer.valueOf(gui.editText1);
                gui.setTimeoutTextArea.setText(gui.defaultTimeoutAreaText);
                //method call to set the timeout.
                gui.setTimeout(timeoutInput);
            default: System.out.print("default case");
           }

    }

}
