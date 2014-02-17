package com.jmr.monitor;


import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.jmr.monitor.packets.Packet;
import com.jmr.monitor.packets.Packet1Connected;
import com.jmr.monitor.packets.Packet2RunCmd;
import com.jmr.monitor.packets.Packet3Response;
import com.jmr.monitor.packets.Packet4Popup;
import com.jmr.monitor.packets.Packet5Tasks;
import com.jmr.monitor.packets.Packet6KillTask;
import com.jmr.monitor.packets.Packet7Info;

public class Main implements Runnable {

	public static Client client;
	private static PacketHandler pHandler;
	
	public static void main(String[] args) {
		try {
			client = new Client();
			client.start();
			register();
			pHandler = new PacketHandler();
			new Main();
		} catch (Exception e) {
			pHandler.checkException(e);
		}
	}
	
	public static void register() {
		//Registers all clients
		client.getKryo().register(Packet.class);
		client.getKryo().register(Packet1Connected.class);
		client.getKryo().register(Packet2RunCmd.class);
		client.getKryo().register(Packet3Response.class);
		client.getKryo().register(Packet4Popup.class);
		client.getKryo().register(String.class);
		client.getKryo().register(String[].class);
		client.getKryo().register(Object[].class);
		client.getKryo().register(Packet5Tasks.class);
		client.getKryo().register(Packet6KillTask.class);
		client.getKryo().register(Packet7Info.class);
	}

	public Main() {
		try {
			connect();
			client.addListener(new Listener() {
				public void received (Connection connection, Object o) {
					if (o instanceof Packet) {
						if (o instanceof Packet2RunCmd) {
							Packet2RunCmd p2 = (Packet2RunCmd) o;
							pHandler.handlePacket2(p2);
						} else if (o instanceof Packet4Popup) {
							Packet4Popup p4 = (Packet4Popup) o;
							pHandler.handlePacket4(p4);
						} else if (o instanceof Packet3Response) {
							Packet3Response p3 = (Packet3Response) o;
							pHandler.handlePacket3(p3);
						} else if (o instanceof Packet6KillTask) {
							Packet6KillTask p6 = (Packet6KillTask) o;
							pHandler.handlePacket6(p6);
						}
					}
				}
			});
			Thread t = new Thread(this);
			t.start();
		} catch (Exception e) {
			pHandler.checkException(e);
		}
	}
	
	public static void connect() {
		try {
			client.connect(5000, "192.168.1.101", 24470, 24470); //Try and connect to the ip
		} catch (IOException e) { //If it times out
			try {
				Thread.sleep(15000); //Wait 15 seconds
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
			System.out.println("Trying to connect.");
			connect(); //Try and reconnect to the server (constant loop to always be ready to connect to the server)
		}

		Packet1Connected packet = new Packet1Connected();
		packet.id = System.getProperty("user.name"); //Set id as the username of the computer the client is on
	
		client.sendTCP(packet); //Send connect packet
	}

	@Override
	public void run() {
		while (true) { //Keep the application running
			try {
				Thread.sleep(3000); //Check if connected or not every 3 seconds
				if (!client.isConnected()) 
					connect();
			} catch (InterruptedException e) {
				pHandler.checkException(e);
			}
		}
	}
	
}
