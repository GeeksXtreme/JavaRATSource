package com.jmr.monitor;

import com.esotericsoftware.kryonet.Connection;
import com.jmr.monitor.packets.Packet1Connected;
import com.jmr.monitor.packets.Packet3Response;
import com.jmr.monitor.packets.Packet5Tasks;
import com.jmr.monitor.packets.Packet7Info;

public class PacketHandler {

	private ClientManager clientManager;
	private Window window;
	
	public PacketHandler(ClientManager clientManager, Window window) {
		this.clientManager = clientManager;
		this.window = window;
	}
	
	public void handlePacket1(Packet1Connected p1, Connection con) {
		String id = p1.id; //Gets id of client
		Client client = new Client(con, id); //Creates new client instance
		if (!clientManager.clientConnected(client)) { //If they arent connected already
			System.out.println(id + " connected."); //Print out
			clientManager.addClient(client); //Add client
			window.addClientToList(id); //Add to window
		}
	}
	
	public void handlePacket3(Packet3Response p3) {
		String response = p3.response;
		System.out.println(response); //Print out response
	}
	
	public void handlePacket5(Packet5Tasks p5) {
		window.updateTasks(p5.tasks); //Update tasks on the window
	}
	
	public void handlePacket7(Packet7Info p7) {
		window.openInfoFrame(); //Opens the info frame
		window.setInfoFrame(p7.info); //Sets the info
	}
	
}
