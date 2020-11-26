import java.awt.Color; // Ư������ �˻�� ���̺귯��
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;


class Beverage // ����� Ŭ����
{
	JLabel nameText; // ������� �ؽ�Ʈ
	JButton drinkButton; // ������� ��ư
	String color;
	Beverage( JLabel label, JButton button) // �ʱ⿡ ���� ������� �߰��ϴ� ������
	{
		nameText = label; // ������ �ؽ�Ʈ�� ����
		drinkButton = button; // ���� ��ư ��ü�� ����
	}
}


class Machine
{
	JLabel inputScreen;
	int tempMoney; // ����ڰ� ������ ��

	Beverage[] beverage = new Beverage[6];// ����� 6��
	Machine(JLabel screen) // ������ 
	{		
		inputScreen = screen;
		tempMoney = 0; /// ���� ���Ե� ���� 0������ �ʱ�ȭ

	}
	void newItem(int index, JLabel label, JButton button) // ���ο� ���� ���� �޼ҵ�
	{
		beverage[index] = new Beverage(label, button);
	}
	void changeColor(int input) // �����6���� ��ư ���� �˸°� �ٲ۴� . ������� ����
	{
		for(int i =0 ; i < 6 ; i++) {
			int t_price =  Integer.parseInt(beverage[i].drinkButton.getText());// ��ư�� �ؽ�Ʈ�� ������ �Ľ��Ѵ�.
			if(input >= t_price) // �ش� ������ �ݾװ� �� 
				beverage[i].drinkButton.setBackground(Color.pink);
			else 
				beverage[i].drinkButton.setBackground(Color.gray);
		}
	}
	
	void pressMoney(int index) // ����ڰ� ȭ���ư�� �������
	{
		// ����ڰ� �ִ�ݾ� �̻� �Է����� �ʵ��� try catch�� ó��
		switch(index)
		{
		case 0 : // 10��
			tempMoney += 10;
			break;
		case 1 : // 50��
			tempMoney += 50;
			break;
		case 2 : // 100��
			tempMoney += 100;
			break;
		case 3 : // 500��
			tempMoney += 500;
			break;
		case 4 : // 1000��
			tempMoney += 1000;
			break;
		}
		inputScreen.setText(Integer.toString(tempMoney)); // ���� ��ũ�� �ݾ� ����
		changeColor(tempMoney); // ��ư ���� ����
	}
	void pressChange() // �ܵ� ��ȯ��ư  
	{
		tempMoney = 0;
		inputScreen.setText(Integer.toString(tempMoney)); // ���� ��ũ�� �ݾ� ����
		changeColor(tempMoney); // ��ư ���� ����		
	}
	boolean pressDrink(int index) // �����ư
	{
		if(beverage[index].drinkButton.getBackground().equals(Color.pink))
		{
			int price = Integer.parseInt(beverage[index].drinkButton.getText());
			// ��ü �����Լ�
			tempMoney -= price;
			inputScreen.setText(Integer.toString(tempMoney)); // ���� ��ũ�� �ݾ� ����
			changeColor(tempMoney); // ��ư ���� ����
			return true;
		}
		
		if(index == 5 && tempMoney == 50 && beverage[index].drinkButton.getBackground().equals(Color.gray))
			// ������ �޴��� �ҷ��� �̽��Ϳ��� 50���� ������ ���·� ������ư ������
		{
			return true;
		}
		return false;
	}
	
	int getTempmoney() {return tempMoney;}
	
}



public class vendingMachine extends JFrame {

	private JPanel contentPane;

	private Socket clientSocket;
	private DataInputStream dataInputStream; // �Է� ��Ʈ��
	private DataOutputStream dataOutputStream; // ��� ��Ʈ��
	
	
	public void connect()
	{
		try {
		clientSocket = new Socket("220.69.208.204", 10002);
		System.out.println("����");
		} catch(Exception e) {
			
		}
	}
	public void streamSetting()
	{
		try {
		dataInputStream = new DataInputStream(clientSocket.getInputStream());
		dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
		} catch(Exception e) {
			
		}
	}
	public void dataSend(int numData,String stringData)
	{	
			try {
				dataOutputStream.writeInt(numData);
				dataOutputStream.writeUTF(stringData);
				}catch(Exception e)
				{
					
				}
		
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
		vendingMachine self = this;


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
		
		Machine myMachine = new Machine(insertScreen); // Machine ��ü ����
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		panel_1.setBounds(12, 23, 269, 306);
		panel.add(panel_1);
		panel_1.setLayout(null);
		
		JLabel Image1 = new JLabel("");
		Image1.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\water.png"));
		Image1.setBounds(22, 20, 57, 58);
		panel_1.add(Image1);
		
		JLabel Image2 = new JLabel("");
		Image2.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\soda.png"));
		Image2.setBounds(105, 20, 57, 58);
		panel_1.add(Image2);
		
		JLabel Image3 = new JLabel("");
		Image3.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\energy.png"));
		Image3.setBounds(191, 20, 57, 58);
		panel_1.add(Image3);
		
		JLabel Image4 = new JLabel("");
		Image4.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\coffee.png"));
		Image4.setBounds(22, 171, 57, 58);
		panel_1.add(Image4);
		
		JLabel Image5 = new JLabel("");
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
		panel_2.setBounds(69, 376, 145, 93);
		panel.add(panel_2);
		panel_2.setLayout(null);
		
		JLabel outlet = new JLabel("");
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
		
		JLabel outScreen = new JLabel("    \uC794\uB3C8 \uBC18\uD658");
		outScreen.setBounds(310, 185, 84, 29);
		panel.add(outScreen);
		
		
		// ���Ǳ⿡ �ʱ� ������ ���
		myMachine.newItem(0, text_1, button_1);
		myMachine.newItem(1, text_2, button_2);
		myMachine.newItem(2, text_3, button_3);
		myMachine.newItem(3, text_4, button_4);
		myMachine.newItem(4, text_5, button_5);
		myMachine.newItem(5, text_6, button_6);
		
		JButton button_change = new JButton("\uC794\uB3C8");
		button_change.setBounds(305, 223, 97, 23);
		panel.add(button_change);
		
		myMachine.changeColor(0);
		button_10.addActionListener(new ActionListener()  // 10�� ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				// myMachine.pressMoney(0); // 10�� ��ư�� �ε��� �μ��� ����
				dataSend(0 , "input");
				myMachine.pressMoney(0);
			}
		});
		button_50.addActionListener(new ActionListener() // 50�� ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				// 50�� ��ư�� �ε��� �μ��� ����
				dataSend(1 , "input");
				myMachine.pressMoney(1);
			}
		});
		button_100.addActionListener(new ActionListener() // 100�� ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				//myMachine.pressMoney(2); // 100�� ��ư�� �ε��� �μ��� ����
				dataSend(2 , "input");
				myMachine.pressMoney(2);
			}
		});
		button_500.addActionListener(new ActionListener() // 500�� ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				//myMachine.pressMoney(3); // 500�� ��ư�� �ε��� �μ��� ����
				dataSend(3 , "input");
				myMachine.pressMoney(3);
			}
		});
		button_1000.addActionListener(new ActionListener() // 1000�� ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				//myMachine.pressMoney(4); // 1000�� ��ư�� �ε��� �μ��� ����
				dataSend(4 , "input");
				myMachine.pressMoney(4);
			}
		});
		button_change.addActionListener(new ActionListener() // �ܵ� ��ȯ ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				myMachine.pressChange();
			}
		});
		button_1.addActionListener(new ActionListener() // 1�� ����� ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				// myMachine.pressDrink(0);
				if(myMachine.pressDrink(0)) {
				 dataSend(0 , "drink"); // ������ �ε����� ������.
					
				 Timer timer = new Timer(50, new ActionListener() {
				      @Override
				        public void actionPerformed(ActionEvent e) {
							outlet.setIcon(new ImageIcon("C:\\Users\\xxeun\\Documents\\vendingMachine\\MyVendingMachine\\src\\Image\\10\uC6D0.PNG"));
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
					     outlet.revalidate();
			    }
			}
		});
		button_2.addActionListener(new ActionListener() // 2�� ����� ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				if(myMachine.pressDrink(1))
				 dataSend(1 , "drink"); // ������ �ε����� ������.
				outlet.setIcon(null);

			}
		});
		button_3.addActionListener(new ActionListener() // 3�� ����� ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				if(myMachine.pressDrink(2))
				 dataSend(2 , "drink"); // ������ �ε����� ������.
			}
		});
		button_4.addActionListener(new ActionListener() // 4�� ����� ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				if(myMachine.pressDrink(3))
				 dataSend(3 , "drink"); // ������ �ε����� ������.
			}
		});
		button_5.addActionListener(new ActionListener() // 5�� ����� ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				if(myMachine.pressDrink(4))
				 dataSend(4 , "drink"); // ������ �ε����� ������.
			}
		});
		button_6.addActionListener(new ActionListener() // 6�� ����� ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				if(myMachine.pressDrink(5))
				 dataSend(5 , "drink"); // ������ �ε����� ������.
			}
		});
	}

}