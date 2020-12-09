import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;



public class DBconnector {
	
	SimpleDateFormat format2 = new SimpleDateFormat ( "yyyy�� MM��dd�� HH��mm��ss��");
	Calendar cal = Calendar.getInstance();
	 public int index;
	 public int stock;
	 public String name;
	 public int price;
	 public int[] priority = new int[5]; // 5���� ������ �켱������ �����ϰ� ����
	 
	 
	 private Connection con; // DB�� ������ ����� ��ü
	 private Statement st; // DB ���¸� ������ ��ü
	 private ResultSet rs; // DB ���� ����� �޾ƿ� ��ü

	 
	 public DBconnector() {
		 try {
			 loginInfo lnfo = new loginInfo();
			 con = DriverManager.getConnection("jdbc:mysql://localhost:3306/vendingmachine", "root",new loginInfo().dbPw); // ��Ʈ�������� DB����
			 st = con.createStatement();
			 st.executeUpdate("USE machine;"); // ����� ��Ű�� ����
			 System.out.println("����!");
		 }
		 catch(Exception e) {
			 System.out.println("�����ͺ��̽� ���� ���� : " + e.getMessage());
		 }
	 }

	 

	 
	 
	  public static void naverMailSend(String drink) { 
		  String host = "smtp.naver.com"; // ���̹��� ��� ���̹� ����, gmail��� gmail ���� 
		  String user = new loginInfo().naverId; // �߽��� ���̵� 
		  String password = new loginInfo().naverPw; // �߽��� ��й�ȣ      
		  // SMTP ���� ������ �����Ѵ�. 
		  Properties props = new Properties(); 
		  props.put("mail.smtp.host", host); 
		  props.put("mail.smtp.port", 587); 
		  props.put("mail.smtp.auth", "true"); 
		  
		  Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() 
		  {
			  protected PasswordAuthentication getPasswordAuthentication() {
				  return new PasswordAuthentication(user, password); 
				  } 
			  }); 
		  
		  try { 
			  MimeMessage message = new MimeMessage(session); 
			  message.setFrom(new InternetAddress(user));
			  message.addRecipient(Message.RecipientType.TO, new InternetAddress("ths3630802@naver.com")); 
			  
			  // ���� ����
			  message.setSubject("���˸�"); 
			  
			  String text = drink + "��� ���� 0�� �Դϴ� �߰� ���� ��Ź�帳�ϴ�.";
			  // ���� ����
			  message.setText(text); 
			  
			  // send the message
			  Transport.send(message);
			  System.out.println("Success Message Send"); 
			  } catch (Exception e) { 
				  e.printStackTrace(); 
				  System.out.println("���� ����"); 
				 }
		 }
	 



	 public void mail(String str)
	 {
		 naverMailSend(str);
	 }
	public void checkStock() // ���Ȯ��
	{
		
	}
	public boolean login(String id, String pw) {
		String search = "select * from member where userID = '" + id + "'"; // �ش� ���̵� �ִ��� �˻��Ѵ�.
		try {
			rs = st.executeQuery(search);
			if(rs.next())  // �ش� ���̵� �ִ� ��� 
			{
				if(pw.equals(rs.getString("userPW"))) // ��й�ȣ �˻� 
				{
					System.out.println("login!");
					return true;
				}
				JOptionPane.showMessageDialog(null, "��й�ȣ�� �ٸ��ϴ�."); // ���̵� ������ ������ ��й�ȣ�� �ٸ���� �̸� ǥ���Ѵ�.
				System.out.println("fail");
				return false;
			}
		} catch(Exception e) {
			System.out.println("db���� ����");
			return false;
		}
		return true;
	}
	
	public boolean regist(String id, String pw, String nick) 
	{
		String reg = "INSERT INTO member(userID, userPW, nickname) " + "VALUES('" + id + "', '" + pw + "', '"+ nick +"' )"; // ���̵�� ��й�ȣ�� DB�� insert�Ѵ�.
		try {
			st.executeUpdate(reg);
			System.out.println("��ϿϷ�");
			return true;
		} catch(Exception e) {
			System.out.print(e.getMessage());
		}
		return false;
	}
	
	public String getNick(String id)
	{		
		String search = "select * from member where userID = '" + id +"' "; // �ش� �ε��� ���Ḧ �ҷ��´�
		try {
			rs = st.executeQuery(search);
			if(rs.next()) {
				return rs.getString("nickname");
			}
			return "-1";
		}catch(Exception e) {
			System.out.println("������ ���� ����");

		}
		
		return "-1";
	}
	
	public String getName(int index) // �ش� �ε��� ���� �̸� ��������
	{
		int pri = getFirstPriority(index);
		String search = "select * from stock where dIndex = " + index +" && dPriority = "+ pri +" order by dPriority"; // �ش� �ε��� ���Ḧ �ҷ��´�
			
		try {
			rs = st.executeQuery(search);
			while(rs.next()) { // 0�� ��� ��ŵ�ϱ�
				if(rs.getInt(5) != 0)
					return rs.getString(3);
			}
			return "������"; // ��� ���ΰ� ���� ��
		}catch(Exception e) {
			System.out.println("������ ���� ����");

		}
		return "";
	}
	public String getLastName(int index) // �ش� �ε����� ������ ���� �̸� ��������
	{
		int pri = getLastPriority(index);
		String search = "select * from stock where dIndex = " + index +" && dPriority = "+ pri +" order by dPriority"; // �ش� �ε��� ���Ḧ �ҷ��´�
			
		try {
			rs = st.executeQuery(search);
			while(rs.next()) { // 0�� ��� ��ŵ�ϱ�
				if(rs.getInt(5) != 0)
					return rs.getString(3);
			}
			return "������"; // ��� ���ΰ� ���� ��
		}catch(Exception e) {
			System.out.println("������ ���� ����");

		}
		return "";
	}
	public int getFirstPriority(int index) // �ش� �ε������� ��� �ִ� ù��° �켱���� �������� 
	{
		String search = "select * from stock where dIndex = " + index +" order by dPriority"; // �ش� �ε��� ���Ḧ �ҷ��´� �ε��� �������� ����
		
		try {
			rs = st.executeQuery(search);
			while(rs.next()) { // 0�� ��� ��ŵ�ϱ�
				if(rs.getInt(5) != 0)
					return rs.getInt(2);
			}
			// ���� �ε��� ���ΰ� 0�� ��� ������ �ε��� ��ȯ
			return getLastPriority(index);
		}catch(Exception e) {
			System.out.println("������ ���� ����");

		}
		return 0;
	}
	public int getLastPriority(int index) // �ش� �ε������� ������ �켱���� �������� 
	{
		String search = "select * from stock where dIndex = " + index +" order by dPriority desc"; // �ش� �ε��� ���Ḧ �ҷ��´� �ε��� �������� ����
		
		try {
			rs = st.executeQuery(search);
			if(rs.next()) {
				return rs.getInt(2);
			}
			return 0;
		}catch(Exception e) {
			System.out.println("������ ���� ����");

		}
		return 0;
	}
	public int getPrice(int index) // �ش� �ε��� ���� ���� ��������
	{
		int pri = getFirstPriority(index);
		String search = "select * from stock where dIndex = " + index +" && dPriority = "+ pri +" order by dPriority"; // �ش� �ε��� ���Ḧ �ҷ��´�
		
		try {
			rs = st.executeQuery(search);
			while(rs.next()) {
				if(rs.getInt(5) != 0)
				 return rs.getInt(4);
			}
			return 0; // ��� ���� ��� 0����
		}catch(Exception e) {
			System.out.println("������ ���� ����");

		}
		return -1;
	}
	public int getAllStock(int index) // �ش� �ε��� ���� ���� ���� ��������
	{
		String search = "select * from stock where dIndex = " + index +" order by dPriority"; // �ش� �ε��� ���Ḧ �ҷ��´�
		
		try {
			rs = st.executeQuery(search);
			int sum = 0;
			while(rs.next()) {
				sum += rs.getInt(5);
			}
			return sum;
		}catch(Exception e) {
			System.out.println("������ ���� ����");

		}
		return -1;
	}
	public int getStock(int index) // �ش� �ε��� ���� �켱������ ���� ���� ��������
	{
		int pri = getFirstPriority(index);
		String search = "select * from stock where dIndex = " + index +" && dPriority = "+ pri +" order by dPriority"; // �ش� �ε��� ���Ḧ �ҷ��´�
		
		try {
			rs = st.executeQuery(search);
			int sum = 0;
			if(rs.next()) {
				sum += rs.getInt(5);
			}
			return sum;
		}catch(Exception e) {
			System.out.println("������ ���� ����");

		}
		return -1;
	}

	public void deleteZero() // ��� 0�� ���� DB���� ����
	{
		try {
			st.executeUpdate("delete from stock where dStock = 0;"); // 0�� ���� ����
		}catch(Exception e) {
			System.out.println("������ ���� ����");

		}
	}
	public void insertDrink(int index, String name, int price, int stock)
	{ 
		int pri = getLastPriority(index) + 1;
		String str = "insert into stock(dIndex , dPriority, dName, dPrice , dStock) values ("+ index +", "+pri+", '"+name+"' , "+price+" , "+stock+")";
		try {
			st.executeUpdate(str); // 0�� ���� ����
		}catch(Exception e) {
			System.out.println("������ ���� ����");
		}
		
	}
	public void incStock(int index) // ���� ��� ����
	{
		int stock = getStock(index) + 1;
		int pri = getFirstPriority(index);
		String str = "update stock set dStock = " + stock + " where dPriority = " + pri + " && dIndex = " +index;
		try {
			st.executeUpdate(str); // �ε����� ���� ť�� ��� ���̱�
		}catch(Exception e) {
			System.out.println("���� ���� ����");
		}
	
	}
	public void updateChange(int index , int stock) // �ܵ� ��� ����
	{
		int pri = getFirstPriority(index);
		String str = "update stock set dStock = " + stock + " where dPriority = " + pri + " && dIndex = " +index;
		try {
			st.executeUpdate(str); // db�� ����
		}catch(Exception e) {
			System.out.println("���� ���� ����");
		}
	}
	
	public boolean reduceStock(int index) // ���� ��� ���̱�
	{
		int stock = getStock(index) - 1;
		int pri = getFirstPriority(index);
		String str = "update stock set dStock = " + stock + " where dPriority = " + pri + " && dIndex = " +index;
		try {
			st.executeUpdate(str); // �ε����� ���� ť�� ��� ���̱�
		}catch(Exception e) {
			System.out.println("���� ����");
		}
		if(stock == 0) // ���� �켱������ ��� �� �پ��ٸ� ���� ����� ������ �޾ƿ;� �ϱ� ������ false�� ��ȯ�Ѵ�.
		{
			return true;
		}

		return false;
	}
	public void updateEmptyDate(int index ) // ��� ���� ���� ��¥
	{
		int first = getFirstPriority(index);
		int last = getLastPriority(index);
		int pri;
		if(first == last) // ������ �ε����� ��� 0�̶��
			pri = last;
		else 
			pri = first - 1; // ��� 0�� ������ �ε����� �����´�.
		
		String date = format2.format(new Date()); // ���糯¥
		String str = "update stock set emptyDate = '" + date + "' where dPriority = " + pri + " && dIndex = " +index;
		try {
			st.executeUpdate(str); // �ε����� ���� ť�� ��� ���̱�
		}catch(Exception e) {
			System.out.println("��¥ ���� ����");
		}
	}
	public boolean changePw(String id, String pw) // ��й�ȣ ����
	{
		String search = "select * from member where userID = '" + id + "'"; // �ش� ���̵� �ִ��� �˻��Ѵ�.
		try {
			rs = st.executeQuery(search);
			if(rs.next())  // �ش� ���̵� �ִ� ��� 
			{
				String str = "update member set userPW = '" + pw + "' where userID = '" + id + "'";
				st.executeUpdate(str);
				return true;
			}
			else
			return false;
		} catch(Exception e) {
			System.out.println("db���� ����");
		}
		return false;
	}
	
	public void inputIncome(int income) // ������ db�� �߰��� ������.
	{
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		String search = "select * from income where year = " + year +" && month = "+ month + " && day = " + day ; // �ش� �ε��� ���Ḧ �ҷ��´�
		
		try {
			rs = st.executeQuery(search);
			// �ش� ��¥�� �����Ͱ� ������ �߰��Ѵ�.
			if(rs.next()) { 
				income += rs.getInt(4);
				String update = "update income set income  = "+ income +" where year = " + year +" && month = "+ month + " && day = " + day ; // �ش� �ε��� ���Ḧ �ҷ��´�
				st.executeLargeUpdate(update);
			}
			// ��¥�� �����Ͱ� ������ �߰��Ѵ�.
			else {
				String input = "insert into income (year, month, day, income) values ("+year+", "+month+ ", "+day+", "+income+")";
				st.executeLargeUpdate(input);
			}
			return;
		}catch(Exception e) {
			System.out.println("������ ���� ����");

		}
		return;
	}
	public int getCurrentIncome() // ���� ����
	{
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String search = "select * from income where year = " + year +" && month = "+ month + " && day = " + day ; // �ش� �ε��� ������ �ҷ��´�
		try {
			rs = st.executeQuery(search);
			// �ش� ��¥�� �����Ͱ� ������ ��ȯ.
			if(rs.next()) { 
				return rs.getInt(4);
			}
			// ��¥�� �����Ͱ� ������ -1 ��ȯ
			return 0;
		}catch(Exception e) {
			System.out.println("������ ���� ����");

		}
		return 0;
	}
	
	public int getMonthIncome(int month) // ���� ����
	{
		int year = cal.get(Calendar.YEAR);
		String search = "select * from income where year = " + year +" && month = "+ month; // �ش� �ε��� ������ �ҷ��´�
		try {
			rs = st.executeQuery(search);
			int sum = 0;
			// �ش� ��¥�� �����Ͱ� ������ �ݺ�.
			while(rs.next()) { 
				sum += rs.getInt(4); // �Ѵ� ������ ���Ѵ�.
			}
			 return sum;
		}catch(Exception e) {
			System.out.println("������ ���� ����");
		}
		return -1;
	}
	
	public static void main(String[] args) {

		DBconnector db = new DBconnector();
	}
	
}
