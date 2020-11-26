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

class Keyboard // Ű���� ��ü
{
	boolean shift;
	JButton button;
	char ch; // Ÿ�� ����
	
	public Keyboard(JButton btn ,char ch)
	{
		shift = false; 
		button = btn;
		this.ch = ch;
	}
	
	public void pressShift() // shift ��ư�� �������� �빮�ڿ� Ư�����ڷ� ��ȯ
	{
		if(shift) // shift�� ���� ���¶�� ���ڴ� Ư�����ڷ� ���ڴ� �빮�ڷ� ��ȯ�Ѵ�.
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
	Stack idStack, pwStack; // ���ĺ��� �����ϴ� ����
	JLabel id, pw; // ���̵� . ��й�ȣ ĭ
	String inputId , inputPw; // ���̵�, ��й�ȣ ���ڿ�
	boolean Enter, Shift; // ���̵�� ��й�ȣ�� �ٲ㰡�� �Է¹��� �� �ֵ���
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
				pressAlpha(button[index].getCh()); // ���ÿ� �ش� ĳ���� ����
				readAlpha(); // ������ �������� �о�� ��Ʈ���� �󺧿� ����
			}
		});
	}
	
	public void pressAlpha(String str) // ���ĺ� ��ư�� Ŭ���Ұ�� �ش� �ε����� ���� �����͸� ���ÿ� ����
	{
		if(Enter) 
			idStack.push(str);
		else
			pwStack.push(str);
	}
	public void readAlpha() // ���ÿ� ���ڿ��� �о�鿩 �ؽ�Ʈ �ʵ忡 ǥ��
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
	public void pressEnter() // ���͸� �������� ���̵� Ȥ�� ��� ���� Ȱ��ȭ ��Ų��.
	{
		Enter = !Enter;
		if(!Enter)
			pw.setText("�Է��ϼ���");
		else 
			id.setText("�Է��ϼ���");			
	}
	public void pressShift() // shift�� ������� ���ں���
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
			JOptionPane.showMessageDialog(null, "��й�ȣ�� 5�ڸ� �̻� , ���� Ȥ�� Ư�����ڸ� �����Ͽ��� �մϴ�.");
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
		
		loginField field = new loginField(ID , password); // loginField Ŭ���� ���

		
		
		JButton btn1 = new JButton("a");
		btn1.setBounds(26, 235, 42, 42);
		contentPane.add(btn1);
		field.registBtn(btn1, 0, 'a'); // �ش��ư�� field Ŭ������ ����Ѵ�.

		
		JButton btn2 = new JButton("b");
		btn2.setBounds(77, 235, 42, 42);
		contentPane.add(btn2);
		field.registBtn(btn2, 1, 'b'); // �ش��ư�� field Ŭ������ ����Ѵ�.


		JButton btn3 = new JButton("c");
		btn3.setBounds(133, 235, 42, 42);
		contentPane.add(btn3);
		field.registBtn(btn3, 2, 'c'); // �ش��ư�� field Ŭ������ ����Ѵ�.

		
		JButton btn4 = new JButton("d");
		btn4.setBounds(190, 235, 44, 42);
		contentPane.add(btn4);
		field.registBtn(btn4, 3, 'd'); // �ش��ư�� field Ŭ������ ����Ѵ�.

		JButton btn5 = new JButton("e");
		btn5.setBounds(244, 235, 44, 42);
		contentPane.add(btn5);
		field.registBtn(btn5, 4, 'e'); // �ش��ư�� field Ŭ������ ����Ѵ�.

		JButton btn6 = new JButton("f");
		btn6.setBounds(26, 277, 42, 42);
		contentPane.add(btn6);
		field.registBtn(btn6, 5, 'f'); // �ش��ư�� field Ŭ������ ����Ѵ�.

		JButton btn7 = new JButton("g");
		btn7.setBounds(77, 277, 42, 42);
		contentPane.add(btn7);
		field.registBtn(btn7, 6, 'g'); // �ش��ư�� field Ŭ������ ����Ѵ�.

		JButton btn8 = new JButton("h");
		btn8.setBounds(133, 277, 42, 42);
		contentPane.add(btn8);
		field.registBtn(btn8, 7, 'h'); // �ش��ư�� field Ŭ������ ����Ѵ�.

		JButton btn9 = new JButton("i");
		btn9.setBounds(190, 277, 44, 42);
		contentPane.add(btn9);
		field.registBtn(btn9, 8, 'i'); // �ش��ư�� field Ŭ������ ����Ѵ�.

		JButton btn10 = new JButton("j");
		btn10.setBounds(244, 277, 44, 42);
		contentPane.add(btn10);
		field.registBtn(btn10, 9, 'j'); // �ش��ư�� field Ŭ������ ����Ѵ�.

		JButton btn11 = new JButton("1");
		btn11.setBounds(26, 193, 42, 42);
		contentPane.add(btn11);
		field.registBtn(btn11, 10, '1'); // �ش��ư�� field Ŭ������ ����Ѵ�.

		JButton btn12 = new JButton("2");
		btn12.setBounds(77, 193, 42, 42);
		contentPane.add(btn12);
		field.registBtn(btn12, 11, '2'); // �ش��ư�� field Ŭ������ ����Ѵ�.

		JButton btn13 = new JButton("3");
		btn13.setBounds(133, 193, 42, 42);
		contentPane.add(btn13);
		field.registBtn(btn13, 12, '3'); // �ش��ư�� field Ŭ������ ����Ѵ�.

		JButton btn14 = new JButton("4");
		btn14.setBounds(190, 193, 44, 42);
		contentPane.add(btn14);
		field.registBtn(btn14, 13, '4'); // �ش��ư�� field Ŭ������ ����Ѵ�.

		JButton btn15 = new JButton("5");
		btn15.setBounds(244, 193, 44, 42);
		contentPane.add(btn15);
		field.registBtn(btn15, 14, '5'); // �ش��ư�� field Ŭ������ ����Ѵ�.

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
