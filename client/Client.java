import java.net.*;
import java.io.*;

public class Client {

	private Socket serverSocket;
	private BufferedReader input;
	private PrintWriter output;

	public static void main(String[] args) {

		// Handle command line args
		// Handle list command
		if (args.length == 1 && args[0].equals("list")) {

			list();

			// Handle put command
		} else if (args.length == 2 && args[0].equals("put")) {

			put(args[1]);

		} else {

			System.out.println("Incorrect command args. Possible arguments: list, put <fname>");

			System.exit(1);

		}

		return;

	}

	private static void list() {

		Client client = new Client();

		// Connect to the local server on port 9500
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

		// Create a client to connect to the server
		Client client = new Client();

		// Connect to the local server on port 9500
		// Create input and output streams
		client.connect("localhost", 9500);

		// Create new file object
		File file = new File(fname);

		try {

			// Buffered reader to read the file into the program
			BufferedReader fileReader = new BufferedReader(new FileReader(file));

			// Tell the server the command and filename
			client.output.println("PUT");

			client.output.println(fname);

			// Read the file into the program and send it off line by line
			String line;

			while ((line = fileReader.readLine()) != null) {

				client.output.println(line);

			}

			// Tell the server that it is finished
			client.output.println("TERMINATE");

			// Read any responses from the server (i.e error messages)
			// Print out these messages
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
			serverSocket = new Socket(hostname, port);

			// Create buffered reader input
			input = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

			// Create printwriter output
			// Autoflush on
			output = new PrintWriter(serverSocket.getOutputStream(), true);

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

		// Close resources to prevent leaks
		try {

			if (input != null)
				input.close();

			if (output != null)
				output.close();

			if (serverSocket != null)
				serverSocket.close();

		} catch (IOException IOerror) {

			System.out.println(IOerror);

			System.exit(1);

		}

		return;
	}

}