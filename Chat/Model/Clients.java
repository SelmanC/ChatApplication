package Chat.Model;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class Clients {
	private static HashMap<String, ObjectOutputStream> clientList;
	
	public Clients(){clientList = new HashMap<>();}
	
	public void setClient(String email, ObjectOutputStream client) throws IOException{
		clientList.put(email, client);
	}
	public ObjectOutputStream getClient(String email){
		return clientList.get(email);
	}
	public void removeClient(String email){
		clientList.remove(email);
	}
	public boolean isOnline(String email){
		return clientList.containsKey(email);
	}
	public int Count(){
		return clientList.size();
	}
	public String GetEmail(){
		return clientList.keySet().iterator().next();
	}
}
