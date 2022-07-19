import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//Server
public class ReceiverV {

        ServerSocket receiver;
        Socket conc = null;

        ObjectOutputStream out;
        ObjectInputStream in;

        String ack, pkt, data = "";
        int SeqNum = 0;
        int count = 0;

        int slideWin = 1;
        int loopCount = 0;

        public void run() throws IOException, InterruptedException {
            receiver = new ServerSocket(1500, 10);
            conc = receiver.accept();

            if (conc != null)
                System.out.println("Connection established");

            out = new ObjectOutputStream(conc.getOutputStream());
            in = new ObjectInputStream(conc.getInputStream());

            do{
                try {
                    pkt = (String) in.readObject();
                    ack = pkt;
                    if (Integer.parseInt(ack)>=8){ 
                        System.out.println("\nMsg received : " + ack);
                        out.writeObject("8");
                        out.flush();
                        System.out.println("Sending Ack 8");
                        continue;
                    }
                    count = Integer.parseInt(ack) + 1;
                    System.out.println("\nMsg received : " + ack);
                    ack = Integer.toString(count);
                    //to send acknowledgement to client
                    out.writeObject(ack);
                    out.flush();
                    System.out.println("Sending Ack " + count);

                    loopCount++;

                } catch (Exception e) {
                }
            }while(loopCount < 16);

            in.close();
            out.close();
            receiver.close();
            System.out.println("\nConnection Terminated.");
        }

        public static void main(String args[]) throws IOException, InterruptedException {
           ReceiverV R = new ReceiverV();
           R.run();
        }
}