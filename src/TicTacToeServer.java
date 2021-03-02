import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
public class TicTacToeServer {

    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(8001);
        System.out.println("Tic Tac Toe Server is Running");
        try {
            while (true) {
                Game game = new Game();
                Game.Player playerX = game.new Player(listener.accept(), 'X');
                Game.Player playerO = game.new Player(listener.accept(), 'O');
                playerX.setOpponent(playerO);
                playerO.setOpponent(playerX);
                game.currentPlayer = playerX;
                playerX.start();
                playerO.start();
            }
        } finally {
            listener.close();
        }
    }
}

class Game {
    // Arreglo del tablero
    public Player[] board = {
            null, null, null,
            null, null, null,
            null, null, null};

    // Chequea si alguno de los jugadores a ganado
    public boolean checkWinner() {
        return
                (board[0] != null && board[0] == board[1] && board[0] == board[2])
                        ||(board[3] != null && board[3] == board[4] && board[3] == board[5])
                        ||(board[6] != null && board[6] == board[7] && board[6] == board[8])
                        ||(board[0] != null && board[0] == board[3] && board[0] == board[6])
                        ||(board[1] != null && board[1] == board[4] && board[1] == board[7])
                        ||(board[2] != null && board[2] == board[5] && board[2] == board[8])
                        ||(board[0] != null && board[0] == board[4] && board[0] == board[8])
                        ||(board[2] != null && board[2] == board[4] && board[2] == board[6]);
    }

    //jugador actual
    Player currentPlayer;


    // hilo del jugador por si trata de hacer un move
    public synchronized boolean legalMove(int location, Player player) {
        if (player == currentPlayer && board[location] == null) {
            board[location] = currentPlayer;
            currentPlayer = currentPlayer.opponent;
            currentPlayer.otherPlayerMoved(location);
            return true;
        }
        return false;
    }

    // Chequea si el tablero esta lleno o no
    public boolean checkBoardStatus() {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == null) {
                return false;
            }
        }
        return true;
    }
    class Player extends Thread {

        char mark;
        Player opponent;
        Socket socket;
        BufferedReader input;
        PrintWriter output;
        // Aqui es donde se inicializa los canales para la conexion
        public Player(Socket socket, char mark) {
            this.socket = socket;
            this.mark = mark;
            try {
                input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                output.println("WELCOME " + mark);
                output.println("MESSAGE Waiting for opponent to connect");
            } catch (IOException e) {
                System.out.println("A player disconnected: " + e);
            }
        }
        //Se asigna el oponente
        public void setOpponent(Player opponent) {
            this.opponent = opponent;
        }


        //Si el otro jugador hizo una movida
        public void otherPlayerMoved(int location) {
            output.println("OPPONENT_MOVED " + location);
            output.println(
                    checkWinner() ? "DEFEAT" : checkBoardStatus() ? "DRAW" : "");
        }
        private void clearBoard() {
            for (int i = 0; i < 9; i++) {
                board[i] = null;
            }

        }

        public void run() {
            try {
                // Cuando los dos Jugadores estan conectados
                output.println("MESSAGE 2 playes Connected");

                // Avisa al jugador que es su turno
                if (mark == 'X') {
                    output.println("MESSAGE Your move");
                }

                // La comunicacion entre los dos clients por el servidor
                while (true) {
                    String command = input.readLine();
                    if (command.startsWith("MOVE")) {

                        int location = Integer.parseInt(command.substring(5));
                        if (legalMove(location, this)) {
                            output.println("VALID_MOVE");
                            output.println(checkWinner() ? "VICTORY"
                                    : checkBoardStatus() ? "DRAW"
                                    : "");
                            if(checkWinner()) {

                                System.out.println( "player" + this.mark);
                            }
                        } else {
                            output.println("MESSAGE Cannot move yet");
                        }
                    } else if (command.startsWith("QUIT")) {
                        try{socket.close();} catch (IOException e) {}
                        return;
                    } else if (command.startsWith("CLEAN")) {
                        clearBoard();
                        output.println("WELCOME " + mark);

                    }
                }
            } catch (IOException e) {
                System.out.println("A player disconnected: " + e);
            } finally {
                try {socket.close(); System.out.println("Socket closed on server side for : player " + currentPlayer);} catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}