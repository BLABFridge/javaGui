import java.util.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.*;
import java.awt.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.jdatepicker.impl.*;
import org.jdatepicker.util.*;
import org.jdatepicker.*;
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
//	public JButton getListButton;
	public JButton setTimeout;
	public JButton getExpiring;
	public JPanel topPane;
	private JPanel midPane;
	private JPanel botPane;
	private JScrollPane scrollP;
	private JDatePickerImpl addedPicker, expiryPicker;
	private JTextArea nameTextField, daysLeftField;
	private	TableRowSorter<TableModel> sorter; 
	private DatagramSocket sock;
	private InetAddress fridgeControllerInetAddress;

	public static final String fridgeControllerInetAddressAsString = "172.17.197.117";
	public static final int fridgeControllerPort = 1111;
	public static final char[] blankTagCodeCharArray = {'0','0','0','0','0','0','0','0','0','0'}; //we don't know the tagcode so a blank one is required to use the FoodItem factory method

	public GUIClass(String s){
		super(s);
		this.setSize(400, 400);
		this.setLayout(new GridBagLayout());
		midPane = new JPanel();
		midPane.setLayout(new GridBagLayout());
		midPane.setSize(400, 300);
		midPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 7;
		c.weighty = 1;
		this.add(midPane);
//		getListButton = new JButton("Get List");
		setTimeout = new JButton("Set Timeout");
		getExpiring = new JButton("Get Expiring");
		
		expiringTextArea = new JTextArea(defaultExpiringAreaText);
		setTimeoutTextArea = new JTextArea(defaultTimeoutAreaText);
		// Moved all table creation and listing into new method called itemsToTable();
		// Just request all items from the beginning and put into table and let
		// the JTable library do the filtering.
/*
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
		scrollP = new JScrollPane(table, 
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
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		pane.add(getListButton, c);
*/
		// This is the date picker library.
		TextChangeListener jTextListener = new TextChangeListener(this);

		LocalDateTime today = LocalDateTime.now();
		UtilDateModel model = new UtilDateModel();
		model.setDate(today.getYear(), today.getMonthValue(), today.getDayOfMonth());
		model.setSelected(true);
		model.addChangeListener(jTextListener);

		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);

		addedPicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		expiryPicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

		c.insets = new Insets(3, 3, 3, 3);
		c.gridx = 0;
		c.gridy = 4;
		midPane.add(new JTextField("Item:"), c);
		
		c.gridx = 1;
		c.gridy = 4;
		nameTextField = new JTextArea();
		nameTextField.getDocument().addDocumentListener(new DocumentListener());
		midPane.add(nameTextField, c);

		c.gridx = 0;
		c.gridy = 5;
		midPane.add(new JTextField("Added Before:"), c);
		
		c.gridx = 1;
		c.gridy = 5;
		midPane.add(addedPicker, c);

		c.gridx = 0;
		c.gridy = 6;
		midPane.add(new JTextField("Expires Before:"), c);

		c.gridx = 1;
		c.gridy = 6;
		midPane.add(expiryPicker, c);
		
		c.gridx = 0;
		c.gridy = 7;
		midPane.add(new JTextField("Days Until Expiration:"), c);

		c.gridx = 1;
		c.gridy = 7;
		daysLeftField = new JTextArea("100");
		daysLeftField.getDocument().addDocumentListener(new DocumentListener());
		midPane.add(daysLeftField, c);
		
	
		//NEED TO ADD THE BUTTONS AND THE TEXT AREAS IN THE APPROPRIATE PLACE
/*		c.gridx = 0;
		c.gridy = 4;
		pane.add(getExpiring, c); 
		c.gridx = 1;
		pane.add(expiringTextArea, c);
		c.gridy = 5;
		c.gridx = 0;
		pane.add(setTimeout,c);
		c.gridx = 1;
		pane.add(setTimeoutTextArea,c);
*/		
		//add()
//		getListButton.addActionListener(new ButtonListener(this));
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
		
		topPane = new JPanel();
		topPane.setLayout(new GridBagLayout());
		topPane.setSize(400, 400);
		int listSize = itemList.size();
		Object data[][] = new Object[listSize][5];
		int i = 0;
		String[] columnNames = {"Food Name", 
			"Date Added", 
			"Expiry Date", 
			"Shelf Life", 
			"Time Until Expiration"};
		for(FoodItem item:itemList) {
			data[i][0] = item.getName();
			System.out.println(item.getDateAdded());
			data[i][1] = Date.from(item.getDateAdded().atZone(ZoneId.systemDefault()).toInstant());
			data[i][2] = Date.from(item.expiresOn().atZone(ZoneId.systemDefault()).toInstant());
			data[i][3] = item.getLifetime();
			data[i][4] = item.expiresInDays();
			i += 1;
		}
		JTable table = new JTable(data, columnNames);
		sorter = new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);
		scrollP = new JScrollPane(table, 
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 4;
		c.weighty = 6;
		c.gridx = 0;
		c.gridy = 0;
		topPane.add(scrollP, c);
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 6;
		this.add(topPane, c);

	}

	public void applyNewFilter() {
		RowFilter<TableModel, Object> nameFilter = null;
		RowFilter<TableModel, Object> addedFilter = null;
		RowFilter<TableModel, Object> expiryFilter = null;
		RowFilter<TableModel, Object> lifeFilter = null;
		RowFilter<TableModel, Object> remainingFilter = null;
		java.util.List<RowFilter<TableModel, Object>> filters = new ArrayList<RowFilter<TableModel, Object>>();
		RowFilter<TableModel, Object> compoundFilter = null;

		try {
			nameFilter = RowFilter.regexFilter(nameTextField.getText(), 0);
			addedFilter = RowFilter.dateFilter(RowFilter.ComparisonType.BEFORE, (Date) addedPicker.getModel().getValue(), 1);
			expiryFilter = RowFilter.dateFilter(RowFilter.ComparisonType.BEFORE, (Date) expiryPicker.getModel().getValue(), 2);
//			lifeFilter = RowFilter.numberFilter(RowFilter.ComparisonType.BEFORE, (int) lifeTextField.getText(), 3);
			remainingFilter = RowFilter.numberFilter(RowFilter.ComparisonType.BEFORE, Integer.valueOf(daysLeftField.getText()), 4);
			
			filters.add(nameFilter);
			filters.add(addedFilter);
			filters.add(expiryFilter);
			filters.add(remainingFilter);
			
			compoundFilter = RowFilter.andFilter(filters);
			sorter.setRowFilter(compoundFilter);
			
		} catch(java.util.regex.PatternSyntaxException e) {
			e.getMessage();
		}
	}

	public ArrayList<FoodItem> getItemsExpiringBefore(int days) { //this should be called by the actionEvent created by a button press
		byte[] buf = new byte[100];
		buf[0] = '9';
		buf[1] = FoodItem.opcodeDelimiter.getBytes()[0];
		byte[] daysAsBytes = Integer.toString(days).getBytes();
		System.arraycopy(daysAsBytes, 0, buf, 2, daysAsBytes.length);
		buf[daysAsBytes.length + 2] = FoodItem.opcodeDelimiter.getBytes()[0];
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
	
	    /*Sends the new timeout to the controller, does not wait on response */
   	 public void setTimeout(int newTimeout){
       		 byte[] buf = new byte[100];
       		 buf[0] = '8';
        	 buf[1] = FoodItem.opcodeDelimiter.getBytes()[0];
        	 if(newTimeout == 0){
           		 buf[2] = '0';
        	 }else{
            		buf[2] = (byte) newTimeout;
        	 }
        
       		 DatagramPacket p = new DatagramPacket(buf, buf.length, fridgeControllerInetAddress, fridgeControllerPort);
       		 try{
           		 sock.send(p);
        	 }catch(IOException e){
            		System.out.println("Error sending timeout packet");
        	 }
        
    	}

	public static void main(String[] args){
		GUIClass mainFrame = new GUIClass("Fridge Controller Controller");
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		ArrayList<FoodItem> itemList = mainFrame.getItemsExpiringBefore(0);
		mainFrame.itemsToTable(itemList);
		
	}



}
