import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {

	private ServerSocket serverSocket;
	private ExecutorService service;

	public void runServer(int port) {

		// Create 20 thread executor pool
		service = Executors.newFixedThreadPool(20);

		try {

			// Create serversocket on port
			serverSocket = new ServerSocket(port);
			
			// Infinite loop for running server
			while (true) {

				// Listen for a client trying to connect
				Socket clientSocket = serverSocket.accept();

				// Pass any client that is found into the executor service
				service.submit(new ClientHandler(clientSocket));

			}

		} catch (IOException IOerror) {

			System.out.println(IOerror);

			System.exit(1);

		}

	}

	public static void main( String[] args ) {

		Server server = new Server();

		// Create an executor server on port 80
		server.runServer(9500);

	}

	// Runnable class, objects of which handle IO streams with clients
	private class ClientHandler implements Runnable {

		private Socket clientSocket;
		private BufferedReader input;
		private PrintWriter output;

		// Constructor to allow client handler to store the client's socket
		public ClientHandler(Socket clientSocket) {
			
			this.clientSocket = clientSocket;

		}

		@Override
		public void run() {

			System.out.println("Server: found host");

			try {
				// Set up IO streams
				// Create buffered reader input
				input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
				
				// Create printwriter output
				// Autoflush on
				output = new PrintWriter(clientSocket.getOutputStream(), true);
			
			} catch (IOException ioError) {

				System.out.println(ioError);

				System.exit(1);

			}

			handleRequest();
	
		}

		// Handle the commands that come from the client
		private void handleRequest() {

			String firstLine;

			try {

				// Read first line to check the command type
				firstLine = input.readLine();

				if (firstLine.equals("LIST")) list();

				if (firstLine.equals("PUT")) put();

				output.println("TERMINATE");

			} catch (IOException ioError) {

				System.out.println(ioError);

			} finally {

				// Close resources to prevent leaks
				try {

					if (input != null) input.close();

					if (output != null) output.close();

					if (clientSocket != null) clientSocket.close();

				} catch (IOException ioError) {

					System.out.println(ioError);

				
				}

			}

			return;
		}

		// Handle the list command
		private void list() {

			// Specify the directory
			File directory = new File("./serverFiles");

			// Get all files in the directory
			File[] files = directory.listFiles();
	
			// Print out each file
			for (File file : files) {

				output.println(file.getName());

			}

			return;

		}

		// Handle the put command
		private void put() {

			String inputLine;

			try {

				// Read next line which should be filename
				File file = new File(input.readLine());

				if (!file.createNewFile()) {

					output.println("File already exists");

				}

				PrintWriter fileWriter = new PrintWriter(file);

				while ((inputLine = input.readLine()) != null) {

					if (inputLine.equals("TERMINATE")) {
	
						break;
		
					}

					fileWriter.println(inputLine);
	
				}

				fileWriter.close();

			} catch (IOException ioError) {

				System.out.println(ioError);

			}

		}
	
	}

}