//import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class ToFile {
    private BufferedImage siteImage;
    private String url;// = "https://upload.wikimedia.org/wikipedia/commons/9/97/The_Earth_seen_from_Apollo_17.jpg";
    //private int byteLength;
    private ArrayList<int[]> data;


    private ArrayList<DatagramPacket> packetsUDP;
    private DatagramPacket sendPacketUDP;
    private ArrayList<byte[]> packetsTCP;

    private ArrayList<byte[]> bytes = new ArrayList<>();

    //int[] to byte[]
    private ByteBuffer byteBuffer;
    private IntBuffer intBuffer;
    private byte[] array;

    //HEADER STUFF
    private int version = 2;
    private int padding = 0;
    private int extension = 0;
    private int csrcCount = 1;
    private int marker = 0;
    private int payloadType = 8;
    private int sequenceNumber = 0;
    private long timestamp = 1;



    //private int length = 12 + 200;
    private byte buf[];// = new byte[12 + data.get(0).length]; //allocate this big enough to hold the RTP header + audio data
    private byte temp[];
    private byte combined[];



    public ToFile(String url, int num) {
        this.url = url;
        constructPacketData();
        if(num == 0) {
            constructPacketsUDP();
        }
        else{
            //tcpNew();
        }
    }

    public void urlToFile(String url) throws IOException {
        URL site = new URL(url);
        siteImage = ImageIO.read(site);
        System.out.println(siteImage.getType());
    }

    public ArrayList<int[]> constructPacketData() {
        data = new ArrayList<>();

        if (siteImage == null) {
            try {
                urlToFile(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int[] col;

        for (int i = 0; i < siteImage.getWidth(); i++) {
            col = new int[siteImage.getHeight()];
            for (int j = 0; j < siteImage.getHeight(); j++) {
                col[j] = siteImage.getRGB(i, j);
            }
            data.add(col);
        }
        //byteLength = data.size();
        return data;
    }





    public void packetCreationUDP(int num) {
        buf = new byte[12]; //allocate this big enough to hold the RTP header + audio data
        //assemble the first bytes according to the RTP spec (note, the spec marks version as bit 0 and 1, but
        //this is really the high bits of the first byte ...
        buf[0]=(byte)((version &0x3)<<6|(padding &0x1)<<5|(extension &0x1)<<4|(csrcCount &0xf));

        //2.byte
        buf[1]=(byte)((marker &0x1)<<7|payloadType &0x7f);

        //squence number, 2 bytes, in big endian format. So the MSB first, then the LSB.
        buf[2]=(byte)((sequenceNumber &0xff00)>>8);
        buf[3]=(byte)(sequenceNumber  &0x00ff);

        //packet timestamp , 4 bytes in big endian format
        buf[4]=(byte)((timestamp &0xff000000)>>24);
        buf[5]=(byte)((timestamp &0x00ff0000)>>16);
        buf[6]=(byte)((timestamp &0x0000ff00)>>8);
        buf[7]=(byte)(timestamp &0x000000ff);
        //our CSRC , 4 bytes in big endian format
        buf[8]=(byte)((sequenceNumber &0xff000000)>>24);
        buf[9]=(byte)((sequenceNumber &0x00ff0000)>>16);
        buf[10]=(byte)((sequenceNumber &0x0000ff00)>>8);
        buf[11]=(byte)(sequenceNumber &0x000000ff);
        sequenceNumber++;

        temp = bytes.get(num);
        combined = new byte[12 + temp.length];

        System.arraycopy(buf, 0, combined, 0, buf.length);
        System.arraycopy(temp, 0, combined, buf.length, temp.length);

        sendPacketUDP = new DatagramPacket(combined, combined.length);
        sendPacketUDP.setPort(3000);
        packetsUDP.add(sendPacketUDP);
    }

    public void constructPacketsUDP() {
        packetsUDP = new ArrayList<>();
        for(int i = 0; i < data.size(); i++){
            byteBuffer = ByteBuffer.allocate(data.size() * 4);
            intBuffer = byteBuffer.asIntBuffer();
            intBuffer.put(data.get(i));

            array = byteBuffer.array();
            bytes.add(array);
            /*sendPacket = new DatagramPacket(array, array.length);
            packets.add(sendPacket);*/
            packetCreationUDP(i);
        }
    }

    public ArrayList<DatagramPacket> getPacketsUDP(){
        return packetsUDP;
    }

    public ArrayList<ArrayList<Integer>> tcpNew(){
        ArrayList<ArrayList<Integer>> vals = new ArrayList<>();
        for(int i = 0; i < siteImage.getWidth(); i++){
            vals.add(new ArrayList<>());
            vals.get(i).add(i);
            for(int j = 0; j < siteImage.getHeight(); j++){
                vals.get(i).add(siteImage.getRGB(i,j));
            }
        }
        return vals;
    }

    /*public ArrayList<byte[]> udpNew() throws IOException {
        ArrayList<ArrayList<Integer>> vals = new ArrayList<>();
        for(int i = 0; i < siteImage.getWidth(); i++){
            vals.add(new ArrayList<>());
            vals.get(i).add(i);
            for(int j = 0; j < siteImage.getHeight(); j++){
                vals.get(i).add(siteImage.getRGB(i,j));
            }
        }
        ArrayList<byte[]> byteVals = new ArrayList<>();

        // write to byte array
        ByteArrayOutputStream baos;
        DataOutputStream out;
        byte[] bytes;
        for(int i = 0; i < vals.size(); i++) {
            baos = new ByteArrayOutputStream();
            out = new DataOutputStream(baos);
            for (int element : vals.get(i)) {
                out.writeInt(element);
            }
            bytes = baos.toByteArray();
            byteVals.add(bytes);
        }
        return byteVals;
    }*/

    public ArrayList<String> udpNew() {
        ArrayList<String> sendData = new ArrayList<>();
        String temp = "";
        for(int i = 0; i < siteImage.getHeight(); i++){
            temp+= i + " ";
            for(int j = 0; j < siteImage.getWidth(); j++){
                temp += siteImage.getRGB(i, j) + " ";
            }
            sendData.add(temp);
            temp = "";
        }
        return sendData;
    }

    public ArrayList<byte[]> udpNew2(){
        sequenceNumber = 0;
        //int rowNum = 0;
        int currentX = 0;
        int currentY = 0;
        ArrayList<byte[]> bytes = new ArrayList<>();
        while(currentY != siteImage.getHeight() - 1) {
            buf = new byte[516];
            buf[0] = (byte) ((sequenceNumber & 0xff000000) >> 24);
            buf[1] = (byte) ((sequenceNumber & 0x00ff0000) >> 16);
            buf[2] = (byte) ((sequenceNumber & 0x0000ff00) >> 8);
            buf[3] = (byte) (sequenceNumber & 0x000000ff);
            sequenceNumber++;

            for(int i = 4; i < 516; i+=4){
                //System.out.println("Sequence Num: " + sequenceNumber + "    X: " + currentX + "   Y: " + currentY);
                buf[i] = (byte)((siteImage.getRGB(currentX, currentY)& 0xff000000) >> 24);
                buf[i+1] = (byte)((siteImage.getRGB(currentX, currentY)& 0x00ff0000) >> 16);
                buf[i+2] = (byte)((siteImage.getRGB(currentX, currentY)& 0x0000ff00) >> 8);
                buf[i+3] = (byte)((siteImage.getRGB(currentX, currentY)& 0x000000ff));
                if(currentX == siteImage.getWidth() - 1){
                    currentX = 0;
                    if(currentY != siteImage.getHeight() - 1){
                        currentY++;
                        //rowNum++;
                        break;
                    }
                    break;
                }
                else{
                    currentX++;
                }
            }
            bytes.add(buf);
        }
        System.out.println(bytes.size());
        return bytes;
    }


    public byte[] getDimensions(){
        int height = siteImage.getHeight();
        int width = siteImage.getWidth();
        byte[] bytes = new byte[8];

        bytes[0] = (byte) ((width & 0xff000000) >> 24);
        bytes[1] = (byte) ((width & 0x00ff0000) >> 16);
        bytes[2] = (byte) ((width & 0x0000ff00) >> 8);
        bytes[3] = (byte) (width & 0x000000ff);
        bytes[4] = (byte) ((height & 0xff000000) >> 24);
        bytes[5] = (byte) ((height & 0x00ff0000) >> 16);
        bytes[6] = (byte) ((height & 0x0000ff00) >> 8);
        bytes[7] = (byte) (height & 0x000000ff);

        return bytes;
    }
}
