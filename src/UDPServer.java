import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {
    public static void main(String[]args){
        final int port = 3005;

        DatagramPacket data;
        DatagramPacket ack;
        DatagramSocket server;
        try{
            server = new DatagramSocket(port);
            ack = new DatagramPacket(new byte[1], 1);
            System.out.printf("Waiting on udp:%s:%d%n", InetAddress.getLocalHost().getHostAddress(), port);
            server.receive(ack);
            System.out.println("Received packet");
            server.send(ack);
            System.out.println("Sent packet");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
