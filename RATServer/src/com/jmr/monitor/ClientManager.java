package com.jmr.monitor;

import java.util.ArrayList;

import com.esotericsoftware.kryonet.Connection;

public class ClientManager {

	public ArrayList<Client> clients = new ArrayList<Client>(); //List of all connected clients
	
	public ClientManager() {
		
	}
	
	public void addClient(Client client) { //Adds client to list
		clients.add(client);
	}
	
	public Client getClient(String id) { //Gets the client with the given id
		for (Client c : clients)
			if (c.getId().equalsIgnoreCase(id))
				return c;
		return null;
	}
	
	public Client getClient(Connection con) { //Gets the client with the given connection
		for (Client c : clients)
			if (c.getConnection() == con)
				return c;
		return null;
	}
	
	public boolean clientConnected(Client client) { //Checks if a client is still connected given the client
		for (Client c : clients)
			if (c.getId().equalsIgnoreCase(client.getId()))
				return true;
		return false;
	}

}
