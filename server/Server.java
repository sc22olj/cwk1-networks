import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {

	private ServerSocket serverSocket;
	private ExecutorService service;
	private Log serverLog;

	public void runServer(int port) {

		// Create 20 thread executor pool
		service = Executors.newFixedThreadPool(20);

		// Create a new log file
		serverLog = new Log();

		try {

			// Create serversocket on port 9500
			serverSocket = new ServerSocket(port);

			// Infinite loop for running server
			while (true) {

				// Listen for a client trying to connect
				Socket clientSocket = serverSocket.accept();

				// Pass any client that is found into the executor service
				service.submit(new ClientHandler(clientSocket));

			}

		} catch (IOException ioError) {

			System.out.println(ioError);

			System.exit(1);

		}

	}

	public static void main(String[] args) {

		// Create and run a server on port 9500
		Server server = new Server();

		server.runServer(9500);

	}

	// Runnable class, objects of which handle IO streams with clients
	private class ClientHandler implements Runnable {

		private Socket clientSocket;
		private String clientIP;
		private BufferedReader input;
		private PrintWriter output;

		// Constructor to allow client handler to store the client's socket
		public ClientHandler(Socket clientSocket) {

			this.clientSocket = clientSocket;

		}

		@Override
		public void run() {

			try {

				// Create a string for the client IP
				clientIP = InetAddress.getLocalHost().getHostAddress();

				// Set up IO streams
				// Create buffered reader input
				input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

				// Create printwriter output
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

				// Run corresponding command
				if (firstLine.equals("LIST")) {

					list();

				} else if (firstLine.equals("PUT")) {

					// Make sure that the file was created
					if (!put()) {

						output.println("File already exists on server");

						return;

					}

				} else {

					output.println("Invalid command");

					return;

				}

				// Store the request in the log file
				serverLog.writeLogToFile(clientIP, firstLine.toLowerCase());

			} catch (IOException ioError) {

				System.out.println(ioError);

			} finally {

				// Tell client that output (if any)
				output.println("TERMINATE");

				// Close resources to prevent leaks
				try {

					if (input != null)
						input.close();

					if (output != null)
						output.close();

					if (clientSocket != null)
						clientSocket.close();

				} catch (IOException ioError) {

					System.out.println(ioError);

				}

			}

			return;
		}

		// Reads the files in the serverFiles directory
		// Send the file names to the client
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

		// Handles recieving a file from a client
		// Returns false if the file sent by the client already exists in the server
		private Boolean put() {

			String inputLine;

			try {

				// Read next line which should be filename
				File file = new File(input.readLine());

				// Create the file
				// Check it doesn't already exist
				if (!file.createNewFile()) {

					return false;

				}

				// Write to the file
				PrintWriter fileWriter = new PrintWriter(file);

				String previousLine = null;

				while ((inputLine = input.readLine()) != null) {

					if (inputLine.equals("TERMINATE")) {

						break;

					}

					// Print previous line so EOF newline can be avoided
					if (previousLine != null) {

						fileWriter.println(previousLine);

					}

					previousLine = inputLine;

				}

				// Final line is printed without newline
				if (previousLine != null) {

					fileWriter.print(previousLine);

				}

				fileWriter.close();

			} catch (IOException ioError) {

				System.out.println(ioError);

			}

			return true;

		}

	}

}