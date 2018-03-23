# TCPTransfer
This is my first attempt at working with sockets and networking.  
It's a simple direct file sharing tool written in java, which uses TCP sockets to transfer data between computers. It can be used from the command line or UI, if no arguments are specified.

## How it works
First, one computer will be the host of the transference and the other one the client. This doesn't affect to who sends or receives de file. Then, the file is transferred using TCP protocol.  
After configuring the connection, the host will wait for 10 seconds for the client to connect, else it will time out. After connecting, the transference starts, printing the transference speed every 5 seconds.

## Command line
Arguments: fileName port [address] [-s]
 * fileName: the full path of the file to be sent, or where the received file will be stored.
 * port: port to bind the socket to
 * address: *optional*, the IP address the client will connect to. If left unspecified, this will be the host.
 * -s: *optional*, if set, this will be the sender.

## User Interface
It's not pretty, but it works. (It's my first attempt at writing user interfaces in java too)  
![User Interface](/img/ui.png)  

Configuration:
 * File: file to be sent, or received file
 * Address: IP address the client connects to. If this is the host, it is unused.
 * Port: port to send the packets through.
 * Mode: sending or receiving mode
 * Host: mark if this is the host of the connection
 * Transfer: start the file transference
 * Output: console output

## To do
 * Improve UI
 * Allow file drag and drop
 * Allow file selection dialogue
 * File transference progress feedback
 * Handshake and send file information before transference
