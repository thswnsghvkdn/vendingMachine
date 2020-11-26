import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import lib.Queue;

import javax.swing.JTextField;
import java.awt.List;

class ManageItem // �� ������ Ŭ����
{
	int income; // �����ۺ� ����
	String name; // ���� �̸�
	int price; // ����
	ManageItem(String name, int price) // �μ��� ���� ���ݰ� �̸��� �ʱ�ȭ
	{
		this.name = name;
		this.price = price;
	}
	// Ŭ������ ���Ϳ� ���ͱ���
	void setName(String name) { this.name = name;}
	void setPrice(int price) {this.price = price;}
	String getName() {return name;}
	int getPrice() {return price;}
}

class ManageBeverage // ����� Ŭ����
{
	
	Queue queue; // �� �������� ť�� �����Ѵ�.
	String nameText; // ������� �ؽ�Ʈ
	ManageBeverage(String name, int price) // �ʱ⿡ ���� ������� �߰��ϴ� ������
	{
		queue = new Queue();
		for(int i = 0 ; i < 3; i++)
			insertNode(name, price);
	}
	public void insertNode(String name, int price) // ����� �߰� �޼ҵ� �Ŀ� ���̺귯���� ������ �� ť
	{
		queue.enqueue(name, price);
	}
	public void deleteNode() // ����� ��� ���� �޼ҵ� �Ŀ� ���̺귯���� ����
	{
		queue.deque();
	}
	
}


class ManageMachine
{
	int income; // �� ����
	int[] won = {1000, 500, 100, 50 ,10}; // �� ����
	int[] change = new int[5]; // �Ž����� ȭ������� 5���� �ִ�.
	
	ManageBeverage[] beverage = new ManageBeverage[6];// ����� 6��
	ManageMachine() // ������ 
	{		
		for(int i = 0 ; i < 5 ; i++)
		{
			change[i] = 5; // �Ž������� �� 5���� �ʱ�ȭ
		}
	}
	void newItem(int index, String name, int price) // ���ο� ���� ���� �޼ҵ�
	{
		beverage[index] = new ManageBeverage(name, price);
	}
	void inputMoney(int index) // ����ڰ� ȭ�並 �����Ѱ��
	{
		change[index]++;
	}
	int retDrink(int index) // ����ڰ� �Է��� ���� ��� ���ҽ�Ų��.
	{
		int price = beverage[index].queue.getPrice();
		income += price;
		beverage[index].deleteNode();
		return price; // ť�� ��� ���� ù��° ������ ������ ����
	}
	void pressChange(int temp) // �ܵ� ��ȯ��ư  
	{
		int index = 0; // �ܵ� �迭�� �ε���
		int tempChange; // �� �������� �Ž����� ����
		
		while(temp > 0)
		{
			tempChange = temp / won[index]; // ȭ�󰳼�
			if(change[index] > tempChange) // �ܵ��� ��ȯ�� �� �ִ� ���
			{
				change[index] -= tempChange; // �ܵ����� ����
				temp %= won[index]; // �ش� ȭ�󰳼���ŭ �ܵ�����
				 // �ִϸ��̼����� �ش� �ܵ���ȯ
			}
			else // �ܵ��� ������ ��� 
			{
				temp = temp - (change[index] * won[index]); // ������ �Ž������� ����
				change[index] = 0;
			}
			index++; // ���� ȭ��
		}
	}
	
}


public class Manager extends JFrame {

	private JPanel contentPane;

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private DataInputStream dataInputStream; // �Է� ��Ʈ��
	private DataOutputStream dataOutputStream; // ��� ��Ʈ��
	
	
	
	public void serverSetting(List list) {
		try {
			serverSocket = new ServerSocket(10002); // ���ε�
			list.add("����");
			System.out.println("��������");
			clientSocket = serverSocket.accept();
			dataInputStream = new DataInputStream(clientSocket.getInputStream());
			dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
		} catch (Exception e)
		{
			
		}
	}
	
	public void closeAll()
	{
		try {
		serverSocket.close();
		clientSocket.close();
		dataInputStream.close();
		dataOutputStream.close();
		} catch (Exception e)
		{
			
		}
	}
	
	public void inputDrink(ManageMachine m) {
		try {
			int driknIndex =  dataInputStream.readInt();
			
		}
		catch(Exception e)
		{
			//
		}
	}
	public void inputMoney(ManageMachine m) {
		try {
			int moneyIndex =  dataInputStream.readInt();
			m.inputMoney(moneyIndex);
		}
		catch(Exception e)
		{
			//
		}
	}
	public void dataSend(int numData,String stringData)
	{	
		try {
			dataOutputStream.writeInt(numData);
			dataOutputStream.writeUTF(stringData);
		}
		catch(Exception e)
		{
			
		}
	}
	
	public void dataRecv(ManageMachine m, List list) {
		new Thread (new Runnable() { // �׻� ������� �Է��� ��ٸ����� while���� ������� ���۽�Ų��.
			public void run() {
				int numData;
				String strData;
				try {
				while(true) 
					{
						numData = dataInputStream.readInt();
						strData =  dataInputStream.readUTF(); 
						if(strData.equals("input")) // ����ڰ� ȭ�� �����Ѱ��
						{
							m.inputMoney(numData);
							list.add("����� ��������");
						}
						else if(strData.equals("drink")) // ����ڰ� �����ư�� Ŭ���Ѱ��
						{  
							int price = m.retDrink(numData);
							m.income += price; // ��������
							String text = m.beverage[numData].queue.getName();
							String message;
							message = "����ڰ� " + text + "�� �簬���ϴ�." ;
							list.add(message);
						}
					}
				}
				catch (Exception e) 
				{}
			}
		}).start();

					
	}

	

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Manager frame = new Manager();
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
	public Manager() {

		ManageMachine manageMachine = new ManageMachine();
		manageMachine.newItem(0, "��", 450);
		manageMachine.newItem(1, "�ݶ�", 750);
		manageMachine.newItem(2, "�̿�����", 550);
		manageMachine.newItem(3, "Ŀ��", 500);
		manageMachine.newItem(4, "��Ÿ����", 750);
		manageMachine.newItem(5, "����", 700);
		
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 534, 491);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		List list = new List();
		list.setBounds(32, 41, 288, 327);
		contentPane.add(list);
		serverSetting(list);
		dataRecv(manageMachine , list);
	}
}
