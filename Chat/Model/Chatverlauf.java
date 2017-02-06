package Chat.Model;


import java.io.Serializable;
import java.util.Date;

public class Chatverlauf <T> implements Serializable{
	private final String user;
	private final T content;
	public final String format;
	private final Date date;
	private final String user2;
	private String fileName;
	
	public Chatverlauf(String user1, String user2, T content, String format){
		this.content = content;
		this.user = user1;
		this.user2 = user2;
		this.format = format;
		date = new Date();
	}
	
	public void setFileName(String fileName){
		fileName = fileName;
	}
		
	@Override
	public String toString(){
		return user + ": " + content;
	}
	public String getUser(){
		return user2;
	}
	public T getContent(){
		return content;
	}
	
	public String getFileName(){
		return fileName;
	}
}
