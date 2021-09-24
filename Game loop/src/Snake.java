import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class Snake implements ActionListener ,KeyListener{
    Color appleColor = new Color(153,0,0);
     static Snake snake;
     int score;
     final int WIDTH = 805, HEIGHT = 810;
     Renderer renderer;
     ArrayList<Point> snakeBody;
     Point applePosition;
     static final int snakeSize = 20;
    Direction snakeDirection;
    Random random;
    enum Direction{
        UP, DOWN, LEFT, RIGHT
    }
    public Snake()
    {
        random = new Random();
        JFrame jframe = new JFrame();
        Timer timer = new Timer(200, this);
        renderer = new Renderer();
        renderer.setBackground(Color.BLACK);

        jframe.add(renderer);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(WIDTH, HEIGHT);
        jframe.setResizable(false);
        jframe.addKeyListener(this);
        jframe.setVisible(true);
        applePosition = new Point(200, 100);
        snakeBody = new ArrayList<>();
        snakeBody.add(0,new Point(400, 400));
        score = 0;
       snakeDirection = Direction.UP;
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //region snake movement
        if(snakeDirection == Direction.UP){
            snakeBody.add(0,new Point(snakeBody.get(0).X,snakeBody.get(0).Y- Snake.snakeSize));
        }else  if(snakeDirection == Direction.DOWN){
            snakeBody.add(0,new Point(snakeBody.get(0).X,snakeBody.get(0).Y+ Snake.snakeSize));
        }else  if(snakeDirection == Direction.LEFT){
            snakeBody.add(0,new Point(snakeBody.get(0).X- Snake.snakeSize,snakeBody.get(0).Y));
        }else  if(snakeDirection == Direction.RIGHT) {
            snakeBody.add(0,new Point(snakeBody.get(0).X+ Snake.snakeSize,snakeBody.get(0).Y));
        }
        //endregion
        //region snake collision detection
        if (snakeBody.get(0).X < 0 || snakeBody.get(0).X >= (WIDTH - snakeSize)){
            System.exit(1);
        }
        if (snakeBody.get(0).Y < 0 || snakeBody.get(0).Y >= (HEIGHT - 2 * snakeSize)){
            System.exit(1);
        }
        if(touchingSnake( snakeBody.get(0),1)){
            System.exit(1);
        }

        if (snakeBody.get(0).X==applePosition.X&&snakeBody.get(0).Y==applePosition.Y){
            score = score + 1;
            replaceApple();
        }else {
            snakeBody.remove(snakeBody.size()-1);
        }
        //endregion
        renderer.repaint();
    }


    public void repaint(Graphics g) {
        g.setColor(appleColor);
        Font myFont = new Font ("TimesRoman", Font.PLAIN, 18);
        g.setFont(myFont);
        g.drawString("SCORE: ", 10, 18);
        g.drawString(String.valueOf(score),88,18);
        //region draw apple
        g.setColor(appleColor);
        g.fillRect(applePosition.X, applePosition.Y, snakeSize, snakeSize);
        //endregion
        //draw snake
        g.setColor(Color.GREEN);
        for(int i = 0; i < snakeBody.size();i = i +1){
            g.fillRect(snakeBody.get(i).X,snakeBody.get(i).Y,snakeSize, snakeSize);
        }

        //endregion
    }

    public void replaceApple(){
        do {
            applePosition.X = random.nextInt(WIDTH / snakeSize) * snakeSize;
            applePosition.Y = random.nextInt(HEIGHT / snakeSize) * snakeSize;
        } while(touchingSnake(applePosition,0));
    }

    //pass in 0 to startIndex to include head or 1 to exclude head
    public boolean touchingSnake(Point point,int startIndex){
        for(int i = startIndex; i < snakeBody.size();i = i +1) {
            if (point.comparePoint(snakeBody.get(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()== KeyEvent.VK_UP){
            snakeDirection = Direction.UP;
        }else  if(e.getKeyCode()== KeyEvent.VK_DOWN){
            snakeDirection = Direction.DOWN;
        }else  if(e.getKeyCode()== KeyEvent.VK_LEFT){
           snakeDirection = Direction.LEFT;
        }else  if(e.getKeyCode()== KeyEvent.VK_RIGHT) {
           snakeDirection = Direction.RIGHT;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }



    public static void main(String[] args) {
    snake = new Snake();
    }



}
