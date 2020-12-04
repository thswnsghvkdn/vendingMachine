import java.sql.*;

public class DBconnector {

	 private Connection con; // DB에 연결을 담당할 객체
	 private Statement st; // DB 상태를 저장할 객체
	 private ResultSet rs; // DB 상태 결과를 받아올 객체

	 public DBconnector() {
		 try {
			 loginInfo lnfo = new loginInfo();
			 con = DriverManager.getConnection("jdbc:mysql://localhost:3306/vendingmachine", "root",new loginInfo().dbPw); // 루트계정으로 DB연결
			 st = con.createStatement();
			 st.executeUpdate("USE new_schema;"); // 사용할 스키마 선정
			 System.out.println("연결!");
		 }
		 catch(Exception e) {
			 System.out.println("데이터베이스 연결 오류 : " + e.getMessage());
		 }
	 }

	public boolean login(String id, String pw) {
		String search = "select * from member where " + id + ";"; // 해당 아이디가 있는지 검사한다.
		try {
			rs = st.executeQuery(search);
			if(rs.next())  // 해당 아이디가 있는 경우 
			{
				if(pw.equals(rs.getString("userPw"))) // 비밀번호 검사 
				{
					System.out.println("login!");
					return true;
				}
				System.out.println("fail");
				return false;
			}
		} catch(Exception e) {
			System.out.println("db접속 오류");
			return false;
		}
		return true;
	}
	
	public boolean regist(String id, String pw) 
	{
		String reg = "INSERT INTO member(userId, userPw) " + "VALUES('" + id + "', '" + pw + "');"; // 아이디와 비밀번호로 DB에 insert한다.
		try {
			st.executeUpdate(reg);
			System.out.println("등록완료");
			return true;
		} catch(Exception e) {
			System.out.print(e.getMessage());
		}
		return false;
	}
}
