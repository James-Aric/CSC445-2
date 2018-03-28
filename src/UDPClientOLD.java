import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class UDPClientOLD {
    public static void main(String[]args) throws UnknownHostException{
        final int port = 3005;
        //System.out.println(InetAddress.getLocalHost());
        DatagramSocket server;
        String ip = "129.3.154.126";
        DatagramPacket dataPacket;
        DatagramPacket recievedPacket;
        DatagramPacket ack = new DatagramPacket(new byte[1], 1);
        ArrayList<byte[]> recievedBytes = new ArrayList<>();
        String url = "https://i-cdn.phonearena.com/images/article/50441-image/Hey-were-not-trying-to-pick-you-up-were-just-snapping-a-picture-using-Google-Glass.jpg";
        DatagramPacket urlPacket = new DatagramPacket(url.getBytes(), url.getBytes().length, InetAddress.getByName(ip), port);
        //System.out.println(InetAddress.getByName(ip));
        try{
            System.out.printf("Waiting on udp:%s:%d%n", InetAddress.getLocalHost().getHostAddress(), port);
            //connect to server, send the url;
            server = new DatagramSocket();
            server.send(urlPacket);
            System.out.println("Sent URL");
            //ack
            server.receive(ack);
            System.out.println("recieved Ack");
            //receive acknowledgement and packet with the size of the data.

            dataPacket = new DatagramPacket(new byte[3], 3);
            ack.setAddress(ack.getAddress());
            server.send(ack);
            //FAILING HERE?
            server.receive(dataPacket);
            System.out.println("Received data");

            byte[] dataSize = new byte[dataPacket.getData()[0]];
            int packetNum = dataPacket.getData()[1];
            System.out.println(packetNum);
            recievedPacket = new DatagramPacket(dataSize, dataSize.length, urlPacket.getAddress(), port);
            System.out.println(dataSize.length);
            server.send(new DatagramPacket(new byte[1], 1, recievedPacket.getAddress(), port));


            for(int i = 0; i < 0; i++){
                server.receive(recievedPacket);
                recievedBytes.add(recievedPacket.getData());
                server.send(new DatagramPacket(new byte[1], 1, recievedPacket.getAddress(), port));
                System.out.println("sent and received");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


}
