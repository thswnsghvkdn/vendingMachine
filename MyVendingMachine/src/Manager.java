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
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import lib.Heap;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class DrinkStock {
	public int stock; // ������ ���
	public int index; // ������ �ε���
	DrinkStock(int index,int stock)
	{
		this.index = index;
		this.stock = stock;
	}
}
class ManageBeverage // ����� Ŭ����
{
	
	Queue queue; // �� �������� ť�� �����Ѵ�.
	String nameText; // ������� �ؽ�Ʈ
	ManageBeverage() // �ʱ⿡ ���� ������� �߰��ϴ� ������
	{
		queue = new Queue();
	}
	public void insertNode(String name, int price) // ����� �߰� �޼ҵ� �Ŀ� ���̺귯���� ������ �� ť
	{
		queue.enqueue(name, price);
	}
	public void deleteNode() // ����� ��� ���� �޼ҵ� �Ŀ� ���̺귯���� ����
	{
		queue.deque();
	}
	public int getStock()
	{
		return queue.getStock();
	}
	
}


class ManageMachine
{
	int income; // �� ����
	int[] won = {10, 50, 100, 500 ,1000}; // �� ����
	public int[] change = new int[5]; // �Ž����� ȭ������� 5���� �ִ�.
	
	public ManageBeverage[] beverage = new ManageBeverage[6];// ����� 6��
	ManageMachine() // ������ 
	{		
		for(int i = 0 ; i < 5 ; i++)
		{
			beverage[i] = new ManageBeverage();
		}
	}
	void orderDrink(int index, String name, int price, int num) // �ε�����ġ�� ���Ḧ num�� ����
	{
		for(int i = 0 ; i < num ; i++)
		beverage[index].insertNode(name, price);
	}
	void newItem(int index, DBconnector db) // ���ο� ���� ���� �޼ҵ�
	{
		String name = db.getName(index);
		int price = db.getPrice(index);
		int count = db.getStock(index);
		for(int j = 0 ; j < count ; j++)
		beverage[index].insertNode(name, price);
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
	DBconnector db = new DBconnector();
	ManageMachine manageMachine = new ManageMachine();
	List list = new List();
	private JPanel contentPane;
	private Heap heap = new Heap();
	private ServerSocket serverSocket;
	private DataInputStream dataInputStream; // �Է� ��Ʈ��
	private DataOutputStream dataOutputStream; // ��� ��Ʈ��
	ExecutorService executorService; // ������ Ǯ
	Vector<Client> connections = new Vector<Client>(); // ����ȭ�� �����Ͽ� �ӵ��� �������� �� �� ������ ���͸� ���
	private final JButton Start = new JButton();
	boolean start = false; // �������� �÷���

	
	public void serverSetting() {
		// ������ Ǯ�� ����� cpu�� ������ �� �ִ� ���μ��� ���� �μ��� �ش�.
		executorService = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors()
		);
		
		try {
			serverSocket = new ServerSocket(10002); // ���ε�
			list.add("���� ����");
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
				int index = 1;

				while(true) // Ŭ���̾�Ʈ�� ���Ӵ�⸦ �ݺ�
				{
					Socket clientSocket;
					try {
						clientSocket = serverSocket.accept();
						list.add(clientSocket.getRemoteSocketAddress().toString() + " ����!"); // ���ӵ� Ŭ���̾�Ʈ �ּ�
						Client client = new Client(clientSocket, index++);
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
		  serverSocket.close();
		  dataInputStream.close();
		  dataOutputStream.close();
		} catch(Exception e) {}
	}
	class Client
	{
		int id; // Ŭ���̾�Ʈ ���̵�
		Socket socket;
		public DataOutputStream outputStream;
		public DataInputStream inputStream;
		public Client(Socket socket_, int id)
		{
			this.id = id;
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
							
							if(strData.equals("start"))
							{
								outputStream.writeUTF("init");
					
								for(int i = 0 ; i < 5 ; i++)
									{
										outputStream.writeUTF(db.getName(i));
										outputStream.writeInt(db.getPrice(i));
										outputStream.writeInt(db.getStock(i + 5));
									}
								
							}
							else if(strData.equals("input")) // ����ڰ� ȭ�� �����Ѱ��, �ش�ȭ���� ���Խÿ��� ȭ���� ��� ���� ������ Ŭ���̾�Ʈ���� ������.
							{
								numData = inputStream.readInt();
								manageMachine.inputMoney(numData); // ���� manageMachine ��ü�� �ܵ� ����� ���� �����Ѵ�.
								db.incStock(numData + 5); // ������ ���̽��� �ش� ������ ��� �߰��Ѵ�. 
								
								Iterator<Client> iterator = connections.iterator();
								while(iterator.hasNext()) { // Ŭ���̾�Ʈ ���ο��� ������.
										Client client = iterator.next();
										client.outputStream.writeUTF("input");
										client.outputStream.writeInt(numData);
								} 
								list.add("����� ��������");
							}
							
							else if(strData.equals("change")) // �ܵ���ȯ
							{
								int temp = inputStream.readInt();

								int index = 0; // �ܵ� �迭�� �ε���
								int tempChange; // �� �������� �Ž����� ����
								
								while(temp > 0)
								{
									tempChange = temp /  manageMachine.won[4 - index]; // ȭ�󰳼�
									if(manageMachine.change[4 - index] > tempChange) // �ܵ��� ��ȯ�� �� �ִ� ���
									{
										manageMachine.change[4 - index] -= tempChange; // �ܵ����� ����
										temp %= manageMachine.won[4 - index]; // �ش� ȭ�󰳼���ŭ �ܵ�����
										if(manageMachine.change[4 - index] == 0)
										{
											Iterator<Client> iterator = connections.iterator();
											while(iterator.hasNext()) { // Ŭ���̾�Ʈ ���ο��� �ܵ� ������ ������.
													Client client = iterator.next();
													client.outputStream.writeUTF("changeEmpty");
													client.outputStream.writeInt(4 - index);
											} 
										}
										 // �ִϸ��̼����� �ش� �ܵ���ȯ
									}
									else // �ܵ��� ������ ��� 
									{
										temp = temp - (manageMachine.change[4 - index] * manageMachine.won[4 - index]); // ������ �Ž������� ����
										manageMachine.change[4 - index] = 0;

										Iterator<Client> iterator = connections.iterator();
										while(iterator.hasNext()) { // Ŭ���̾�Ʈ ���ο��� �ܵ� ������ ������.
												Client client = iterator.next();
												client.outputStream.writeUTF("changeEmpty");
												client.outputStream.writeInt(4 - index);
										} 
									}
									
									db.updateChange(9 - index, manageMachine.change[4 - index]);
									index++; // ���� ȭ��
								}
							}
	
							else if(strData.equals("drink")) // ����ڰ� �����ư�� Ŭ���Ѱ��
							{  
								numData = inputStream.readInt();
								int income; // �ش� ���� �ε����� ����
								String name; // �ش� ���� �ε����� �̸�
								if(numData == 5) // ���� ���� �κ�
								{
									numData = heap.delete(); // ���� ���� ���� ������ �ε���
									outputStream.writeUTF("random");
									outputStream.writeInt(numData);
									income = 700; // ������ ������쿡 ����
								}
								else
							      income = db.getPrice(numData); // �Ϲ� ���Ḧ ��������� ����
								name = db.getName(numData); // ��� ���̱� ���� �̸��� �޾ƿ´�.
								
								if(manageMachine.beverage[numData].getStock() > 0) {
									String text = manageMachine.beverage[numData].queue.getName();
									int price = manageMachine.retDrink(numData);
									manageMachine.income += price; // ��������
									String message;
									message = "����ڰ� " + text + "�� �簬���ϴ�." ;
									list.add(message);
									boolean isEmpty = db.reduceStock(numData); // db�� ��� ������Ʈ
									if(isEmpty) {
										db.updateEmptyDate(numData); // ��� ���� ��¥ ����
										if(db.getAllStock(numData) == 0) 	// �ش� �ε����� ��� ���ᰡ �� �����ٸ�
										{
											Iterator<Client> iterator = connections.iterator();
											while(iterator.hasNext()) { // Ŭ���̾�Ʈ ���ο��� ������.
													Client client = iterator.next();
													client.outputStream.writeUTF("drinkEmpty");
													client.outputStream.writeInt(numData);												
											} 
											
											outputStream.writeUTF("sendMail"); // Ŭ���̾�Ʈ�� �α��� ������ ��û�Ѵ�.
											outputStream.writeUTF(name); // �����ε����� �̸��� �ٽñ� ���� �ޱ� ���� �����Ѵ�.
											
										}
										else {
											// �ش� ������  ���ο� �̸��� ������ ��� Ŭ���̾�Ʈ ���Ǳ⿡  �˸���.
											manageMachine.newItem(numData, db);
											Iterator<Client> iterator = connections.iterator();
											while(iterator.hasNext()) { // Ŭ���̾�Ʈ ���ο��� ������.
													Client client = iterator.next();
													client.outputStream.writeUTF("drinkChange");
													client.outputStream.writeInt(numData);
													client.outputStream.writeUTF(db.getName(numData));
													client.outputStream.writeInt(db.getPrice(numData));
											} 
										}
									}
									db.inputIncome(income); // �Ϸ� ���� ����
									heap.insert(numData, manageMachine.beverage[numData].getStock()); // �پ�� ������� �� Ʈ���� ������Ʈ
								}
							}
							else if(strData.equals("sendMail")) // ����ڿ���  �α��� ������ �޾Ƽ� ������ ������.
							{
								String id = inputStream.readUTF();
								String name = inputStream.readUTF();
								db.naverMailSend(name, db.getMail(id)); // ��� ������ ������ ��Ȳ�� ������� ���Ϸ� ������.
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
							else if(strData.equals("income")) //�Ϸ� ���� ��Ȳ
							{
								inputStream.readInt();
								outputStream.writeUTF("income");
								outputStream.writeInt(db.getCurrentIncome()); // ������ ���̽����� �Ϸ� ������ �ҷ��´�.
							}
							else if(strData.equals("monthIncome"))
							{
								int month = inputStream.readInt(); // Ŭ���̾�Ʈ�� ��û�� �� 
								outputStream.writeUTF("monthIncome");
								outputStream.writeInt(db.getMonthIncome(month)); // ������ ���̽����� ���� ������ �ҷ��´�.								
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
									manageMachine.change[i] -= minimum;
									db.updateChange( i + 5, manageMachine.change[i] ); // db������Ʈ
									sum += ( manageMachine.change[i]) * won[i]; 
								}
								
								
								outputStream.writeUTF("collectlogin");
								outputStream.writeInt(sum);
								list.add("Ŭ���̾�Ʈ " + id + " : " +sum + "�� �����ϼ̽��ϴ�.");
							}
							else if(strData.equals("inputCollect"))
							{
								String id = inputStream.readUTF();
								int sum = inputStream.readInt();
								db.inputCollect(id, sum);
							}
							else if(strData.equals("order")) // ���� ����
							{
								
								int index = inputStream.readInt(); // �ֹ��� ���� ��ġ
								int orderNum  = inputStream.readInt(); // �ֹ��� ���� ��
								String beverageName = inputStream.readUTF(); // ���� �̸�
								int beveragePrice = inputStream.readInt(); // ���� ����
								int stock = db.getStock(index);

								
								manageMachine.orderDrink(index, beverageName, beveragePrice, orderNum);
								list.add("Ŭ���̾�Ʈ "+ id + "���� " + beverageName + "����" + orderNum + "�� ����, ���� : " + beveragePrice);
								heap.insert(index, manageMachine.beverage[index].getStock()); // ���ֵ� ��� �������� ��Ʈ���� ������Ʈ
								db.insertDrink(index, beverageName, beveragePrice, orderNum); // DB������Ʈ
								
								if(stock == 0) // ������� 0�� ���ٸ� 
								{
									// ����ڿ��� ���ο� ������ ������ ������.
									Iterator<Client> iterator = connections.iterator();
									while(iterator.hasNext()) { // Ŭ���̾�Ʈ ���ο��� ������.
											Client client = iterator.next();
											client.outputStream.writeUTF("order");
											client.outputStream.writeInt(index);
											client.outputStream.writeUTF(beverageName);
											client.outputStream.writeInt(beveragePrice);
									} 
								}
								
							}	
							else if(strData.equals("fill")) // �ܵ� ����
							{
								int won[] = {10 , 50 ,100 , 500 ,1000};

								int maximum = inputStream.readInt(); // ������ �ܵ��ѷ�
								int sum; // ������ �� �ܵ� �׼�
								int index; // ������ �ܵ� ����
								list.add("Ŭ���̾�Ʈ " + id +"���� �ܵ��� �����Ͽ����ϴ�.");

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
									db.updateChange(i + 5, manageMachine.change[i]); // db������Ʈ 
									sum = index * won[i]; 
									list.add(won[i] + "�� " +index + "�� ����, ��" + sum + "�� �����Ͽ����ϴ�.");
								}
								Iterator<Client> iterator = connections.iterator();
								while(iterator.hasNext()) { // Ŭ���̾�Ʈ ���ο��� ������.
										Client client = iterator.next();
										client.outputStream.writeUTF("fill");

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


		// ��� �������� �ʱ� �ִ� ���� �����Ѵ�.
		for(int i = 0 ; i < 5 ; i++)
			heap.insert(i, 3);
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 383, 488);
		contentPane = new JPanel();
		contentPane.setForeground(Color.CYAN);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		

		list.setBounds(34, 67, 288, 327);
		contentPane.add(list);
		
		JLabel lblNewLabel = new JLabel("Vending machine Server");
		lblNewLabel.setFont(new Font("����", Font.BOLD, 15));
		lblNewLabel.setBounds(81, 10, 205, 39);
		contentPane.add(lblNewLabel);
		Start.setText("\uC11C\uBC84 \uC2DC\uC791");
		Start.setBackground(Color.LIGHT_GRAY);
		Start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(start == false)
				{

					Start.setBackground(Color.pink);
					
					// �ʱ⿡ �����ͺ��̽��� ���� ����Ǿ��ִ� ���ᵥ���͸� �����´�.
					for(int i = 0 ; i < 5; i++)
					{
						manageMachine.newItem(i, db);
						manageMachine.change[i] = db.getStock(i + 5); // ���� ���
					}
					
					// ��� �������� �ʱ� �ִ� ���� �����Ѵ�.
					for(int i = 0 ; i < 5 ; i++)
						heap.insert(i, db.getStock(i));
					
					serverSetting();
					Start.setText("��������");
					start = !start;
				}
				else
				{
					stopServer();
					list.add("������ ����Ǿ����ϴ�.");
					Start.setBackground(Color.gray);
					Start.setText("��������");
					start = !start;
				}
			}
		});
		Start.setBounds(132, 410, 97, 23);
		
		contentPane.add(Start);
	}
}
