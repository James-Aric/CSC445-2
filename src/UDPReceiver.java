import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPReceiver {

    //Variables to alter packet size/number
    static int packetSize = 1000;
    static int packetNum = 1024;
    static int port = 2525;

    public static void main(String[]args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[packetSize];

            System.out.printf("Listening on udp:%s:%d%n", InetAddress.getLocalHost().getHostAddress(), port);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            DatagramPacket packets[] = new DatagramPacket[packetNum];
            InetAddress IPAddress;
            DatagramPacket sendPacket;
            for(int i = 0; i < packetNum; i++) {
                serverSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("RECEIVED: " + sentence);
                packets[i] = receivePacket;
                if(packetNum == 1) {
                    IPAddress = packets[0].getAddress();
                    byte[] sendData = receivePacket.getData();
                    sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, packets[0].getPort());
                    serverSocket.send(sendPacket);
                }
                else{
                    IPAddress = packets[i].getAddress();
                    sendPacket = new DatagramPacket(new byte[1], 1, IPAddress, packets[i].getPort());
                    serverSocket.send(sendPacket);
                }
                System.out.println(i);
            }

            serverSocket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
