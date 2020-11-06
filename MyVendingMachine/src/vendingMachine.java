import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.util.regex.Pattern;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import java.awt.Color; // 특수문자 검사용 라이브러리

class Item // 각 음료의 클래스
{
	int income; // 아이템별 매출
	String name; // 음료 이름
	int price; // 가격
	Item(String name, int price) // 인수로 들어온 가격과 이름을 초기화
	{
		this.name = name;
		this.price = price;
	}
	// 클래스의 게터와 세터구현
	void setName(String name) { this.name = name;}
	void setPrice(int price) {this.price = price;}
	String getName() {return name;}
	int getPrice() {return price;}
}
class Beverage // 음료수 클래스
{
	
	int stock; // 음료수 재고
	Item item; // 각 음료수는 연결리스트로 관리된다.
	JLabel nameText; // 음료수의 텍스트
	JButton drinkButton; // 음료수의 버튼
	String color;
	Beverage(String name, int price, JLabel label, JButton button) // 초기에 세개 음료수를 추가하는 생성자
	{
		nameText = label; // 음료의 텍스트를 연결
		drinkButton = button; // 음료 버튼 객체를 연결
		stock = 3;
		for(int i = 0 ; i < 3; i++)
			insertNode(new Item(name, price));
	}
	public void insertNode(Item item) // 음료수 추가 메소드 후에 라이브러리로 구현할 것 스택 
	{}
	public void deleteNode(Item item) // 음료수 노드 제거 메소드 후에 라이브러리로 구현
	{}
	
}


class Machine
{
	JLabel inputScreen;
	int income; // 총 매출
	int tempMoney; // 사용자가 삽입한 돈
	int[] change = new int[5]; // 거스름돈 화폐단위는 5개가 있다.
	int[] input = new int[5]; // 사용자가 입력한 화폐
	Beverage[] beverage = new Beverage[6];// 음료수 6개
	Machine(JLabel screen) // 생성자 
	{
		inputScreen = screen;
		tempMoney = 0; /// 현재 삽입된 돈을 0원으로 초기화
		for(int i = 0 ; i < 5 ; i++)
		{
			change[i] = 5; // 거스름돈은 각 5개씩 초기화
			input[i] = 0; // 사용자가 입력한 화폐 개수 처음에는 0개로 초기화
		}
	}
	void newItem(int index, String name, int price, JLabel label, JButton button) // 새로운 음료 생성 메소드
	{
		beverage[index] = new Beverage(name, price, label, button);
	}
	void changeColor(int input) // 음료수6개의 버튼 색을 알맞게 바꾼다 . 쓰레드로 구현
	{
		for(int i =0 ; i < 6 ; i++) {
			int t_price =  Integer.parseInt(beverage[i].drinkButton.getText());// 버튼의 텍스트를 정수로 파싱한다.
			if(input >= t_price) // 해당 음료의 금액과 비교 
				beverage[i].drinkButton.setBackground(Color.pink);
			else 
				beverage[i].drinkButton.setBackground(Color.gray);
		}
	}
	void pressMoney(int index) // 사용자가 음료버튼을 누를경우
	{
		input[index]++;
		change[index]++;
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
			break;
		}
		inputScreen.setText(Integer.toString(tempMoney)); // 투입 스크린 금액 갱신
		changeColor(tempMoney); // 버튼 색상 갱신
	}
	int getTempmoney() {return tempMoney;}
	
}

class Manager // 관리자 메뉴 클래스
{
	
	String id, password;
	void resist()
	{
		
	}
	boolean isPossible(String str)
	{
		if(str.length() < 8) return false; // 길이 재기
		if(str.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*")) return false; // 특수문자 확인
		else // 특수문자를 포함한다면
		{
			if(str.matches(".*[0-9].*")) return true; // 숫자를 포함하면 true
			else return false;
		}
	}
	void showIncome() 
	{
		
	}
	void pressChange() // 잔돈버튼
	{
		
	}
	void searchIncome(String name)
	{
		
	}
	void changeInformation(String Item, int name)
	{
		
	}
}

public class vendingMachine extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					vendingMachine frame = new vendingMachine();
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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 499, 561);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(27, 26, 425, 486);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel insertScreen = new JLabel("    \uD22C\uC785\uAE08\uC561");
		insertScreen.setForeground(Color.BLACK);
		insertScreen.setBackground(Color.WHITE);
		insertScreen.setBounds(310, 84, 84, 32);
		panel.add(insertScreen);
		
		Machine myMachine = new Machine(insertScreen); // Machine 객체 생성
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		panel_1.setBounds(12, 23, 269, 306);
		panel.add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\water.png"));
		lblNewLabel.setBounds(22, 20, 57, 58);
		panel_1.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\soda.png"));
		lblNewLabel_1.setBounds(105, 20, 57, 58);
		panel_1.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\energy.png"));
		lblNewLabel_2.setBounds(191, 20, 57, 58);
		panel_1.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("");
		lblNewLabel_3.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\coffee.png"));
		lblNewLabel_3.setBounds(22, 171, 57, 58);
		panel_1.add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("");
		lblNewLabel_4.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\starbucks.png"));
		lblNewLabel_4.setBounds(108, 171, 57, 58);
		panel_1.add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("");
		lblNewLabel_5.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\question.png"));
		lblNewLabel_5.setBounds(193, 171, 57, 58);
		panel_1.add(lblNewLabel_5);

		
		JButton button_1 = new JButton("450");
		button_1.setBackground(Color.GRAY);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
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
		
		JLabel text_1 = new JLabel("     \uBB3C");
		text_1.setBounds(22, 99, 57, 15);
		panel_1.add(text_1);
		
		JLabel text_2 = new JLabel("   \uCF5C\uB77C");
		text_2.setBounds(105, 99, 57, 15);
		panel_1.add(text_2);
		
		JLabel text_3 = new JLabel("   \uC774\uC628\uC74C\uB8CC");
		text_3.setBounds(183, 99, 74, 15);
		panel_1.add(text_3);
		
		JLabel text_4 = new JLabel("    \uCEE4\uD53C");
		text_4.setBounds(22, 239, 57, 15);
		panel_1.add(text_4);
		
		JLabel text_5 = new JLabel(" \uC2A4\uD0C0\uBC85\uC2A4");
		text_5.setBounds(105, 239, 57, 15);
		panel_1.add(text_5);
		
		JLabel text_6 = new JLabel("    \uB79C\uB364");
		text_6.setBounds(191, 239, 57, 15);
		panel_1.add(text_6);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.WHITE);
		panel_2.setBounds(69, 376, 137, 62);
		panel.add(panel_2);
		
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
		
		JLabel outScreen = new JLabel("   \uC794\uB3C8 \uBC18\uD658");
		outScreen.setBounds(310, 185, 84, 29);
		panel.add(outScreen);
		
		
		// 자판기에 초기 아이템 등록
		myMachine.newItem(0, "물", 450, text_1, button_1);
		myMachine.newItem(1, "콜라", 750, text_2, button_2);
		myMachine.newItem(2, "이온음료", 550, text_3, button_3);
		myMachine.newItem(3, "커피", 450, text_4, button_4);
		myMachine.newItem(4, "스타벅스", 750, text_5, button_5);
		myMachine.newItem(5, "랜덤", 7000, text_6, button_6);
		
		
		button_10.addActionListener(new ActionListener()  // 10원 버튼 클릭
		{
			public void actionPerformed(ActionEvent e) {
				myMachine.pressMoney(0);
			}
		});
	}

}