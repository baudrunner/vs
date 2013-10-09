package testMichel;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextPane;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.GridLayout;

import javax.swing.JCheckBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;

import javax.swing.JTextArea;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ClientGui {

	static MessageServiceClient msgClient;
	
	final static JTextArea txtrScrolltext = new JTextArea();
	
	private JFrame frame;
	private JTextField sendMessageField;
	private  AutoGetMessage autoGetRunnableObj;

	/**
	 * Launch the application.
	 * 
	 * @throws NotBoundException
	 * @throws RemoteException
	 */
	public static void main(String[] args) throws RemoteException,
			NotBoundException {
		try {
			msgClient = new MessageServiceClient();
			System.out.println(msgClient);
		} catch (NotBoundException e) {
			System.err.println("Couldn't find remote object in registry");
			e.printStackTrace();
		} catch (RemoteException e) {
			System.err.println("Error while Communicating with RMI-Server");
			System.err.println();
		}

		ClientGui client = new ClientGui();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGui window = new ClientGui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the application.
	 */
	public ClientGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 737, 461);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		sendMessageField = new JTextField();
		sendMessageField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				
				if(e.getKeyChar() == '\n'){
					msgClient.sendMessage(sendMessageField.getText());
					sendMessageField.setText(null);
				}
				
			}
		});
		sendMessageField.setBounds(129, 252, 461, 19);
		frame.getContentPane().add(sendMessageField);
		sendMessageField.setColumns(50);
		
		autoGetRunnableObj = new AutoGetMessage(msgClient);
		

		JButton btnSend = new JButton("send");
		btnSend.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				msgClient.sendMessage(sendMessageField.getText());
			}
		});
		btnSend.setBounds(609, 250, 69, 22);
		frame.getContentPane().add(btnSend);

		JLabel lblClient = new JLabel("Client");
		lblClient.setBounds(346, 12, 70, 15);
		frame.getContentPane().add(lblClient);

		JLabel lblMessages = new JLabel("Messages:");
		lblMessages.setBounds(30, 254, 81, 15);
		frame.getContentPane().add(lblMessages);

		JButton btnGetmessage = new JButton("getNextMessage");
		btnGetmessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setText(msgClient.getMessage());
			}
		});
		btnGetmessage.setBounds(524, 299, 154, 22);
		frame.getContentPane().add(btnGetmessage);

		final JCheckBox chckbxNewCheckBox = new JCheckBox("getAllMessages");
		chckbxNewCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (chckbxNewCheckBox.isSelected()) {
					Thread messageThread = new Thread(autoGetRunnableObj);
					autoGetRunnableObj.setRunning(true);
					messageThread.start();					
					System.out.println("automatisches Abholen der Nachrrichten");
					
				} else {
					autoGetRunnableObj.setRunning(false);
				}

			}
		});
		chckbxNewCheckBox.setBounds(524, 329, 141, 23);
		frame.getContentPane().add(chckbxNewCheckBox);
		
	
		txtrScrolltext.setEditable(false);
		txtrScrolltext.setBounds(30, 52, 200, 83);
		JScrollPane scrollPane = new JScrollPane (txtrScrolltext, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(30, 52, 648, 175);
		frame.getContentPane().add(scrollPane);
		
		
	}
	
	
	public static void setText(String text){
		if(text != null){
			txtrScrolltext.setText(txtrScrolltext.getText() + System.getProperty("line.separator") + text);
			txtrScrolltext.setCaretPosition((txtrScrolltext.getDocument().getLength()));
		}
	}
}




