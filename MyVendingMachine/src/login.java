import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import lib.Stack;

class Keyboard // 키보드 객체
{
	boolean shift;
	JButton button;
	char ch; // 타자 문자
	
	public Keyboard(JButton btn ,char ch)
	{
		shift = false; 
		button = btn;
		this.ch = ch;
	}
	
	public void pressShift() // shift 버튼을 눌렀을때 대문자와 특수문자로 교환
	{
		if(shift) // shift가 눌린 상태라면 숫자는 특수문자로 문자는 대문자로 변환한다.
		{
			if(Character.isAlphabetic(ch))
			{
				ch -= 32;
				button.setText(Character.toString(ch));
			}
			else 
			{
				ch -= 15;
				button.setText(Character.toString(ch));
			}
		}
		else
		{
			if(Character.isAlphabetic(ch))
			{
				ch += 32;
				button.setText(Character.toString(ch));
			}
			else 
			{
				ch += 15;
				button.setText(Character.toString(ch));
			}
		}
	}
	public void setShift(boolean flag)
	{
		shift = flag;
	}
	public String getCh()
	{
		return Character.toString(ch);
	}
	
	
}

class loginField {
	Stack idStack, pwStack; // 알파벳을 저장하는 스택
	JLabel id, pw; // 아이디 . 비밀번호 칸
	String inputId , inputPw; // 아이디, 비밀번호 문자열
	boolean Enter, Shift; // 아이디와 비밀번호를 바꿔가며 입력받을 수 있도록
	Keyboard[] button = new Keyboard[15];
	
	loginField(JLabel id, JLabel pw) {
		idStack = new Stack();
		pwStack = new Stack();
		this.id = id;
		this.pw = pw;
		Enter = true;
		Shift = false;
	}
	
	public void registBtn(JButton btn, int index, char ch)
	{
		button[index] = new Keyboard(btn,ch);
	}
	
	public void registEvent(int index)
	{
		button[index].button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pressAlpha(button[index].getCh()); // 스택에 해당 캐릭터 삽입
				readAlpha(); // 스택을 역순으로 읽어온 스트링을 라벨에 쓰기
			}
		});
	}
	
	public void pressAlpha(String str) // 알파벳 버튼을 클릭할경우 해당 인덱스로 들어온 데이터를 스택에 저장
	{
		if(Enter) 
			idStack.push(str);
		else
			pwStack.push(str);
	}
	public void readAlpha() // 스택에 문자열을 읽어들여 텍스트 필드에 표시
	{
		String temp = "";
		if(Enter)
		{
			temp = idStack.retString();
			id.setText(temp);
		}
		else 
		{
			int size = pwStack.retString().length();
			for(int i = 0 ; i < size ; i++)
				temp += '*';
			pw.setText(temp);
		}
	}
	public void pressBack()
	{
		if(Enter) 
			idStack.pop();
		else
			pwStack.pop();
		readAlpha();
	}
	public void pressEnter() // 엔터를 눌렀을때 아이디 혹은 비번 라벨을 활성화 시킨다.
	{
		Enter = !Enter;
		if(!Enter)
			pw.setText("입력하세요");
		else 
			id.setText("입력하세요");			
	}
	public void pressShift() // shift를 누를경우 문자변경
	{
		Shift = !Shift;
		for(int i = 0 ; i < 15; i++)
		{
			button[i].setShift(Shift);
			button[i].pressShift();
		}
	}
	public void pressRegister()
	{
		String temp = pwStack.retString();
		if(temp.matches(".*[0-9|!-%].*") && temp.length() > 5)
		{
			
		}
		else 
		{
			JOptionPane.showMessageDialog(null, "비밀번호는 5자리 이상 , 숫자 혹은 특수문자를 포함하여야 합니다.");
		}
	}
	
}


public class login extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					login frame = new login();
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
	public login() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 338, 469);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel ID = new JLabel("New label");
		ID.setBackground(Color.WHITE);
		ID.setBounds(77, 64, 219, 23);
		contentPane.add(ID);
		
		JLabel password = new JLabel("New label");
		password.setBackground(Color.WHITE);
		password.setBounds(77, 105, 219, 23);
		contentPane.add(password);
		
		loginField field = new loginField(ID , password); // loginField 클래스 등록

		
		
		JButton btn1 = new JButton("a");
		btn1.setBounds(26, 235, 42, 42);
		contentPane.add(btn1);
		field.registBtn(btn1, 0, 'a'); // 해당버튼을 field 클래스에 등록한다.

		
		JButton btn2 = new JButton("b");
		btn2.setBounds(77, 235, 42, 42);
		contentPane.add(btn2);
		field.registBtn(btn2, 1, 'b'); // 해당버튼을 field 클래스에 등록한다.


		JButton btn3 = new JButton("c");
		btn3.setBounds(133, 235, 42, 42);
		contentPane.add(btn3);
		field.registBtn(btn3, 2, 'c'); // 해당버튼을 field 클래스에 등록한다.

		
		JButton btn4 = new JButton("d");
		btn4.setBounds(190, 235, 44, 42);
		contentPane.add(btn4);
		field.registBtn(btn4, 3, 'd'); // 해당버튼을 field 클래스에 등록한다.

		JButton btn5 = new JButton("e");
		btn5.setBounds(244, 235, 44, 42);
		contentPane.add(btn5);
		field.registBtn(btn5, 4, 'e'); // 해당버튼을 field 클래스에 등록한다.

		JButton btn6 = new JButton("f");
		btn6.setBounds(26, 277, 42, 42);
		contentPane.add(btn6);
		field.registBtn(btn6, 5, 'f'); // 해당버튼을 field 클래스에 등록한다.

		JButton btn7 = new JButton("g");
		btn7.setBounds(77, 277, 42, 42);
		contentPane.add(btn7);
		field.registBtn(btn7, 6, 'g'); // 해당버튼을 field 클래스에 등록한다.

		JButton btn8 = new JButton("h");
		btn8.setBounds(133, 277, 42, 42);
		contentPane.add(btn8);
		field.registBtn(btn8, 7, 'h'); // 해당버튼을 field 클래스에 등록한다.

		JButton btn9 = new JButton("i");
		btn9.setBounds(190, 277, 44, 42);
		contentPane.add(btn9);
		field.registBtn(btn9, 8, 'i'); // 해당버튼을 field 클래스에 등록한다.

		JButton btn10 = new JButton("j");
		btn10.setBounds(244, 277, 44, 42);
		contentPane.add(btn10);
		field.registBtn(btn10, 9, 'j'); // 해당버튼을 field 클래스에 등록한다.

		JButton btn11 = new JButton("1");
		btn11.setBounds(26, 193, 42, 42);
		contentPane.add(btn11);
		field.registBtn(btn11, 10, '1'); // 해당버튼을 field 클래스에 등록한다.

		JButton btn12 = new JButton("2");
		btn12.setBounds(77, 193, 42, 42);
		contentPane.add(btn12);
		field.registBtn(btn12, 11, '2'); // 해당버튼을 field 클래스에 등록한다.

		JButton btn13 = new JButton("3");
		btn13.setBounds(133, 193, 42, 42);
		contentPane.add(btn13);
		field.registBtn(btn13, 12, '3'); // 해당버튼을 field 클래스에 등록한다.

		JButton btn14 = new JButton("4");
		btn14.setBounds(190, 193, 44, 42);
		contentPane.add(btn14);
		field.registBtn(btn14, 13, '4'); // 해당버튼을 field 클래스에 등록한다.

		JButton btn15 = new JButton("5");
		btn15.setBounds(244, 193, 44, 42);
		contentPane.add(btn15);
		field.registBtn(btn15, 14, '5'); // 해당버튼을 field 클래스에 등록한다.

		JButton Shift = new JButton("Shift");
		Shift.setBounds(26, 331, 97, 23);
		contentPane.add(Shift);
		
		JLabel lblNewLabel = new JLabel("ID");
		lblNewLabel.setBounds(26, 66, 30, 19);
		contentPane.add(lblNewLabel);
		
		JLabel lblPassword = new JLabel("PW");
		lblPassword.setBounds(26, 107, 39, 19);
		contentPane.add(lblPassword);
		
		JButton lgnBtn = new JButton("Login");
		lgnBtn.setBackground(Color.CYAN);
		lgnBtn.setBounds(22, 378, 112, 23);
		contentPane.add(lgnBtn);
		
		JButton regBtn = new JButton("Register");

		regBtn.setBackground(Color.CYAN);
		regBtn.setBounds(189, 378, 97, 23);
		contentPane.add(regBtn);
		
		JButton btnBackspace = new JButton("<- Backspace");
		btnBackspace.setBounds(166, 152, 120, 23);
		contentPane.add(btnBackspace);
		
		JButton btnEnter = new JButton("Enter");
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnEnter.setBounds(190, 331, 97, 23);
		contentPane.add(btnEnter);
		

		for(int i = 0 ; i < 15 ; i++)
		{
			field.registEvent(i);
		}
		Shift.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				field.pressShift();
			}
		});
		
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				field.pressEnter();
			}
		});
		btnBackspace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				field.pressBack();
			}
		});
		regBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				field.pressRegister();
			}
		});
	}
}
