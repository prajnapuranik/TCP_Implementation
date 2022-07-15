import java.net.*;
import java.io.*;

public class server{
    public static void main(String [] args) throws IOException {
        ServerSocket ss = new ServerSocket(4999);
        Socket s = ss.accept();
        System.out.println("Client Connected");

//        //To SEND data to client
//        PrintWriter pr = new PrintWriter(s.getOutputStream());
//        pr.println("Hi Client!");
//        pr.flush();

        //To RECEIVE data from client
//        InputStreamReader in = new InputStreamReader(s.getInputStream());
//        BufferedReader bf = new BufferedReader(in);
//        String str=bf.readLine();
//        //Prints data sent from client and stored in str
//        System.out.println("client:"+str);

//        for (int i = 1; i <= 10; i++) {
            InputStreamReader in = new InputStreamReader(s.getInputStream());
            BufferedReader bf = new BufferedReader(in);
            String str = bf.readLine();
            System.out.println("client:" + str);

            PrintWriter pr = new PrintWriter(s.getOutputStream());
            pr.println("ACK");
            //pr.flush();

//        }
    }
}