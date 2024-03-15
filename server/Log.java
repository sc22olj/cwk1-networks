import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

// Class which creates a log file
public class Log {

    FileWriter logFileWriter;

    Log() {

        try {

            logFileWriter = new FileWriter("log.txt", false);

            logFileWriter.close();

        } catch (IOException ioError) {

            System.out.println(ioError);

        }

    }

    // Returns a string in the specified format
    public String logToString(String clientIP, String request) {
        
        LocalDateTime currentDateTime = LocalDateTime.now();

        String date = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String time = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        return date + "|" + time + "|" + clientIP + "|" + request;
    }

    // Writes a string to the log file by opening and appending it
    public void writeLogToFile(String clientIP, String request) {

        try {

            logFileWriter = new FileWriter("log.txt", true);

            logFileWriter.write(logToString(clientIP, request) + "\n");

            logFileWriter.close();

        } catch (IOException ioError) {

            System.out.println(ioError);

        }

    }

}
