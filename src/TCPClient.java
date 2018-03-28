import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class TCPClient {

    public static void main(String[]args) throws Exception{
        //String url = "https://i-cdn.phonearena.com/images/article/50441-image/Hey-were-not-trying-to-pick-you-up-were-just-snapping-a-picture-using-Google-Glass.jpg";
        String url = "https://www.bigstockphoto.com/images/homepage/module-4.jpg";
        //String ip = InetAddress.getLocalHost().toString();
        String ip = "129.3.158.31";
        int port = 2525;
        Socket clientSocket = new Socket(ip, port);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());
        ObjectInputStream valuesFromServer = new ObjectInputStream(clientSocket.getInputStream());

        System.out.println("Sending url to server");

        byte[] temp = url.getBytes();
        //first send
        outToServer.writeInt(temp.length);
        //first read
        inFromServer.readInt();
        //second send
        outToServer.write(temp, 0 , temp.length);

        //receive metadata
        int arraySize = inFromServer.readInt();
        outToServer.writeInt(1);
        int arrayNum = inFromServer.readInt();
        outToServer.writeInt(1);
        System.out.println(arraySize + " " + arrayNum);

        ArrayList<Integer> ints;
        ArrayList<ArrayList<Integer>> allInts = new ArrayList<>();
        for(int i = 0; i < arraySize; i++){
            allInts.add((ArrayList<Integer>)valuesFromServer.readObject());
            outToServer.writeInt(1);
            System.out.println("Success!" + i);
        }

        System.out.println("Done!");

        try{
            BufferedImage image = new BufferedImage(allInts.size(),allInts.get(0).size()-1, BufferedImage.TYPE_INT_RGB);
            for(int i = 0; i < allInts.size(); i++){
                for(int j = 0; j < allInts.get(0).size()-1; j++){
                    image.setRGB(i, j, allInts.get(i).get(j+1));
                }
            }
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setLayout(new FlowLayout());
            frame.getContentPane().add(new JLabel(new ImageIcon(image)));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
