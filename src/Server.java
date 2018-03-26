import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class Server {

    public static void main(String[]args) {
        ToFile data = new ToFile("https://i-cdn.phonearena.com/images/article/50441-image/Hey-were-not-trying-to-pick-you-up-were-just-snapping-a-picture-using-Google-Glass.jpg");
        ArrayList<DatagramPacket> sendPackets = data.getPackets();
        System.out.println(sendPackets.size());
        DatagramSocket client;
        try {
            client = new DatagramSocket(3000);
            for(int i = 0; i < sendPackets.size(); i++){
                client.send(sendPackets.get(i));
                client.receive(new DatagramPacket(new byte[10],10));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
