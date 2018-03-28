import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    public static void main(String[]args){
        String ip = "129.3.154.126";
        final int port = 3005;

        DatagramPacket data;
        DatagramPacket ack;
        DatagramSocket client;

        try{
            System.out.printf("Waiting on udp:%s:%d%n", InetAddress.getLocalHost().getHostAddress(), port);
            client = new DatagramSocket();
            ack = new DatagramPacket(new byte[1], 1, InetAddress.getByName(ip), port);
            client.send(ack);
            System.out.println("Sent packet");
            client.receive(ack);
            System.out.println("Received packet");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
