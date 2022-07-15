package TCP_Implementation.SlidingWindowImpl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//Client
public class Sender {

        Socket sender;

        ObjectOutputStream out;
        ObjectInputStream in;

        String pkt;
        int SeqNum = 1;
        int ackNum = 0;

        public void sendFrames() throws IOException {
            System.out.println("---------------------------------");
            pkt = String.valueOf(SeqNum);
            out.writeObject(pkt);
            System.out.println("Sent  " + SeqNum);
            out.flush();
        }

        int getNextSeqNumber(){
            return ++SeqNum;
        }

        public void run() throws IOException
        {
            sender = new Socket("localhost",1500);

            out = new ObjectOutputStream(sender.getOutputStream());
            in = new ObjectInputStream(sender.getInputStream());

            do{
                try
                {
                    sendFrames();
                    String Ack = (String)in.readObject();
                    ackNum = Integer.parseInt(Ack);
                    System.out.println("ACK received : " + ackNum);
                }
                catch(Exception e)
                {
                }
            }while(SeqNum < 15 && getNextSeqNumber() == ackNum);


            in.close();
            out.close();
            sender.close();
            System.out.println("\nConnection Terminated");
        }

        public static void main(String as[]) throws IOException
        {
            Sender s = new Sender();
            s.run();
        }
}
