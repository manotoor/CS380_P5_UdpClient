//Manvinder Toor
//Udp client

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class UdpClient{
	public static void main(String[] args){
		try(Socket socket = new Socket("18.221.102.182", 38005)){
			System.out.println("Connected to Server.");
			Inputstream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			//Get destination address (should be 18,221,102,182)
			byte[] destinationAddress = socket.getInetAddress().getAddress();
			//Send handshake message (hard code dead beef IPv4 and protocol UDP)
			//get response
			//if response good we are still connected so read new port
			//send 12 packets
				//Create Udp Packet
				//Calculate RTT
				//Read server response
			//Get average RTT of all responses

		}
		System.out.println("Disconnected from Server.");
		catch(Exception e){
			System.out.println("Sorry something went wrong!");
		}
	}
	//Handshake function
	//Add checksum function
	//add IPv4 function
	//Pseudo header function
	//UDP Packet function
}
