
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

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
        int segNum=1;
        double received=0;
        boolean flag = false;
        double goodput=0;
        double resentCount=0;

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
                    resentCount++;
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
                            // keepCount();
                            
                            segNum++;

                            //Calculate goodput for every 1000 segments received
                            if((segNum+resentCount)% 1000 == 0) {
                                calculateGoodPut();
                            }
                            if((segNum+resentCount)% 10000==0){
                            writeFiles();
                            }

                            loopCount++;
                        } catch (EOFException e) {
//                   System.out.println("End");
                        }
                    }
                }
            }while(winSize <= Math.pow(2,16)+1);

            in.close();
            out.close();
            receiver.close();
            System.out.println("\nConnection Terminated.");
        }

        private void calculateGoodPut() throws IOException{
            System.out.println("Receive Count is:" + segNum);
            goodput=segNum/(segNum + resentCount);
            System.out.println("Good put is:" + goodput);
            System.out.println("Sent Count is:" +resentCount);
            File file2=new File("goodput.csv");
            FileWriter fw2 =new FileWriter(file2,true);
            PrintWriter pw2 = new PrintWriter(fw2);
            pw2.println(goodput+",");
            pw2.close();
        }
        private void writeFiles() throws IOException{
        File file1=new File("dropped.csv");
        File file3=new File("received.csv");
        FileWriter fw1 =new FileWriter(file1,true);
        PrintWriter pw1 = new PrintWriter(fw1);
        FileWriter fw3 =new FileWriter(file3,true);
        PrintWriter pw3 = new PrintWriter(fw3);
        received=segNum+resentCount;
        pw1.println(resentCount+",");
        pw3.println(received+",");
        pw1.close();
        pw3.close();
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