import java.awt.Color; // 특수문자 검사용 라이브러리
import java.awt.EventQueue;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;


class Beverage // 음료수 클래스
{
	public boolean empty = false; // 빈 재고 확인
	public JLabel nameText; // 음료수의 텍스트
	public JButton drinkButton; // 음료수의 버튼
	String color;
	Beverage( JLabel label, JButton button) // 초기에 세개 음료수를 추가하는 생성자
	{
		nameText = label; // 음료의 텍스트를 연결
		drinkButton = button; // 음료 버튼 객체를 연결
	}
}


class Machine
{
	public boolean[] changeState = new boolean[5]; // 잔돈 재고 상태 

	int dollarLimit = 0; // 천원 상한선
	int won[] = {10 , 50 , 100 , 500 ,1000};
	private String id;
	JLabel changeScreen; // 잔돈현황 스크린
	JLabel inputScreen; // 삽입한 돈이 나오는 스크린
	int tempMoney; // 사용자가 삽입한 돈
	Beverage[] beverage = new Beverage[6];// 음료수 6개
	Machine(JLabel screen, JLabel change) // 생성자 
	{		
		inputScreen = screen;
		changeScreen = change;
		tempMoney = 0; /// 현재 삽입된 돈을 0원으로 초기화
		for(int i = 0 ; i < 5 ; i++)
			changeState[i] = false;

	}
	
	public void screenChange()
	{
		boolean allClear = true; // 모든 재고가 있는지 판단.
		String str = ""; // 재고상태 문자열
		for(int i = 0 ; i < 5; i++)
		{
			if(changeState[i]) // 해당인덱스의 잔돈에 재고가 없다면
			{
				str += won[i] + "원 ";
				allClear = false;
			}
		}
		if(allClear) // 모든 재고가 있다면
		{
			changeScreen.setText("    잔돈 반환");
		}
		else 
		{
			changeScreen.setText(str + "없음");
		}
	}
	
	
	
	void setId(String id)
	{
		this.id = id;
	}
	void newItem(int index, JLabel label, JButton button) // 새로운 음료 생성 메소드
	{
		beverage[index] = new Beverage(label, button);
	}
	void changeColor(int input) // 음료수6개의 버튼 색을 알맞게 바꾼다 
	{
		for(int i =0 ; i < 6 ; i++) {
			int t_price =  Integer.parseInt(beverage[i].drinkButton.getText());// 버튼의 텍스트를 정수로 파싱한다.
			if(input >= t_price && beverage[i].empty == false) // 해당 음료의 금액과 비교 
				beverage[i].drinkButton.setBackground(Color.pink);
			else 
				beverage[i].drinkButton.setBackground(Color.gray);
		}
	}
	
	void pressMoney(int index) // 사용자가 화폐버튼을 누를경우
	{
		if(tempMoney >= 5000) // 투입금액 상한선.
			return;
		if(index == 4 && dollarLimit >= 3) // 천원이 들어왔을 때 천원 한계가 넘을경우 종료
			return;
		// 사용자가 최대금액 이상 입력하지 않도록 try catch로 처리
		switch(index)
		{
		case 0 : // 10원
			tempMoney += 10;
			break;
		case 1 : // 50원
			tempMoney += 50;
			break;
		case 2 : // 100원
			tempMoney += 100;
			break;
		case 3 : // 500원
			tempMoney += 500;
			break;
		case 4 : // 1000원
			tempMoney += 1000;
			dollarLimit++;
			break;
		}
		inputScreen.setText(Integer.toString(tempMoney)); // 투입 스크린 금액 갱신
		changeColor(tempMoney); // 버튼 색상 갱신
	}
	void pressChange() // 잔돈 반환버튼  
	{
		// 천원 한도와 투입금액 0으로 초기화
		dollarLimit = 0;
		tempMoney = 0;
		inputScreen.setText(Integer.toString(tempMoney)); // 투입 스크린 금액 갱신
		changeColor(tempMoney); // 버튼 색상 갱신		
	}
	int pressDrink(int index) // 음료버튼
	{
		if(beverage[index].drinkButton.getBackground().equals(Color.pink))
		{
			int price = Integer.parseInt(beverage[index].drinkButton.getText());
			// 객체 삭제함수
			tempMoney -= price;
			inputScreen.setText(Integer.toString(tempMoney)); // 투입 스크린 금액 갱신
			changeColor(tempMoney); // 버튼 색상 갱신
			return 1;
		}
		
		if(index == 5 && tempMoney == 50 && beverage[index].drinkButton.getBackground().equals(Color.gray))
			// 관리자 메뉴를 불러올 이스터에그 50원을 투입한 상태로 랜덤버튼 누르기
		{	
			return -1;
		}
		return 0;
	}
	
	int getTempmoney() {return tempMoney;}
	
	void chageDrink(int index,String name, int price) // 음료 큐의 노드의 정보로 자판기에 표시한다.
	{

		beverage[index].nameText.setText(name);
		beverage[index].nameText.setHorizontalAlignment(SwingConstants.CENTER); //텍스트 가운데 정렬
		beverage[index].drinkButton.setText(Integer.toString(price));		
	}
	
	
}

class MyException extends Exception{
	public MyException(String s) {
		super(s);
	}
}
public class vendingMachine extends JFrame {
	JLabel outlet;
	JLabel Image1;
	JLabel Image2;
	JLabel Image3;
	JLabel Image4;
	JLabel Image5;
	private JLabel outScreen;
	private String userID;
	private int orderIndex; // 사용자가 음료 주문시 음료의 위치
	private String orderName; // 사용자가 음료 주문시 주문할 음료의 이름
	private String orderPrice; // 사용자가 음료 주문시 주문할 음료의 이름
	private int orderNum; // 사용자가 음료 주문시 음료의 위치
	
	private Machine myMachine;
	private JPanel contentPane;
	List list;
	private Socket clientSocket;
	private DataInputStream dataInputStream; // 입력 스트림
	private DataOutputStream dataOutputStream; // 출력 스트림
	
	//login loginMenu = new login();

	public void connect()
	{
		try {
		clientSocket = new Socket("220.69.208.204", 10002);
		System.out.println("접속");
		} catch(Exception e) {
			
		}
	}
	public void streamSetting()
	{
		try {
		dataInputStream = new DataInputStream(clientSocket.getInputStream());
		dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
		System.out.println("데이터 스트림 연결");
		} catch(Exception e) {
			
		}
		
	}
	public void dataSend(int numData,String stringData)
	{	
		try {
			dataOutputStream.writeUTF(stringData);
			dataOutputStream.writeInt(numData);
			}catch(Exception e)
			{
				System.out.println("데이터를 보내는 중 문제 발견");
				System.out.println(e.getMessage());
			}
	}		
	
	// 데이터를 받아오는 메소드 서버에서 보내는 문자열 스트림으로 다른 기능을 구현
	public void dataRecv() {
		new Thread() { // 항상 사용자의 입력을 기다리도록 while문을 스레드로 동작시킨다.
			public void run() {
				int numData;
				String strData;
				String str = "";
				try {
				while(true) 
					{
						strData =  dataInputStream.readUTF();  // 서버가 보낸 문자열 스트림  파악
						
						if(strData.equals("init")) // 자판기 초기화
						{
							for(int i = 0 ; i < 5 ; i++)
							{
								String name = dataInputStream.readUTF();//db에 저장되어있는 음료이름
								int price = dataInputStream.readInt();//db에 저장되어있는 음료재고
								int stock = dataInputStream.readInt(); //db에 저장되어있는 잔돈재고
								myMachine.chageDrink(i, name, price);
								if(stock == 0)
									myMachine.changeState[i] = true; // 재고없음 표시
							}
							myMachine.screenChange(); // 잔돈 텍스트 필드에 상태 표시
						}
						
						else if(strData.equals("random")) // 랜덤 음료수를 뽑은 경우
						{
							// 음료인덱스에 따른 배출구 아이콘 생성
							int num = dataInputStream.readInt();
							switch(num)
							{
								case 0:
									outlet.setIcon(Image1.getIcon());
									break;
								case 1:
									outlet.setIcon(Image2.getIcon());
									break;
								case 2:
									outlet.setIcon(Image3.getIcon());
									break;
								case 3:
									outlet.setIcon(Image4.getIcon());
									break;
								case 4:
									outlet.setIcon(Image5.getIcon());
									break;
							}
							 Timer timer = new Timer(1250, new ActionListener() {
							      @Override
							        public void actionPerformed(ActionEvent e) {
										outlet.setIcon(null);
							        }
							      });
							    timer.setRepeats(false);
							     timer.start();
						}
						else if(strData.equals("stock")) // 재고 버튼을 클릭한 경우
						{
							list.add("음료 재고 현황");
							for(int i = 0 ; i < 5 ; i++)
							{
								numData = dataInputStream.readInt();
								str = myMachine.beverage[i].nameText.getText() + "칸에 현재 음료" + numData + "개 남음";
								list.add(str);
							}
							
							list.add("화폐 재고 현황");
							String won[] = {"10", "50", "100" , "500" ,"1000"};
							for(int i = 0 ; i < 5 ; i++)
							{
								numData = dataInputStream.readInt();
								str = won[i] + "원 " + numData + "개 남음";
								list.add(str);
							}							
						}
						
						
						else if(strData.equals("income")) 
						{
							int income = dataInputStream.readInt();
							list.add("하루 매출 현황 : " + income);
						}
						else if(strData.equals("collect")) 
						{
							int income = dataInputStream.readInt();
							list.add(income + "원 수금하셨습니다.");
						}
						else if(strData.equals("drinkEmpty"))
						{
							numData = dataInputStream.readInt();
							myMachine.beverage[numData].empty = true;
							myMachine.beverage[numData].nameText.setText("재고없음");
							myMachine.beverage[numData].nameText.setHorizontalAlignment(SwingConstants.CENTER); //텍스트 가운데 정렬
							myMachine.beverage[numData].drinkButton.setBackground(Color.gray);

						}
						else if(strData.equals("drinkChange")) // 새로운 음료로 변경
						{
							numData = dataInputStream.readInt();
							String name = dataInputStream.readUTF();//db에 저장되어있는 음료이름
							int price = dataInputStream.readInt();//db에 저장되어있는 음료재고
							myMachine.chageDrink(numData, name, price);
						}
						else if(strData.equals("order")) // 음료주문 
						{
							int index = dataInputStream.readInt(); // 주문할 음료 위치
							String beverageName = dataInputStream.readUTF(); // 음료 이름
							int beveragePrice = dataInputStream.readInt(); // 음료 가격
							
							myMachine.beverage[index].empty = false;
							myMachine.beverage[index].nameText.setText(beverageName);
							myMachine.beverage[index].nameText.setHorizontalAlignment(SwingConstants.CENTER); //텍스트 가운데 정렬
							myMachine.beverage[index].drinkButton.setText(Integer.toString(beveragePrice));
							myMachine.beverage[index].drinkButton.setHorizontalAlignment(SwingConstants.CENTER); //텍스트 가운데 정렬
							myMachine.changeColor(myMachine.tempMoney);
							
						}
						else if(strData.equals("fill"))  // 잔돈 보충 
						{
							for(int i = 0  ; i < 5 ; i++)
							myMachine.changeState[i] = true;
							myMachine.screenChange();
							list.add("잔돈 충전을 완료했습니다.");
						}
						else if(strData.equals("changeEmpty"))
						{
							numData = dataInputStream.readInt();
							myMachine.changeState[numData] = true;
							myMachine.screenChange();
						}
	
					}
				}
				catch (Exception e) 
				{}
			}
		}.start();


	}
	
	public void repaint(vendingMachine frame)
	{
		frame.repaint();
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					vendingMachine frame = new vendingMachine();
					frame.dataOutputStream.writeUTF("start");
					frame.setVisible(true);			
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public vendingMachine() {

		
		connect();
		streamSetting();
		dataRecv();

		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 542, 569);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setForeground(Color.PINK);
		panel.setBounds(27, 26, 487, 494);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel insertScreen = new JLabel("    \uD22C\uC785\uAE08\uC561");
		insertScreen.setForeground(Color.BLACK);
		insertScreen.setBackground(Color.WHITE);
		insertScreen.setBounds(310, 84, 84, 32);
		panel.add(insertScreen);
		
		outScreen = new JLabel("          \uC794\uB3C8 \uBC18\uD658");
		outScreen.setBounds(305, 185, 182, 29);
		panel.add(outScreen);
		
		myMachine = new Machine(insertScreen, outScreen); // Machine 객체 생성
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		panel_1.setBounds(12, 23, 269, 306);
		panel.add(panel_1);
		panel_1.setLayout(null);
		
		Image1 = new JLabel("");
		Image1.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\water.png"));
		Image1.setBounds(22, 20, 57, 58);
		panel_1.add(Image1);
	
		
		Image2 = new JLabel("");
		Image2.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\soda.png"));
		Image2.setBounds(105, 20, 57, 58);
		panel_1.add(Image2);
		
		Image3 = new JLabel("");
		Image3.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\energy.png"));
		Image3.setBounds(191, 20, 57, 58);
		panel_1.add(Image3);
		
		Image4 = new JLabel("");
		Image4.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\coffee.png"));
		Image4.setBounds(22, 171, 57, 58);
		panel_1.add(Image4);
		
		Image5 = new JLabel("");
		Image5.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\starbucks.png"));
		Image5.setBounds(108, 171, 57, 58);
		panel_1.add(Image5);
		
		JLabel lblNewLabel_5 = new JLabel("");
		lblNewLabel_5.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\question.png"));
		lblNewLabel_5.setBounds(193, 171, 57, 58);
		panel_1.add(lblNewLabel_5);

		
		JButton button_1 = new JButton("450");
		button_1.setBounds(22, 128, 57, 23);
		panel_1.add(button_1);
		
		JButton button_2 = new JButton("750");
		button_2.setBounds(105, 128, 57, 23);
		panel_1.add(button_2);
		
		JButton button_3 = new JButton("550");
		button_3.setBounds(189, 128, 57, 23);
		panel_1.add(button_3);
		
		JButton button_4 = new JButton("500");
		button_4.setBounds(22, 263, 57, 23);
		panel_1.add(button_4);
		
		JButton button_5 = new JButton("750");
		button_5.setBounds(105, 263, 57, 23);
		panel_1.add(button_5);
		
		JButton button_6 = new JButton("700");
		button_6.setBounds(189, 263, 57, 23);
		panel_1.add(button_6);
		
		JLabel text_1 = new JLabel("\uBB3C");
		text_1.setBounds(22, 99, 50, 15);
		panel_1.add(text_1);
		
		JLabel text_2 = new JLabel("\uCF5C\uB77C");
		text_2.setBounds(105, 99, 57, 15);
		panel_1.add(text_2);
		
		JLabel text_3 = new JLabel("\uC774\uC628\uC74C\uB8CC");
		text_3.setBounds(191, 99, 50, 15);
		panel_1.add(text_3);
		
		JLabel text_4 = new JLabel("\uCEE4\uD53C");
		text_4.setBounds(22, 239, 50, 15);
		panel_1.add(text_4);
		
		JLabel text_5 = new JLabel("\uC2A4\uD0C0\uBC85\uC2A4");
		text_5.setBounds(110, 239, 57, 15);
		panel_1.add(text_5);
		
		JLabel text_6 = new JLabel("\uB79C\uB364");
		text_6.setHorizontalAlignment(SwingConstants.CENTER); //텍스트 가운데 정렬
		text_6.setBounds(191, 239, 49, 15);
		panel_1.add(text_6);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.WHITE);
		panel_2.setBounds(69, 376, 145, 93);
		panel.add(panel_2);
		panel_2.setLayout(null);
		
		outlet = new JLabel("");
		outlet.setBounds(40, 5, 69, 78);
		panel_2.add(outlet);
		
		JButton button_10 = new JButton("");
		button_10.setBackground(Color.WHITE);
		button_10.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\10\uC6D0.PNG"));

		button_10.setBounds(296, 354, 32, 29);
		panel.add(button_10);
		
		JButton button_50 = new JButton("50");
		button_50.setBounds(345, 354, 60, 32);
		panel.add(button_50);
		
		JButton button_100 = new JButton("100");
		button_100.setBounds(273, 396, 60, 32);
		panel.add(button_100);
		
		JButton button_500 = new JButton("500");
		button_500.setBounds(345, 396, 60, 32);
		panel.add(button_500);
		
		JButton button_1000 = new JButton("1000");
		button_1000.setBounds(273, 437, 60, 32);
		panel.add(button_1000);
		

		
		// 자판기에 초기 아이템 등록
		myMachine.newItem(0, text_1, button_1);
		myMachine.newItem(1, text_2, button_2);
		myMachine.newItem(2, text_3, button_3);
		myMachine.newItem(3, text_4, button_4);
		myMachine.newItem(4, text_5, button_5);
		myMachine.newItem(5, text_6, button_6);

		JButton button_change = new JButton("\uC794\uB3C8");
		button_change.setBounds(305, 223, 76, 23);
		panel.add(button_change);
		
		myMachine.changeColor(0);
		button_10.addActionListener(new ActionListener()  // 10원 버튼 클릭
		{
			public void actionPerformed(ActionEvent e) {
				// myMachine.pressMoney(0); // 10원 버튼의 인덱스 인수로 전달
				dataSend(0 , "input");
				myMachine.changeState[0] = false;
				myMachine.screenChange();
				myMachine.pressMoney(0);
			}
		});
		button_50.addActionListener(new ActionListener() // 50원 버튼 클릭
		{
			public void actionPerformed(ActionEvent e) {
				// 50원 버튼의 인덱스 인수로 전달
				dataSend(1 , "input");
				myMachine.changeState[1] = false;
				myMachine.screenChange();
				myMachine.pressMoney(1);
			}
		});
		button_100.addActionListener(new ActionListener() // 100원 버튼 클릭
		{
			public void actionPerformed(ActionEvent e) {
				//myMachine.pressMoney(2); // 100원 버튼의 인덱스 인수로 전달
				dataSend(2 , "input");
				myMachine.changeState[2] = false;
				myMachine.screenChange();
				myMachine.pressMoney(2);
			}
		});
		button_500.addActionListener(new ActionListener() // 500원 버튼 클릭
		{
			public void actionPerformed(ActionEvent e) {
				//myMachine.pressMoney(3); // 500원 버튼의 인덱스 인수로 전달
				dataSend(3 , "input");
				myMachine.changeState[3] = false;
				myMachine.screenChange();
				myMachine.pressMoney(3);
			}
		});
		button_1000.addActionListener(new ActionListener() // 1000원 버튼 클릭
		{
			public void actionPerformed(ActionEvent e) {
				//myMachine.pressMoney(4); // 1000원 버튼의 인덱스 인수로 전달
				dataSend(4 , "input");
				myMachine.changeState[4] = false;
				myMachine.screenChange();
				myMachine.pressMoney(4);
			}
		});
		button_change.addActionListener(new ActionListener() // 잔돈 반환 버튼 클릭
		{
			public void actionPerformed(ActionEvent e) {
				dataSend(myMachine.tempMoney , "change");
				myMachine.pressChange();
			}
		});
		button_1.addActionListener(new ActionListener() // 1번 음료수 버튼 클릭
		{
			public void actionPerformed(ActionEvent e) {
				// myMachine.pressDrink(0);
				if(myMachine.pressDrink(0) == 1) {
				 dataSend(0 , "drink"); // 서버에 인덱스를 보낸다.
					
				 // 타이머를 이용하여 배출구에 음료표시
				 Timer timer = new Timer(50, new ActionListener() {
				      @Override
				        public void actionPerformed(ActionEvent e) {
							outlet.setIcon(new ImageIcon("C:\\\\Users\\\\xxeun\\\\Documents\\\\vendingMachine\\\\MyVendingMachine\\\\src\\\\Image\\\\water.png"));
				        }
				      });
				    timer.setRepeats(false);
				     timer.start();
					 Timer timer2 = new Timer(1250, new ActionListener() {
					      @Override
					        public void actionPerformed(ActionEvent e) {
								outlet.setIcon(null);
					        }
					      });
					    timer2.setRepeats(false);
					     timer2.start();								    
					     }
			}
		});
		button_2.addActionListener(new ActionListener() // 2번 음료수 버튼 클릭
		{
			public void actionPerformed(ActionEvent e) {
				if(myMachine.pressDrink(1) == 1)
				 dataSend(1 , "drink"); // 서버에 인덱스를 보낸다.
				outlet.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\soda.png"));
				 Timer timer = new Timer(1250, new ActionListener() {
				      @Override
				        public void actionPerformed(ActionEvent e) {
							outlet.setIcon(null);
				        }
				      });
				    timer.setRepeats(false);
				     timer.start();

			}
		});
		button_3.addActionListener(new ActionListener() // 3번 음료수 버튼 클릭
		{
			public void actionPerformed(ActionEvent e) {
				if(myMachine.pressDrink(2) == 1)
				 dataSend(2 , "drink"); // 서버에 인덱스를 보낸다.
				outlet.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\energy.png"));
				 Timer timer = new Timer(1250, new ActionListener() {
				      @Override
				        public void actionPerformed(ActionEvent e) {
							outlet.setIcon(null);
				        }
				      });
				    timer.setRepeats(false);
				     timer.start();
			}
		});
		button_4.addActionListener(new ActionListener() // 4번 음료수 버튼 클릭
		{
			public void actionPerformed(ActionEvent e) {
				if(myMachine.pressDrink(3) == 1)
				 dataSend(3 , "drink"); // 서버에 인덱스를 보낸다.
				outlet.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\coffee.png"));
				 Timer timer = new Timer(1250, new ActionListener() {
				      @Override
				        public void actionPerformed(ActionEvent e) {
							outlet.setIcon(null);
				        }
				      });
				    timer.setRepeats(false);
				     timer.start();
			}
		});
		button_5.addActionListener(new ActionListener() // 5번 음료수 버튼 클릭
		{
			public void actionPerformed(ActionEvent e) {
				if(myMachine.pressDrink(4) == 1) // 매출
				 dataSend(4 , "drink"); // 서버에 인덱스를 보낸다.
				outlet.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\starbucks.png"));
				 Timer timer = new Timer(1250, new ActionListener() {
				      @Override
				        public void actionPerformed(ActionEvent e) {
							outlet.setIcon(null);
				        }
				      });
				    timer.setRepeats(false);
				     timer.start();
			}
		});
		button_6.addActionListener(new ActionListener() // 6번 음료수 버튼 클릭
		{
			public void actionPerformed(ActionEvent e) {
				if(myMachine.pressDrink(5) == 1)
				 dataSend(5 , "drink"); // 서버에 인덱스를 보낸다.
				else if(myMachine.pressDrink(5) == -1)
				{
					login Loginframe = new login();
					
					Loginframe.lgnBtn = new JButton("Login"); // 로그인 버튼 등록
					Loginframe.lgnBtn.setBackground(Color.CYAN);
					Loginframe.lgnBtn.setBounds(22, 378, 112, 23);
					Loginframe.add(Loginframe.lgnBtn);
					Loginframe.setVisible(true);
					Loginframe.lgnBtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							
							String id = Loginframe.field.getID();
							String pw = Loginframe.field.getPW();
							if(Loginframe.db.login(id, pw))
							{
								userID = id;
								String nick = Loginframe.db.getNick(userID);
								Admin admin = new Admin();
								list = admin.list;
								admin.greeting.setText(nick + "님 환영합니다.");
								admin.greeting.setHorizontalAlignment(SwingConstants.CENTER); //텍스트 가운데 정렬
								admin.setVisible(true);
								adminButtonFunction(admin);
								Loginframe.dispose(); // 해당 로그인 창을 닫는다.
									
							}
							
						}
					});
					
					
					Loginframe.regBtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if(Loginframe.field.pressRegister(Loginframe.db) == true) {
								userID = Loginframe.field.getID();
								String nick = Loginframe.db.getNick(userID);
								Admin admin = new Admin();
								admin.greeting.setText(nick + "님 환영합니다.");
								admin.greeting.setHorizontalAlignment(SwingConstants.CENTER); //텍스트 가운데 정렬

								list = admin.list;
								admin.setVisible(true);
								adminButtonFunction(admin);
								Loginframe.dispose(); // 해당 로그인 창을 닫는다.
							}
						}
					});
				}
			}
		});
	}
	
	private void adminButtonFunction(Admin admin) // 관리자 페이지 버튼 기능 등록
	{
		admin.changeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 dataSend(1 , "change");
			}
		});
		admin.fillBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String temp = JOptionPane.showInputDialog("잔돈을 몇개로 충전 하시겠습니까?");
				dataSend(Integer.parseInt(temp) , "fill");
				list.add("잔돈을 "+ temp+"개로 충전하였습니다.");
			}
		});
		admin.incomeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 dataSend(1 , "income");
			}
		});
		admin.stockBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 dataSend(1 , "stock");
			}
		});
		admin.autoBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 dataSend(1 , "mail");
			}
		});

		admin.orderBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// orderDrink 메세지를 띄워서 사용자가 추가할 음료의 정보를 받아온다.
				OrderDrinkMessage message = new OrderDrinkMessage(); 
				// 콤보박스에 현재음료이름 채워넣기 사용자가 음료위치를 선택한다.
				for(int i = 0 ; i < 5 ; i++)
				{
					message.comboBox.addItem(myMachine.beverage[i].nameText.getText());
				}
				message.setVisible(true);
				message.submit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						 orderIndex = message.comboBox.getSelectedIndex(); // 주문할 위치
						 orderNum = message.comboBox_1.getSelectedIndex() + 1; // 주문할 수량
						 
						 orderName = message.nameField.getText();
						 orderPrice = message.priceField.getText();
						// 메시지 박스의 정보가 문제없으면 큐에 노드를 삽입한다.
						 try {
							 if(orderName.length() == 0) throw new MyException("이름을 입력해주세요");
							 for(int i = 0 ; i < orderPrice.length() ; i++)
							 {
								 char tmp = orderPrice.charAt(i);
								 if(Character.isDigit(tmp) == false)
									 throw new MyException("금액을 입력해주세요");
							 }
								try {
									dataOutputStream.writeUTF("order");
									dataOutputStream.writeInt(orderIndex);
									dataOutputStream.writeInt(orderNum * 10);
									dataOutputStream.writeUTF(orderName);
									dataOutputStream.writeInt(Integer.parseInt(orderPrice));

									}catch(Exception err)
									{
										System.out.println("데이터를 보내는 중 문제 발견");
										System.out.println(err.getMessage());
									}
								list.add(orderName + " 음료 " + orderNum * 10 +" 개 발주하셨습니다.");
							 message.dispose();
					
						 }catch(MyException er)
						 {
							 JOptionPane.showMessageDialog(null,er.getMessage());
						 }
						 
					}
				});


			}
		});
		// 수집버튼을 누를때에는 최소로 남겨놓을 잔돈을 사용자에게 입력받아 서버로 전달한다. 사용자는 남겨놓은 화폐를 제외한 금액을 서버로부터 확인 할 수 있다.
		admin.collectBtn.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				String temp = JOptionPane.showInputDialog("잔돈을 몇개 남겨 놓으시겠습니까?");
				dataSend(Integer.parseInt(temp) , "collect");
			}
		});
		
		// 비밀번호 변경 구현
		admin.changeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				login Loginframe = new login();
				Loginframe.setVisible(true);

				// 비밀번호 변경 버튼 생성
				Loginframe.regBtn.setText("변경");
				Loginframe.regBtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// 입력한 아이디와 사용자 아이디가 같다면
						if(userID.equals(Loginframe.field.getID())) 
						{
							// db에서 비밀번호 변경
							Loginframe.db.changePw(userID, Loginframe.field.getPW());
							JOptionPane.showMessageDialog(null,"변경 완료!");
							Loginframe.dispose(); // 해당 로그인 창을 닫는다.
						}
						else
							JOptionPane.showMessageDialog(null,"입력한 아이디가 사용자아이디와 다릅니다.");
					}
				});
			}
		});
	}

}