import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class MineSweeper implements ActionListener {
    JFrame frame;
    JPanel textPanel;
    JPanel buttonPanel;
    JButton[][] buttons;
    int[][] solutions;
    JLabel textField;

    Random random;

    int gridSize;
    int bombs;
    final int WIDTH = 570;
    final int HEIGHT = 570;
    boolean gameOver;

    ArrayList<Integer> xBombPositions;
    ArrayList<Integer> yBombPositions;
    public MineSweeper(){

        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (Exception ex) {
            System.out.println("Can't set the look and feel.");
        }

        gridSize = 10;
        bombs = 9;
        xBombPositions = new ArrayList<>();
        yBombPositions = new ArrayList<>();

        this.createBombs();
        this.handleBombOverlapping();

        frame = new JFrame();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textPanel = new JPanel();
        textPanel.setVisible(true);
        textPanel.setBackground(Color.black);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(gridSize,gridSize));
        buttonPanel.setVisible(true);

        textField = new JLabel();
        textField.setHorizontalAlignment(JLabel.CENTER);
        textField.setFont(new Font("MV Boil",Font.BOLD,20));
        textField.setForeground(Color.blue);
        textField.setText(bombs+" Bombs");

        buttons = new JButton[gridSize][gridSize];
        solutions = new int[gridSize][gridSize];

        this.createButtons();
        this.getSolution();

        textPanel.add(textField);
        frame.setTitle("Mine Sweeper!");
        frame.add(textPanel,BorderLayout.NORTH);
        frame.add(buttonPanel,BorderLayout.CENTER);
        frame.setSize(WIDTH,HEIGHT);
        frame.revalidate();
        frame.setLocationRelativeTo(null);

        this.displayBombMap();
    }

    private void createBombs(){
        random = new Random();
        for(int i = 0; i<bombs; i++){
            xBombPositions.add(random.nextInt(gridSize));
            yBombPositions.add(random.nextInt(gridSize));
        }
    }
    private void handleBombOverlapping(){
        boolean bombIsOverlapped;
        for(int i = 0; i<bombs; i++){
            for(int j = i+1; j<bombs; j++ ){
                bombIsOverlapped = xBombPositions.get(i).equals(xBombPositions.get(j)) &&
                        yBombPositions.get(i).equals(yBombPositions.get(j));
                if(bombIsOverlapped){
                    xBombPositions.set(j,random.nextInt(gridSize));
                    yBombPositions.set(j,random.nextInt(gridSize));

                    i = 0;//Resetting the count variables to start checking from the beginning.
                    j = 0;
                }
            }
        }
    }

    private void createButtons(){
        for(int i = 0; i<buttons.length; i++){
            for(int j = 0; j<buttons[0].length; j++){
                buttons[i][j] = new JButton();
                buttons[i][j].setFocusable(false);
                buttons[i][j].setFont(new Font("MV Boil",Font.BOLD,20));
                buttons[i][j].setText("");
                buttons[i][j].addActionListener(this);
                buttonPanel.add(buttons[i][j]);
            }
        }
    }

    private void getSolution(){
        for(int x = 0; x<solutions.length; x++){
            for(int y = 0; y<solutions[0].length; y++){
                countBombs(x,y);
            }
        }
    }

     private void countBombs(int x, int y){
         boolean changed = false;
         int bombsAround = 0;
         boolean isBomb;
         for(int i = 0; i<xBombPositions.size(); i++){
             isBomb = ((xBombPositions.get(i).equals(x)) && (yBombPositions.get(i)).equals(y));
             if(isBomb){
                 solutions[x][y]=gridSize+1;
                 changed = true;
             }
         }

         if(!changed){
             for(int i = 0; i<xBombPositions.size(); i++){
                 if(xBombPositions.get(i).equals(x+1) && yBombPositions.get(i).equals(y))//checking to the right button
                     bombsAround++;
                 if(xBombPositions.get(i).equals(x-1) && yBombPositions.get(i).equals(y))//checking to the left button
                     bombsAround++;
                 if(xBombPositions.get(i).equals(x) && yBombPositions.get(i).equals(y+1))//checking to the button below
                     bombsAround++;
                 if(xBombPositions.get(i).equals(x) && yBombPositions.get(i).equals(y-1))//checking to the button above
                     bombsAround++;
                 if(xBombPositions.get(i).equals(x+1) && yBombPositions.get(i).equals(y+1))//checking to the bottom right button
                     bombsAround++;
                 if(xBombPositions.get(i).equals(x-1) && yBombPositions.get(i).equals(y+1))//checking to the bottom left button
                     bombsAround++;
                 if(xBombPositions.get(i).equals(x+1) && yBombPositions.get(i).equals(y-1))//checking to the top right button
                     bombsAround++;
                 if(xBombPositions.get(i).equals(x-1) && yBombPositions.get(i).equals(y-1))//checking to the top left button
                     bombsAround++;
             }
             solutions[x][y] = bombsAround;
         }
     }

     public void check(int x, int y){
         boolean isEmpty = (solutions[x][y] == 0);
         System.out.println("Is empty: "+ isEmpty);
        gameOver = (solutions[x][y] == gridSize +1);
        if(!gameOver){
            buttons[x][y].setText(String.valueOf(solutions[x][y]));
            checkWinner();
            if(isEmpty)
                openConsecutiveZeros(x,y);
        }
        else {
            gameOver(false);
        }
     }
     public void checkWinner(){
        int buttonsLeft = 0;
         for (JButton[] button : buttons) {
             for (int j = 0; j < buttons[0].length; j++) {
                 if (button[j].getText().equals("")) {
                     buttonsLeft++;
                 }
             }
         }
            if((buttonsLeft == bombs))
                gameOver(true);
     }
     public void gameOver(boolean won){
        if(!won) {
            textField.setForeground(Color.red);
            textField.setText("Game over!");
            for (int i = 0; i < buttons.length; i++) {
                for (int j = 0; j < buttons[0].length; j++) {
                    boolean isBomb = (solutions[i][j] == gridSize + 1);
                    if (isBomb)
                        buttons[i][j].setForeground(Color.red);
                    buttons[i][j].setText(String.valueOf(solutions[i][j]));
                    buttons[i][j].setEnabled(false);
                }
            }
        }
        else{
            textField.setForeground(Color.green);
            textField.setText("You won!");
            disableButtons();
        }
     }
     public void disableButtons(){
         for(JButton[] jbs: buttons){
             for(JButton jb: jbs){
                 jb.setEnabled(false);
             }
         }
     }

     public void displayBombMap(){
         for(int i =0; i<gridSize; i++){
             for(int j = 0; j<gridSize; j++){
                 System.out.print(solutions[i][j]+ " ");
             }
             System.out.println();
         }
     }
     int recursiveCall = 0;
     private void openConsecutiveZeros(int x, int y){
         boolean isWithInBounds =(x<solutions.length && x>=0) && (y<solutions[0].length && y>=0);
        if(isWithInBounds){
            if(solutions[x][y] == 0){
                buttons[x][y].setText(String.valueOf(solutions[x][y]));
                openConsecutiveZeros(x, y+1);
                openConsecutiveZeros(x, y-1);
                openConsecutiveZeros(x+1, y);
                openConsecutiveZeros(x+1, y-1);
                openConsecutiveZeros(x+1, y+1);
                openConsecutiveZeros(x-1, y);
                openConsecutiveZeros(x-1, y+1);
                openConsecutiveZeros(x-1, y-1);
            }
        }
     }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JButton jb = (JButton) actionEvent.getSource();
        for(int i = 0; i<gridSize; i++){
            for(int j = 0; j<gridSize; j++){
                if(buttons[i][j].equals(jb))
                    check(i,j);
            }
        }
    }
}
