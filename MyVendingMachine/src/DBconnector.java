import java.sql.*;

public class DBconnector {

	 private Connection con; // DB�� ������ ����� ��ü
	 private Statement st; // DB ���¸� ������ ��ü
	 private ResultSet rs; // DB ���� ����� �޾ƿ� ��ü

	 public DBconnector() {
		 try {
			 loginInfo lnfo = new loginInfo();
			 con = DriverManager.getConnection("jdbc:mysql://localhost:3306/vendingmachine", "root",new loginInfo().dbPw); // ��Ʈ�������� DB����
			 st = con.createStatement();
			 st.executeUpdate("USE new_schema;"); // ����� ��Ű�� ����
			 System.out.println("����!");
		 }
		 catch(Exception e) {
			 System.out.println("�����ͺ��̽� ���� ���� : " + e.getMessage());
		 }
	 }

	public boolean login(String id, String pw) {
		String search = "select * from member where " + id + ";"; // �ش� ���̵� �ִ��� �˻��Ѵ�.
		try {
			rs = st.executeQuery(search);
			if(rs.next())  // �ش� ���̵� �ִ� ��� 
			{
				if(pw.equals(rs.getString("userPw"))) // ��й�ȣ �˻� 
				{
					System.out.println("login!");
					return true;
				}
				System.out.println("fail");
				return false;
			}
		} catch(Exception e) {
			System.out.println("db���� ����");
			return false;
		}
		return true;
	}
	
	public boolean regist(String id, String pw) 
	{
		String reg = "INSERT INTO member(userId, userPw) " + "VALUES('" + id + "', '" + pw + "');"; // ���̵�� ��й�ȣ�� DB�� insert�Ѵ�.
		try {
			st.executeUpdate(reg);
			System.out.println("��ϿϷ�");
			return true;
		} catch(Exception e) {
			System.out.print(e.getMessage());
		}
		return false;
	}
}
