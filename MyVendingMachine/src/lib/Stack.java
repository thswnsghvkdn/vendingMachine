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
	
	public void visitNode(Node next) // 노드들을 순회하면서 string을 더해나간다.
	{
		if(next == null) return;
		visitNode(next.getNextNode()); // 다음 노드 호출
		str += next.getData(); // 호출후에 문자열을 더해 스택의 역순으로 문자열을 저장한다.
	}
	
	public String retString() // 스택들이 저장하고 있는 문자열을 반환한다.
	{
		str = ""; // 문자열초기화
		visitNode(top); // 문자열을 더해간다.
		return str; // 더해진 문자열 반환
	}
	public boolean empty() {
		return top == null;
	}
	public void clear() // 스택에 모든 내용을 삭제
	{
		while(!empty())
		{
			pop();
		}
	}
}
	