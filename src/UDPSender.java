import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPSender {
    static String ip = "129.3.213.22";
    static int packetNum = 1024;
    static int packetSize = 1000;

    public static void main(String[] args) throws InterruptedException, IOException  {
        byte[] buffer = new byte[packetSize];
        InetAddress address = InetAddress.getByName(ip);
        DatagramPacket packet;
        DatagramSocket datagramSocket = new DatagramSocket();
        byte[] receiveData = new byte[packetSize];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        long startTime = System.nanoTime();
        for(int i = 0; i < packetNum; i++){
            packet = new DatagramPacket(buffer, buffer.length, address, 2525);
            datagramSocket.send(packet);
            datagramSocket.receive(receivePacket);
            System.out.println("Echo: " + receivePacket.toString());
        }
        System.out.println(System.nanoTime() - startTime);
        datagramSocket.close();

    }
}