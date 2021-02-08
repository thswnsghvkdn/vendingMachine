package lib;

import java.util.ArrayList;


/* 프로그램 명 : Heap
 * 1개의 main 함수
 * 3개의 클래스
 * 프로그램의 구성환경 : 
 * Windows 10
 * eclipse 2020 - 09
 * JDK 14 
 * Java EE IDE
 * 작성자 : 손준호 
 * 프로그램의 실행결과 
 * : 랜덤버튼을 구현하기 위해 힙을 사용하였습니다.
 * 
 *
 */

public class Heap {
	class DrinkStock {
		public int stock; // 음료의 재고
		public int index; // 음료의 인덱스
		DrinkStock(int index,int stock)
			{
				this.index = index;
				this.stock = stock;
			}
		}
		private ArrayList<DrinkStock> heap;
		
		public Heap() {
			heap = new ArrayList<>();
			heap.add(new DrinkStock(-1 ,1000000)); // 0번 인덱스에 최댓값 삽입
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
		
		public int delete()  // 히프의
		{
			if(heap.size() - 1 < 1) { // 0번 인덱스만 있는 경우이다.
				return -1;
			}
			
			DrinkStock deleteItem = heap.get(1);
			
			heap.set(1,  heap.get(heap.size() - 1)); // 마지막 인덱스의 원소를 제일 처음으로 설정
			heap.remove(heap.size() - 1 );
			

			int pos = 1;
			while( (pos * 2) < heap.size()) 
			{
				
				int max = heap.get(pos * 2).stock; // 왼쪽 자식의 재고
				int maxPos = pos * 2;
				
				// 오른쪽 자식이 있는 경우 오른쪽 자식의 재고와 비교 없을 경우 조건문에 걸리지 않는다.
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
