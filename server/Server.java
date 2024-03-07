import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
		server.runServer(80);

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

			try {

				// Read and print what was sent from the client
				System.out.println(input.readLine());

				// Send OK message back
				output.print("OK");

			} catch (IOException ioError) {

				System.out.println(ioError);

				System.exit(1);

			}

			return;

		}
	
	}

}