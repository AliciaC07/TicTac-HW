import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class SinglePlayer {
    private JFrame frame = new JFrame("Tic Tac Toe");

    private JPanel boardPanel = new JPanel();
    Color playerColor;
    String playerMark = "X";
    String opponentMark = "O";
    private boolean machineTurn = false;
    private boolean playerTurn = false;
    private Square[] board = new Square[9];
    private Square currentPiece;
    private final Font largerFont = new Font("Verdana", Font.BOLD, 95);

    public void genBoard() {


        boardPanel.setBackground(Color.black);
        boardPanel.setLayout(new GridLayout(3, 3, 2, 2));
        for (int i = 0; i < board.length; i++) {
            final int j = i;
            board[i] = new Square();
            board[i].addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    currentPiece = board[j];
                    playerTurn = false;
                    machineTurn = true;
                    playerColor =  Color.PINK;
                    currentPiece.setBackground(playerColor);
                    currentPiece.label.setFont(largerFont);
                    currentPiece.label.setText(playerMark);
                    if (checkBoardStatus() && !checkWinner(playerMark)){
                        CloseDraw();
                    }else if (checkWinner(playerMark)){
                        Close(playerMark);
                    }
                    if(machineTurn){
                        boolean confirm = false;
                        while (!confirm){
                            Random rand = new Random();
                            int value = rand.nextInt(8);
                            if (board[value].label.getText() == null){
                                currentPiece = board[value];
                                playerTurn = true;
                                machineTurn = false;
                                playerColor =  Color.ORANGE;
                                currentPiece.setBackground(playerColor);
                                currentPiece.label.setFont(largerFont);
                                currentPiece.label.setText(opponentMark);
                                if (checkBoardStatus() && !checkWinner(opponentMark)){
                                    CloseDraw();
                                }
                                else if (checkWinner(opponentMark)){
                                    Close(opponentMark);
                                }
                                confirm = true;
                            }
                        }


                    }
                }
            });
            boardPanel.add(board[i]);
        }
        frame.getContentPane().add(boardPanel, "Center");
    }
    static class Square extends JPanel {
        JLabel label = new JLabel((Icon)null);

        public Square() {
            setBackground(Color.white);
            add(label);


        }

    }
    private boolean Close(String player) {
        int response = JOptionPane.showConfirmDialog(frame,
                "Game Over, Player " + player+" Wins!",
                "Tic Tac Toe",
                JOptionPane.CLOSED_OPTION);
        frame.dispose();
        return response == JOptionPane.CLOSED_OPTION;
    }
    private boolean CloseDraw() {
        int response = JOptionPane.showConfirmDialog(frame,
                "Game Over, It's a DRAW ",
                "Tic Tac Toe",
                JOptionPane.CLOSED_OPTION);
        frame.dispose();
        return response == JOptionPane.CLOSED_OPTION;
    }
    public boolean checkBoardStatus() {
        for (int i = 0; i < board.length; i++) {
            if (board[i].label.getText() == null) {
                return false;
            }
        }
        return true;
    }

    public boolean checkWinner(String winnner){
       if (board[0].label.getText() == winnner && board[1].label.getText() == winnner && board[2].label.getText() == winnner){
           return true;
       }
       else if (board[0].label.getText() == winnner && board[3].label.getText() == winnner && board[6].label.getText() == winnner){
            return true;
        }
       else if (board[1].label.getText() == winnner && board[4].label.getText()== winnner && board[7].label.getText() == winnner){
            return true;
        }
       else if (board[2].label.getText() == winnner && board[5].label.getText()== winnner && board[8].label.getText()== winnner){
            return true;
        }
       else if (board[3].label.getText() == winnner && board[4].label.getText() == winnner && board[5].label.getText() == winnner){
            return true;
        }
       else if (board[6].label.getText() == winnner && board[7].label.getText() == winnner && board[8].label.getText() == winnner){
            return true;
        }
       else if (board[2].label.getText() == winnner && board[4].label.getText()== winnner && board[6].label.getText() == winnner) {
           return true;
       }
       else if (board[0].label.getText() == winnner && board[4].label.getText()== winnner && board[8].label.getText() == winnner){
               return true;
       }
           return false;
    }

    public static void main(String[] args) throws Exception {

        SinglePlayer client = new SinglePlayer();

        client.genBoard();
        client.playerTurn = true;


        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setSize(380, 400);
        client.frame.setVisible(true);
        client.frame.setResizable(false);







    }
}
