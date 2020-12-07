import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JOptionPane;



public class DBconnector {
	SimpleDateFormat format2 = new SimpleDateFormat ( "yyyy년 MM월dd일 HH시mm분ss초");

	Calendar cal = Calendar.getInstance();

	 public int index;
	 public int stock;
	 public String name;
	 public int price;
	 public int[] priority = new int[5]; // 5개의 음료의 우선순위를 저장하고 있음
	 
	 
	 private Connection con; // DB에 연결을 담당할 객체
	 private Statement st; // DB 상태를 저장할 객체
	 private ResultSet rs; // DB 상태 결과를 받아올 객체

	 
	 public DBconnector() {
		 try {
			 loginInfo lnfo = new loginInfo();
			 con = DriverManager.getConnection("jdbc:mysql://localhost:3306/vendingmachine", "root",new loginInfo().dbPw); // 루트계정으로 DB연결
			 st = con.createStatement();
			 st.executeUpdate("USE machine;"); // 사용할 스키마 선정
			 System.out.println("연결!");
		 }
		 catch(Exception e) {
			 System.out.println("데이터베이스 연결 오류 : " + e.getMessage());
		 }
	 }

	public void checkStock() // 재고확인
	{
		
	}
	public boolean login(String id, String pw) {
		String search = "select * from member where userID = '" + id + "'"; // 해당 아이디가 있는지 검사한다.
		try {
			rs = st.executeQuery(search);
			if(rs.next())  // 해당 아이디가 있는 경우 
			{
				if(pw.equals(rs.getString("userPW"))) // 비밀번호 검사 
				{
					System.out.println("login!");
					return true;
				}
				JOptionPane.showMessageDialog(null, "비밀번호가 다릅니다."); // 아이디 정보는 있지만 비밀번호가 다른경우 이를 표시한다.
				System.out.println("fail");
				return false;
			}
		} catch(Exception e) {
			System.out.println("db접속 오류");
			return false;
		}
		return true;
	}
	
	public boolean regist(String id, String pw, String nick) 
	{
		String reg = "INSERT INTO member(userID, userPW, nickname) " + "VALUES('" + id + "', '" + pw + "', '"+ nick +"' )"; // 아이디와 비밀번호로 DB에 insert한다.
		try {
			st.executeUpdate(reg);
			System.out.println("등록완료");
			return true;
		} catch(Exception e) {
			System.out.print(e.getMessage());
		}
		return false;
	}
	
	public String getNick(String id)
	{		
		String search = "select * from member where userID = '" + id +"' "; // 해당 인덱스 음료를 불러온다
		try {
			rs = st.executeQuery(search);
			if(rs.next()) {
				return rs.getString("nickname");
			}
			return "-1";
		}catch(Exception e) {
			System.out.println("데이터 전송 오류");

		}
		
		return "-1";
	}
	
	public String getName(int index) // 해당 인덱스 음료 이름 가져오기
	{
		int pri = getFirstPriority(index);
		String search = "select * from stock where dIndex = " + index +" && dPriority = "+ pri +" order by dPriority"; // 해당 인덱스 음료를 불러온다
			
		try {
			rs = st.executeQuery(search);
			while(rs.next()) { // 0인 재고 스킵하기
				if(rs.getInt(5) != 0)
					return rs.getString(3);
			}
			return "재고없음"; // 재고 전부가 없을 때
		}catch(Exception e) {
			System.out.println("데이터 전송 오류");

		}
		return "";
	}
	public int getFirstPriority(int index) // 해당 인덱스에서 재고가 있는 첫번째 우선순위 가져오기 
	{
		String search = "select * from stock where dIndex = " + index +" order by dPriority"; // 해당 인덱스 음료를 불러온다 인덱스 오름차순 정렬
		
		try {
			rs = st.executeQuery(search);
			while(rs.next()) { // 0인 재고 스킵하기
				if(rs.getInt(5) != 0)
					return rs.getInt(2);
			}
			// 만약 인덱스 전부가 0일 경우 마지막 인덱스 반환
			return getLastPriority(index);
		}catch(Exception e) {
			System.out.println("데이터 전송 오류");

		}
		return 0;
	}
	public int getLastPriority(int index) // 해당 인덱스에서 마지막 우선순위 가져오기 
	{
		String search = "select * from stock where dIndex = " + index +" order by dPriority desc"; // 해당 인덱스 음료를 불러온다 인덱스 오름차순 정렬
		
		try {
			rs = st.executeQuery(search);
			if(rs.next()) {
				return rs.getInt(2);
			}
			return 0;
		}catch(Exception e) {
			System.out.println("데이터 전송 오류");

		}
		return 0;
	}
	public int getPrice(int index) // 해당 인덱스 음료 가격 가져오기
	{
		int pri = getFirstPriority(index);
		String search = "select * from stock where dIndex = " + index +" && dPriority = "+ pri +" order by dPriority"; // 해당 인덱스 음료를 불러온다
		
		try {
			rs = st.executeQuery(search);
			while(rs.next()) {
				if(rs.getInt(5) != 0)
				 return rs.getInt(4);
			}
			return 0; // 재고가 없을 경우 0리턴
		}catch(Exception e) {
			System.out.println("데이터 전송 오류");

		}
		return -1;
	}
	public int getAllStock(int index) // 해당 인덱스 음료 제고 전부 가져오기
	{
		String search = "select * from stock where dIndex = " + index +" order by dPriority"; // 해당 인덱스 음료를 불러온다
		
		try {
			rs = st.executeQuery(search);
			int sum = 0;
			while(rs.next()) {
				sum += rs.getInt(5);
			}
			return sum;
		}catch(Exception e) {
			System.out.println("데이터 전송 오류");

		}
		return -1;
	}
	public int getStock(int index) // 해당 인덱스 현재 우선순위의 음료 제고 가져오기
	{
		int pri = getFirstPriority(index);
		String search = "select * from stock where dIndex = " + index +" && dPriority = "+ pri +" order by dPriority"; // 해당 인덱스 음료를 불러온다
		
		try {
			rs = st.executeQuery(search);
			int sum = 0;
			if(rs.next()) {
				sum += rs.getInt(5);
			}
			return sum;
		}catch(Exception e) {
			System.out.println("데이터 전송 오류");

		}
		return -1;
	}
	public void deleteZero() // 재고가 0인 음료 DB에서 제거
	{
		try {
			st.executeUpdate("delete from stock where dStock = 0;"); // 0인 음료 제거
		}catch(Exception e) {
			System.out.println("데이터 삭제 오류");

		}
	}
	public void insertDrink(int index, String name, int price, int stock)
	{ 
		int pri = getLastPriority(index) + 1;
		String str = "insert into stock(dIndex , dPriority, dName, dPrice , dStock) values ("+ index +", "+pri+", '"+name+"' , "+price+" , "+stock+")";
		try {
			st.executeUpdate(str); // 0인 음료 제거
		}catch(Exception e) {
			System.out.println("데이터 삽입 오류");
		}
		
	}
	public void incStock(int index) // 동전 재고 증가
	{
		int stock = getStock(index) + 1;
		int pri = getFirstPriority(index);
		String str = "update stock set dStock = " + stock + " where dPriority = " + pri + " && dIndex = " +index;
		try {
			st.executeUpdate(str); // 인덱스의 현재 큐의 재고 줄이기
		}catch(Exception e) {
			System.out.println("동전 투입 오류");
		}
	
	}
	public void updateChange(int index , int stock) // 잔돈 재고 변경
	{
		int pri = getFirstPriority(index);
		String str = "update stock set dStock = " + stock + " where dPriority = " + pri + " && dIndex = " +index;
		try {
			st.executeUpdate(str); // db에 저장
		}catch(Exception e) {
			System.out.println("동전 투입 오류");
		}
	}
	
	public boolean reduceStock(int index) // 음료 재고 줄이기
	{
		int stock = getStock(index) - 1;
		int pri = getFirstPriority(index);
		String str = "update stock set dStock = " + stock + " where dPriority = " + pri + " && dIndex = " +index;
		try {
			st.executeUpdate(str); // 인덱스의 현재 큐의 재고 줄이기
		}catch(Exception e) {
			System.out.println("반출 오류");
		}
		if(stock == 0) // 현재 우선순위의 재고가 다 줄었다면 다음 재고의 정보를 받아와야 하기 때문에 false를 반환한다.
		{
			return true;
		}

		return false;
	}
	public void updateEmptyDate(int index ) // 재고가 전부 나간 날짜
	{
		int first = getFirstPriority(index);
		int last = getLastPriority(index);
		int pri;
		if(first == last) // 마지막 인덱스의 재고가 0이라면
			pri = last;
		else 
			pri = first - 1; // 재고가 0인 마지막 인덱스를 가져온다.
		
		String date = format2.format(new Date()); // 현재날짜
		String str = "update stock set emptyDate = '" + date + "' where dPriority = " + pri + " && dIndex = " +index;
		try {
			st.executeUpdate(str); // 인덱스의 현재 큐의 재고 줄이기
		}catch(Exception e) {
			System.out.println("날짜 기입 오류");
		}
	}
	public boolean changePw(String id, String pw) // 비밀번호 변경
	{
		String search = "select * from member where " + id + ";"; // 해당 아이디가 있는지 검사한다.
		try {
			rs = st.executeQuery(search);
			if(rs.next())  // 해당 아이디가 있는 경우 
			{
				String str = "update member set userPW = " + pw + " where userID = " + id;
				return true;
			}
			else
			return false;
		} catch(Exception e) {
			System.out.println("db접속 오류");
		}
		return false;
	}
	
	public void inputIncome(int income) // 수입을 db에 추가해 나간다.
	{
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		String search = "select * from income where year = " + year +" && month = "+ month + " && day = " + day ; // 해당 인덱스 음료를 불러온다
		
		try {
			rs = st.executeQuery(search);
			// 해당 날짜에 데이터가 있으면 추가한다.
			if(rs.next()) { 
				income += rs.getInt(4);
				String update = "update income set income  = "+ income +" where year = " + year +" && month = "+ month + " && day = " + day ; // 해당 인덱스 음료를 불러온다
				st.executeLargeUpdate(update);
			}
			// 날짜에 데이터가 없으면 추가한다.
			else {
				String input = "insert into income (year, month, day, income) values ("+year+", "+month+ ", "+day+", "+income+")";
				st.executeLargeUpdate(input);
			}
			return;
		}catch(Exception e) {
			System.out.println("데이터 전송 오류");

		}
		return;
	}
	public int getIncome()
	{
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String search = "select * from income where year = " + year +" && month = "+ month + " && day = " + day ; // 해당 인덱스 음료를 불러온다
		try {
			rs = st.executeQuery(search);
			// 해당 날짜에 데이터가 있으면 반환.
			if(rs.next()) { 
				return rs.getInt(4);
			}
			// 날짜에 데이터가 없으면 -1 반환
			return -1;
		}catch(Exception e) {
			System.out.println("데이터 전송 오류");

		}
		return -1;
	}
	
	public static void main(String[] args) {

		DBconnector db = new DBconnector();
	}
	
}
