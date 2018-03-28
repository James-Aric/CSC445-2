import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class TCPClient {

    public static void main(String[]args) throws Exception{
        System.out.println(InetAddress.getLocalHost());
        String url = "https://i-cdn.phonearena.com/images/article/50441-image/Hey-were-not-trying-to-pick-you-up-were-just-snapping-a-picture-using-Google-Glass.jpg";
        //String url = "https://www.bigstockphoto.com/images/homepage/module-4.jpg";
        //String ip = InetAddress.getLocalHost().toString();
        String ip = "129.3.154.126";
        int port = 2525;
        boolean sequential;
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
        sequential = false;
        ArrayList<Integer> arrays[] = new ArrayList[arraySize];
        if(sequential) {
            for (int i = 0; i < arraySize; i++) {
                allInts.add((ArrayList<Integer>) valuesFromServer.readObject());
                outToServer.writeInt(1);
                System.out.println("Success!" + i);
            }
        }
        else{
            ArrayList<Integer> current;
            String receivedPackets = "";
            int count = 0;
            clientSocket.setSoTimeout(2000);
            while(count < arraySize) {
                for (int i = 0; i < 10; i++) {
                    try {
                        //allInts.add((ArrayList<Integer>) valuesFromServer.readObject());
                        //outToServer.writeInt(1);
                        //System.out.println("Success!" + i);
                        current = (ArrayList<Integer>) valuesFromServer.readObject();
                        arrays[current.get(0)] = current;
                        receivedPackets += current.get(0) + " ";
                        count++;
                    }
                    catch (SocketTimeoutException e){
                        System.out.println("Packet Lost");
                    }
                }
                //outToServer.writeChars(receivedPackets);
                outToServer.writeUTF(receivedPackets);
                System.out.println(count);
                System.out.println(receivedPackets);
                receivedPackets = "";
            }

        }

        System.out.println("Done!");
        if(sequential) {
            try {
                BufferedImage image = new BufferedImage(allInts.size(), allInts.get(0).size() - 1, BufferedImage.TYPE_INT_RGB);
                for (int i = 0; i < allInts.size(); i++) {
                    for (int j = 0; j < allInts.get(0).size() - 1; j++) {
                        image.setRGB(i, j, allInts.get(i).get(j + 1));
                    }
                }
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().setLayout(new FlowLayout());
                frame.getContentPane().add(new JLabel(new ImageIcon(image)));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                BufferedImage image = new BufferedImage(arrays.length, arrays[0].size() - 1, BufferedImage.TYPE_INT_RGB);
                for (int i = 0; i < arrays.length; i++) {
                    for (int j = 0; j < arrays[0].size() - 1; j++) {
                        image.setRGB(i, j, arrays[i].get(j + 1));
                    }
                }
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().setLayout(new FlowLayout());
                frame.getContentPane().add(new JLabel(new ImageIcon(image)));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
