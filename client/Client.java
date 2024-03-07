import java.net.*;
import java.io.*;

public class Client {

	private Socket clientSocket;
	private BufferedReader input;
	private PrintWriter output;

	private static void list() {

		return;

	}

	private static void put(String fname) {

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
		
		// Handle put command
		} else if (args.length > 1 && args[0].equals("put")) {

			System.out.println(args[0]);

		} else {

			System.out.println("Incorrect command args. Possible arguments: list, put <fname>");

			System.exit(1);

		}

		Client client = new Client();

		// Connect to the local server on port 80
		// Create input and output streams
		client.connect("localhost", 80);

		// Perform the desired operation
		client.output.print("testing123");

		try {

			// Wait until the server says OK
			while (!(client.input.readLine()).equals("OK")) {

			}

		} catch (IOException ioError) {

			System.out.println(ioError);

			System.exit(1);

		}

		// Disconnect from the local server on port 80
		// Close input and output streams
		client.disconnect();

	}
}