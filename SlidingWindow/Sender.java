package TCP_Implementation.SlidingWindow;

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
        int missingSeq = 0;
        int slideWin = 1;
        boolean flag = false;

        void sendFrameSize() throws IOException {
            String win = String.valueOf(slideWin);
            out.writeObject(win);
            System.out.println("Window size  " + slideWin);
            out.flush();
        }

        public void sendFrames() throws IOException {

//            if(SeqNum == 4097 || SeqNum == 7169){
//                SeqNum+=1024;
//            }
            pkt = String.valueOf(SeqNum);
            out.writeObject(pkt);
            System.out.println("Sent  " + SeqNum);
            out.flush();
            //SeqNum++;
            SeqNum=SeqNum+1024;
        }

        public void sendLostFrame(int lostSeq) throws IOException {

            System.out.println("-----sending lost packet!-------------");
            pkt = String.valueOf(lostSeq);
            out.writeObject(pkt);
            int lost = Integer.parseInt(pkt);
            System.out.println("Sent  " + lost);
            out.flush();
            flag = false;
            slideWin +=1;
        }

        //calculate the next sequence number
        int getNextSeqNumber(){
            return SeqNum;
        }

        //calculate the previous sequence number to verify ack received
        int getPrevSeqNumber(){
            //return --SeqNum;
            return SeqNum - 1024;
        }


        public void run() throws IOException, ClassNotFoundException {
            sender = new Socket("localhost", 1500);

            //sender = new Socket("192.168.103.124", 1500);
            out = new ObjectOutputStream(sender.getOutputStream());
            in = new ObjectInputStream(sender.getInputStream());

            while (slideWin < 5){

                System.out.println("---------------------------------");
                sendFrameSize();

                int loopCount=0;

                while (loopCount < slideWin) {

                    sendFrames();
                    receiveACK();

                    detectPacketLoss();
                    loopCount++;
                }

                //Modify the sliding window
                //if (SeqNum == ackNum) {
                if(!flag){
                    slideWin *= 2;
                }
                else{
                    //next packet to be sent is packet with the received ACK no
                    //flag = false;
                    //missingSeq = ackNum;
                    if(slideWin > 1)
                        slideWin = slideWin / 2;
                    sendLostFrame(missingSeq);
                }
            }

            in.close();
            out.close();
            sender.close();
            System.out.println("\nConnection Terminated");
        }

        private void detectPacketLoss() {
            //return SeqNum > ackNum ? true: false;
            if(SeqNum > ackNum){
                if(!flag) {
                    missingSeq = ackNum;
                    flag = true;
                }
            }

        }

        private void receiveACK() {
            try {
                String Ack = (String) in.readObject();
                ackNum = Integer.parseInt(Ack);
                System.out.println("ACK received : " + ackNum);
            }catch (Exception e) {
            }
        }

        public static void main(String args[]) throws IOException, ClassNotFoundException {
            Sender s = new Sender();
            s.run();
        }
}
