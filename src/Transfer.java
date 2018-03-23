package tcptransfer;

import java.io.*;
import java.net.*;
import java.nio.file.FileAlreadyExistsException;

/* Transfer class
 * Handles the transference
 */
public class Transfer implements Runnable {

    private static final long notificationTime = 5000L; // Time between transfer speed notifications
    private static final int timeout = 10000; // Host mode wait-for-connection timeout
    private String[] arguments; // Argument input
    private TcpTransferUI boundUI; // UI when created in interface mode

    // Command mode constructor
    public Transfer() {}
    // Interface mode constructor
    public Transfer(String[] arguments, TcpTransferUI boundUI) {
        this.arguments = arguments;
        this.boundUI = boundUI;
    }

    // Parses arguments and starts transfer process
    public void parseArguments(String[] args)
    throws FileNotFoundException, SocketException, UnknownHostException, IOException {
        File file; // Parse file
        int port; // Transfer port
        InetAddress address = null; // Address to connect to
        boolean isSender = false; // Does this send the file? or receive it

        // Check arguments
        if(args.length < 2 || args.length > 4) argumentError(); // Count args

        // Parse required arguments
        file = new File(args[0]); // Parse file
        port = Integer.parseInt(args[1]); // Parse port

        // Parse optional arguments
        if(args.length > 2) {
            if(args.length > 3) {
                // Check the 4rd argument is '-s'
                if(args[3].equals("-s")) isSender = true;
                else argumentError();
                // Save address
                address = InetAddress.getByName(args[2]);
            }
            else {
                if(args[2].equals("-s")) {
                    isSender = true;
                }
                else {
                    address = InetAddress.getByName(args[2]);
                }
            }
        }

        // Run transfer
        startTransference(file, port, isSender, address);
    }

    // Prints corect argument usage
    static void argumentError() {
        Main.print("Arguments: fileName port [address] [-s]");
        System.exit(0);
    }

    // Transference process
    public void startTransference(File file, int port, boolean isSender, InetAddress address)
    throws FileNotFoundException, SocketException, UnknownHostException, IOException, FileAlreadyExistsException
    {
        // Is in host mode?
        boolean isHost = (address == null);
        // Check if file to be sent exists and is not a directory
        if(isSender && (!file.exists() || file.isDirectory())) throw new FileNotFoundException();
        if(!isSender && file.exists()) throw new FileAlreadyExistsException("Output file alreay exists");
        if(isHost) {
            Main.print("Waiting for incoming connections...");
        }
        else {
            Main.print("Trying to connect...");
        }
        // Connect as client or as host
        Socket socket = isHost ? host(port) : connect(address, port);
        Main.print("Connected!");
        Main.print("Starting transference...");

        // Transference process

        long startTime = System.currentTimeMillis(); // Measure elapsed time

        // Define streams
        Closeable fileStream;
        InputStream in;
        OutputStream out;

        // Set values to streams
        if(isSender) {
            socket.shutdownInput();
            fileStream = new FileInputStream(file);
            in = (FileInputStream)fileStream;
            out = socket.getOutputStream();
        }
        else {
            socket.shutdownOutput();
            fileStream = new FileOutputStream(file);
            in = socket.getInputStream();
            out = (FileOutputStream)fileStream;
        }

        // Pipe input and output streams
        int bufferSize = isSender ? socket.getSendBufferSize() : socket.getReceiveBufferSize();
        pipeStreams(in, out, bufferSize);
        socket.close();
        // Close file stream
        fileStream.close();

        // Calculate elapsed time
        long elapsedTime = System.currentTimeMillis() - startTime;
        long elapsedTimeSeconds = elapsedTime / 1000;
        if (elapsedTimeSeconds == 0) elapsedTimeSeconds = 1;
        Main.print("Transference completed @ " + (file.length() / elapsedTimeSeconds) + " B/s");
    }

    // Returns the socket for the host
    private Socket host(int port) throws IOException {
        // Setup welcome socket
        ServerSocket serverSocket = new ServerSocket(port);
        // Wait for connection
        serverSocket.setSoTimeout(timeout);
        serverSocket.setReuseAddress(true);
        Socket socket = serverSocket.accept();
        // Close welcome socket as it is no longer needed
        serverSocket.close();
        // Return connection socket
        return socket;
    }

    // Returns the socket for the client
    private Socket connect(InetAddress address, int port) throws IOException {
        // Create socket
        Socket socket = new Socket(address, port);
        // Return connection socket
        return socket;
    }

    // Pipe two streams
    private void pipeStreams(InputStream in, OutputStream out, int bufferSize) throws IOException {
        // Create intermediate buffer
        byte[] buffer = new byte[bufferSize];
        // Message notification timer
        long lastNotification = System.currentTimeMillis(); // Measure elapsed time
        // Message notification byte counter
        int transferedBytes = 0;

        // Redirect input stream to output stream
        while(true) {
            int readBytes = in.read(buffer);
            if(readBytes == -1) break;
            out.write(buffer, 0, readBytes);

            // Check for notification timer
            transferedBytes += readBytes;
            if(System.currentTimeMillis() - lastNotification > notificationTime) {
                long elapsedSeconds = (System.currentTimeMillis() - lastNotification) / 1000;
                if(elapsedSeconds == 0) elapsedSeconds = 1;
                Main.print("Transfer rate: " + transferedBytes / elapsedSeconds + " B/s");
                // Reset counter
                lastNotification = System.currentTimeMillis();
                transferedBytes = 0;
            }
        }
    }

    // Threaded run
    public void run() {
        try {
            parseArguments(arguments);
        }
        catch (Exception exception) {
            Main.print(exception.toString());
        }
        finally {
            boundUI.unlock();
        }
    }

}
