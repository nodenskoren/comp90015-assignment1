/************************************************************
 * COMP90015 Assignment 1
 * Nodens F. Koren, 1060811
 ************************************************************/

/************************************************************
 * Acknowledgement: The code for the edit distance function
 * is from: https://www.geeksforgeeks.org/edit-distance-dp-5/
 ************************************************************/

package assignment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DictionaryServer {
	private static Map<String, String> dictionary = new HashMap<String, String>();
	private static String directory = "dictionary.txt";
	private static int PORT = 4444;

    static int min(int x, int y, int z)
    {
        if (x <= y && x <= z)
            return x;
        if (y <= x && y <= z)
            return y;
        else
            return z;
    }
 
    static int editDistDP(String str1, String str2, int m,
                          int n)
    {
        // Create a table to store results of subproblems
        int dp[][] = new int[m + 1][n + 1];
 
        // Fill d[][] in bottom up manner
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                // If first string is empty, only option is
                // to insert all characters of second string
                if (i == 0)
                    dp[i][j] = j; // Min. operations = j
 
                // If second string is empty, only option is
                // to remove all characters of second string
                else if (j == 0)
                    dp[i][j] = i; // Min. operations = i
 
                // If last characters are same, ignore last
                // char and recur for remaining string
                else if (str1.charAt(i - 1)
                         == str2.charAt(j - 1))
                    dp[i][j] = dp[i - 1][j - 1];
 
                // If the last character is different,
                // consider all possibilities and find the
                // minimum
                else
                    dp[i][j] = 1
                               + min(dp[i][j - 1], // Insert
                                     dp[i - 1][j], // Remove
                                     dp[i - 1]
                                       [j - 1]); // Replace
            }
        }
 
        return dp[m][n];
    }

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws ClassNotFoundException {
		
		ServerSocket listeningSocket = null;
		//Socket clientSocket = null;
		//loadDict(directory);
		File file = new File(directory);
		try{
			if(!file.exists()){
				file.createNewFile();
				FileOutputStream fileOutputStream = new FileOutputStream(directory);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
				dictionary.put("null", "null");
				objectOutputStream.writeObject(dictionary);
				fileOutputStream.close();
				objectOutputStream.close();
			}
			FileInputStream fileInputStream = new FileInputStream(directory);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			dictionary = (HashMap<String,String>) objectInputStream.readObject();
			fileInputStream.close();
			objectInputStream.close();
		} 
		catch(IOException e) {
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
		
		try {
			if(args.length == 2) {
				if (Integer.parseInt(args[0]) <= 1024 || Integer.parseInt(args[0]) >= 49151) {
					System.out.println("Error: Port number out of bound. Please enter an integer between 1,024 and 49,151!");
					System.exit(-1);
				}
				else {
					PORT = Integer.parseInt(args[0]);
					directory = args[1];
				}
			}
			
			else if(args.length == 0 || args.length == 1 || args.length > 2) {
				System.out.println("Please run in the format of: java - jar DictionaryServer.jar <PORT> <dictionary-directory>.");
				System.exit(-1);				
			}

			//Create a server socket listening on port 4444
			listeningSocket = new ServerSocket(PORT);
			int i = 0; //counter to keep track of the number of clients
			
			
			//Listen for incoming connections for ever 
			while (true) 
			{
				System.out.println("Server listening on port " + PORT + " for a connection");
				//Accept an incoming client connection request 
				Socket clientSocket = listeningSocket.accept(); //This method will block until a connection request is received
				i++;
				System.out.println("Client conection number " + i + " accepted:");
				//System.out.println("Remote Port: " + clientSocket.getPort());
				//System.out.println("Remote Hostname: " + clientSocket.getInetAddress().getHostName());
				//System.out.println("Local Port: " + clientSocket.getLocalPort());
				
				Thread t = new Thread(() -> {
					try {
						clientHandler(clientSocket);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				t.start();
			}
		}
		catch (BindException ex) {
			System.out.println("The address is already in use! \n");
		}
		catch (SocketException ex)
		{
			ex.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Please run in the format of: java - jar DictionaryServer.jar <PORT> <dictionary-directory>.");
		}
		catch (NumberFormatException e) {
			System.out.println("Error: Port number out of bound. Please enter an integer between 1,024 and 49,151!");
		}
		finally
		{
			if(listeningSocket != null)
			{
				try
				{
					listeningSocket.close();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void clientHandler(Socket client) throws ClassNotFoundException
	{
		try(Socket clientSocket = client) {
			InputStream inputStream = clientSocket.getInputStream();

			ObjectInputStream in = new ObjectInputStream(inputStream);

			//Get the input/output streams for reading/writing data from/to the socket
			//BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));


			//Read the message from the client and reply
			//Notice that no other connection can be accepted and processed until the last line of 
			//code of this loop is executed, incoming connections have to wait until the current
			//one is processed unless...we use threads!
			@SuppressWarnings("unchecked")
			ArrayList<String> clientMsg = (ArrayList<String>) in.readObject();
			
			try 
			{
				while(clientMsg != null) 
				{
					String mode = clientMsg.get(0);
					String word = clientMsg.get(1);
					String definition = clientMsg.get(2);
					System.out.println(mode.equals("Search"));
					if(clientMsg.size() != 3) {
						out.write("The message received was dirty!" + "\n");
					}
					else if(mode.equals("Search")) {
						if(dictionary.containsKey(word)){
							out.write(dictionary.get(word) + "\n");
						}
						else {
							String temp = null;
					        for (Map.Entry<String,String> entry : dictionary.entrySet()) {
					            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
					            if((entry.getKey().length() - word.length()) <= 1 || (word.length() - entry.getKey().length()) <= 1) {
					            	if(editDistDP(word, entry.getKey(), word.length(), entry.getKey().length()) <= 1) {
						            	out.write("Your search terms did not match any entries. Do you mean " + entry.getKey() + "?\n");
						            	out.write(dictionary.get(entry.getKey()));
						            	temp = entry.getKey();
						            	break;
					            	}
					            }
					        }
					        if(temp == null) {
								out.write("Your search terms did not match any entries.\n");
					        }
					        
						}
					}
					
					else {
						modifyDictionary(clientMsg, out);
					}

					//System.out.println("TEST: " + mode + " " + word + " " + definition);
					//System.out.println("Message from client " + i + ": " + clientMsg);
					//out.write("Server Ack " + clientMsg + "\n");
					//System.out.println(clientMsg);
					out.flush();
					System.out.println("Response sent");
					clientMsg = null;
				}
			}
			
			catch(SocketException e)
			{
				System.out.println("closed...");
			}
			
			clientSocket.close();
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
	private static synchronized void modifyDictionary(ArrayList<String> clientMsg, BufferedWriter out) throws IOException {
		String mode = clientMsg.get(0);
		String word = clientMsg.get(1);
		String definition = clientMsg.get(2);		
		if(mode.equals("Add")) {
			if(dictionary.containsKey(word)) {
				out.write("Failed to add word. The word is in the dictionary already!\n");
			}
			else {
				if(definition.equals("")) {
					out.write("Failed to add word. Please include a proper definition!\n");								
				}
				else {
					dictionary.put(word, definition);
					out.write("The word has been successfully added to the dictionary.\n");	
					out.newLine();
					out.flush();					
					saveDict(directory, dictionary);					
				}
			}
			
		}
		
		else if(mode.equals("Remove")) {
			if(dictionary.containsKey(word)) {
				dictionary.remove(word);
				out.write("The word has been successfully removed.\n");
				out.newLine();
				out.flush();					
				saveDict(directory, dictionary);				
			}
			else {
				out.write("Failed to remove word. The word does not exist in the dictionary.\n");
			}
			
		}
		
		else if(mode.equals("Update")) {
			if(dictionary.containsKey(word)) {
				if(definition.equals("")) {
					out.write("Failed to update definition. Please include a proper definition!\n");
				}
				else {
					dictionary.replace(word, definition);
					out.write("The definition has been successfully updated.\n");
					out.newLine();
					out.flush();					
					saveDict(directory, dictionary);
				}
			}
			else {
				out.write("Failed to update definition. The word does not exist in the dictionary.\n");
			}
			
		}
		
		else {
			out.write("Error: Unknown Action.\n");
		}
	}
	
	private static void saveDict(String dir, Map<String, String> dict){
		try {
			
			FileOutputStream fileOutputStream = new FileOutputStream(dir);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			
			objectOutputStream.writeObject(dict);	
			fileOutputStream.close();
			objectOutputStream.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}	

}
