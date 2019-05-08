import java.net.*;
import java.util.ArrayList;

public class UDPServer {
    boolean ipv4, sequential, drops;
    public UDPServer(boolean ipv4, boolean sequential, boolean drops){
        this.ipv4 = ipv4;
        this.sequential = sequential;
        this.drops = drops;
    }

    public void run(){
        String ip;
        final int windowSize = 4;
        final int sendPort = 3008;
        final int thisPort = 3007;
        DatagramPacket data;
        DatagramPacket urlPack;
        DatagramPacket ack;
        DatagramSocket server;
        ToFile toFile;
        String url;
        InetAddress sendAddress;
        try {
            server = new DatagramSocket(thisPort);
            ack = new DatagramPacket(new byte[1], 1);
            System.out.printf("Waiting on udp: " + Inet4Address.getLocalHost().getHostAddress() + "    " + Inet6Address.getLocalHost().getHostAddress());
            server.receive(ack);
            System.out.println("Received connection test");
            server.send(ack);
            System.out.println("Sent confirmation");

            urlPack = new DatagramPacket(new byte[200], 200);

            server.receive(urlPack);
            ip = urlPack.getAddress().toString();
            ip = ip.substring(1,ip.length());
            if(ipv4){
                sendAddress = Inet4Address.getByName(ip);
            }
            else{
                sendAddress = Inet6Address.getByName(ip);
            }
            System.out.println("Received URL");
            server.send(ack);
            System.out.println("Sent ack");
            url = new String(urlPack.getData());
            ///ALL ABOVE THIS WORKS PROPERLY
            server.receive(ack);
            System.out.println("Received ACK");
            //THIS IS WHERE THE FUN BEGINS

            toFile = new ToFile(url);
            System.out.println("Created ToFile Object");
            ArrayList<byte[]> bytesToSend = toFile.udpData();
            System.out.println("Created files");
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
            System.out.println("Created metadata");
            data = new DatagramPacket(temp, 12, sendAddress, urlPack.getPort());
            server.send(data);
            System.out.println("Sent metadata");
            server.receive(data);
            System.out.println("Received ack");

            if(sequential){
                server.setSoTimeout(5);
                for(int i = 0; i < packetCount; i++){
                    try {
                        data = new DatagramPacket(bytesToSend.get(i), bytesToSend.get(i).length, sendAddress, urlPack.getPort());
                        server.send(data);
                        server.receive(ack);
                    }catch(Exception e){
                        i--;
                    }
                }
            }
            else{
                String result;
                String splitResult[];
                int test;
                data = new DatagramPacket(bytesToSend.get(0), bytesToSend.get(0).length, sendAddress, urlPack.getPort());
                server.send(data);
                server.setSoTimeout(500);
                while(packetNumsToSend.size() != 0){
                    try {
                        for (int i = 0; i < windowSize; i++) {
                            if (packetNumsToSend.size() > i) {
                                test = packetNumsToSend.get(i);
                                data = new DatagramPacket(bytesToSend.get(test), bytesToSend.get(test).length, sendAddress, urlPack.getPort());
                                server.send(data);
                            }
                        }
                        data = new DatagramPacket(new byte[200], 200);
                        server.receive(data);
                        result = new String(data.getData());
                        splitResult = result.split(" ");
                        for (int i = 0; i < splitResult.length - 1; i++) {
                            packetNumsToSend.remove((Integer) Integer.parseInt(splitResult[i].trim()));
                        }
                    }
                    catch (SocketTimeoutException e){
                        System.out.println("Results not received, resending window");
                    }
                }
                server.send(new DatagramPacket(new byte[4], 4, sendAddress, urlPack.getPort()));
            }
            System.out.println("Finished!");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
