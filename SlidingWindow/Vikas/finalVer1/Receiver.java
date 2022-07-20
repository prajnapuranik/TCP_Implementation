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
        double receiveCount=0;
        double sentCount=0;

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
                    try{
                    String lostAck = (String) in.readObject();
                    System.out.println("\nlost message received : " + lostAck);
                    //seqNum++;
                    flag = false;
                    sentCount++;
                    }
                    catch(Exception e){}
                }else
                {
                    //Get sliding window size
                    try {
                        String size = (String) in.readObject();
                        winSize = Integer.parseInt(size);
                        System.out.println("---------------------------------");
                        System.out.println("\nWindow size received : " + winSize);
                    } catch (Exception e){}

                    loopCount = 0;

                    while (loopCount < winSize) {

                        try {
                            pkt = (String) in.readObject();
                            ack = pkt;
                            System.out.println("\nMsg received : " + ack);
                            count = Integer.parseInt(ack);
                            if (seqNum>=Math.pow(2,16)+1){
                                count=1;
                                seqNum=1;
                            }
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

                            //Keep track of number of packets received
                            keepCount();

                            //Calculate goodput for every 1000 segments received
                            if(receiveCount % 10 == 0) {
                                calculateGoodPut();
                            }
                            

                            loopCount++;
                        } catch (EOFException e) {
//                   System.out.println("End");
                        }
                    }
                }
            }
            while(winSize <= Math.pow(2,16)+1);

            in.close();
            out.close();
            receiver.close();
            System.out.println("\nConnection Terminated.");
        }

        private void keepCount(){
            //keep count of number of received packets
            if(seqNum/1024 < receiveCount){
                //this condition means maximum sliding window is achieved and packet seqNo has been reset to 1
                receiveCount += (seqNum/1024);
            }else {
                //first iteration
                receiveCount = seqNum / 1024;
            }
        }

        private void calculateGoodPut(){

            System.out.println("Receiver count is: " +receiveCount);
            System.out.println("Sender count is: " +sentCount);
            
                System.out.println("Good put is:" + receiveCount/(receiveCount + sentCount));
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