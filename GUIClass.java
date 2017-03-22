import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;

import java.util.ArrayList;

class GUIClass extends JFrame {
	
	public JTextArea textArea;
	public JButton getListButton;
	public JButton enterAddingModeButton;
	public JPanel panel;

	private DatagramSocket sock;
	private InetAddress fridgeControllerInetAddress;

	public static final String fridgeControllerInetAddressAsString = "10.0.0.41";
	public static final int fridgeControllerPort = 1111;
	public static final char[] blankTagCodeCharArray = {'0','0','0','0','0','0','0','0','0','0'}; //we don't know the tagcode so a blank one is required to use the FoodItem factory method

	public GUIClass(String s){
		super(s);
		this.setSize(400, 400);
		JPanel pane = new JPanel();
		pane.setLayout(new GridBagLayout());
		pane.setSize(400, 400);
		this.add(pane);
		getListButton = new JButton("Get List");
		enterAddingModeButton = new JButton("Enter Adding Mode");
		//THIS NEEDS MORE WORK- IS NOT COMPLETE
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;

		pane.add(getListButton, c);
		//add()

		try{
			sock = new DatagramSocket();
		} catch(IOException e){
			System.out.println("Error creating a socket");
		}

		try{
			fridgeControllerInetAddress = InetAddress.getByName(fridgeControllerInetAddressAsString);
		} catch (UnknownHostException e){
			System.out.println("No Host " + fridgeControllerInetAddressAsString);
		}

	}

	public ArrayList<FoodItem> getItemsExpiringBefore(int days){ //this should be called by the actionEvent created by a button press
		byte[] buf = new byte[100];
		buf[0] = '9';
		buf[1] = FoodItem.opcodeDelimiter.getBytes()[0];
		byte[] daysAsBytes = Integer.toString(days).getBytes();
		System.arraycopy(daysAsBytes, 0, buf, 2, daysAsBytes.length);
		DatagramPacket p = new DatagramPacket(buf, buf.length, fridgeControllerInetAddress, fridgeControllerPort);
		try{
			sock.send(p);
		} catch(IOException e){
			System.out.println("Error sending on socket");
		}

		ArrayList<FoodItem> items = new ArrayList<FoodItem>();

		while(true){
			try{
				sock.receive(p);
			} catch(IOException e){
				System.out.println("Error receiving on socket");
			}
			buf = p.getData();
			if (buf[0] == '9') break; //we got the last packet, exit.
			items.add(FoodItem.getFoodItemFromByteArray(blankTagCodeCharArray, buf));
		}
		return items;
	}

	public static void main(String[] args){
		GUIClass mainFrame = new GUIClass("Fridge Controller Controller");
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
	}



}
