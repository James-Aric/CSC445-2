import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

public class TCPServer {
    public static void main(String[]args) throws Exception{
        String ip = InetAddress.getLocalHost().toString();
        int port = 2525;
        ServerSocket serverSocket = new ServerSocket(port);
        Socket connection = serverSocket.accept();
        DataOutputStream outToServer = new DataOutputStream(connection.getOutputStream());
        DataInputStream inFromServer = new DataInputStream(connection.getInputStream());
        ObjectOutputStream valsToClient = new ObjectOutputStream(connection.getOutputStream());
        System.out.println("Receiving!");

        //first read
        int size = inFromServer.readInt();
        byte[] bytes = new byte[size];
        //firstSend
        outToServer.writeInt(1);
        //second read
        inFromServer.readFully(bytes);
        System.out.println(new String(bytes));

        ToFile toFile = new ToFile(new String(bytes), 1);
        ArrayList<ArrayList<Integer>> vals = toFile.tcpNew();
        //send metadata
        outToServer.writeInt(vals.size());
        //third read
        inFromServer.readInt();
        //third send
        outToServer.writeInt(vals.size());
        //fourth read
        inFromServer.readInt();

        for(int i = 0; i < vals.size(); i++){
            valsToClient.writeObject(vals.get(i));
            inFromServer.readInt();
            System.out.println("Success!" + i);
        }
    }
}
