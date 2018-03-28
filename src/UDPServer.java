import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class UDPServer {

    public static void main(String[]args) {
        final int port = 3005;
        ToFile data;
        ArrayList<byte[]> byteList;
        //System.out.println(sendPackets.size());
        //System.out.println("https://i-cdn.phonearena.com/images/article/50441-image/Hey-were-not-trying-to-pick-you-up-were-just-snapping-a-picture-using-Google-Glass.jpg".getBytes().length);
        DatagramSocket client;
        String url;// = "https://i-cdn.phonearena.com/images/article/50441-image/Hey-were-not-trying-to-pick-you-up-were-just-snapping-a-picture-using-Google-Glass.jpg";
        DatagramPacket urlPacket = new DatagramPacket(new byte[200], 200);
        DatagramPacket dataPacket;
        DatagramPacket ack;
        try {
            client = new DatagramSocket(port);
            client.receive(urlPacket);
            System.out.println("received url");
            url = new String(urlPacket.getData());
            data = new ToFile(url, 0);
            byteList = data.udpNew();
            System.out.println(url);
            //SEND PACKET WITH DATA SIZE
            double bytes = Math.ceil(byteList.get(0).length/256);
            System.out.println(bytes);
            byte[] temp = new byte[3];
            temp[0] = (byte)bytes;
            temp[1] = (byte)byteList.size();
            temp[2] = (byte)byteList.get(0).length;
            //System.out.println("byte: " + (temp[1] + 256*temp[0]) + "     actual: " + byteList.get(0).length);
            //System.out.println("byte: " + (temp[2]& 0xFF) + "     actual: " + sendPackets.size());
            dataPacket = new DatagramPacket(temp, 3, urlPacket.getAddress(), port);
            client.send(new DatagramPacket(new byte[1], 1, InetAddress.getLocalHost(), port));
            client.send(dataPacket);
            System.out.println("Sent data size + packet amount");
            ack = new DatagramPacket(new byte[1],1, urlPacket.getAddress(), port);
            Thread.sleep(1000);
            client.receive(ack);
            System.out.println("received data ack");
            DatagramPacket sendPacket;
            for(int i = 0; i < byteList.size(); i++){
                sendPacket = new DatagramPacket(byteList.get(i), byteList.get(i).length, urlPacket.getAddress(), port);
                System.out.println(sendPacket.getLength());
                client.send(sendPacket);
                client.receive(new DatagramPacket(new byte[1],1, urlPacket.getAddress(), port));
                //System.out.println("sent and received");
            }
            System.out.println("Done!");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
