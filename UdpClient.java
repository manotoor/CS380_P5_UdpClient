//Manvinder Toor
//Udp client

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

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
			double averageRTT = 0;

			//Get destination address (should be 18,221,102,182)
			dAdrs = socket.getInetAddress().getAddress();

			//Send handshake message (hard code dead beef IPv4 and protocol UDP)
			handshake = handshake(dAdrs);
			os.write(handshake);

			//if response good we are still connected so read new port
			is.read(rCode);
			System.out.print("Handshake response: 0x");
			for (int i = 0; i < rCode.length; i++) {
				System.out.printf("%02X", rCode[i]);
			}
			//Get port number after response code
			is.read(port);
			System.out.print("\nPort number received: ");
			short portN = (short)((short)port[0] << 8 | (short)port[1]);
			System.out.print(Short.toUnsignedInt(portN) + "\n\n");

			int dataLength = 2;
			//send 12 packets
			for(int i = 0; i < 12; i++){
				//variables for documenting time
				long startTime;
				long RoundTripTime;
				long endTime;
				System.out.println("Sending packet with " + dataLength + " bytes of data.");
				//Create Udp Packet
				byte[] packet = createUdpPacket(port, dAdrs, dataLength);

				//Calculate RTT
				startTime = System.currentTimeMillis();
				os.write(packet);
				is.read(rCode);
				endTime = System.currentTimeMillis();
				RoundTripTime = endTime - startTime;
				averageRTT = averageRTT + RoundTripTime;

				//Read server response
				System.out.print("Server response: 0x");
				for(int j = 0; j < rCode.length;j++){
					System.out.printf("%02X", rCode[j]);
				}
				System.out.println("\nRTT: " + RoundTripTime +"ms\n");
				dataLength = dataLength * 2;
			}
			//Get average RTT of all responses (of 12 responses)
			averageRTT = averageRTT / 12;
			System.out.printf("Average RTT: %.2fms%n%n", averageRTT);
		}
		catch(Exception e){
			System.out.println("Sorry something went wrong!");
		}
		System.out.println("Disconnected from server.");
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
	public static byte[] createUdpPacket(byte[] portNumber, byte[] destinationAddress, int dataSize){
		//Ipv4 + ports + datasize
		int size = 28 + dataSize;
		byte[] packet = new byte[size];
		//Version & HLen (version 4 Hlen 5)
		packet[0] = 0x45;
		//TOS
		packet[1] = 0;
		//Length (24)
		packet[2] = (byte)(size >>> 8);
		packet[3] = (byte)(size);
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
		//Cecksum initial set to zero
		packet[10] = 0;
		packet[11] = 0;
		//Source Address
		packet[12] = 127;
		packet[13] = 0;
		packet[14] = 0;
		packet[15] = 1;
		//Destination Address
		for (int i = 0; i < destinationAddress.length; i++) {
			packet[i + 16] = destinationAddress[i];
		}
		//Checksum
		short checkSum = checksum(packet);
		int checkSumUpper = checkSum >> 8 & 0xFF;
		int checkSumLower = checkSum & 0xFF;
		packet[10] = (byte)checkSumUpper;
		packet[11] = (byte)checkSumLower;
		//Source port
		packet[20] = (byte)0;
		packet[21] = (byte)0;
		//Dest port
		packet[22] = (byte)portNumber[0];
		packet[23] = (byte)portNumber[1];
		//Length
		int udpLength = 8 + dataSize;
		packet[24] = (byte)(udpLength >>> 8);
		packet[25] = (byte)(udpLength);
		//checkSum for UDP
		packet[26]= 0;
		packet[27]= 0;
		//Get random data
		for(int j = 28; j < packet.length;j++){
			packet[j] = random();
		}
		//Pseudoheader to do checksum for packet
		byte[] pseudoHeader = pseudoHeader(packet,destinationAddress,dataSize);
		packet[26] = pseudoHeader[17];
		packet[27] = pseudoHeader[18];
		return packet;
	}
	public static byte[] pseudoHeader(byte[] packet, byte[] destinationAddress, int dataSize){
		//pseudoheader to deal with checksum
		byte[] pseudoHeader = new byte[dataSize + 20];
		//source address
		pseudoHeader[0] = 127;
		pseudoHeader[1] = 0;
		pseudoHeader[2] = 0;
		pseudoHeader[3] = 1;
		//desitnation address
		for(int i = 0; i < destinationAddress.length; i++){
			pseudoHeader[i + 4] = destinationAddress[i];
		}
		//zero
		pseudoHeader[8] = 0;
		//protocol
		pseudoHeader[9] = 17;
		//udp length
		pseudoHeader[10] = packet[24];
		pseudoHeader[11] = packet[25];
		//source port
		pseudoHeader[12] = packet[20];
		pseudoHeader[13] = packet[21];
		//destination port
		pseudoHeader[14] = packet[22];
		pseudoHeader[15] = packet[23];
		//length
		pseudoHeader[16] = packet[24];
		pseudoHeader[17] = packet[25];
		//checksum
		pseudoHeader[18] = packet[26];
		pseudoHeader[19] = packet[27];
		//data
		for (int j = 0; j < dataSize; j++) {
			pseudoHeader[j + 20] = packet[j + 28];
		}
		//checksum
		short checkSum = checksum(pseudoHeader);
		int checkSumUpper = checkSum >> 8 & 0xFF;
		int checkSumLower = checkSum & 0xFF;
		pseudoHeader[17] = (byte)checkSumUpper;
		pseudoHeader[18] = (byte)checkSumLower;
		return pseudoHeader;
	}
	public static byte random(){
		Random random = new Random();
		return (byte)random.nextInt(256);
	}
}
