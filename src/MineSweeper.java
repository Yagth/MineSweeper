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
    boolean[][] buttonsChecked;
    JLabel textField;

    Random random;
    ImageIcon bombImage;
    ImageIcon explosionImage;

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

        buttonsChecked = new boolean[gridSize][gridSize];
        this.initialiseCheckedArrayList();

        textPanel.add(textField);
        frame.setTitle("Mine Sweeper!");
        frame.add(textPanel,BorderLayout.NORTH);
        frame.add(buttonPanel,BorderLayout.CENTER);
        frame.setSize(WIDTH,HEIGHT);
        frame.revalidate();
        frame.setLocationRelativeTo(null);

        Dimension dimension = buttons[0][0].getPreferredSize();

        Image tmpBombImage = new ImageIcon("Images/mineBomb.png").getImage();
        tmpBombImage = tmpBombImage.getScaledInstance(dimension.width*2,dimension.height*4,Image.SCALE_AREA_AVERAGING);
        Image tmpExplosionImage = new ImageIcon("Images/explosion.png").getImage();
        tmpExplosionImage = tmpExplosionImage.getScaledInstance(dimension.width*2,dimension.height*4,Image.SCALE_AREA_AVERAGING);

        bombImage = new ImageIcon(tmpBombImage);
        explosionImage = new ImageIcon(tmpExplosionImage);

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

     public void gameLogic(int x, int y){
         initialiseCheckedArrayList();
         boolean isEmpty = (solutions[x][y] == 0);
         gameOver = (solutions[x][y] == gridSize +1);

        if(!gameOver){
            boolean isBomb = solutions[x][y] == gridSize+1;
            if(!isEmpty && !isBomb ) {
                buttons[x][y].setText(String.valueOf(solutions[x][y]));
            }
            checkWinner();
            if(isEmpty)
                openConsecutiveZeros(x,y);
            openZeroNeighbours();
        }
        else {
            gameOver(false);
            buttons[x][y].setIcon(explosionImage);
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
                    boolean isEmpty = (solutions[i][j] == 0);
                    if (isBomb){
                        buttons[i][j].setIcon(bombImage);
                    }
                    if(!isEmpty && !isBomb)  buttons[i][j].setText(String.valueOf(solutions[i][j]));
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
    public void initialiseCheckedArrayList(){
        for(boolean[] bools : buttonsChecked){
            for(boolean bool : bools){
                bool = false;
            }
        }
    }
     private void openConsecutiveZeros(int x, int y){
         boolean isWithInBounds =(x<solutions.length && x>=0) && (y<solutions[0].length && y>=0);
         if(isWithInBounds ) {
             boolean isEmpty = solutions[x][y] == 0;
             boolean Checked = buttonsChecked[x][y];

             if (isEmpty) {
                 buttons[x][y].setEnabled(false);
                 buttonsChecked[x][y] = true;
                 if(!Checked){
                     openConsecutiveZeros(x,y+1);
                     openConsecutiveZeros(x,y-1);
                     openConsecutiveZeros(x+1, y);
                     openConsecutiveZeros(x-1,y);
                 }
             }
         }
     }

     public void openZeroNeighbours(){
        for(int i = 0; i<solutions.length; i++){
            for(int j = 0; j<solutions[0].length; j++){
                if((buttons[i][j].getText().equals("")&&!(buttons[i][j].isEnabled()))){
                    try{
                        if((solutions[i][j+1] != (gridSize +1)&&(solutions[i][j+1] != 0))){
                            buttons[i][j+1].setText(String.valueOf(solutions[i][j+1]));
                            buttons[i][j+1].setEnabled(false);
                        }
                        if(solutions[i][j-1] != (gridSize +1)&&(solutions[i][j-1] != 0)){
                            buttons[i][j-1].setText(String.valueOf(solutions[i][j-1]));
                            buttons[i][j-1].setEnabled(false);
                        }
                        if(solutions[i+1][j] != (gridSize +1)&&(solutions[i+1][j] != 0)){
                            buttons[i+1][j].setText(String.valueOf(solutions[i+1][j]));
                            buttons[i+1][j].setEnabled(false);
                        }
                        if(solutions[i-1][j] != (gridSize +1)&&(solutions[i-1][j] != 0)){
                            buttons[i-1][j].setText(String.valueOf(solutions[i-1][j]));
                            buttons[i-1][j].setEnabled(false);
                        }
                        if(solutions[i+1][j+1] != (gridSize +1)&&(solutions[i+1][j+1] != 0)){
                            buttons[i+1][j+1].setText(String.valueOf(solutions[i+1][j+1]));
                            buttons[i+1][j+1].setEnabled(false);
                        }
                        if(solutions[i-1][j+1] != (gridSize +1)&&(solutions[i-1][j+1] != 0)){
                            buttons[i-1][j+1].setText(String.valueOf(solutions[i-1][j+1]));
                            buttons[i-1][j+1].setEnabled(false);
                        }
                        if(solutions[i-1][j-1] != (gridSize +1)&&(solutions[i-1][j-1] != 0)){
                            buttons[i-1][j-1].setText(String.valueOf(solutions[i-1][j-1]));
                            buttons[i-1][j-1].setEnabled(false);
                        }
                        if(solutions[i+1][j-1] != (gridSize +1)&&(solutions[i+1][j-1] != 0)){
                            buttons[i+1][j-1].setText(String.valueOf(solutions[i+1][j-1]));
                            buttons[i+1][j-1].setEnabled(false);
                        }
                    } catch (Exception ex){}
                }
            }
        }
     }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JButton jb = (JButton) actionEvent.getSource();
        for(int i = 0; i<gridSize; i++){
            for(int j = 0; j<gridSize; j++){
                if(buttons[i][j].equals(jb)){
                    gameLogic(i,j);
                    buttons[i][j].setEnabled(false);
                }
            }
        }
    }
}
