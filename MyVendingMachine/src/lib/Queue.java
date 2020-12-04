package lib;

class Item // 각 음료의 클래스
{
	int income; // 아이템별 매출
	String name; // 음료 이름
	int price; // 가격
	public Item link; // 다른 노드를 참조할 링크
	
	Item() // 인수로 들어온 가격과 이름을 초기화
	{
		this.name = null;
		this.price = 0;
		this.link = null;
	}	
	
	Item(String name, int price) // 인수로 들어온 가격과 이름을 초기화
	{
		this.name = name;
		this.price = price;
		this.link = null;
	}

	Item(String name, int price, Item link) // 인수로 들어온 가격과 이름을 초기화
	{
		this.name = name;
		this.price = price;
		this.link = link;
	}
	// 클래스의 게터와 세터구현
	void setName(String name) { this.name = name;}
	void setPrice(int price) {this.price = price;}
	String getName() {return name;}
	int getPrice() {return price;}
}


public class Queue {
	private Item head; // head 노드 인스턴스 변수
	private Item front; // front 포인터
	private Item rear; // rear 포인터
	private int stock; // 재고
	
	public Queue()
	{
		head = null;
		front = null;
		rear = null;
		stock = 0;
	}
	
	public boolean isEmpty()
	{
		return (front == null && rear == null);
	}
	public void enqueue(String name, int price)
	{
		Item newNode = new Item(name, price);
		if(isEmpty()) {
			this.head = newNode;
			this.front = this.head;
			this.rear = this.head;
		}
		else {
			rear.link = newNode;
			rear = newNode;
		}
		stock++;
	}
	public void deque() {
		Item tempNode;
		// 큐에 노드가 1개 남았을 경우
		if(front.link == null) {
			// head 와 front, rear 포인더에 null을 할당하여 남은 노드와의 연결을 끊고 초기화
			head = null;
			front = null;
			rear = null;
		}
		else {
			tempNode = front.link; // tempNode는 front 포인터가 가리키는 노드의 다음 노드를 할당.
			head = tempNode; // head가 tempNode를 참조하도록 함
			front.link = null; // 기존에 front 포인터가 가리키는 노드의 link를 초기화하여 끊음
			front = head; // front 포인터가 head(다음 노드)를 참조하도록 함
		}
		stock--;
	}

	public int getPrice()
	{
		return front.getPrice();
	}
	public String getName()
	{
		return front.getName();
	}
	public Item peek() // 첫번째 데이터 추출
	{
		return front;
	}
	public void clear() // 큐 초기화
	{
		head = null;
		front = null;
		rear = null;
	}
	public Item searchNode(int price)
	{
		Item tempNode = this.front;
		while(tempNode != null)
		{
			if(tempNode.price == price)
				return tempNode;
			else 
				tempNode = tempNode.link;
		}
		return tempNode;
	}
	public int getStock() { return stock; }
}
