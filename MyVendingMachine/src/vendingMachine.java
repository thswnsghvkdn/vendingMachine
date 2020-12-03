import java.awt.Color; // Ư������ �˻�� ���̺귯��
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

	private String id;
	JLabel inputScreen;
	int tempMoney; // ����ڰ� ������ ��
	Beverage[] beverage = new Beverage[6];// ����� 6��
	Machine(JLabel screen) // ������ 
	{		
		inputScreen = screen;
		tempMoney = 0; /// ���� ���Ե� ���� 0������ �ʱ�ȭ

	}
	
	void setId(String id)
	{
		this.id = id;
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
	int pressDrink(int index) // �����ư
	{
		if(beverage[index].drinkButton.getBackground().equals(Color.pink))
		{
			int price = Integer.parseInt(beverage[index].drinkButton.getText());
			// ��ü �����Լ�
			tempMoney -= price;
			inputScreen.setText(Integer.toString(tempMoney)); // ���� ��ũ�� �ݾ� ����
			changeColor(tempMoney); // ��ư ���� ����
			return 1;
		}
		
		if(index == 5 && tempMoney == 50 && beverage[index].drinkButton.getBackground().equals(Color.gray))
			// ������ �޴��� �ҷ��� �̽��Ϳ��� 50���� ������ ���·� ������ư ������
		{	
			return -1;
		}
		return 0;
	}
	
	int getTempmoney() {return tempMoney;}
	
}

class MyException extends Exception{
	public MyException(String s) {
		super(s);
	}
}
public class vendingMachine extends JFrame {

	private int orderIndex; // ����ڰ� ���� �ֹ��� ������ ��ġ
	private String orderName; // ����ڰ� ���� �ֹ��� �ֹ��� ������ �̸�
	private String orderPrice; // ����ڰ� ���� �ֹ��� �ֹ��� ������ �̸�
	private int orderNum; // ����ڰ� ���� �ֹ��� ������ ��ġ
	
	private Machine myMachine;
	private JPanel contentPane;
	List list;
	private Socket clientSocket;
	private DataInputStream dataInputStream; // �Է� ��Ʈ��
	private DataOutputStream dataOutputStream; // ��� ��Ʈ��
	
	//login loginMenu = new login();

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
		System.out.println("������ ��Ʈ�� ����");
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
				System.out.println("�����͸� ������ �� ���� �߰�");
				System.out.println(e.getMessage());
			}
	}		
	
	
	public void dataRecv() {
		new Thread() { // �׻� ������� �Է��� ��ٸ����� while���� ������� ���۽�Ų��.
			public void run() {
				int numData;
				String strData;
				String str = "";
				try {
				while(true) 
					{
						strData =  dataInputStream.readUTF();  // Ŭ���̾�Ʈ�� ��û �ľ�
						
						if(strData.equals("stock")) // ��� ��ư�� Ŭ���� ���
						{
							list.add("���� ��� ��Ȳ");
							for(int i = 0 ; i < 5 ; i++)
							{
								numData = dataInputStream.readInt();
								str = myMachine.beverage[i].nameText.getText() + " " + numData + "�� ����";
								list.add(str);
							}

							
							list.add("ȭ�� ��� ��Ȳ");
							String won[] = {"10", "50", "100" , "500" ,"1000"};
							for(int i = 0 ; i < 5 ; i++)
							{
								numData = dataInputStream.readInt();
								str = won[i] + "�� " + numData + "�� ����";
								list.add(str);
							}

							
						}
						else if(strData.equals("income")) 
						{
							int income = dataInputStream.readInt();
							list.add("�Ϸ� ���� ��Ȳ : " + income);
						}
						else if(strData.equals("collect")) 
						{
							
						}
						else if(strData.equals("order")) // �����ֹ� 
						{
							
						}
						else if(strData.equals("fill"))  // �ܵ� ���� 
						{
							
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
		
		myMachine = new Machine(insertScreen); // Machine ��ü ����
		
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
				if(myMachine.pressDrink(0) == 1) {
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
					     timer2.start();								    }
			}
		});
		button_2.addActionListener(new ActionListener() // 2�� ����� ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				if(myMachine.pressDrink(1) == 1)
				 dataSend(1 , "drink"); // ������ �ε����� ������.
				outlet.setIcon(null);

			}
		});
		button_3.addActionListener(new ActionListener() // 3�� ����� ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				if(myMachine.pressDrink(2) == 1)
				 dataSend(2 , "drink"); // ������ �ε����� ������.
			}
		});
		button_4.addActionListener(new ActionListener() // 4�� ����� ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				if(myMachine.pressDrink(3) == 1)
				 dataSend(3 , "drink"); // ������ �ε����� ������.
			}
		});
		button_5.addActionListener(new ActionListener() // 5�� ����� ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				if(myMachine.pressDrink(4) == 1) // ����
				 dataSend(4 , "drink"); // ������ �ε����� ������.
			}
		});
		button_6.addActionListener(new ActionListener() // 6�� ����� ��ư Ŭ��
		{
			public void actionPerformed(ActionEvent e) {
				if(myMachine.pressDrink(5) == 1)
				 dataSend(5 , "drink"); // ������ �ε����� ������.
				else if(myMachine.pressDrink(5) == -1)
				{
					login Loginframe = new login();
					Loginframe.setVisible(true);
					Loginframe.regBtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Loginframe.field.pressRegister(Loginframe.db);
							Admin admin = new Admin();
							list = admin.list;
							admin.setVisible(true);
							adminButtonFunction(admin);
							Loginframe.dispose(); // �ش� �α��� â�� �ݴ´�.
						}
					});
				}
			}
		});
	}
	
	private void adminButtonFunction(Admin admin) // ������ ������ ��ư ��� ���
	{
		admin.changeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 dataSend(1 , "change");
			}
		});
		admin.fillBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String temp = JOptionPane.showInputDialog("�ܵ��� ��� ���� �Ͻðڽ��ϱ�?");
				dataSend(Integer.parseInt(temp) , "fill");
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

		admin.orderBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// orderDrink �޼����� ����� ����ڰ� �߰��� ������ ������ �޾ƿ´�.
				OrderDrinkMessage message = new OrderDrinkMessage(); 
				// �޺��ڽ��� ���������̸� ä���ֱ� ����ڰ� ������ġ�� �����Ѵ�.
				for(int i = 0 ; i < 5 ; i++)
				{
					message.comboBox.addItem(myMachine.beverage[i].nameText.getText());
				}
				message.setVisible(true);
				message.submit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						 orderIndex = message.comboBox.getSelectedIndex(); // �ֹ��� ��ġ
						 orderNum = message.comboBox_1.getSelectedIndex() + 1; // �ֹ��� ����
						 
						 orderName = message.nameField.getText();
						 orderPrice = message.priceField.getText();
						// �޽��� �ڽ��� ������ ���������� ť�� ��带 �����Ѵ�.
						 try {
							 if(orderName.length() == 0) throw new MyException("�̸��� �Է����ּ���");
							 for(int i = 0 ; i < orderPrice.length() ; i++)
							 {
								 char tmp = orderPrice.charAt(i);
								 if(Character.isDigit(tmp) == false)
									 throw new MyException("�ݾ��� �Է����ּ���");
							 }
							 message.dispose();
					
						 }catch(MyException er)
						 {
							 JOptionPane.showMessageDialog(null,er.getMessage());
						 }
						 
					}
				});

				try {
					dataOutputStream.writeUTF("order");
					dataOutputStream.writeInt(orderIndex);
					dataOutputStream.writeInt(orderNum * 10);
					dataOutputStream.writeUTF(orderName);
					dataOutputStream.writeInt(Integer.parseInt(orderPrice));

					}catch(Exception err)
					{
						System.out.println("�����͸� ������ �� ���� �߰�");
						System.out.println(err.getMessage());
					}
			}
		});
		// ������ư�� ���������� �ּҷ� ���ܳ��� �ܵ��� ����ڿ��� �Է¹޾� ������ �����Ѵ�. ����ڴ� ���ܳ��� ȭ�� ������ �ݾ��� �����κ��� Ȯ�� �� �� �ִ�.
		admin.collectBtn.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				String temp = JOptionPane.showInputDialog("�ܵ��� � ���� �����ðڽ��ϱ�?");
				dataSend(Integer.parseInt(temp) , "collect");
			}
		});
	}

}