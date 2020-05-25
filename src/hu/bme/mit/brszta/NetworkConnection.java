package hu.bme.mit.brszta;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class multiPlayer {

    boolean isServer;
    private ServerSocket serverSocket;
    private Socket socket;
    private int numplayers;
    private ServerSideConnection player1;
    private ClientSideConnection player2;
    public List<ReceiveListener> listeners;
    private boolean clientconnecting=true;

    public multiPlayer(boolean isServer){
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

    public void writeMyCells(boolean isLeft, int myX,int myY){
        if (isServer) {
            player1.writeIntData(isLeft, myX, myY);
        }
        else
        {
            player2.writeIntData(isLeft, myX, myY);
        }
    }


    public void interuptConnection()
    {
        if (isServer) {
            try {

                serverSocket.close();
            }catch (IOException ex){
                System.out.println("Server connecton close ex");
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

    public boolean[][] getBoard(){
        return player2.getObjIn();
    }




    public void acceptConnection(boolean[][] mines) {
        try {

            System.out.println("Waiting for connections....");


            while (numplayers < 2) {
                System.out.println("accept");
                Socket socket = serverSocket.accept();
                numplayers++;
                System.out.println("Player" + numplayers + ". has connected");
                player1 = new ServerSideConnection(socket, mines);
                Thread t = new Thread(player1);
                t.start();

            }

            System.out.println("Max num of players. No longer accepting connections.");

        } catch (Exception ex) {

            System.out.println("Exception from acceptConnecton()");

        }
    }
    public void requestConnection(String host,int port){

        System.out.println("----Client-----");
        while (clientconnecting){
            try {

                socket = new Socket(host, port);
                player2 = new ClientSideConnection(socket);
                break;

            }
            catch (IOException ex)
            {
                System.out.println("Exception from client side connection constructor");
            }
        }
        clientconnecting=true;

    }
    private static class ServerSideConnection implements Runnable{
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private ObjectOutputStream objOut;
        private ObjectInputStream objIn;
        private List<ReceiveListener> listeners;
        public ServerSideConnection(Socket s,boolean[][] mines)
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
//                for (int i = 0; i < mines.length; i++) {
//                    for (int j = 0; j < mines[i].length; j++) {
//                        if (mines[i][j])
//                            System.out.print("1");
//                        else
//                            System.out.print("0");
//                    }
//                    System.out.println();
//                }
                objOut.writeObject(board);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


    private static class ClientSideConnection implements Runnable {
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private ObjectInputStream objIn;
        private List<ReceiveListener> listeners;

        public ClientSideConnection(Socket s)
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

                while(true) {
                    while (dataIn.available() > 0){

                        boolean leftOrRight = dataIn.readBoolean();
                        int oppX = dataIn.readInt();
                        int oppY = dataIn.readInt();
                        boolean received = dataIn.readBoolean();

                        if(received) {
                            for (ReceiveListener listener : listeners)
                                listener.ReceiveData(leftOrRight, oppX, oppY);
                        }
                        received =false;
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
                System.out.println("Exception from client side write data");
            }
        }

        public boolean[][] getObjIn(){
            boolean[][] board = new boolean[0][];
            try {

                BoardInitMatrix inBoard = (BoardInitMatrix)objIn.readObject();
                board = inBoard.getMatrix();

                for (int i = 0; i < board.length; i++) {
                    for (int j = 0; j < board[i].length; j++) {
                        if (board[i][j])
                            System.out.print("1");
                        else
                            System.out.print("0");
                    }
                    System.out.println();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return board;
        }
    }

}
