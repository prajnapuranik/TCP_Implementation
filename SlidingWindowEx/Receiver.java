package SlidingWindowEx;
import java.io.*;
import java.net.*;
import java.util.*;

//Server
public class Receiver {

        ServerSocket reciever;
        Socket conc = null;

        ObjectOutputStream out;
        ObjectInputStream in;

        String ack, pkt, data = "";
        int delay;

        int SeqNum = 0, RWS = 5;
        int LFR = 0;
        int LAF = LFR + RWS;

        Random rand = new Random();

        public void run() throws IOException, InterruptedException {
            reciever = new ServerSocket(1500, 10);
            conc = reciever.accept();

            if (conc != null)
                System.out.println("Connection established");

            out = new ObjectOutputStream(conc.getOutputStream());
            in = new ObjectInputStream(conc.getInputStream());

            while (LFR < 15) {
                try {
                    pkt = (String) in.readObject();
                    String[] str = pkt.split("\\s");

                    ack = str[0];

                    LFR = Integer.parseInt(ack);

                    if ((SeqNum <= LFR) || (SeqNum > LAF)) {
                        System.out.println("\nMsg received : " + ack);
                        //delay = rand.nextInt(5);
                        out.writeObject(ack);
                        out.flush();
                        System.out.println("sending ack " + ack);
                        SeqNum++;
                    } else {
                        out.writeObject(LFR);
                        out.flush();
                        System.out.println("resending ack " + LFR);
                    }
                } catch (Exception e) {
                }
            }
            in.close();
            out.close();
            reciever.close();
            System.out.println("\nConnection Terminated.");
        }

        public static void main(String args[]) throws IOException, InterruptedException {
           Receiver R = new Receiver();
           R.run();
        }
}
