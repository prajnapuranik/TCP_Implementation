package TCP_Implementation.SlidingWindow.Vikas;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//Client
public class SenderV {

        Socket sender;

        ObjectOutputStream out;
        ObjectInputStream in;

        String pkt,ackNumstring;
        int SeqNum = 1;
        int ackNum = 1;
        int i=0;

        int slideWin = 1;
        int loopCount = 0;
        int prevAckNum=0;


        public void sendFrames(int windowSize) throws IOException {
            System.out.println("---------------------------------");
            for(i=0;i<windowSize;i++){
                pkt = String.valueOf(SeqNum);
                out.writeObject(pkt);
                System.out.println("Sent  " + SeqNum);                
                getNextSeqNumber();
            }
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
                  if (ackNum==(SeqNum)){
                    sendFrames(slideWin);
                    slideWin *= 2;
                    System.out.println("Sliding Window Size: "+slideWin);
                    loopCount++;
                    if (slideWin>=8){ continue;}
                }
                    String Ack = (String)in.readObject();
                    ackNum = Integer.parseInt(Ack);
                    if (prevAckNum==ackNum) { System.out.println("duplicate");
                    ackNumstring=String.valueOf(ackNum);
                    out.writeObject(ackNumstring);
                    out.flush();  }
                    else {prevAckNum=ackNum;}
                    System.out.println("ACK received : " + ackNum);
                    
                }
                catch(Exception e)
                {
                }
            }while(loopCount < slideWin);


            // in.close();
            // out.close();
            // sender.close();
            // System.out.println("\nConnection Terminated");
        }

        public static void main(String as[]) throws IOException
        {
            SenderV s = new SenderV();
            s.run();
        }
}