package com.jmr.monitor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import com.esotericsoftware.minlog.Log;

public class Window extends JFrame {
	
	private final DefaultListModel clientListModel = new DefaultListModel();
	private final JList clientList = new JList(clientListModel);
	
	private final JButton btnPopup = new JButton("Popup Message");
	private final JButton btnCmd = new JButton("Run Command");
	private final JButton btnTasks = new JButton("Task Manager");
	private final JButton btnKill = new JButton("Kill Task");
	private final JButton btnRefresh = new JButton("Refresh Tasks");
	private final JButton btnRedownload = new JButton("Redownload");
	private final JButton btnDisconnect = new JButton("Disconnect");
	
	public Window() {
		super("Server Monitor");
		setSize(550, 400);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane(); //Scroll pane (scroll bars) for the connected clients list
		scrollPane.setSize(200, 350);
		scrollPane.setLocation(10, 10);
		clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.getViewport().add(clientList);
		
		//Sets all locations and sizes
		btnPopup.setLocation(300, 10);
		btnPopup.setSize(150, 25);
		btnCmd.setLocation(300, 50);
		btnCmd.setSize(150, 25);
		btnTasks.setLocation(300, 90);
		btnTasks.setSize(150, 25);
		btnRedownload.setLocation(300, 130);
		btnRedownload.setSize(150, 25);
		btnDisconnect.setLocation(300, 170);
		btnDisconnect.setSize(150, 25);
		
		addButtonListeners(); //Adds all button listeners
		
		//Adds all of the components to the frame
		add(scrollPane);
		add(btnPopup);
		add(btnCmd);
		add(btnTasks);
		add(btnRedownload);
		add(btnDisconnect);
		show(); //Shows the frame
	}
	
	private void addButtonListeners() {
		btnPopup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				String selected = getSelectedClient(); //Gets the selected client from the list of clients
				Client c = Main.clientManager.getClient(selected); //Gets the client from the id
				if (c != null) { //if the client exists
					String msg = "";
					try {
						msg = JOptionPane.showInputDialog("Enter your message:"); //Get message
					} catch (Exception e) {
						Log.error("Could not send popup.");
					}
					if (msg != null && !msg.equalsIgnoreCase("")) //Send message
						c.sendPopup(msg);
				}
			}
			
		});
		
		btnCmd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				String selected = getSelectedClient(); //Gets the selected client from the list of clients
				Client c = Main.clientManager.getClient(selected); //Gets the client from the id
				if (c != null) { //if the client exists
					String cmd = "";
					try {
						cmd = JOptionPane.showInputDialog("Enter the command to run:"); //Get the command to run
					} catch (Exception e) {
						Log.error("Could not send command.");
					}
					if (cmd != null && !cmd.equalsIgnoreCase("")) //Run command
						c.sendCmd(cmd);
				}
			}
			
		});
		
		btnTasks.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				openTaskFrame(); //Opens the tasks
			}
			
		});
		
		btnRedownload.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				String selected = getSelectedClient(); //Gets the selected client from the list of clients
				Client c = Main.clientManager.getClient(selected); //Gets the client from the id
				if (c != null) {
					c.redownload(); //Re-downloads the client
				}
			}
			
		});
		
		btnDisconnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				String selected = getSelectedClient(); //Gets the selected client from the list of clients
				Client c = Main.clientManager.getClient(selected); //Gets the client from the id
				if (c != null) {
					c.disconnect(); //Forcefully disconnect
				}
			}
			
		});
		
		btnKill.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				String selected = getSelectedClient(); //Gets the selected client from the list of clients
				Client c = Main.clientManager.getClient(selected); //Gets the client from the id
				if (c != null) { 
					String process = (String) taskList.getSelectedValue(); //Get the selected task name
					c.killTask(process.trim()); //Request to kill the tasks (trim takes out all spaces)
				}
			}
			
		});

		btnRefresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				String selected = getSelectedClient(); //Gets the selected client from the list of clients
				Client c = Main.clientManager.getClient(selected); //Gets the client from the id
				if (c != null) {
					c.getTasks(); //Requests all of the tasks
				}
			}
			
		});
	}
	
	private final DefaultListModel taskListModel = new DefaultListModel();
	private final JList taskList = new JList(taskListModel);
	
	private void openTaskFrame() { //Opens the task manager window
		JFrame frame = new JFrame("Task Manager");
		frame.setSize(400, 350);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane(); //Creates the scroll pane for the tasks
		scrollPane.setSize(200, 300);
		scrollPane.setLocation(10, 10);
		taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.getViewport().add(taskList);
		
		//Sets the location and sizes for the buttons
		btnKill.setLocation(228, 10);
		btnKill.setSize(150, 25);
		btnRefresh.setLocation(228, 50);
		btnRefresh.setSize(150, 25);

		taskListModel.clear(); //Clears out all previous entries
		String selected = getSelectedClient(); //Gets the selected client from the list of clients
		Client c = Main.clientManager.getClient(selected); //Gets the client from the id
		if (c != null) {
			c.getTasks(); //Requests to get all tasks
		}
		
		//Adds all to the frame
		frame.add(scrollPane);
		frame.add(btnKill);
		frame.add(btnRefresh);
		
		frame.show(); //shows the tasks manager frame
	}
	
	public void addClientToList(String id) { //Adds a client id to the list of clients connected
		clientListModel.addElement(id);
	}
	
	public void removeClient(String id) { //Removes a client id from the list of clients connected
		clientListModel.removeElement(id);
	}
	
	public String getSelectedClient() { //Gets the current client that is selected in the list
		return (String) clientList.getSelectedValue();
	}
	
	public void updateTasks(String[] tasks) { //Updates the tasks in the task manager window
		taskListModel.clear(); //Clears previous entries
		for (int i = 0; i < tasks.length; i++) { //For each tasks
			String s = tasks[i]; //Get the task
			if (!s.equalsIgnoreCase("null")) { //If it doesnt equal null
				taskListModel.addElement(s); //Add the element
				try {
					Thread.sleep(20); //Reason for sleeping is because if you dont the tasks wont show up or load into the list correctly
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private final JTextArea textArea = new JTextArea();
	
	public void openInfoFrame() { //Shows the information frame
		JFrame frame = new JFrame("Information");
		frame.setSize(400, 350);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setLayout(null);
		
		textArea.setFont(new Font("Serif", Font.PLAIN, 12)); //Sets smaller text of the text area
		textArea.setWrapStyleWord(true);
		
		JScrollPane scrollPane = new JScrollPane(); //Adds a scroll pane to the text area
		scrollPane.getViewport().add(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		//Sets the size and location of the text area
		scrollPane.setSize(375, 325);
		scrollPane.setLocation(10, 10);

		textArea.setText(""); //Clears out anything in it
		
		frame.add(scrollPane); //Adds it to the frame
		
		frame.show(); //Shows the frame
	}

	public void setInfoFrame(String[] info) {
		textArea.setText(""); //Clears out the current info
		for (String s : info) //Goes through each entry
			textArea.append(s + "\n"); //Adds it then adds the new line operator
	}
	
}
