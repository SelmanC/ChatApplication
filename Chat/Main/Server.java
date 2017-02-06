package Chat.Main;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import Chat.Controller.DBConnection;
import Chat.Controller.ServerThread;
import Chat.Model.Clients;


public class Server {

	private static ServerSocket server;
	private static Clients clientList;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException{
		int portNr = 8000;
		if(args.length == 1){
			portNr = Integer.parseInt(args[0]);
		}
		System.out.println("portNr: " +portNr);
		
		ServerSocket server = new ServerSocket(portNr);
		DBConnection DBConn = new DBConnection();
		System.out.println("Server ist an");
		clientList = new Clients();
		
		while(true){
			Socket client = server.accept();
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			
			ServerThread thread = new ServerThread(in, clientList, out, DBConn);
			Thread thr = new Thread(thread);
			thr.start();
		}
	}

}
