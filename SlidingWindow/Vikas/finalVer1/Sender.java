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
    int SegNum=1;
    int randomNumber=0;

        void sendFrameSize() throws IOException {
            String win = String.valueOf(slideWin);
            out.writeObject(win);
            System.out.println("Window size  " + slideWin);
            out.flush();
        }
        void generateRandom(){
            randomNumber = (int)(Math.random() * (Math.pow(2,16) + 1)); //generate number between 1 and 2^16
            randomNumber=(randomNumber*1024)+1; //convert number to a multiple of (1024+1)
        }
        public void sendFrames() throws IOException {
            // generateRandom();
            // System.out.println(randomNumber);
            if(SeqNum == 33793){
                 SeqNum+=1024;
            //     // SeqNum+=1;
              }
            pkt = String.valueOf(SeqNum);
            out.writeObject(pkt);
            System.out.println("Sent  " + SeqNum);
            System.out.println("Seg" + SegNum);
            out.flush();
            SeqNum=SeqNum+1024;
            // SeqNum++;
            SegNum++;
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
            SegNum++;
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

            while ((slideWin <= Math.pow(2,16)+1) && SegNum<=100){ //need to set this limit to 10Million

                System.out.println("---------------------------------");
                sendFrameSize();

                int loopCount=0;

                while ((loopCount < slideWin) && SegNum<=100) { //need to set this limit to 10Million
                    if (SeqNum >= Math.pow(2,16)+1){
                        SeqNum=1;
                    }
                    sendFrames();
                    receiveACK();

                    detectPacketLoss();
                    loopCount++;
                }

                //Modify the sliding window
                //if (SeqNum == ackNum) {
                if((!flag)){
                        if ((slideWin*2)<=(Math.pow(2,16)+1)){slideWin *= 2; }
                        else {continue;}
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