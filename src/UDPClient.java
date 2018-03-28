import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class UDPClient {
    public static void main(String[]args){
        String ip = "129.3.154.126";
        final int sendPort = 3005;
        final int thisPort = 3006;
        String url = "https://i-cdn.phonearena.com/images/article/50441-image/Hey-were-not-trying-to-pick-you-up-were-just-snapping-a-picture-using-Google-Glass.jpg";


        DatagramPacket data;
        DatagramPacket ack;
        DatagramSocket client;
        boolean sequential = true;
        ArrayList<String> receivedPackets = new ArrayList<>();
        int packetCount = -1;
        int packetSize = -1;
        String[] windowPackets = new String[1];

        try{
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


            data = new DatagramPacket(new byte[20], 20);
            client.receive(data);
            System.out.println("Received packetCount and packetSize: " + new String(data.getData()));
            String[] metaData = new String(data.getData()).split(" ");
            client.send(ack);
            System.out.println("Sent ack");
            packetCount = Integer.parseInt(metaData[0]);
            packetSize = Integer.parseInt(metaData[1].trim());



            if(sequential){
                data = new DatagramPacket(new byte[packetSize], packetSize);
                for(int i = 0; i < packetCount; i++){
                    client.receive(data);
                    System.out.println("Received: " + i);
                    receivedPackets.add(new String(data.getData()));
                    client.send(ack);
                }
                System.out.println("SUCCESS!!!");
            }
            else{
                data = new DatagramPacket(new byte[packetSize], packetSize);
                //WRITE SLIDING WINDOW FOR UDP YOURE ALMOST THEEEERRRRREEEEEEE
            }

        }catch(Exception e){
            e.printStackTrace();
        }




        //DISPLAYING IMAGE



        if(sequential) {
            try {
                String temp[];
                BufferedImage image = new BufferedImage(receivedPackets.size(), receivedPackets.get(0).split(" ").length - 1, BufferedImage.TYPE_INT_RGB);
                for (int i = 0; i < packetCount; i++) {
                    temp = receivedPackets.get(i).split(" ");
                    //System.out.println(temp.length);
                    for (int j = 0; j < image.getWidth(); j++) {
                        if(temp[j+1].trim().equals("")){

                        }
                        else {
                            image.setRGB(i, j, Integer.parseInt(temp[j + 1].trim()));
                        }
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
                String temp[];
                BufferedImage image = new BufferedImage(receivedPackets.size(), receivedPackets.get(0).split(" ").length - 1, BufferedImage.TYPE_INT_RGB);
                for (int i = 0; i < packetCount; i++) {
                    temp = receivedPackets.get(i).split(" ");
                    //System.out.println(temp.length);
                    for (int j = 0; j < image.getWidth(); j++) {
                        if(temp[j+1].trim().equals("")){

                        }
                        else {
                            image.setRGB(i, j, Integer.parseInt(temp[j + 1].trim()));
                        }
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
