package lib;

class Node {
	private String data;
	private Node nextNode;
	public Node(String data) {
		this.data = data;
		this.nextNode = null;
	}
	protected void linkNode(Node node)
	{
		this.nextNode = node;
	}
	protected String getData() {
		return this.data;
	}
	protected Node getNextNode() {
		return this.nextNode;
	}
}

public class Stack {

	Node top;
	String str;
	public Stack() {
		this.top = null;
	}
	public void push(String data)
	{
		Node node = new Node(data);
		node.linkNode(top);
		top = node;
	}
	
	public String peek() {
		return top.getData();
	}
	
	public void pop() {
		if(empty()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		else 
			top = top.getNextNode();
	}
	
	public void visitNode(Node next) // ������ ��ȸ�ϸ鼭 string�� ���س�����.
	{
		if(next == null) return;
		visitNode(next.getNextNode()); // ���� ��� ȣ��
		str += next.getData(); // ȣ���Ŀ� ���ڿ��� ���� ������ �������� ���ڿ��� �����Ѵ�.
	}
	
	public String retString() // ���õ��� �����ϰ� �ִ� ���ڿ��� ��ȯ�Ѵ�.
	{
		str = ""; // ���ڿ��ʱ�ȭ
		visitNode(top); // ���ڿ��� ���ذ���.
		return str; // ������ ���ڿ� ��ȯ
	}
	public boolean empty() {
		return top == null;
	}
	public void clear() // ���ÿ� ��� ������ ����
	{
		while(!empty())
		{
			pop();
		}
	}
}
	