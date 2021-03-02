import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TicTacToeClient {

    private JFrame frame = new JFrame("Tic Tac Toe");
    private JLabel messageLabel = new JLabel("");
    private JPanel boardPanel = new JPanel();
    Color playerColor;
    Color opponentColor;
    String playerMark;
    String opponentMark;
    private Square[] board = new Square[9];
    private Square currentPiece;

    private static int PORT = 8001;
    public Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    private final Font largerFont = new Font("Verdana", Font.BOLD, 95);

    public void generateConnection(String serverAddress) throws IOException {
        socket = new Socket(serverAddress, PORT);
        input = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
    }

    public void generateBoard() {
        messageLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(messageLabel, "South");

        boardPanel.setBackground(Color.black);
        boardPanel.setLayout(new GridLayout(3, 3, 2, 2));
        for (int i = 0; i < board.length; i++) {
            final int j = i;
            board[i] = new Square();
            board[i].addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    currentPiece = board[j];
                    output.println("MOVE " + j);}});
            boardPanel.add(board[i]);
        }
        frame.getContentPane().add(boardPanel, "Center");
    }

   public void game() throws Exception {
        String response;
       response = input.readLine();
       if (response.startsWith("WELCOME")) {
           char play = response.charAt(8);

           playerColor = play == 'X' ? Color.PINK : Color.ORANGE;
           opponentColor = play == 'X' ? Color.ORANGE : Color.PINK;
           playerMark = play == 'X' ? "X" : "O";
           opponentMark = play == 'X' ? "O" : "X";

           frame.setTitle("Tic Tac Toe  Player " + play);
       }
       while (true) {
           response = input.readLine();

           if (response.startsWith("VALID_MOVE")) {
               messageLabel.setText("Valid move, please wait");
               currentPiece.setBackground(playerColor);
               currentPiece.label.setFont(largerFont);
               currentPiece.label.setText(playerMark);
               System.out.println(currentPiece.label.getVerticalAlignment());

               currentPiece.repaint();
           } else if (response.startsWith("OPPONENT_MOVED")) {
               int loc = Integer.parseInt(response.substring(15));
               board[loc].setBackground(opponentColor);
               board[loc].label.setFont(largerFont);
               board[loc].label.setText(opponentMark);
               board[loc].repaint();
               messageLabel.setText("Opponent moved, your turn");
           } else if (response.startsWith("VICTORY")) {
               messageLabel.setText("Congrats!, You win");
               break;
           } else if (response.startsWith("DEFEAT")) {
               messageLabel.setText("Sorry, you lose");
               break;
           } else if (response.startsWith("DRAW")) {
               messageLabel.setText("It's a Draw");
               break;
           } else if (response.startsWith("MESSAGE")) {
               messageLabel.setText(response.substring(8));
           }


       }

   }





    //Dibuja el cuadrado donde se juega
    static class Square extends JPanel {
        JLabel label = new JLabel((Icon)null);

        public Square() {
            setBackground(Color.white);
            add(label);


        }

    }
    private boolean Close() {
        int response = JOptionPane.showConfirmDialog(frame,
                "Game Over",
                "Tic Tac Toe",
                JOptionPane.CLOSED_OPTION);
        frame.dispose();
        return response == JOptionPane.CLOSED_OPTION;
    }


    public static void main(String[] args) throws Exception {


        String serverAddress = (args.length == 0) ? "localhost" : args[1];

        TicTacToeClient ticTacToeClient = new TicTacToeClient();
        ticTacToeClient.generateConnection(serverAddress);
        ticTacToeClient.generateBoard();


        while (true) {
            ticTacToeClient.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ticTacToeClient.frame.setSize(380, 400);
            ticTacToeClient.frame.setVisible(true);
            ticTacToeClient.frame.setResizable(false);
            ticTacToeClient.game();

            if (!ticTacToeClient.Close()) {
                break;
            }

        }


    }


}