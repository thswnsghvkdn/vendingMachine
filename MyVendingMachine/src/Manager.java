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
	public int stock; // 음료의 재고
	public int index; // 음료의 인덱스
	DrinkStock(int index,int stock)
	{
		this.index = index;
		this.stock = stock;
	}
}
class ManageBeverage // 음료수 클래스
{
	
	Queue queue; // 각 아이템은 큐로 관리한다.
	String nameText; // 음료수의 텍스트
	ManageBeverage() // 초기에 세개 음료수를 추가하는 생성자
	{
		queue = new Queue();
	}
	public void insertNode(String name, int price) // 음료수 추가 메소드 후에 라이브러리로 구현할 것 큐
	{
		queue.enqueue(name, price);
	}
	public void deleteNode() // 음료수 노드 제거 메소드 후에 라이브러리로 구현
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
	int income; // 총 매출
	int[] won = {10, 50, 100, 500 ,1000}; // 원 단위
	public int[] change = new int[5]; // 거스름돈 화폐단위는 5개가 있다.
	
	public ManageBeverage[] beverage = new ManageBeverage[6];// 음료수 6개
	ManageMachine() // 생성자 
	{		
		for(int i = 0 ; i < 5 ; i++)
		{
			beverage[i] = new ManageBeverage();
		}
	}
	void orderDrink(int index, String name, int price, int num) // 인덱스위치에 음료를 num개 보충
	{
		for(int i = 0 ; i < num ; i++)
		beverage[index].insertNode(name, price);
	}
	void newItem(int index, DBconnector db) // 새로운 음료 생성 메소드
	{
		String name = db.getName(index);
		int price = db.getPrice(index);
		int count = db.getStock(index);
		for(int j = 0 ; j < count ; j++)
		beverage[index].insertNode(name, price);
	}
	void inputMoney(int index) // 사용자가 화페를 투입한경우
	{
		change[index]++;
	}
	int retDrink(int index) // 사용자가 입력한 음료 재고를 감소시킨다.
	{
		int price = beverage[index].queue.getPrice();
		income += price;
		beverage[index].deleteNode();
		return price; // 큐에 대기 중인 첫번째 음료의 가격을 리턴
	}
	void pressChange(int temp) // 잔돈 반환버튼  
	{
		int index = 0; // 잔돈 배열의 인덱스
		int tempChange; // 각 단위마다 거스름돈 개수
		
		while(temp > 0)
		{
			tempChange = temp / won[index]; // 화폐개수
			if(change[index] > tempChange) // 잔돈을 반환할 수 있는 경우
			{
				change[index] -= tempChange; // 잔돈개수 차감
				temp %= won[index]; // 해당 화폐개수만큼 잔돈차감
				 // 애니메이션으로 해당 잔돈반환
			}
			else // 잔돈이 부족한 경우 
			{
				temp = temp - (change[index] * won[index]); // 가능한 거스름돈만 차감
				change[index] = 0;
			}
			index++; // 다음 화폐
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
	private DataInputStream dataInputStream; // 입력 스트림
	private DataOutputStream dataOutputStream; // 출력 스트림
	ExecutorService executorService; // 쓰레드 풀
	Vector<Client> connections = new Vector<Client>(); // 동기화가 가능하여 속도는 느리더라도 좀 더 안전한 벡터를 사용
	private final JButton Start = new JButton();
	boolean start = false; // 서버시작 플래그

	
	public void serverSetting() {
		// 스레드 풀을 만들고 cpu가 가용할 수 있는 프로세스 수로 인수를 준다.
		executorService = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors()
		);
		
		try {
			serverSocket = new ServerSocket(10002); // 바인드
			list.add("서버 열림");
			System.out.println("서버열림");
		} catch (Exception e)
		{
			if(!serverSocket.isClosed()) // 서버를 안전하게 닫아준다.
				stopServer();
			return;
		}
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int index = 1;

				while(true) // 클라이언트의 접속대기를 반복
				{
					Socket clientSocket;
					try {
						clientSocket = serverSocket.accept();
						list.add(clientSocket.getRemoteSocketAddress().toString() + " 접속!"); // 접속된 클라이언트 주소
						Client client = new Client(clientSocket, index++);
						connections.add(client); // 벡터에 클라이언트 소켓을 추가한다.
						// connections.size() 연결개수
					} catch (IOException e) {
						// TODO Auto-generated catch block
						if(!serverSocket.isClosed())
							stopServer();
						break; // 클라이언트의 연결요청 응답중 예외가 발생할 경우 서버를 종료하고 반복문을 종료한다.
					} 

				}
			}
		};
		
		executorService.submit(runnable); // 작업객체를 스레드풀에 인수로 전달한다.
	}
	public void stopServer()
	{
		try {
		Iterator<Client> iterator = connections.iterator();
		while(iterator.hasNext()) { // 클라이언트들을 제거
			Client client = iterator.next();
			client.socket.close();
			iterator.remove();
		  }
		  if(serverSocket != null && !serverSocket.isClosed()) { // 클라이언트들을 모두 종료시킨후 서버를 종료
			  serverSocket.close();
		  }
		  if(executorService != null && !executorService.isShutdown()) { // 스레트 풀 종료
			  executorService.shutdown();
		  }
		  serverSocket.close();
		  dataInputStream.close();
		  dataOutputStream.close();
		} catch(Exception e) {}
	}
	class Client
	{
		int id; // 클라이언트 아이디
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
			Runnable runnable = new Runnable() { // 항상 사용자의 입력을 기다리도록 while문을 스레드로 동작시킨다.
				public void run() {
					int numData;
					String strData;
					try {
					while(true) 
						{
							strData =  inputStream.readUTF();  // 클라이언트의 요청 파악
							
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
							else if(strData.equals("input")) // 사용자가 화폐를 투입한경우, 해당화폐의 투입시에는 화폐의 재고가 생긴 정보를 클라이언트에게 보낸다.
							{
								numData = inputStream.readInt();
								manageMachine.inputMoney(numData); // 현재 manageMachine 객체에 잔돈 멤버의 값을 증가한다.
								db.incStock(numData + 5); // 데이터 베이스의 해당 동전의 재고를 추가한다. 
								
								Iterator<Client> iterator = connections.iterator();
								while(iterator.hasNext()) { // 클라이언트 전부에게 보낸다.
										Client client = iterator.next();
										client.outputStream.writeUTF("input");
										client.outputStream.writeInt(numData);
								} 
								list.add("사용자 동전투입");
							}
							
							else if(strData.equals("change")) // 잔돈반환
							{
								int temp = inputStream.readInt();

								int index = 0; // 잔돈 배열의 인덱스
								int tempChange; // 각 단위마다 거스름돈 개수
								
								while(temp > 0)
								{
									tempChange = temp /  manageMachine.won[4 - index]; // 화폐개수
									if(manageMachine.change[4 - index] > tempChange) // 잔돈을 반환할 수 있는 경우
									{
										manageMachine.change[4 - index] -= tempChange; // 잔돈개수 차감
										temp %= manageMachine.won[4 - index]; // 해당 화폐개수만큼 잔돈차감
										if(manageMachine.change[4 - index] == 0)
										{
											Iterator<Client> iterator = connections.iterator();
											while(iterator.hasNext()) { // 클라이언트 전부에게 잔돈 없음을 보낸다.
													Client client = iterator.next();
													client.outputStream.writeUTF("changeEmpty");
													client.outputStream.writeInt(4 - index);
											} 
										}
										 // 애니메이션으로 해당 잔돈반환
									}
									else // 잔돈이 부족한 경우 
									{
										temp = temp - (manageMachine.change[4 - index] * manageMachine.won[4 - index]); // 가능한 거스름돈만 차감
										manageMachine.change[4 - index] = 0;

										Iterator<Client> iterator = connections.iterator();
										while(iterator.hasNext()) { // 클라이언트 전부에게 잔돈 없음을 보낸다.
												Client client = iterator.next();
												client.outputStream.writeUTF("changeEmpty");
												client.outputStream.writeInt(4 - index);
										} 
									}
									
									db.updateChange(9 - index, manageMachine.change[4 - index]);
									index++; // 다음 화폐
								}
							}
	
							else if(strData.equals("drink")) // 사용자가 음료버튼을 클릭한경우
							{  
								numData = inputStream.readInt();
								int income; // 해당 음료 인덱스의 가격
								String name; // 해당 음료 인덱스의 이름
								if(numData == 5) // 랜덤 음료 부분
								{
									numData = heap.delete(); // 가장 많이 남은 음료의 인덱스
									outputStream.writeUTF("random");
									outputStream.writeInt(numData);
									income = 700; // 랜덤을 누른경우에 수입
								}
								else
							      income = db.getPrice(numData); // 일반 음료를 누른경우의 수입
								name = db.getName(numData); // 재고를 줄이기 전에 이름을 받아온다.
								
								if(manageMachine.beverage[numData].getStock() > 0) {
									String text = manageMachine.beverage[numData].queue.getName();
									int price = manageMachine.retDrink(numData);
									manageMachine.income += price; // 매출증가
									String message;
									message = "사용자가 " + text + "을 사갔습니다." ;
									list.add(message);
									boolean isEmpty = db.reduceStock(numData); // db에 재고 업데이트
									if(isEmpty) {
										db.updateEmptyDate(numData); // 재고가 나간 날짜 저장
										if(db.getAllStock(numData) == 0) 	// 해당 인덱스의 모든 음료가 다 나갔다면
										{
											Iterator<Client> iterator = connections.iterator();
											while(iterator.hasNext()) { // 클라이언트 전부에게 보낸다.
													Client client = iterator.next();
													client.outputStream.writeUTF("drinkEmpty");
													client.outputStream.writeInt(numData);												
											} 
											
											outputStream.writeUTF("sendMail"); // 클라이언트의 로그인 정보를 요청한다.
											outputStream.writeUTF(name); // 음료인덱스의 이름을 다시금 전달 받기 위해 전달한다.
											
										}
										else {
											// 해당 음료의  새로운 이름과 가격을 모든 클라이언트 자판기에  알린다.
											manageMachine.newItem(numData, db);
											Iterator<Client> iterator = connections.iterator();
											while(iterator.hasNext()) { // 클라이언트 전부에게 보낸다.
													Client client = iterator.next();
													client.outputStream.writeUTF("drinkChange");
													client.outputStream.writeInt(numData);
													client.outputStream.writeUTF(db.getName(numData));
													client.outputStream.writeInt(db.getPrice(numData));
											} 
										}
									}
									db.inputIncome(income); // 하루 매출 저장
									heap.insert(numData, manageMachine.beverage[numData].getStock()); // 줄어든 사이즈로 힙 트리를 업데이트
								}
							}
							else if(strData.equals("sendMail")) // 사용자에게  로그인 정보를 받아서 메일을 보낸다.
							{
								String id = inputStream.readUTF();
								String name = inputStream.readUTF();
								db.naverMailSend(name, db.getMail(id)); // 재고가 부족한 음료의 현황을 사용자의 메일로 보낸다.
							}
							else if(strData.equals("stock")) // 재고 요청
							{
								inputStream.readInt();
								outputStream.writeUTF("stock");
								// 음료 큐에서 재고를 반환받아 클라이언트에게 전달한다.
								for(int i = 0 ; i < 5; i++) {
									outputStream.writeInt(manageMachine.beverage[i].queue.getStock());
								}
								// 잔돈 재고를 클라이언트에게 제공
								for(int i = 0 ; i < 5; i++) {
									outputStream.writeInt(manageMachine.change[i]);
								}
							}
							else if(strData.equals("income")) //하루 매출 현황
							{
								inputStream.readInt();
								outputStream.writeUTF("income");
								outputStream.writeInt(db.getCurrentIncome()); // 데이터 베이스에서 하루 매출을 불러온다.
							}
							else if(strData.equals("monthIncome"))
							{
								int month = inputStream.readInt(); // 클라이언트가 요청한 월 
								outputStream.writeUTF("monthIncome");
								outputStream.writeInt(db.getMonthIncome(month)); // 데이터 베이스에서 월별 매출을 불러온다.								
							}
							else if(strData.equals("collect")) // 수금 사용자에게 받은 최소 개수만큼을 제외한 나머지 화폐를 반환한다.
							{
								int minimum  = inputStream.readInt();
								int won[] = {10 , 50 ,100 , 500 ,1000};
								int sum = 0;
								for(int i = 0 ; i < 5; i++)
								{
									if(manageMachine.change[i] <= minimum) // 남은 화폐 개수가 최소기준 보다 적다면 스킵
										continue;
									// sum 변수에 최소개수를 제외한 나머지 화폐들을 합한다.
									manageMachine.change[i] -= minimum;
									db.updateChange( i + 5, manageMachine.change[i] ); // db업데이트
									sum += ( manageMachine.change[i]) * won[i]; 
								}
								
								
								outputStream.writeUTF("collectlogin");
								outputStream.writeInt(sum);
								list.add("클라이언트 " + id + " : " +sum + "원 수금하셨습니다.");
							}
							else if(strData.equals("inputCollect"))
							{
								String id = inputStream.readUTF();
								int sum = inputStream.readInt();
								db.inputCollect(id, sum);
							}
							else if(strData.equals("order")) // 음료 발주
							{
								
								int index = inputStream.readInt(); // 주문할 음료 위치
								int orderNum  = inputStream.readInt(); // 주문할 음료 수
								String beverageName = inputStream.readUTF(); // 음료 이름
								int beveragePrice = inputStream.readInt(); // 음료 가격
								int stock = db.getStock(index);

								
								manageMachine.orderDrink(index, beverageName, beveragePrice, orderNum);
								list.add("클라이언트 "+ id + "에서 " + beverageName + "음료" + orderNum + "개 발주, 가격 : " + beveragePrice);
								heap.insert(index, manageMachine.beverage[index].getStock()); // 발주된 재고 기준으로 힙트리를 업데이트
								db.insertDrink(index, beverageName, beveragePrice, orderNum); // DB업데이트
								
								if(stock == 0) // 현재재고가 0이 였다면 
								{
									// 사용자에게 새로운 음료의 정보를 보낸다.
									Iterator<Client> iterator = connections.iterator();
									while(iterator.hasNext()) { // 클라이언트 전부에게 보낸다.
											Client client = iterator.next();
											client.outputStream.writeUTF("order");
											client.outputStream.writeInt(index);
											client.outputStream.writeUTF(beverageName);
											client.outputStream.writeInt(beveragePrice);
									} 
								}
								
							}	
							else if(strData.equals("fill")) // 잔돈 보충
							{
								int won[] = {10 , 50 ,100 , 500 ,1000};

								int maximum = inputStream.readInt(); // 보충할 잔돈총량
								int sum; // 보충한 총 잔돈 액수
								int index; // 보충할 잔돈 개수
								list.add("클라이언트 " + id +"에서 잔돈을 보충하였습니다.");

								for(int i = 0 ; i < 5; i++)
								{
									sum = 0;
									index = 0;
									// 사용자가 원한 잔돈개수 만큼 잔돈을 추가한다.
									while(manageMachine.change[i] < maximum)
									{
										index++;
										manageMachine.change[i]++;
									}
									db.updateChange(i + 5, manageMachine.change[i]); // db업데이트 
									sum = index * won[i]; 
									list.add(won[i] + "원 " +index + "개 보충, 총" + sum + "원 보충하였습니다.");
								}
								Iterator<Client> iterator = connections.iterator();
								while(iterator.hasNext()) { // 클라이언트 전부에게 보낸다.
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


		// 재고를 기준으로 초기 최대 힙을 구성한다.
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
		lblNewLabel.setFont(new Font("굴림", Font.BOLD, 15));
		lblNewLabel.setBounds(81, 10, 205, 39);
		contentPane.add(lblNewLabel);
		Start.setText("\uC11C\uBC84 \uC2DC\uC791");
		Start.setBackground(Color.LIGHT_GRAY);
		Start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(start == false)
				{

					Start.setBackground(Color.pink);
					
					// 초기에 데이터베이스로 부터 저장되어있는 음료데이터를 가져온다.
					for(int i = 0 ; i < 5; i++)
					{
						manageMachine.newItem(i, db);
						manageMachine.change[i] = db.getStock(i + 5); // 동전 재고
					}
					
					// 재고를 기준으로 초기 최대 힙을 구성한다.
					for(int i = 0 ; i < 5 ; i++)
						heap.insert(i, db.getStock(i));
					
					serverSetting();
					Start.setText("서버종료");
					start = !start;
				}
				else
				{
					stopServer();
					list.add("서버가 종료되었습니다.");
					Start.setBackground(Color.gray);
					Start.setText("서버시작");
					start = !start;
				}
			}
		});
		Start.setBounds(132, 410, 97, 23);
		
		contentPane.add(Start);
	}
}
