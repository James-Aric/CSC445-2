import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Client {
    public static void main(String[]args){
        DatagramSocket server;
        DatagramPacket recievedPacket;
        try{
            server = new DatagramSocket(3000);
            for(int i = 0; i < 0; i++);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
