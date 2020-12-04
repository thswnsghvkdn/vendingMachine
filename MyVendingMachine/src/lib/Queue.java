package lib;

class Item // �� ������ Ŭ����
{
	int income; // �����ۺ� ����
	String name; // ���� �̸�
	int price; // ����
	public Item link; // �ٸ� ��带 ������ ��ũ
	
	Item() // �μ��� ���� ���ݰ� �̸��� �ʱ�ȭ
	{
		this.name = null;
		this.price = 0;
		this.link = null;
	}	
	
	Item(String name, int price) // �μ��� ���� ���ݰ� �̸��� �ʱ�ȭ
	{
		this.name = name;
		this.price = price;
		this.link = null;
	}

	Item(String name, int price, Item link) // �μ��� ���� ���ݰ� �̸��� �ʱ�ȭ
	{
		this.name = name;
		this.price = price;
		this.link = link;
	}
	// Ŭ������ ���Ϳ� ���ͱ���
	void setName(String name) { this.name = name;}
	void setPrice(int price) {this.price = price;}
	String getName() {return name;}
	int getPrice() {return price;}
}


public class Queue {
	private Item head; // head ��� �ν��Ͻ� ����
	private Item front; // front ������
	private Item rear; // rear ������
	private int stock; // ���
	
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
		// ť�� ��尡 1�� ������ ���
		if(front.link == null) {
			// head �� front, rear ���δ��� null�� �Ҵ��Ͽ� ���� ������ ������ ���� �ʱ�ȭ
			head = null;
			front = null;
			rear = null;
		}
		else {
			tempNode = front.link; // tempNode�� front �����Ͱ� ����Ű�� ����� ���� ��带 �Ҵ�.
			head = tempNode; // head�� tempNode�� �����ϵ��� ��
			front.link = null; // ������ front �����Ͱ� ����Ű�� ����� link�� �ʱ�ȭ�Ͽ� ����
			front = head; // front �����Ͱ� head(���� ���)�� �����ϵ��� ��
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
	public Item peek() // ù��° ������ ����
	{
		return front;
	}
	public void clear() // ť �ʱ�ȭ
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
