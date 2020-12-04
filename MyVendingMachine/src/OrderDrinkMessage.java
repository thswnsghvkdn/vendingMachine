import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class OrderDrinkMessage extends JFrame {

	public JLabel Title;
	public JButton submit;
	public JComboBox comboBox;
	private JPanel contentPane;
	private JLabel Index;
	public JTextField nameField;
	public JTextField priceField;
	private JLabel Title_1;
	public JComboBox comboBox_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//OrderDrinkMessage frame = new OrderDrinkMessage();
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
	public OrderDrinkMessage() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 331);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		Title = new JLabel("\uC8FC\uBB38\uD558\uC2E4 \uC74C\uB8CC\uC758 \uC815\uBCF4\uB97C \uC785\uB825\uD574\uC8FC\uC138\uC694");
		Title.setFont(new Font("굴림", Font.BOLD, 16));
		Title.setBounds(79, 20, 296, 37);
		contentPane.add(Title);
		
		Index = new JLabel("  \uC8FC\uBB38\uD558\uC2E4  \uC74C\uB8CC\uC704\uCE58");
		Index.setBounds(8, 67, 121, 37);
		contentPane.add(Index);
		
		JLabel Title_2 = new JLabel("\uC8FC\uBB38\uD558\uC2E4 \uC74C\uB8CC\uC774\uB984");
		Title_2.setBounds(155, 67, 107, 37);
		contentPane.add(Title_2);
		
		JLabel Title_3 = new JLabel("\uC8FC\uBB38\uD558\uC2E4 \uC74C\uB8CC\uAC00\uACA9");
		Title_3.setBounds(292, 67, 107, 37);
		contentPane.add(Title_3);
		
		nameField = new JTextField();
		nameField.setBounds(150, 128, 116, 21);
		contentPane.add(nameField);
		nameField.setColumns(10);
		
		priceField = new JTextField();
		priceField.setColumns(10);
		priceField.setBounds(287, 128, 116, 21);
		contentPane.add(priceField);
		
		submit = new JButton("\uC81C\uCD9C");

		submit.setBounds(155, 243, 97, 23);
		contentPane.add(submit);
		
		comboBox = new JComboBox();
		comboBox.setBounds(31, 127, 80, 23);
		contentPane.add(comboBox);
		
		Title_1 = new JLabel("\uC8FC\uBB38\uD558\uC2E4 \uC74C\uB8CC \uC218\uB7C9");
		Title_1.setBounds(76, 177, 107, 37);
		contentPane.add(Title_1);
		
		comboBox_1 = new JComboBox();
		comboBox_1.setBounds(227, 184, 80, 23);
		contentPane.add(comboBox_1);
		comboBox_1.addItem("10개");
		comboBox_1.addItem("20개");
		comboBox_1.addItem("30개");
		comboBox_1.addItem("40개");
		comboBox_1.addItem("50개");
	}
}
