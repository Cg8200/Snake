import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

public class Snake implements ActionListener ,KeyListener,MouseListener{
    Color appleColor = new Color(153,0,0);
     static Snake snake;
     Direction nextSnakeDirection;
     int score;
     final int WIDTH = 805, HEIGHT = 810;
     Renderer renderer;
     ArrayList<Point> snakeBody;
     Point applePosition;
     static final int snakeSize = 20;
    Direction snakeDirection;
    Random random;
    Clip clip;
    Clip clip2;
    Clip clip3;
    Clip clip4;
    Button restart;
    int highScore;
   static boolean isAlive;
    String key = "Bar12345Bar12345"; // 128 bit key
    Key aesKey;
    Cipher cipher;

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
        jframe.addMouseListener(this);
        jframe.setVisible(true);
        applePosition = new Point(200, 100);
        snakeBody = new ArrayList<>();
        snakeBody.add(0,new Point(400, 400));
        score = 0;
        highScore = readFromFile();
       snakeDirection = Direction.UP;
       nextSnakeDirection = Direction.UP;
       //region sound initialization
        try {
            Path currentRelativePath = Paths.get("");
            String path = currentRelativePath.toAbsolutePath().toString();
            URL file = new URL("file:"+path+"/Collect.wav");
            URL file2 = new URL("file:"+path+"/Finger Snap.wav");
            URL file3 = new URL("file:"+path+"/Lose.wav");
            URL file4 = new URL("file:"+path+"/DanceAround.wav");
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            AudioInputStream ais2 = AudioSystem.getAudioInputStream(file2);
            AudioInputStream ais3 = AudioSystem.getAudioInputStream(file3);
            AudioInputStream ais4 = AudioSystem.getAudioInputStream(file4);
             clip = AudioSystem.getClip();
            clip2 = AudioSystem.getClip();
            clip3 = AudioSystem.getClip();
            clip4 = AudioSystem.getClip();
            clip.open(ais);
            clip2.open(ais2);
            clip3.open(ais3);
            clip4.open(ais4);

        }catch (Exception sound){
            System.out.println(sound.toString());
        }
        restart = new Button("RESTART", 360, 600, 20);
        isAlive = true;
         aesKey = new SecretKeySpec(key.getBytes(), "AES");
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        timer.start();
        clip4.setFramePosition(0);
        clip4.loop(-1);

       // clip4.start();
    }
    public void restart(){
        isAlive = true;
        clip4.setFramePosition(0);
        clip4.loop(-1);
        applePosition = new Point(200, 100);
        snakeBody = new ArrayList<>();
        snakeBody.add(0,new Point(400, 400));
        score = 0;
        snakeDirection = Direction.UP;
        nextSnakeDirection = Direction.UP;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(isAlive) {

            //region snake movement
            if (nextSnakeDirection != snakeDirection) {
                clip2.setFramePosition(50);
                clip2.start();
            }
            snakeDirection = nextSnakeDirection;
            if (snakeDirection == Direction.UP) {
                snakeBody.add(0, new Point(snakeBody.get(0).X, snakeBody.get(0).Y - Snake.snakeSize));
            } else if (snakeDirection == Direction.DOWN) {
                snakeBody.add(0, new Point(snakeBody.get(0).X, snakeBody.get(0).Y + Snake.snakeSize));
            } else if (snakeDirection == Direction.LEFT) {
                snakeBody.add(0, new Point(snakeBody.get(0).X - Snake.snakeSize, snakeBody.get(0).Y));
            } else if (snakeDirection == Direction.RIGHT) {
                snakeBody.add(0, new Point(snakeBody.get(0).X + Snake.snakeSize, snakeBody.get(0).Y));
            }
            //endregion
            //region snake collision detection

            if (snakeBody.get(0).X < 0 || snakeBody.get(0).X >= (WIDTH - snakeSize)) {
                death();
            }
            if (snakeBody.get(0).Y < 0 || snakeBody.get(0).Y >= (HEIGHT - 2 * snakeSize)) {
                death();
            }
            if (touchingSnake(snakeBody.get(0), 1)) {
                death();
            }

            if (snakeBody.get(0).X == applePosition.X && snakeBody.get(0).Y == applePosition.Y) {
                score = score + 1;
                if(score>highScore){
                    highScore = score;
                    writeToFile();
                }
                clip.setFramePosition(50);
                clip.start();
                replaceApple();
            } else {
                snakeBody.remove(snakeBody.size() - 1);
            }
            //endregion
        }
        renderer.repaint();
    }


    public void repaint(Graphics g) {
        g.setColor(appleColor);
        Font myFont = new Font ("TimesRoman", Font.PLAIN, 18);
        g.setFont(myFont);
        g.drawString("SCORE: ", 10, 18);
        g.drawString(String.valueOf(score),88,18);
        g.drawString("HIGHSCORE: ", 10, 40);
        g.drawString(String.valueOf(highScore),130,40);
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
        if(restart.isVisible) {
            restart.Draw(g);
        }
    }

    public void replaceApple(){
        do {
            applePosition.X = random.nextInt(WIDTH / snakeSize-1) * snakeSize;
            applePosition.Y = random.nextInt(HEIGHT / snakeSize-2) * snakeSize;
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
    public void death(){
        isAlive = false;
        clip3.setFramePosition(50);
        clip3.start();
        clip4.stop();
        restart.isVisible= true;
       try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       // System.exit(1);
    }
    private void writeToFile(){
        File f = new File("highScore.txt");
        FileWriter fw = null;
        try {
             fw = new FileWriter(f);
        } catch (IOException e) {
           System.out.println("File not found  =(");
        }
        try{
            BufferedWriter bw = new BufferedWriter(fw);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(String.valueOf(highScore).getBytes());
            bw.write(new String(encrypted));
            bw.flush();
            bw.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    private int readFromFile() {
        File f = new File("highScore.txt");
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            while (br.ready()) {

                byte[] encrypted = br.readLine().getBytes();
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                String decrypted = new String(cipher.doFinal(encrypted));
                return Integer.parseInt(decrypted);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_UP && !(snakeDirection == Direction.DOWN)) {
            nextSnakeDirection = Direction.UP;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && !(snakeDirection == Direction.UP)) {
            nextSnakeDirection = Direction.DOWN;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && !(snakeDirection == Direction.RIGHT)) {
            nextSnakeDirection = Direction.LEFT;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && !(snakeDirection == Direction.LEFT)) {
            nextSnakeDirection = Direction.RIGHT;
        }
    }


        @Override
        public void mouseClicked(MouseEvent e) {
            if (restart.isClicked(e.getX(), e.getY())) {
                restart();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }














    @Override
    public void keyReleased(KeyEvent e) {

    }



    public static void main(String[] args) {
    snake = new Snake();
    }



}
