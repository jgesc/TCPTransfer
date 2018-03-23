package tcptransfer;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;

/* TcpTransferUI class
 * Creates an interface to configure the transference
 */
public class TcpTransferUI {

    //Components
    private Label fileLabel, addressLabel, portLabel, hostLabel, modeLabel, outputLabel;
    private TextField fileField, addressField, portField;
    private CheckboxGroup modeGroup;
    private Checkbox sendCheckbox, receiveCheckbox, hostCheckbox;
    private Button transferButton;
    private TextArea outputArea;

    public TcpTransferUI() {
        // Main frame
        Frame mainFrame = new Frame("TCPTransfer");
        mainFrame.setLayout(null);
        mainFrame.setSize(300, 300);
        mainFrame.setVisible(true);
        mainFrame.setResizable(false);

        // Get inets to calculate positions
        Insets insets = mainFrame.getInsets();

        // Register closing method
        mainFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    System.exit(0);
                 }
             }
        );

        // File label
        fileLabel = new Label("File:");
        fileLabel.setBounds(insets.left, insets.top, 300 - insets.right - insets.left, 20);
        mainFrame.add(fileLabel);

        // File field
        fileField = new TextField();
        fileField.setBounds(insets.left, insets.top + 20, 300 - insets.right - insets.left, 20);
        mainFrame.add(fileField);

        // Address label
        addressLabel = new Label("Address:");
        addressLabel.setBounds(insets.left, insets.top + 40, 250 - insets.left, 20);
        mainFrame.add(addressLabel);

        // Address field
        addressField = new TextField();
        addressField.setBounds(insets.left, insets.top + 60, 250 - insets.left, 20);
        mainFrame.add(addressField);

        // Port label
        portLabel = new Label("Port:");
        portLabel.setBounds(250, insets.top + 40, 50, 20);
        mainFrame.add(portLabel);

        // Port field
        portField = new TextField();
        portField.setBounds(250, insets.top + 60, 45, 20);
        mainFrame.add(portField);

        // Mode label
        modeLabel = new Label("Mode:");
        modeLabel.setBounds(insets.left, insets.top + 90, 200 - insets.left, 20);
        mainFrame.add(modeLabel);

        // Host label
        hostLabel = new Label("Is host?:");
        hostLabel.setBounds(200, insets.top + 90, 100, 20);
        mainFrame.add(hostLabel);

        // Mode checkboxes
        modeGroup = new CheckboxGroup();
        sendCheckbox = new Checkbox("Send", modeGroup, true);
        receiveCheckbox = new Checkbox("Receive", modeGroup, false);
        sendCheckbox.setBounds(insets.left, insets.top + 100, 100 - insets.left, 40);
        receiveCheckbox.setBounds(100, insets.top + 100, 100, 40);
        mainFrame.add(sendCheckbox);
        mainFrame.add(receiveCheckbox);

        // Host checkbox
        hostCheckbox = new Checkbox("Host", false);
        hostCheckbox.setBounds(200, insets.top + 100, 100, 40);
        mainFrame.add(hostCheckbox);

        // Host checkbox event register
        hostCheckbox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    addressField.setEnabled(!hostCheckbox.getState());
                    addressField.setEditable(!hostCheckbox.getState());
                 }
             }
        );

        // Transfer button
        transferButton = new Button("Transfer");
        transferButton.setBounds(insets.left, insets.top + 140, 300 - insets.right - insets.left, 20);
        mainFrame.add(transferButton);

        // Output label
        outputLabel = new Label("Output:");
        outputLabel.setBounds(insets.left, insets.top + 160, 300 - insets.right - insets.left, 20);
        mainFrame.add(outputLabel);

        // Output area
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setBounds(insets.left, insets.top + 180, 300 - insets.right - insets.left, 300 - insets.top - 180 - insets.bottom);
        mainFrame.add(outputArea);

        // Register button event
        transferButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    int argumentCount = 2;
                    String file = fileField.getText();
                    String address = addressField.getText();
                    String port = portField.getText();
                    boolean isSender = sendCheckbox.getState();
                    boolean isHost = hostCheckbox.getState();

                    if(isSender) argumentCount++;
                    if(!isHost) argumentCount++;

                    String[] arguments = new String[argumentCount];
                    arguments[0] = file;
                    arguments[1] = port;
                    if(argumentCount > 2) arguments[2] = isHost ? "-s" : address;
                    if(argumentCount > 3) arguments[3] = "-s";

                    Transfer transfer = new Transfer(arguments, TcpTransferUI.this);
                    lock();
                    new Thread(transfer).start();
                }
            }
        );
    }

    // Print line to the UI console
    public void printOutput(String output) {
        outputArea.append(output + "\n");
    }

    // Lock the interface while transfering
    private void lock() {
        fileField.setEnabled(false);
        addressField.setEnabled(false);
        portField.setEnabled(false);
        hostCheckbox.setEnabled(false);
        sendCheckbox.setEnabled(false);
        receiveCheckbox.setEnabled(false);
        transferButton.setEnabled(false);
    }

    // Unlock the interface after transmission
    public void unlock() {
        fileField.setEnabled(true);
        addressField.setEnabled(!hostCheckbox.getState());
        portField.setEnabled(true);
        hostCheckbox.setEnabled(true);
        sendCheckbox.setEnabled(true);
        receiveCheckbox.setEnabled(true);
        transferButton.setEnabled(true);
    }
}
