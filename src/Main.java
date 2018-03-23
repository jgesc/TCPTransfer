package tcptransfer;

/* Main class
 * Contains the main() method
 */
public class Main {

    private static boolean enableUI = false;
    private static TcpTransferUI ui;

    private Main() {}; // Static class

    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                Transfer transfer = new Transfer();
                transfer.parseArguments(args);
            }
            catch(Exception exception) {
                exception.printStackTrace();
            }
        }
        else {
            ui = new TcpTransferUI();
            enableUI = true;
        }
    }

    public static void print(String line) {
        if(enableUI) ui.printOutput(line);
        else System.out.println(line);
    }

}
