/*
 * Project 1: Myperf
 * COSC 465, Computer Networks, Spring 2017
 * Authors: Laura Bunn, Bryan Dewan, and Leslie Subaldo
 */
import java.io.*;
import java.net.*;

public class Myperf {
    public static void client(String args[]) {
    	// Client mode must be invoked as follows:
    	// java Myperf -c -h <server_hostname> -p <server_port> -t <time>
    	System.out.println("Welcome to client mode");

    	//Check the arguments for validity
    	//Parse them out so we can utilize them, if they are valid
    	if (args.length != 7) {
    		System.out.println("Error: missing or additional arguments");
    		return;
    	}
    	String hostName = args[2];
		  int portNumber = Integer.parseInt(args[4]);
		  if (portNumber < 1024 || portNumber > 65535) {
			     System.out.println("Error: port number must be in the range 1024 to 65535");
			        return;
		  }
		  long givenTime = Long.parseLong(args[6]);
		  System.out.println("All arguments look good");
		  System.out.println("Host name: " + hostName + " Port number: " + portNumber + " Time: " + givenTime);

		  // Make the socket and run the client code
		  try {
		    Socket clientSocket = new Socket(hostName, portNumber);
	      	OutputStream out = clientSocket.getOutputStream();
			
			// Make the byte array and instantiate throughput metrics
	      	byte[] dummyKB = new byte[1000];
	      	int numOfKBSent = 0;
			//Do some calculations to compute time
			long givenTimeInMillis = givenTime*1000;
			long startTime = System.currentTimeMillis();
			long endTime = startTime + givenTimeInMillis;
			System.out.println("start time: " + startTime + " end time: " + endTime);
	        while (System.currentTimeMillis() <= endTime) {
            	out.write(dummyKB);
				numOfKBSent++;
        	}
	      //Calculate sent and throughput and print them out
	      	int megabits = numOfKBSent/125;
	      	long mbps = megabits/givenTime;
	      	System.out.println("sent=" + numOfKBSent + " KB " + "throughput=" + mbps + " Mbps");
	      	out.close();
			clientSocket.close();
	    } catch (UnknownHostException e) {
	          System.err.println("Don't know about host " + hostName);
	          System.exit(1);
	    } catch (IOException e) {
	          System.err.println(e + " Couldn't get I/O for the connection to " + hostName);
	          System.err.println(e);
	          System.exit(1);
	        }
	 }

    public static void server(String args[]) {
		//Server mode must be invoked as follows:
		//java Myperf -s -p <listen_port>
    	System.out.println("Welcome to server mode");

    	//Checking arguments for validity
    	if (args.length != 3) {
    		System.out.println("Error: missing or additional arguments");
    	}
    	else {
    		int listenPort = Integer.parseInt(args[2]);
    		if (listenPort < 1024 || listenPort > 65535) {
    			System.out.println("Error: port number must be in the range 1024 to 65535");
				return;
    		}
			System.out.println("All arguments look good");
			//Create server socket
	      	try {
		      	ServerSocket serverSocket = new ServerSocket(listenPort);
				//server socket waiting/listening for client socket connection
		      	Socket clientSocket = serverSocket.accept();
		      	PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		      	InputStream in = clientSocket.getInputStream();

				//Reading data
		      	int inputCount;
		      	int numOfBytesReceived = 0;
		      	long startTime = System.currentTimeMillis();
				System.out.println(startTime);
						byte bytes[] = new byte[1000];
				while ((inputCount = in.read(bytes, 0, 1000)) != -1) {
					numOfBytesReceived += inputCount;
				}

				//Calculating throughput
				long endTime = System.currentTimeMillis();
				System.out.println(endTime);
				long totalTime = endTime - startTime;
				totalTime = totalTime/1000; //time in seconds
				int megabits = numOfBytesReceived/125000;
				long mbps = megabits/totalTime;
				System.out.println("received=" + numOfBytesReceived/1000 + " KB " + "throughput=" + mbps + "Mbps");

				//Closing input and output streams and both sockets
				in.close();
				out.close();
				clientSocket.close();
				serverSocket.close();

				//Catching exceptions
			 } catch (IOException e) {
			      System.err.println("Couldn't get I/O for the connection");
		        System.exit(1);
			}
		}
	  }
    public static void main(String args[]) {
        System.out.println("Myperf is running");
        if (args[0].equals("-c")){
        	client(args);
        }
        else if (args[0].equals("-s")) {
        	server(args);
        }
    }
  }
