package TCP_Implementation.SlidingWindow;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//Server
public class Receiver {

        ServerSocket receiver;
        Socket conc = null;

        ObjectOutputStream out;
        ObjectInputStream in;

        String ack, pkt= "";
        int count = 0;
        int winSize = 0;
        int loopCount = 0;
        int seqNum = 1;
        boolean flag = false;

        private int getPrevSeqNum(){
            return seqNum;
        }

//        private int getNextSeqNum(){
//
//        }

        public void run() throws IOException, InterruptedException, ClassNotFoundException {
            receiver = new ServerSocket(1500, 10);
            conc = receiver.accept();

            if (conc != null)
                System.out.println("Connection established");

            out = new ObjectOutputStream(conc.getOutputStream());
            in = new ObjectInputStream(conc.getInputStream());

            do {
                //make up for packet loss
                if(flag){
                    String lostAck = (String) in.readObject();
                    System.out.println("\nlost message received : " + lostAck);
                    //seqNum++;
                    flag = false;
                }else {
                    //Get sliding window size
                    try {
                        String size = (String) in.readObject();
                        winSize = Integer.parseInt(size);
                        System.out.println("---------------------------------");
                        System.out.println("\nWindow size received : " + winSize);
                    } catch (Exception e) {
                    }

                    loopCount = 0;

                    while (loopCount < winSize) {
                        try {
                            pkt = (String) in.readObject();
                            ack = pkt;
                            System.out.println("\nMsg received : " + ack);
                            count = Integer.parseInt(ack);

                            if (count != getPrevSeqNum()) {
                                //lost packet
                                System.out.println("Lost packet!");
                                flag = true;
                                sendACK(getPrevSeqNum());
                            } else {
                                //sendACK(count + 1);
                                sendACK(count + 1024);
                            }
                            //seqNum = count + 1;
                            seqNum = count + 1024;
                            loopCount++;
                        } catch (EOFException e) {
//                   System.out.println("End");
                        }
                    }
                }
            }while(winSize < 10);

            in.close();
            out.close();
            receiver.close();
            System.out.println("\nConnection Terminated.");
        }

        //to send acknowledgement to client
        private void sendACK(int count){
            try {
                ack = Integer.toString(count);
                out.writeObject(ack);
                out.flush();
                System.out.println("Sending Ack " + count);
            }catch(Exception e){
            }
        }
        public static void main(String args[]) throws IOException, InterruptedException, ClassNotFoundException {
           Receiver R = new Receiver();
           R.run();
        }
}
