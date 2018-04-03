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



    public ToFile(String url) throws IOException{
        this.url = url;
        urlToFile(url);
    }

    public void urlToFile(String url) throws IOException {
        URL site = new URL(url);
        siteImage = ImageIO.read(site);
        //System.out.println(siteImage.getType());
    }

    public ArrayList<byte[]> udpData(){
        sequenceNumber = 0;
        //int rowNum = 0;
        int currentX = 0;
        int currentY = 0;
        ArrayList<byte[]> bytes = new ArrayList<>();
        while(currentY != siteImage.getHeight()) {
            buf = new byte[516];
            buf[0] = (byte) ((sequenceNumber & 0xff000000) >> 24);
            buf[1] = (byte) ((sequenceNumber & 0x00ff0000) >> 16);
            buf[2] = (byte) ((sequenceNumber & 0x0000ff00) >> 8);
            buf[3] = (byte) (sequenceNumber & 0x000000ff);
            sequenceNumber++;

            for(int i = 4; i < 516; i+=4){
                System.out.println("Sequence Num: " + sequenceNumber + "    X: " + currentX + "   Y: " + currentY);
                buf[i] = (byte)((siteImage.getRGB(currentX, currentY)& 0xff000000) >> 24);
                buf[i+1] = (byte)((siteImage.getRGB(currentX, currentY)& 0x00ff0000) >> 16);
                buf[i+2] = (byte)((siteImage.getRGB(currentX, currentY)& 0x0000ff00) >> 8);
                buf[i+3] = (byte)((siteImage.getRGB(currentX, currentY)& 0x000000ff));
                if(currentX == siteImage.getWidth() - 1){
                    currentX = 0;
                    if(currentY != siteImage.getHeight()){
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
        System.out.println(bytes.size() * 516 + " bytes total");
        //System.out.println(bytes.size());
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
