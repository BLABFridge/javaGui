import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;

import java.util.ArrayList;

class GUIClass extends JFrame {
	
	public JTextArea textArea;
	public JTextArea expiringTextArea;
	public JTextArea setTimeoutTextArea;
	public String editText1 = "";
	public String defaultExpiringAreaText = "Enter date";
	public String defaultTimeoutAreaText = "Enter Timeout";
	public JButton getListButton;
	public JButton setTimeout;
	public JButton getExpiring;
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
		setTimeout = new JButton("Set Timeout");
		getExpiring = new JButton("Get Expiring");
		
		expiringTextArea = new JTextArea(defaultExpiringAreaText);
		setTimeoutTextArea = new JTextArea(defaultTimeoutAreaText);
		
		String[] columnNames = {"Food Name", 
			"Date Added", 
			"Expiry Date", 
			"Shelf Life", 
			"Time Until Expiration"};
		LocalDate today = LocalDate.now();
		LocalDate apple = today.plus(4, ChronoUnit.DAYS);
		LocalDate orange = today.plus(6, ChronoUnit.DAYS);
		Object[][] data = {{"Orange", today, orange, 6, ChronoUnit.DAYS.between(today, orange)}, 
				{"Apple", today, apple, 3, ChronoUnit.DAYS.between(today, apple)}};
		
		JTable table = new JTable(data, columnNames);
		table.setAutoCreateRowSorter(true);
		JScrollPane scrollP = new JScrollPane(table, 
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//THIS NEEDS MORE WORK- IS NOT COMPLETE
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 4;
		c.weighty = 4;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(scrollP, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;

		pane.add(getListButton, c);
		

		
		//NEED TO ADD THE BUTTONS AND THE TEXT AREAS IN THE APPROPRIATE PLACE
		c.gridx = 0;
		c.gridy = 2;
		pane.add(getExpiring, c); 
		c.gridx = 1;
		pane.add(expiringTextArea, c);
		c.gridy = 3;
		c.gridx = 0;
		pane.add(setTimeout,c);
		c.gridx = 1;
		pane.add(setTimeoutTextArea,c);
		
		//add()
		getListButton.addActionListener(new ButtonListener(this));
		getExpiring.addActionListener(new ButtonListener(this));
		setTimeout.addActionListener(new ButtonListener(this));

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

	public void itemsToTable(ArrayList<FoodItem> itemList) {
		Object data[][];
		int i = 0;
		for(FoodItem item:itemList) {
			for(int j = 0; j < 5; j += 1) {
				//data[i][j] = item.
			}
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

