package main;

import main.service.ServerService;
import main.socket.Server;

import java.io.*;

public class ServerApplication {
	public static ServerService service = new ServerService();
    public static Server server = new Server();

    
    private void run() throws IOException {
    	server.startServer(9999);
    	
    	// service.printAllHospital();
    	// service.printAllPatient();

    }


    public static void main (String[] args) throws IOException {
        ServerApplication serverApplication = new ServerApplication();
        serverApplication.run();
    }
}
