package SlidingWindowEx;

import java.io.*;
import java.net.*;

//Client
public class Sender {

        Socket sender;

        ObjectOutputStream out;
        ObjectInputStream in;

        String pkt;
        int SeqNum = 1, SWS = 5;
        int LAR = 0, LFS = 0;
        int NF;

        public void SendFrames() throws IOException {
            if((SeqNum<=15)&&(SWS > (LFS - LAR)) )
            {
                    NF = SWS - (LFS - LAR);
                    for(int i=0;i<NF;i++)
                    {
                        pkt = String.valueOf(SeqNum);
                        out.writeObject(pkt);
                        LFS = SeqNum;
                        System.out.println("Sent  " + SeqNum);
                        SeqNum++;
                        out.flush();
                    }
            }
        }

        public void run() throws IOException
        {
            sender = new Socket("localhost",1500);

            out = new ObjectOutputStream(sender.getOutputStream());
            in = new ObjectInputStream(sender.getInputStream());

            while(LAR<15)
            {
                try
                {
                    SendFrames();
                    String Ack = (String)in.readObject();
                    LAR = Integer.parseInt(Ack);
                    System.out.println("ack received : " + LAR);
                }
                catch(Exception e)
                {
                }
            }

            in.close();
            out.close();
            sender.close();
            System.out.println("\nConnection Terminated.");
        }

        public static void main(String as[]) throws IOException
        {
            Sender s = new Sender();
            s.run();
        }
}
