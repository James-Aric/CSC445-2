//import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToFile {
    BufferedImage siteImage;
    String url = "https://upload.wikimedia.org/wikipedia/commons/9/97/The_Earth_seen_from_Apollo_17.jpg";
    int byteLength;

    public ToFile(String url) {
        this.url = url;
        constructPacketData();
        constructPackets();
    }

    public void urlToFile(String url) throws IOException {
        URL site = new URL(url);
        siteImage = ImageIO.read(site);
    }

    ArrayList<int[]> data;

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
        byteLength = data.size();
        return data;
    }


    ArrayList<DatagramPacket> packets;
    DatagramPacket sendPacket;

    ArrayList<byte[]> bytes = new ArrayList<>();

    //int[] to byte[]
    ByteBuffer byteBuffer;
    IntBuffer intBuffer;
    byte[] array;

    //HEADER STUFF
    int version = 2;
    int padding = 0;
    int extension = 0;
    int csrcCount = 1;
    int marker = 0;
    int payloadType = 8;
    int sequenceNumber = 0;
    long timestamp = 1;



    int length = 12 + 200;
    byte buf[];// = new byte[12 + data.get(0).length]; //allocate this big enough to hold the RTP header + audio data
    byte temp[];
    byte combined[];


    public void packetCreation(int num) {
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

        sendPacket = new DatagramPacket(combined, combined.length);
        sendPacket.setPort(3000);
        packets.add(sendPacket);
    }
    /////////////////


    public void constructPackets() {
        packets = new ArrayList<>();
        for(int i = 0; i < data.size(); i++){
            byteBuffer = ByteBuffer.allocate(data.size() * 4);
            intBuffer = byteBuffer.asIntBuffer();
            intBuffer.put(data.get(i));

            array = byteBuffer.array();
            bytes.add(array);
            /*sendPacket = new DatagramPacket(array, array.length);
            packets.add(sendPacket);*/
            packetCreation(i);
        }
    }

    public ArrayList<DatagramPacket> getPackets(){
        return packets;
    }







}
