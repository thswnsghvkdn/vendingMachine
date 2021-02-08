package lib;

import java.util.ArrayList;


/* ���α׷� �� : Heap
 * 1���� main �Լ�
 * 3���� Ŭ����
 * ���α׷��� ����ȯ�� : 
 * Windows 10
 * eclipse 2020 - 09
 * JDK 14 
 * Java EE IDE
 * �ۼ��� : ����ȣ 
 * ���α׷��� ������ 
 * : ������ư�� �����ϱ� ���� ���� ����Ͽ����ϴ�.
 * 
 *
 */

public class Heap {
	class DrinkStock {
		public int stock; // ������ ���
		public int index; // ������ �ε���
		DrinkStock(int index,int stock)
			{
				this.index = index;
				this.stock = stock;
			}
		}
		private ArrayList<DrinkStock> heap;
		
		public Heap() {
			heap = new ArrayList<>();
			heap.add(new DrinkStock(-1 ,1000000)); // 0�� �ε����� �ִ� ����
		}
		public void insert(int index, int stock) {
			heap.add(new DrinkStock(index, stock));
			int p = heap.size() - 1;
			while(p > 1 && heap.get( p / 2 ).stock < heap.get(p).stock)
			{
				DrinkStock temp = heap.get(p / 2);
				heap.set(p / 2, heap.get(p));
				heap.set(p, temp);
				
				p = p / 2;
			}		
		}
		
		public int delete()  // ������
		{
			if(heap.size() - 1 < 1) { // 0�� �ε����� �ִ� ����̴�.
				return -1;
			}
			
			DrinkStock deleteItem = heap.get(1);
			
			heap.set(1,  heap.get(heap.size() - 1)); // ������ �ε����� ���Ҹ� ���� ó������ ����
			heap.remove(heap.size() - 1 );
			

			int pos = 1;
			while( (pos * 2) < heap.size()) 
			{
				
				int max = heap.get(pos * 2).stock; // ���� �ڽ��� ���
				int maxPos = pos * 2;
				
				// ������ �ڽ��� �ִ� ��� ������ �ڽ��� ���� �� ���� ��� ���ǹ��� �ɸ��� �ʴ´�.
				if(( (pos * 2 + 1) < heap.size()) && max < heap.get(pos * 2 + 1).stock ) {
					max = heap.get(pos * 2 + 1).stock;
					maxPos = pos*2 + 1;
				}
				
				if(heap.get(pos).stock > max) {
					break;
				}
				
				DrinkStock temp = heap.get(pos);
				heap.set(pos, heap.get(maxPos));
				heap.set(maxPos, temp);
				pos = maxPos;
			}
			
			return deleteItem.index;
		}
	
}
