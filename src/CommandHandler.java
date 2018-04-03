import java.util.Scanner;

public class CommandHandler {
    public static void main(String[]args){
        Scanner input = new Scanner(System.in);
        String userInput = "";
        boolean ipv4 = false;
        boolean sequential = false;
        boolean drops = false;
        String url = "https://i.imgur.com/aIAZsYj.jpg";
        System.out.println("c - Client\t\t\ts - Server\n4 - IPv4\t\t\t6 - IPv6\ns - Sequential\t\tw - Sliding Window\nd - Drops");
        userInput = input.nextLine();

        UDPClient clientServer;
        UDPServer serverServer;

        if(userInput.charAt(1) == '4'){
            ipv4 = true;
        }
        else if(userInput.charAt(1) == '6'){
            ipv4 = false;
        }
        if(userInput.charAt(2) == 's'){
            sequential = true;
        }
        else if(userInput.charAt(2) == 'w'){
            sequential = false;
        }
        if(userInput.charAt(3) == 'd'){
            drops = true;
        }

        if(userInput.charAt(0) == 'c'){
            clientServer = new UDPClient(url, ipv4, sequential, drops);
            try {
                clientServer.run();
            }catch(Exception e){
                System.out.println("Something fucked up in the client");
                e.printStackTrace();
            }
        }
        else if(userInput.charAt(0) == 's'){
            serverServer = new UDPServer(ipv4, sequential, drops);
            try {
                serverServer.run();
            }catch(Exception e){
                System.out.println("Something fucked up in the server");
                e.printStackTrace();
            }
        }
    }
}
