package Chat.Controller;
import java.io.Closeable;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DBConnection implements Closeable{
	private java.sql.Connection con;
	
	static {
		try{
			Class.forName("com.mysql.jdbc.Driver");
		}catch(Exception e){e.printStackTrace();}
	}
	
	public DBConnection() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/users","root","");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean login(String email, String password){
		PreparedStatement stmt;
		ResultSet rs;
		try{
		String sql = "SELECT * FROM logindata WHERE MAIL = ? AND PASSWORD = ?;";
		stmt = con.prepareStatement(sql);
		stmt.setString(1, email);
		stmt.setString(2, password);
		rs = stmt.executeQuery();		
		if(rs.next()){
			
			PreparedStatement stmt2;
			ResultSet rs2;
			
			String sql2 = "SELECT mail FROM logindata WHERE NAME = ? AND PASSWORD = ?;";
			stmt2 = con.prepareStatement(sql2);
			stmt2.setString(1, email);
			stmt2.setString(2, password);
			rs2 = stmt2.executeQuery();
			rs2.next();
			return true;
		}else{return false;}
		}catch(Exception e){System.out.println(e);}
		return false;
	}
	
	public String getUserName(String email){
		PreparedStatement stmt;
		ResultSet rs;
		try{
		String sql = "SELECT NAME FROM logindata WHERE MAIL = ?;";
		stmt = con.prepareStatement(sql);
		stmt.setString(1, email);
		rs = stmt.executeQuery();
		if(rs.next()){
			return rs.getString("name");
		}
		}
		catch(Exception e){System.out.println(e);}
		return "";
	}
	
	
	public boolean register(String name, String pw, String mail){
		String name2 = name;
		String pw2 = pw;
		String mail2 = mail;
		PreparedStatement stmt;
		
		String dateString = null;
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-hh.mm.ss");
		java.util.Date currentDate = Calendar.getInstance().getTime();
		
		try{
		dateString = formatter.format( currentDate );
		String sql = "INSERT INTO logindata (NAME, MAIL, PASSWORD, REGISTER_DATE) VALUES(?, ?, ?, ?);";
		stmt = con.prepareStatement(sql);
		stmt.setString(1, name2);
		stmt.setString(2, mail2);
		stmt.setString(3, pw2);
		stmt.setString(4, dateString);
		stmt.executeUpdate();
		createFriendList(mail2);
		return true;
		}
		catch(Exception e){return false;}
	}
	
	public void createFriendList(String userMail){
		PreparedStatement stmt;
		String sql = "CREATE TABLE "+userMail+" (id INTEGER(8) NOT NULL AUTO_INCREMENT, friendmail VARCHAR(255), PRIMARY KEY(id));";
		try {
			stmt = con.prepareStatement(sql);
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Map<String, String> getFriends(String email) throws SQLException {
		Map<String, String> firendList = new HashMap();
		String sql = "SELECT friendmail, mail, name FROM "+email+ " join logindata on " + email + ".friendmail = logindata.mail;";
		ResultSet rs = null;
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		while(rs.next()){
			firendList.put(rs.getString("friendmail"), rs.getString("name"));
		}
		
		return firendList;
	}
	
	
	public void addNewFriend(String friendName, String email){
		String sql = "INSERT INTO "+email+" (friendmail) VALUES (?);";
		String kontrollSql = "SELECT mail FROM logindata WHERE MAIL = ?;";
		
		try{
			PreparedStatement stmtControl = con.prepareStatement(kontrollSql);
			stmtControl.setString(1, friendName);
			ResultSet rs2 = stmtControl.executeQuery();
			if(rs2.next()){
				PreparedStatement stmt = con.prepareStatement(sql);
				stmt.setString(1, friendName);
				stmt.executeUpdate();
			}
		} catch (SQLException e) {e.printStackTrace();}
	}
	

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
