import java.awt.EventQueue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.List;

import lib.Queue;



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
	public int[] change = new int[5]; // �Ž����� ȭ������� 5���� �ִ�.
	
	public ManageBeverage[] beverage = new ManageBeverage[6];// ����� 6��
	ManageMachine() // ������ 
	{		
		for(int i = 0 ; i < 5 ; i++)
		{
			change[i] = 5; // �Ž������� �� 5���� �ʱ�ȭ
		}
	}
	void orderDrink(int index, String name, int price, int num) // �ε�����ġ�� ���Ḧ num�� ����
	{
		for(int i = 0 ; i < num ; i++)
		beverage[index].insertNode(name, price);
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
	ManageMachine manageMachine = new ManageMachine();
	List list = new List();
	private JPanel contentPane;

	private ServerSocket serverSocket;
	private DataInputStream dataInputStream; // �Է� ��Ʈ��
	private DataOutputStream dataOutputStream; // ��� ��Ʈ��
	ExecutorService executorService; // ������ Ǯ
	Vector<Client> connections =new Vector<Client>(); // ����ȭ�� �����Ͽ� �ӵ��� �������� �� �� ������ ���͸� ���
	

	
	public void serverSetting() {
		// ������ Ǯ�� ����� cpu�� ������ �� �ִ� ���μ��� ���� �μ��� �ش�.
		executorService = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors()
		);
		
		try {
			serverSocket = new ServerSocket(10002); // ���ε�
			list.add("����");
			System.out.println("��������");
		} catch (Exception e)
		{
			if(!serverSocket.isClosed()) // ������ �����ϰ� �ݾ��ش�.
				stopServer();
			return;
		}
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true) // Ŭ���̾�Ʈ�� ���Ӵ�⸦ �ݺ�
				{
					Socket clientSocket;
					try {
						clientSocket = serverSocket.accept();
						list.add(clientSocket.getRemoteSocketAddress().toString()); // ���ӵ� Ŭ���̾�Ʈ �ּ�
						Client client = new Client(clientSocket);
						connections.add(client); // ���Ϳ� Ŭ���̾�Ʈ ������ �߰��Ѵ�.
						// connections.size() ���ᰳ��
					} catch (IOException e) {
						// TODO Auto-generated catch block
						if(!serverSocket.isClosed())
							stopServer();
						break; // Ŭ���̾�Ʈ�� �����û ������ ���ܰ� �߻��� ��� ������ �����ϰ� �ݺ����� �����Ѵ�.
					} 

				}
			}
		};
		
		executorService.submit(runnable); // �۾���ü�� ������Ǯ�� �μ��� �����Ѵ�.
	}
	public void stopServer()
	{
		try {
		Iterator<Client> iterator = connections.iterator();
		while(iterator.hasNext()) { // Ŭ���̾�Ʈ���� ����
			Client client = iterator.next();
			client.socket.close();
			iterator.remove();
		  }
		  if(serverSocket != null && !serverSocket.isClosed()) { // Ŭ���̾�Ʈ���� ��� �����Ų�� ������ ����
			  serverSocket.close();
		  }
		  if(executorService != null && !executorService.isShutdown()) { // ����Ʈ Ǯ ����
			  executorService.shutdown();
		  }
		} catch(Exception e) {}
	}
	class Client
	{
		Socket socket;
		DataOutputStream outputStream;
		DataInputStream inputStream;
		public Client(Socket socket_)
		{
			socket = socket_;
			try {
			outputStream = new DataOutputStream(socket.getOutputStream());
			inputStream = new DataInputStream(socket.getInputStream());
			} catch(Exception e) {}
			
			dataRecv();
		}
		
		public void dataRecv() {
			Runnable runnable = new Runnable() { // �׻� ������� �Է��� ��ٸ����� while���� ������� ���۽�Ų��.
				public void run() {
					int numData;
					String strData;
					try {
					while(true) 
						{
							strData =  inputStream.readUTF();  // Ŭ���̾�Ʈ�� ��û �ľ�
							
							if(strData.equals("input")) // ����ڰ� ȭ�� �����Ѱ��
							{
								numData = inputStream.readInt();
								manageMachine.inputMoney(numData);
								list.add("����� ��������");
							}
							else if(strData.equals("drink")) // ����ڰ� �����ư�� Ŭ���Ѱ��
							{  
								numData = inputStream.readInt();
								int price = manageMachine.retDrink(numData);
								manageMachine.income += price; // ��������
								String text = manageMachine.beverage[numData].queue.getName();
								String message;
								message = "����ڰ� " + text + "�� �簬���ϴ�." ;
								list.add(message);
							}
							else if(strData.equals("stock")) // ��� ��û
							{
								inputStream.readInt();
								outputStream.writeUTF("stock");
								// ���� ť���� ��� ��ȯ�޾� Ŭ���̾�Ʈ���� �����Ѵ�.
								for(int i = 0 ; i < 5; i++) {
									outputStream.writeInt(manageMachine.beverage[i].queue.getStock());
								}
								// �ܵ� ��� Ŭ���̾�Ʈ���� ����
								for(int i = 0 ; i < 5; i++) {
									outputStream.writeInt(manageMachine.change[i]);
								}
							}
							else if(strData.equals("income")) // ���� ��Ȳ
							{
								inputStream.readInt();
								outputStream.writeUTF("income");
								outputStream.writeInt(manageMachine.income);
							}
							else if(strData.equals("collect")) // ���� ����ڿ��� ���� �ּ� ������ŭ�� ������ ������ ȭ�� ��ȯ�Ѵ�.
							{
								int minimum  = inputStream.readInt();
								int won[] = {10 , 50 ,100 , 500 ,1000};
								int sum = 0;
								for(int i = 0 ; i < 5; i++)
								{
									if(manageMachine.change[i] <= minimum) // ���� ȭ�� ������ �ּұ��� ���� ���ٸ� ��ŵ
										continue;
									// sum ������ �ּҰ����� ������ ������ ȭ����� ���Ѵ�.
									sum += ( manageMachine.change[i] - minimum) * won[i]; 
								}
								list.add(sum + "�� �����ϼ̽��ϴ�.");
							}
							else if(strData.equals("order")) // ���� �ֹ�
							{
								
								int index = inputStream.readInt(); // �ֹ��� ���� ��ġ
								int orderNum  = inputStream.readInt(); // �ֹ��� ���� ��
								String beverageName = inputStream.readUTF(); // ���� �̸�
								int beveragePrice = inputStream.readInt(); // ���� ����
								manageMachine.orderDrink(index, beverageName, beveragePrice, orderNum);
								list.add(beverageName + "����" + orderNum + "�� �ֹ�, ���� : " + beveragePrice);
							}	
							else if(strData.equals("fill")) // �ܵ� ����
							{
								int won[] = {10 , 50 ,100 , 500 ,1000};

								int maximum = inputStream.readInt(); // ������ �ܵ��ѷ�
								int sum; // ������ �� �ܵ� �׼�
								int index; // ������ �ܵ� ����
								list.add("���� ��Ȳ");
								for(int i = 0 ; i < 5; i++)
								{
									sum = 0;
									index = 0;
									// ����ڰ� ���� �ܵ����� ��ŭ �ܵ��� �߰��Ѵ�.
									while(manageMachine.change[i] < maximum)
									{
										index++;
										manageMachine.change[i]++;
									}
									sum = index * won[i]; 
									list.add(won[i] + "�� " +index + "�� ����, ��" + sum + "�� �����ϼ̽��ϴ�.");
								}
							}
						}
					}
					catch (Exception e) 
					{}
				}
			};
			executorService.submit(runnable);
	
		}

		
		void send(String data)
		{
			try {
				DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
				outputStream.writeUTF(data);
			} catch(Exception e)
			{
				
			}
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
		

		list.setBounds(32, 41, 288, 327);
		contentPane.add(list);
		serverSetting();
	}
}
