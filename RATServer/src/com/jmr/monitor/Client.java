package com.jmr.monitor;

import com.esotericsoftware.kryonet.Connection;
import com.jmr.monitor.packets.Packet2RunCmd;
import com.jmr.monitor.packets.Packet3Response;
import com.jmr.monitor.packets.Packet4Popup;
import com.jmr.monitor.packets.Packet6KillTask;

public class Client {

	private final Connection con;
	private final String id;
	
	public Client(Connection connection, String id) {
		this.con = connection;
		this.id = id;
	}
	
	public Connection getConnection() {
		return con;
	}
	
	public String getId() {
		return id;
	}
	
	public void sendPopup(String msg) { //Sends popup given the message
		Packet4Popup p4 = new Packet4Popup();
		p4.msg = msg;
		con.sendTCP(p4);
	}
	
	public void sendCmd(String cmd) { //Runs console command given the command
		Packet2RunCmd p2 = new Packet2RunCmd();
		p2.cmd = cmd;
		con.sendTCP(p2);
	}

	public void killTask(String task) { //Kills a task given the name
		Packet6KillTask p6 = new Packet6KillTask();
		p6.task = task;
		con.sendTCP(p6);
	}
	
	public void getTasks() { //Requests all tasks
		Packet3Response p3 = new Packet3Response();
		p3.response = "get tasks";
		con.sendTCP(p3);
	}

	public void disconnect() { //Forcefully disconnects the client
		Packet3Response p3 = new Packet3Response();
		p3.response = "disconnect";
		con.sendTCP(p3);
	}
	
	public void redownload() { //Redownloads the newest client file
		Packet3Response p3 = new Packet3Response();
		p3.response = "download";
		con.sendTCP(p3);
	}
	
}
