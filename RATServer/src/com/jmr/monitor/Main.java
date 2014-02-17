package com.jmr.monitor;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.jmr.monitor.packets.Packet;
import com.jmr.monitor.packets.Packet1Connected;
import com.jmr.monitor.packets.Packet2RunCmd;
import com.jmr.monitor.packets.Packet3Response;
import com.jmr.monitor.packets.Packet4Popup;
import com.jmr.monitor.packets.Packet5Tasks;
import com.jmr.monitor.packets.Packet6KillTask;
import com.jmr.monitor.packets.Packet7Info;

public class Main {

	public static Server server;
	public static ClientManager clientManager;
	public static Window window;
	
	private static PacketHandler pHandler;
	
	public static void main(String[] args) throws IOException {
		server = new Server(16384, 5096); //Sets the max amount of in and out bytes. Had to bump it up to transfer a lot of information
		clientManager = new ClientManager();
		server.start();
		server.bind(24470, 24470); //Binds it to 24470, TCP & UDP
		register();
		window = new Window();
		pHandler = new PacketHandler(clientManager, window);
		new Main();
	}
	
	public static void register() {
		//Registers all packets
		server.getKryo().register(Packet.class);
		server.getKryo().register(Packet1Connected.class);
		server.getKryo().register(Packet2RunCmd.class);
		server.getKryo().register(Packet3Response.class);
		server.getKryo().register(Packet4Popup.class);
		server.getKryo().register(String.class);
		server.getKryo().register(String[].class);
		server.getKryo().register(Object[].class);
		server.getKryo().register(Packet5Tasks.class);
		server.getKryo().register(Packet6KillTask.class);
		server.getKryo().register(Packet7Info.class);
	}
	
	public Main() {
		server.addListener(new Listener() {
			   public void received (Connection con, Object o) {
				   if (o instanceof Packet) { //If it is a packet
					   if (o instanceof Packet1Connected) { //If packet1
						   Packet1Connected p1 = (Packet1Connected) o;
						   pHandler.handlePacket1(p1, con);
					   } else if (o instanceof Packet3Response) { //If packet3
						   Packet3Response p3 = (Packet3Response) o;
						   pHandler.handlePacket3(p3);
					   } else if (o instanceof Packet5Tasks) { //If packet5
						   Packet5Tasks p5 = (Packet5Tasks) o;
						   pHandler.handlePacket5(p5);
					   } else if (o instanceof Packet7Info) { //If packet7
						   Packet7Info p7 = (Packet7Info) o;
						   pHandler.handlePacket7(p7);
					   }
				   }
			   }
			   
			   public void disconnected (Connection con) { //Called when client disconnects
				   Client c = clientManager.getClient(con); //Get client
				   if (c != null) { //If they exist
					   window.removeClient(c.getId()); //Remove them from the connected clients on the window
					   clientManager.clients.remove(c); //Remove them from the client list
				   }
			   }
			
		});
	}

}
