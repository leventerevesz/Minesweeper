package hu.bme.mit.brszta;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Class containing all network-related functionality.
 */
public class NetworkConnection {

    boolean isServer;
    private ServerSocket serverSocket;
    private Socket socket;
    private int numplayers;
    private HostSideConnection player1;
    private GuestSideConnection player2;
    public List<ReceiveListener> listeners;
    private boolean clientconnecting=true;

    public NetworkConnection(boolean isServer){
        this.isServer =isServer;
        this.listeners = new ArrayList<>();

        if(this.isServer){
            System.out.println("-----Game Server-----");
            this.numplayers = 1;

            try
            {
                serverSocket=new ServerSocket(51734);
            }
            catch (IOException ex)
            {
                System.out.println("Exception from constructor");
            }
        }
    }


    /**
     * Connect Click event listener to this network connection.
     * @param listener
     */
    public void addReceiveListener(ReceiveListener listener)
    {
        listeners.add(listener);
        if (isServer) {
            player1.setListeners(listeners);
        }
        else
        {
            player2.setListeners(listeners);
        }
    }

    public void sendClick(boolean isLeft, int myX, int myY){
        if (isServer) {
            player1.writeIntData(isLeft, myX, myY);
        }
        else
        {
            player2.writeIntData(isLeft, myX, myY);
        }
    }

    public void interruptConnection()
    {
        if (isServer) {
            try {
                serverSocket.close();
            }catch (IOException ex){
                System.out.println("Server connection close ex");
            }
        }
        else{
            clientconnecting=false;
        }
    }


    public void requestingData() {
        Thread t = new Thread(player2);
        t.start();

    }

    /**
     * Receive board initializer matrix from the host.
     * @return BoardInitMatrix
     * @see BoardInitMatrix
     * @see BoardBuilder
     */
    public boolean[][] getBoardInitMatrix(){
        return player2.getObjIn();
    }

    /**
     * Set up connection from the server side, and immediately send the board initializer matrix.
     * @param booleanMatrix : board initializer matrix
     * @see BoardInitMatrix
     * @see BoardBuilder
     * @return
     */
    public boolean acceptConnection(boolean[][] booleanMatrix) {
        boolean connected=false;
        try {
            System.out.println("Waiting for connections....");
            while (numplayers < 2) {
                Socket socket = serverSocket.accept();
                numplayers++;
                System.out.println("Player" + numplayers + ". has connected");
                player1 = new HostSideConnection(socket, booleanMatrix);
                Thread t = new Thread(player1);
                t.start();
                connected=true;
            }
            System.out.println("Maximum number of players. No longer accepting connections.");

        } catch (Exception ex) {
            connected=false;
            System.out.println("Exception from acceptConnection()");
        }
        return connected;
    }

    /**
     * Attempt connecting to the given host.
     * @param hostAddress
     * @param port
     * @return
     */
    public boolean requestConnection(String hostAddress, int port){
        boolean connected=false;
        System.out.println("----Client-----");
        while (clientconnecting){
            try {
                socket = new Socket(hostAddress, port);
                player2 = new GuestSideConnection(socket);
                connected=true;
                break;
            }
            catch (IOException ex)
            {
                connected=false;
                System.out.println("Exception from client side connection constructor");
            }
        }
        clientconnecting=true;
        return connected;
    }


    /**
     * Host side communication.
     * Send board init matrix, listen to incoming click events, send outgoing click events.
     */
    private static class HostSideConnection implements Runnable{
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private ObjectOutputStream objOut;
        private ObjectInputStream objIn;
        private List<ReceiveListener> listeners;
        public HostSideConnection(Socket s, boolean[][] mines)
        {
            socket=s;
            try
            {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                objOut= new ObjectOutputStream(socket.getOutputStream());
                sendBoard(mines);
            }
            catch (IOException ex)
            {
                System.out.println("Exception from ssc constructor");
            }
        }

        public void run()
        {
            try
            {

                while(true) {
                    while (dataIn.available() > 0){
                        boolean LeftOrRight=dataIn.readBoolean();
                        int x = dataIn.readInt();
                        int y = dataIn.readInt();
                        boolean received = dataIn.readBoolean();

                        if(received) {
                            for (ReceiveListener listener : listeners)
                                listener.ReceiveData(LeftOrRight,x, y);
                        }
                    }
                }
            }
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(null,"A kapcsolat megszakadt próbáld újra","Hiba",JOptionPane.ERROR_MESSAGE);
                System.out.println("Exception from run() ");
            }
        }
        public void setListeners(List<ReceiveListener> listeners) {
            this.listeners = listeners;
        }

        public void writeIntData(boolean LOrR, int myX,int myY){
            try
            {
                dataOut.writeBoolean(LOrR);
                dataOut.writeInt(myX);
                dataOut.writeInt(myY);
                dataOut.writeBoolean(true);
                dataOut.flush();
            }
            catch (IOException ex)
            {
                System.out.println("Exception from client side write data");
            }
        }

        public void sendBoard(boolean[][] mines){
            try {

                BoardInitMatrix board = new BoardInitMatrix(mines);
                objOut.writeObject(board);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Guest side communication.
     * Receive board init matrix, listen to incoming click events, send outgoing click events.
     */
    private static class GuestSideConnection implements Runnable {
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private ObjectInputStream objIn;
        private List<ReceiveListener> listeners;

        public GuestSideConnection(Socket s)
        {
            socket=s;
            try
            {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                objIn = new ObjectInputStream(socket.getInputStream());
            }
            catch (IOException ex)
            {
                System.out.println("Exception from csc constructor");
            }
        }

        public void run()
        {
            try
            {
                while (true) {
                    while (dataIn.available() > 0){
                        boolean leftOrRight = dataIn.readBoolean();
                        int oppX = dataIn.readInt();
                        int oppY = dataIn.readInt();
                        boolean received = dataIn.readBoolean();

                        if(received) {
                            for (ReceiveListener listener : listeners)
                                listener.ReceiveData(leftOrRight, oppX, oppY);
                        }
                    }
                }
            }
            catch (IOException ex)
            {
                System.out.println("Exception from run() ");
            }
        }

        public void setListeners(List<ReceiveListener> listeners) {

            this.listeners = listeners;
        }

        public void writeIntData(boolean LOrR, int myX,int myY){
            try
            {
                dataOut.writeBoolean(LOrR);
                dataOut.writeInt(myX);
                dataOut.writeInt(myY);
                dataOut.writeBoolean(true);
                dataOut.flush();
            }
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(null,"A kapcsolat megszakadt próbáld újra","Hiba",JOptionPane.ERROR_MESSAGE);
                System.out.println("Exception from client side write data");
            }
        }

        public boolean[][] getObjIn(){
            boolean[][] board = new boolean[0][];
            try {
                BoardInitMatrix inBoard = (BoardInitMatrix)objIn.readObject();
                board = inBoard.getMatrix();
                System.out.print("Received the BoardInitMatrix.\n\n");
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null,"Az adatok fogadása nem lehetséges, próbáld újra.","Hiba",JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
            return board;
        }
    }
}
