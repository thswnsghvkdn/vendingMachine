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



class ManageBeverage // 음료수 클래스
{
	
	Queue queue; // 각 아이템은 큐로 관리한다.
	String nameText; // 음료수의 텍스트
	ManageBeverage(String name, int price) // 초기에 세개 음료수를 추가하는 생성자
	{
		queue = new Queue();
		for(int i = 0 ; i < 3; i++)
			insertNode(name, price);
	}
	public void insertNode(String name, int price) // 음료수 추가 메소드 후에 라이브러리로 구현할 것 큐
	{
		queue.enqueue(name, price);
	}
	public void deleteNode() // 음료수 노드 제거 메소드 후에 라이브러리로 구현
	{
		queue.deque();
	}
	
}


class ManageMachine
{
	int income; // 총 매출
	int[] won = {1000, 500, 100, 50 ,10}; // 원 단위
	public int[] change = new int[5]; // 거스름돈 화폐단위는 5개가 있다.
	
	public ManageBeverage[] beverage = new ManageBeverage[6];// 음료수 6개
	ManageMachine() // 생성자 
	{		
		for(int i = 0 ; i < 5 ; i++)
		{
			change[i] = 5; // 거스름돈은 각 5개씩 초기화
		}
	}
	void orderDrink(int index, String name, int price, int num) // 인덱스위치에 음료를 num개 보충
	{
		for(int i = 0 ; i < num ; i++)
		beverage[index].insertNode(name, price);
	}
	void newItem(int index, String name, int price) // 새로운 음료 생성 메소드
	{
		beverage[index] = new ManageBeverage(name, price);
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
	ManageMachine manageMachine = new ManageMachine();
	List list = new List();
	private JPanel contentPane;

	private ServerSocket serverSocket;
	private DataInputStream dataInputStream; // 입력 스트림
	private DataOutputStream dataOutputStream; // 출력 스트림
	ExecutorService executorService; // 쓰레드 풀
	Vector<Client> connections =new Vector<Client>(); // 동기화가 가능하여 속도는 느리더라도 좀 더 안전한 벡터를 사용
	

	
	public void serverSetting() {
		// 스레드 풀을 만들고 cpu가 가용할 수 있는 프로세스 수로 인수를 준다.
		executorService = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors()
		);
		
		try {
			serverSocket = new ServerSocket(10002); // 바인드
			list.add("열림");
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
				while(true) // 클라이언트의 접속대기를 반복
				{
					Socket clientSocket;
					try {
						clientSocket = serverSocket.accept();
						list.add(clientSocket.getRemoteSocketAddress().toString()); // 접속된 클라이언트 주소
						Client client = new Client(clientSocket);
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
			Runnable runnable = new Runnable() { // 항상 사용자의 입력을 기다리도록 while문을 스레드로 동작시킨다.
				public void run() {
					int numData;
					String strData;
					try {
					while(true) 
						{
							strData =  inputStream.readUTF();  // 클라이언트의 요청 파악
							
							if(strData.equals("input")) // 사용자가 화폐를 투입한경우
							{
								numData = inputStream.readInt();
								manageMachine.inputMoney(numData);
								list.add("사용자 동전투입");
							}
							else if(strData.equals("drink")) // 사용자가 음료버튼을 클릭한경우
							{  
								numData = inputStream.readInt();
								int price = manageMachine.retDrink(numData);
								manageMachine.income += price; // 매출증가
								String text = manageMachine.beverage[numData].queue.getName();
								String message;
								message = "사용자가 " + text + "을 사갔습니다." ;
								list.add(message);
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
							else if(strData.equals("income")) // 매출 현황
							{
								inputStream.readInt();
								outputStream.writeUTF("income");
								outputStream.writeInt(manageMachine.income);
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
									sum += ( manageMachine.change[i] - minimum) * won[i]; 
								}
								list.add(sum + "원 수금하셨습니다.");
							}
							else if(strData.equals("order")) // 음료 주문
							{
								
								int index = inputStream.readInt(); // 주문할 음료 위치
								int orderNum  = inputStream.readInt(); // 주문할 음료 수
								String beverageName = inputStream.readUTF(); // 음료 이름
								int beveragePrice = inputStream.readInt(); // 음료 가격
								manageMachine.orderDrink(index, beverageName, beveragePrice, orderNum);
								list.add(beverageName + "음료" + orderNum + "개 주문, 가격 : " + beveragePrice);
							}	
							else if(strData.equals("fill")) // 잔돈 보충
							{
								int won[] = {10 , 50 ,100 , 500 ,1000};

								int maximum = inputStream.readInt(); // 보충할 잔돈총량
								int sum; // 보충한 총 잔돈 액수
								int index; // 보충할 잔돈 개수
								list.add("보충 현황");
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
									sum = index * won[i]; 
									list.add(won[i] + "원 " +index + "개 보충, 총" + sum + "원 충전하셨습니다.");
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


		manageMachine.newItem(0, "물", 450);
		manageMachine.newItem(1, "콜라", 750);
		manageMachine.newItem(2, "이온음료", 550);
		manageMachine.newItem(3, "커피", 500);
		manageMachine.newItem(4, "스타벅스", 750);
		manageMachine.newItem(5, "랜덤", 700);
		
		
		
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
