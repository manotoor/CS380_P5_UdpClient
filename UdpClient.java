//Manvinder Toor
//Udp client

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class UdpClient{
	public static void main(String[] args){
		try(Socket socket = new Socket("18.221.102.182", 38005)){
			System.out.println("Connected to Server.");

			//Variables
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			byte[] dAdrs = null;
			byte[] handshake = null;
			byte[] rCode = new byte[4];
			byte[] port = new byte[2];

			//Get destination address (should be 18,221,102,182)
			dAdrs = socket.getInetAddress().getAddress();

			//Send handshake message (hard code dead beef IPv4 and protocol UDP)
			handshake = handshake(destinationAddress);
			os.write(handshake);

			//get response code (if it's bad we disconnect)
			is.read(rCode);
			System.out.print("Handshake response: 0x");
			for (int i = 0; i < rCode.length; i++) {
				System.out.printf("%02X", rCode[i]);
			}
			//Get port number after response code
			is.read(port);
			System.out.print("Port number received: ");
			short port = (short)((short)portNumber[0] << 8 | (short)portNumber[1]);
			System.out.print(Short.toUnsignedInt(port) + "\n");

			//if response good we are still connected so read new port
			//send 12 packets
				//Create Udp Packet
				//Calculate RTT
				//Read server response
			//Get average RTT of all responses

		}
		catch(Exception e){
			System.out.println("Sorry something went wrong!");
		}
		System.out.println("Disconnected from Server.");
	}
	//Handshake function IPv4 packet with UDP protocol and hardcode message DEADBEEF
	private static byte[] handshake(byte[] destAddress){
		byte[] packet = new byte[24];
		//Version & HLen (version 4 Hlen 5)
		packet[0] = 0x45;
		//TOS
		packet[1] = 0;
		//Length (24)
		packet[2] = 0;
		packet[3] = 0x18;
		//Ident
		packet[4] = 0;
		packet[5] = 0;
		//Flags & offset (Flag 64)
		packet[6] = 0x40;
		packet[7] = 0;
		//TTL	(ttl 50)
		packet[8] = 0x32;
		//Protocol - UDP (udp 17)
		packet[9] = 0x11;
		//Source Address
		packet[12] = 127;
		packet[13] = 0;
		packet[14] = 0;
		packet[15] = 1;
		//Destination Address
		for (int i = 0; i < destAddress.length; i++) {
			packet[i + 16] = destAddress[i];
		}
		//Checksum
		short checkSum = checksum(packet);
		int checkSumUpper = checkSum >> 8 & 0xFF;
		int checkSumLower = checkSum & 0xFF;
		packet[10] = (byte)checkSumUpper;
		packet[11] = (byte)checkSumLower;
		//Data hardcode to DEADBEEF
		packet[20] = (byte)0xDE;
		packet[21] = (byte)0xAD;
		packet[22] = (byte)0xBE;
		packet[23] = (byte)0xEF;
		return packet;
	}
	//Add checksum function
	private static short checksum(byte[] b){
		//Initialize Sum
		int sum = 0;
		//int i = 0;
		//Loop through bytes
		for(int i =0; i < b.length-1; i= i+2){
			//take upper shift 4 bits AND wit
			byte upper = b[i];
			byte lower = b[i+1];
			//upper = (byte)(upper << 8 & 0xFF00);
			//lower = (byte)(lower & 0xFF);
			int result = ((upper << 8 & 0xFF00) + (lower & 0x00FF));
			//sum = sum + ((firstHalf << 8 & 0xFF00) + (secondHalf & 0xFF));
			//add to sum
			sum = sum + (result);
			//check to make sure no overflow
			if ((sum & 0xFFFF0000) > 0) {
				sum &= 0xFFFF;
				sum++;
			}
		}
		//For Odd
		if(b.length %2 == 1){
			//add odd bit to sum
			sum = sum + ((b[b.length-1] << 8) & 0xFF00);
			
			// Check overflow
			if((sum & 0xFFFF0000) > 0) {
				sum &= 0xFFFF;
				sum++;
			}
		}
		//return sum
		return (short)~(sum & 0xFFFF);
	}
	//add IPv4 function
	//Pseudo header function
	//UDP Packet function
}
