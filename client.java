import java.net.*;
import java.io.*;

public class client{
    public static void main(String [] args) throws IOException{
        Socket s = new Socket("localhost",4999);

//        //To RECEIVE data from server
//        InputStreamReader in = new InputStreamReader(s.getInputStream());
//        BufferedReader bf = new BufferedReader(in);
//        String str=bf.readLine();
//        System.out.println("server:"+str);

        //To SEND data to server
        //pr.println("is it working");
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);

        PrintWriter pr = new PrintWriter(s.getOutputStream());

        for(int i=1;i<=10;i++){
            pr.println(i);
            //pr.flush();
            String str=bf.readLine();

            if(!str.equalsIgnoreCase("ACK")){
                break;
            }else{
                System.out.println("Acknowledged");
            }

        }

    }
}