import java.util.Scanner;

public class CommandHandler {
    public static void main(String[]args){
        Scanner input = new Scanner(System.in);
        String userInput = "";
        boolean udp = false;
        boolean client = false;
        boolean window = false;
        System.out.println("c - Client\t\t\ts - Server\nt - TCP\t\t\t\tu - UDP\ns - Sequential\t\tw - Sliding Window");
        userInput = input.next();
        while(!userInput.equals("q")){
            if(userInput.equals("c")){
                client = true;
            }
            else{
                client = false;
            }
            userInput = input.next();
            if(userInput.equals("t")){
                udp = false;
            }
            else{
                udp = true;
            }
            userInput = input.next();
            if(userInput.equals("s")){
                window = false;
            }
            else{
                window = true;
            }
            break;
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
