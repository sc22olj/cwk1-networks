import java.net.*;
import java.io.*;

public class Client {

	private Socket clientSocket;
	private BufferedReader input;
	private PrintWriter output;

	private static void list() {

		Client client = new Client();

			// Connect to the local server on port 80
			// Create input and output streams
			client.connect("localhost", 9500);

			try {

				client.output.println("LIST");

				client.output.println("TERMINATE");
	
				String inputString;
	
				while ((inputString = client.input.readLine()) != null) {

					if (inputString.equals("TERMINATE")) {
	
						break;

					}

					System.out.println(inputString);
	
				}
	
			} catch (IOException ioError) {
	
				System.out.println(ioError);
	
				System.exit(1);
	
			}

			client.disconnect();

		return;

	}

	private static void put(String fname) {

		Client client = new Client();

			// Connect to the local server on port 80
			// Create input and output streams
			client.connect("localhost", 9500);

			// Create new file object
			File file = new File(fname);

			try {

				// Buffered reader to read the file into the program
				BufferedReader fileReader = new BufferedReader(new FileReader(file));

				client.output.println("PUT");

				client.output.println(fname);

				// Read the file into the program and send it off line by line
				String line;

				while((line = fileReader.readLine()) != null) {

					client.output.println(line);

				}

				client.output.println("TERMINATE");

				// Read any responses from the server (i.e error messages)
				String inputString;

				while ((inputString = client.input.readLine()) != null) {

					if (inputString.equals("TERMINATE")) {
	
						break;

					}

					System.out.println(inputString);
	
				}

				fileReader.close();

			} catch (FileNotFoundException fileError) {

				System.out.println("File does not exist locally");

			} catch (IOException ioError) {

				System.out.println(ioError);

			}

			client.disconnect();

		return;
	}

	public void connect(String hostname, int port) {

		try {

			// Create a socket which connects to the server's port
			// Client port is assigned by the OS
			clientSocket = new Socket(hostname, port);

			// Create buffered reader input
			input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			// Create printwriter output
			// Autoflush on
			output = new PrintWriter(clientSocket.getOutputStream(), true);

		} catch (UnknownHostException hostError) {

			System.out.println(hostError);

			System.exit(1);

		} catch (IOException ioError) {

			System.out.println(ioError);

			System.exit(1);

		} 

		return;
	}
	
	public void disconnect() {

		try {

			input.close();

			output.close();

			clientSocket.close();

		} catch (IOException IOerror) {

			System.out.println(IOerror);

			System.exit(1);
			
		}

		return;
	}

	public static void main( String[] args ) {

		// Handle command line args
		// Handle list command
		if (args.length == 1 && args[0].equals("list")) {

			System.out.println(args[0]);

			list();
		
		// Handle put command
		} else if (args.length == 2 && args[0].equals("put")) {

			System.out.println(args[0]);

			put(args[1]);

		} else {

			System.out.println("Incorrect command args. Possible arguments: list, put <fname>");

			System.exit(1);

		}

	}

}