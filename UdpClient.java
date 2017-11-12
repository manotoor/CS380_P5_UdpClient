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

		}
		System.out.println("Disconnected from Server.");
		catch(Exception e){
			System.out.println("Sorry something went wrong!");
		}
	}
}
