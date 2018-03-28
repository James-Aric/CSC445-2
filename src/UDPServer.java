import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class UDPServer {
    public static void main(String[]args){
        final int sendPort = 3006;
        final int thisPort = 3005;
        DatagramPacket data;
        DatagramPacket urlPack;
        DatagramPacket ack;
        DatagramSocket server;
        ToFile toFile;
        String url;
        boolean sequential = true;
        try{
            server = new DatagramSocket(thisPort);
            ack = new DatagramPacket(new byte[1], 1);
            System.out.printf("Waiting on udp:%s:%d%n", InetAddress.getLocalHost().getHostAddress(), thisPort);
            server.receive(ack);
            System.out.println("Received connection test");
            server.send(ack);
            System.out.println("Sent confirmation");

            urlPack = new DatagramPacket(new byte[200], 200);

            server.receive(urlPack);
            System.out.println("Received URL");
            server.send(ack);
            System.out.println("Sent ack");
            url = new String(urlPack.getData());
            ///ALL ABOVE THIS WORKS PROPERLY
            server.receive(ack);
            //THIS IS WHERE THE FUN BEGINS

            toFile = new ToFile(url, 0);

            ArrayList<String> packets = toFile.udpNew();

            int packetCount = packets.size();
            int packetLength = packets.get(0).getBytes().length;

            System.out.println(packetCount + " " + packetLength);

            String metaData = packetCount + " " + packetLength;
            data = new DatagramPacket(metaData.getBytes(), metaData.getBytes().length, ack.getAddress(), sendPort);
            server.send(data);
            System.out.println("Sent packet multiplier");
            server.receive(ack);
            System.out.println("Received ack");
            if(sequential){
                System.out.println("Beginning packet sending");
                for(int i = 0; i < packetCount; i++){
                    data = new DatagramPacket(packets.get(i).getBytes(), packets.get(i).getBytes().length, ack.getAddress(), sendPort);
                    server.send(data);
                    System.out.println("Sent: " + i);
                    server.receive(ack);
                    System.out.println("Acked");
                }
                System.out.println("Packet sending finished");
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
