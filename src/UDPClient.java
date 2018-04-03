import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class UDPClientNew {
    public static void main(String[]args) {
        String ip = "pi.cs.oswego.edu";
        final int windowSize = 50;
        final int sendPort = 3007;
        final int thisPort = 3008;
        int width = 0, height = 0;

        //String url = "https://i-cdn.phonearena.com/images/article/50441-image/Hey-were-not-trying-to-pick-you-up-were-just-snapping-a-picture-using-Google-Glass.jpg";
        String url = "https://upload.wikimedia.org/wikipedia/commons/c/c9/Moon.jpg";
        String[] stringsFromServer = new String[0];
        DatagramPacket data;
        DatagramPacket ack;
        DatagramSocket client;
        boolean sequential = false;
        int packetCount = -1;
        ArrayList<byte[]> receivedData = new ArrayList<>();
        byte[][] windowData = new byte[1][1];

        try {
            System.out.printf("Waiting on udp:%s:%d%n", InetAddress.getLocalHost().getHostAddress(), thisPort);
            client = new DatagramSocket(thisPort);
            ack = new DatagramPacket(new byte[1], 1, InetAddress.getByName(ip), sendPort);
            client.send(ack);
            System.out.println("Sent connection test");
            client.receive(ack);
            System.out.println("Received valid connection");

            data = new DatagramPacket(url.getBytes(), url.getBytes().length, ack.getAddress(), sendPort);

            client.send(data);
            System.out.println("Sent url");
            client.receive(ack);
            System.out.println("Received ack");

            ///ALL ABOVE THIS WORKS PROPERLY
            client.send(ack);
            //THIS IS WHERE THE FUN BEGINS


            data = new DatagramPacket(new byte[12], 12);
            client.receive(data);
            int[] countAndDimensions = bytesToInts(data.getData());
            packetCount = countAndDimensions[0];
            width = countAndDimensions[1];
            height = countAndDimensions[2];

            System.out.println("Received packetCount: " + packetCount + "    Width: " + width + "     Height: " + height);
            client.send(ack);
            byte[] sequenceNum = new byte[4];

            windowData = new byte[packetCount][516];
            for(int i = 0; i < packetCount; i++){
                windowData[i] = null;
            }

            int spot;
            if(sequential){
                for(int i = 0; i < packetCount; i++){
                    data = new DatagramPacket(new byte[516], 516);
                    client.receive(data);
                    System.out.println("Received: " + i);
                    receivedData.add(data.getData());
                    client.send(ack);
                }
            }
            else{
                int count = 0;
                int num;
                ByteBuffer bb;
                client.setSoTimeout(1000);
                while(count < packetCount - 1) {
                    String result = "";
                    for (int i = 0; i < windowSize; i++){
                        try {
                            data = new DatagramPacket(new byte[516], 516);
                            client.receive(data);
                            //System.out.println("Packet received");
                            sequenceNum = new byte[4];
                            sequenceNum[0] = data.getData()[0];
                            sequenceNum[1] = data.getData()[1];
                            sequenceNum[2] = data.getData()[2];
                            sequenceNum[3] = data.getData()[3];
                            bb = ByteBuffer.wrap(sequenceNum);
                            num = bb.getInt();
                            if(windowData[num] == null){
                                windowData[num] = data.getData();
                                count++;
                                result += num + " ";
                                System.out.println(num + "    " + count);
                            }
                            if(count >= packetCount){
                                break;
                            }
                            //System.out.println(count);
                        }
                        catch (Exception e){
                            count--;
                            e.printStackTrace();
                            System.out.println("Packet lost");
                        }
                    }
                    data = new DatagramPacket(result.getBytes(), result.getBytes().length, ack.getAddress(), ack.getPort());
                    client.send(data);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }







        if(sequential) {
            try {
                int test;
                byte[] byteTest = new byte[4];
                ByteBuffer bb;
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                int currentX = 0;
                int currentY = 0;
                for (int i = 0; i < receivedData.size(); i++) {
                    //System.out.println(temp.length);
                    for (int j = 4; j < 516; j+=4) {
                        byteTest[0] = receivedData.get((i))[j];
                        byteTest[1] = receivedData.get((i))[j + 1];
                        byteTest[2] = receivedData.get((i))[j + 2];
                        byteTest[3] = receivedData.get((i))[j + 3];
                        bb = ByteBuffer.wrap(byteTest);
                        test = bb.getInt();
                        System.out.println(test);
                        if(test != 0) {
                            image.setRGB(currentX, currentY, test);
                            currentX++;
                        }
                        if(currentX == image.getWidth()){
                            currentY++;
                            break;
                        }
                    }
                    if(currentX >= image.getWidth() -1) {
                        currentX = 0;
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
        else {
            try {
                int test;
                byte[] byteTest = new byte[4];
                ByteBuffer bb;
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                int currentX = 0;
                int currentY = 0;
                for (int i = 0; i < packetCount; i++) {
                    //System.out.println(temp.length);
                    for (int j = 4; j < 516; j += 4) {
                        byteTest[0] = windowData[i][j];
                        byteTest[1] = windowData[i][j+1];
                        byteTest[2] = windowData[i][j+2];
                        byteTest[3] = windowData[i][j+3];
                        bb = ByteBuffer.wrap(byteTest);
                        test = bb.getInt();
                        //System.out.println(test);
                        if (test != 0) {
                            image.setRGB(currentX, currentY, test);
                            currentX++;
                        }
                        if (currentX == image.getWidth()) {
                            currentY++;
                            break;
                        }
                    }
                    if (currentX >= image.getWidth() - 1) {
                        currentX = 0;
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

    public static int[] bytesToInts(byte[] bytes){
        int[] countAndDimensions = new int[3];
        ByteBuffer bb;
        byte[] temp;
        for(int i = 0; i < 12; i+=4){
            temp = new byte[4];
            temp[0] = bytes[i];
            temp[1] = bytes[i + 1];
            temp[2] = bytes[i + 2];
            temp[3] = bytes[i + 3];
            bb = ByteBuffer.wrap(temp);
            if(i == 0){
                countAndDimensions[0] = bb.getInt();
                System.out.println(countAndDimensions[0]);
            }
            else if(i == 4){
                countAndDimensions[1] = bb.getInt();
                System.out.println(countAndDimensions[1]);
            }
            else if(i == 8){
                countAndDimensions[2] = bb.getInt();
                System.out.println(countAndDimensions[2]);
            }
            else{
                System.out.println("Error?");
            }
        }
        return countAndDimensions;
    }
}
