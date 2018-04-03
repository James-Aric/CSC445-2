import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class UDPServerNew {
    public static void main(String[]args){
        final int windowSize = 50;
        final int sendPort = 3008;
        final int thisPort = 3007;
        DatagramPacket data;
        DatagramPacket urlPack;
        DatagramPacket ack;
        DatagramSocket server;
        ToFile toFile;
        String url;
        boolean sequential = false;
        try {
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
            ArrayList<byte[]> bytesToSend = toFile.udpNew2();
            ArrayList<Integer> packetNumsToSend = new ArrayList<>();
            for(int i = 0; i < bytesToSend.size(); i++){
                packetNumsToSend.add(i);
            }
            byte[] dimensions = toFile.getDimensions();
            int packetCount = bytesToSend.size();
            byte[] temp = new byte[12];
            temp[0] = (byte)((packetCount & 0xff000000) >> 24);
            temp[1] = (byte)((packetCount& 0x00ff0000) >> 16);
            temp[2] = (byte)((packetCount& 0x0000ff00) >> 8);
            temp[3] = (byte)(packetCount& 0x000000ff);
            for(int i = 0; i < 8; i++){
                temp[i+4] = dimensions[i];
            }
            data = new DatagramPacket(temp, 12, urlPack.getAddress(), urlPack.getPort());
            server.send(data);
            System.out.println("Sent metadata");
            server.receive(data);
            System.out.println("Received ack");

            if(sequential){
                for(int i = 0; i < packetCount; i++){
                    data = new DatagramPacket(bytesToSend.get(i), bytesToSend.get(i).length, urlPack.getAddress(), urlPack.getPort());
                    server.send(data);
                    System.out.println("Sent packet");
                    server.receive(ack);
                    System.out.println("Received ack");
                }
            }
            else{
                String result;
                String splitResult[];
                int test;
                while(packetNumsToSend.size() != 0){
                    for(int i = 0; i < windowSize; i++){
                        if(packetNumsToSend.size() > i) {
                            test = packetNumsToSend.get(i);
                            data = new DatagramPacket(bytesToSend.get(test), bytesToSend.get(test).length, urlPack.getAddress(), urlPack.getPort());
                            server.send(data);
                        }
                    }
                    data = new DatagramPacket(new byte[200], 200);
                    server.receive(data);
                    result = new String(data.getData());
                    splitResult = result.split(" ");
                    for(int i = 0; i < splitResult.length - 1; i++){
                        packetNumsToSend.remove((Integer) Integer.parseInt(splitResult[i].trim()));
                    }
                    System.out.println(result);
                    result = "";
                }
            }
            System.out.println("test");

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
