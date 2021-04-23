/************************************** 
 * COMP90015 Assignment 1
 * Nodens F. Koren, 1060811
 **************************************/

package assignment;

import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JComboBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JTextArea;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.*;
import javax.swing.JLabel;

public class DictionaryClient {

	private JFrame frmDictionary;
	private static int PORT = 4444;
	private static String ADDRESS = "localhost";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		if(args.length == 2) {
			if (Integer.parseInt(args[1]) <= 1024 || Integer.parseInt(args[1]) >= 49151) {
				System.out.println("Error: Port number out of bound. Please enter an integer between 1,024 and 49,151!");
				System.exit(-1);
			}
			else {
				PORT = Integer.parseInt(args[1]);
				ADDRESS = args[0];
			}
		}
		
		else if(args.length == 0 || args.length == 1 || args.length > 2) {
			System.out.println("Please run in the format of: java - jar DictClient.jar <ADDRESS> <PORT>.");
			System.exit(-1);				
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DictionaryClient window = new DictionaryClient();
					window.frmDictionary.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DictionaryClient() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDictionary = new JFrame();
		frmDictionary.setTitle("Dictionary");
		frmDictionary.setBounds(100, 100, 488, 505);
		frmDictionary.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDictionary.getContentPane().setLayout(null);
		
	    String[] choices = { "Search", "Add", "Remove", "Update"};
	    
		JComboBox comboBox = new JComboBox(choices);
		JComboBox<String> comboBox_1 = new JComboBox();
		
		comboBox_1.setEditable(true);
		
		comboBox.setBounds(250, 36, 102, 33);
		frmDictionary.getContentPane().add(comboBox);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(10, 223, 454, 235);
		frmDictionary.getContentPane().add(textArea);
		
		JEditorPane editorPane_1 = new JEditorPane();
		editorPane_1.setBounds(10, 120, 454, 48);
		frmDictionary.getContentPane().add(editorPane_1);
		
		JButton btnNewButton = new JButton("Submit");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
                ArrayList<String> titleList = new ArrayList<String>();
                titleList.add(comboBox.getSelectedItem().toString());
                if(comboBox_1.getSelectedItem() == null) {
                    titleList.add("");
                }
                else {
                    titleList.add(comboBox_1.getSelectedItem().toString());                	
                }
                titleList.add(editorPane_1.getText().toString());

                if(!titleList.get(1).equals("")) {
	            
		    		Socket socket = null;
		    		try 
		    		{
		    			// Create a stream socket bounded to any port and connect it to the
		    			// socket bound to localhost on port 4444
		    			socket = new Socket(ADDRESS, PORT);
		    			System.out.println("Connection established");
	
		    			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		    			out.writeObject(titleList);
		    				
	
		    			// Get the input/output streams for reading/writing data from/to the socket
		    			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		    			out.flush();
		    			System.out.println("Message sent");
		    				
		    			// Receive the reply from the server by reading from the socket input stream
		    			String line = null;
		    			String received = "";
		    			while ((line = in.readLine()) != null) {
		    				received = received + line + "\n\n";
		    			}

			    		textArea.setText(received);

		    			comboBox_1.insertItemAt(titleList.get(1), 0);
		    		} 
		    		catch (ConnectException f) {
		    		    // host and port combination not valid
		    			System.out.println("Error: please connect to a valid port!");
		    		}
		    		catch (UnknownHostException f)
		    		{
		    			System.out.println("Error: unknown host!");	    			
		    		}
		    		catch (IOException f)
		    		{
		    			f.printStackTrace();
		    		}	    		
		    		finally
		    		{
		    			// Close the socket
		    			if (socket != null)
		    			{
		    				try
		    				{
		    					socket.close();
		    				}
		    				catch (IOException f) 
		    				{
		    					f.printStackTrace();
		    				}
		    			}
		    		}
                }
			}
		});
		btnNewButton.setBounds(362, 36, 102, 33);
		frmDictionary.getContentPane().add(btnNewButton);

		
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		
		comboBox_1.setBounds(10, 36, 230, 33);
		frmDictionary.getContentPane().add(comboBox_1);
		
		JLabel lblNewLabel = new JLabel("Enter (or choose from history) the word you want to search, add, remove, or update.");
		lblNewLabel.setBounds(10, 10, 416, 20);
		frmDictionary.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Type in the definition.");
		lblNewLabel_1.setBounds(10, 97, 416, 20);
		frmDictionary.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Search results.");
		lblNewLabel_2.setBounds(10, 200, 454, 20);
		frmDictionary.getContentPane().add(lblNewLabel_2);

	}
}
