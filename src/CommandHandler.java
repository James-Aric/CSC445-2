import java.util.Scanner;

public class CommandHandler {
    public static void main(String[]args){
        Scanner input = new Scanner(System.in);
        String userInput = "";
        boolean client = false;
        boolean ipv4 = false;
        boolean sequential = false;
        boolean drops = false;
        String url = "http://cheb-room.ru/uploads/cheb/2016/11/w9RC4W-QqXw-200x200.jpg";
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
            client = true;
            clientServer = new UDPClient(url, ipv4, sequential, drops);
            try {
                clientServer.run();
            }catch(Exception e){
                System.out.println("Something fucked up in the client");
            }
        }
        else if(userInput.charAt(0) == 's'){
            client = false;
            serverServer = new UDPServer(ipv4, sequential, drops);
            try {
                serverServer.run();
            }catch(Exception e){
                System.out.println("Something fucked up in the server");
            }
        }

        /*
        if(userInput.equals("c")){
                System.out.println("u - UDP \nt - TCP");
                client = true;
                userInput = input.nextLine();
                if(userInput.equals("u")){
                    udp = true;
                }
                else if(userInput.equals("t")){
                    udp = false;
                }
                else{
                    System.out.println("Invalid command.");
                    System.exit(0);
                }
            }
            else if(userInput.equals("s")){
                if(userInput.equals("u")){
                    udp = true;
                }
                else if(userInput.equals("t")){
                    udp = false;
                }
                else{
                    System.out.println("Invalid command.");
                    System.exit(0);
                }
            }
            else{
                System.out.println("Invalid command");
            }
         */
    }
}
