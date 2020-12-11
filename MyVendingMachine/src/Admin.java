import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.List;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;


/* ���α׷� �� : admin
 * 1���� main �Լ�
 * 3���� Ŭ����
 * ���α׷��� ����ȯ�� : 
 * Windows 10
 * eclipse 2020 - 09
 * JDK 14 
 * Java EE IDE
 * �ۼ��� : ����ȣ 
 * ���α׷��� ������ 
 * : ������ ������ �κ� 
 * �ֿ� ��ɵ��� ���Ǳ��� ������ ���� �Ǳ⿡ ���Ǳ� �κп��� ������ �Ͽ����ϴ�.
 *
 */


public class Admin extends JFrame {
	public JButton stockBtn;
	public JButton incomeBtn;
	public JButton collectBtn;
	public JButton orderBtn;
	public JButton fillBtn;
	public JButton changeBtn;
	public List list = new List();

	
	private DataInputStream dataIn; // �Է� ��Ʈ��
	private DataOutputStream dataOut; // ��� ��Ʈ��
	String userId;
	private JPanel contentPane;
	public JLabel greeting;
	public JButton incomeBtn2;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//Admin frame = new Admin();
					//frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Admin() {
		


		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 464, 528);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		list.setBounds(30, 66, 240, 347);
		contentPane.add(list);
		
		stockBtn = new JButton("\uC7AC\uACE0 \uD655\uC778");
		stockBtn.setBounds(297, 66, 124, 41);
		contentPane.add(stockBtn);
		
		incomeBtn = new JButton("\uC77C\uB9E4\uCD9C \uD655\uC778");
		incomeBtn.setBounds(297, 117, 124, 41);
		contentPane.add(incomeBtn);
		
		collectBtn = new JButton("\uC218\uAE08");
		collectBtn.setBounds(297, 219, 124, 41);
		contentPane.add(collectBtn);
		
		orderBtn = new JButton("\uC74C\uB8CC \uC8FC\uBB38");
		orderBtn.setBounds(297, 270, 124, 41);
		contentPane.add(orderBtn);
		
		fillBtn = new JButton("\uC794\uB3C8 \uBCF4\uCDA9");
		fillBtn.setBounds(297, 321, 124, 41);
		contentPane.add(fillBtn);
		
		changeBtn = new JButton("\uBE44\uBC00\uBC88\uD638 \uBCC0\uACBD");
		changeBtn.setBounds(297, 372, 124, 41);
		contentPane.add(changeBtn);

		
		greeting = new JLabel("greeting");
		greeting.setBounds(30, 10, 240, 31);
		contentPane.add(greeting);
		
		incomeBtn2 = new JButton("\uC6D4\uBCC4\uB9E4\uCD9C \uD655\uC778");

		incomeBtn2.setBounds(297, 168, 124, 41);
		contentPane.add(incomeBtn2);
		
	
	}

}
