package SlidingWindowImpl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

//Server
public class Receiver {

        ServerSocket receiver;
        Socket conc = null;

        ObjectOutputStream out;
        ObjectInputStream in;

        String ack, pkt, data = "";
        int SeqNum = 0;
        int count = 0;

        Random rand = new Random();

        public void run() throws IOException, InterruptedException {
            receiver = new ServerSocket(1500, 10);
            conc = receiver.accept();

            if (conc != null)
                System.out.println("Connection established");

            out = new ObjectOutputStream(conc.getOutputStream());
            in = new ObjectInputStream(conc.getInputStream());

            while (count <= 15) {
                try {
                    pkt = (String) in.readObject();
                    ack = pkt;
                    count = Integer.parseInt(ack) + 1;
                    System.out.println("\nMsg received : " + ack);
                    ack = Integer.toString(count);

                    //to send acknowledgement to client
                    out.writeObject(ack);
                    out.flush();
                    System.out.println("Sending Ack " + count);
                } catch (Exception e) {
                }
            }
            in.close();
            out.close();
            receiver.close();
            System.out.println("\nConnection Terminated.");
        }

        public static void main(String args[]) throws IOException, InterruptedException {
           Receiver R = new Receiver();
           R.run();
        }
}
