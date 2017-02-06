package Chat.Controller;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Chat.Model.Chatverlauf;
import Chat.Model.Clients;

public class ServerThread implements Runnable{
	
	private final DBConnection DBConn;
	private final ObjectInputStream in;
	private final ObjectOutputStream out;
	private final List<Chatverlauf> verlauf;
	private final Clients clientList;
	private  String email;
	
	public ServerThread(ObjectInputStream in, Clients clientList, ObjectOutputStream out, DBConnection DBConn) {
		this.in = in;
		this.out = out;
		this.verlauf = new ArrayList<>();
		this.clientList = clientList;
		this.DBConn = DBConn;
	}
	
	private String getData(){
		String userInput;	
		try {
			userInput = (String) in.readObject();
			return userInput;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	private boolean userInformationAndRegistrationAndLogin(Map<String, String> queryData){
		
		String typ = queryData.get("typ");
		
		if(typ == null || typ.isEmpty()){
			return false;
		}
		
		String userInput = "";
		switch(typ){
			case "reg":
				return checkUserReg(queryData.get("user"), queryData.get("email"), queryData.get("pw"));
			case "login":
				return checkUserLogin(queryData.get("email"), queryData.get("pw"));
			default:
				return false;
		}
	}
	private boolean checkUserLogin(String email, String pw){
		if(email == null || email.isEmpty() || pw == null || pw.isEmpty()){
			return false;
		}	
		return DBConn.login(email, pw);
	}
	
	private boolean checkUserReg(String user, String email, String pw){
		if(user == null || user.isEmpty() || email == null || email.isEmpty() || pw == null || pw.isEmpty()){
			return false;
		}
		return DBConn.register(user, pw, email);
	}
	
	private Map<String, String> splitQuery(String query){
		String[] splitQuery = query.split("&");
		Map<String, String> values = new HashMap<>(); 
		int i = 0;
		for(String q : splitQuery){
			String[] qSplit = q.split("=");
			values.put(qSplit[0], qSplit[1]); 
			i++;
		}
		return values;
	}
	
	public boolean setEmail(String email){
		try {
			clientList.setClient(email, out);
			this.email = email;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public void run() {
		
		//Bisherige Verlauf anzeigen
		while(true){
			try {
				Object nachricht = in.readObject();
				System.out.println("a");
				if(nachricht instanceof Chatverlauf){
					System.out.println("b");
				    Chatverlauf chat = (Chatverlauf) nachricht;
					if(chat != null){
						if(clientList.isOnline(chat.getUser())){
							clientList.getClient(chat.getUser()).writeObject(chat);
						}
						else{
					     //Ansonsten in Datenbank abspeichern
						}
					}
				}
				else if(nachricht instanceof String){
					String userInput = (String) nachricht;
					Map<String, String> queryData = splitQuery(userInput);
					if(queryData.isEmpty() || !queryData.containsKey("typ")){
						out.writeObject(false);
					}
					else if(queryData.get("typ").equals("getfriends")){
						try {
							Map<String, String> friendList = DBConn.getFriends(queryData.get("email"));
							out.writeObject(friendList);
						} catch (SQLException e) {
							e.printStackTrace();
							out.writeObject(null);
						}
					}
					else if(queryData.get("typ").equals("getusername")){
						String email = queryData.get("email");
						out.writeObject(DBConn.getUserName(email));
					}
					else if(queryData.get("typ").equals("addfriend")){
						String friendEmail = queryData.get("friendmail");
						String myEmail = queryData.get("email");
						DBConn.addNewFriend(friendEmail, myEmail);
					}
					else if(queryData.get("typ").equals("email")){
						setEmail(queryData.get("email"));
					}
					else {
						if(userInformationAndRegistrationAndLogin(queryData)){
							out.writeObject(true);
						} 
						else{
							out.writeObject(false);
						}
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				clientList.removeClient(email);
				System.out.println(clientList.Count());
				break;
			} 
		}
	}

}
