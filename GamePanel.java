import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener{

    int boardWidth = 360;
    int boardHeight = 640;

    Image background, bird, pipe1, pipe2;
    int birdX = boardWidth/8;
    int birdY = boardWidth/2;
    int birdHeight = 50;    
    int birdWidth = 50;

    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;
    int speedX = -4;
    Timer gameLoop;
    Timer makePipe;
    double score = 0;
    boolean gameOver = false;

    int lastSpeedUpScore=0;
    
    //private String playerName;
    private String playerName;

    boolean showRestartPrompt = false;
    
    Image gameOverBackground;
    
    //making the bird
    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;
        
        Bird(Image img){ //constructor for the bird
            this.img=img;
        }
    }

    //instantiating a Bird object which will handle tha game logic
    Bird flappyBird;
    int speedY = 0;
    int gravity = 1;


    //method for objects to move
    public void move(){
        speedY+=gravity;
        flappyBird.y += speedY;
        flappyBird.y = Math.max(flappyBird.y, 0); //making sure bird does not go out of bounds
    
        for (int i = 0; i<pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += speedX; 

            if (collision(flappyBird, pipe)){
                gameOver=true;
            }

            if (!pipe.passed && flappyBird.x > pipe.x+pipe.width){
                pipe.passed=true;
                score+=0.5;
            }
        }

        if ((int)score % 5 == 0 && (int)score != lastSpeedUpScore) {
            speedX -= .5; // Accelerate pipe speed
            lastSpeedUpScore = (int)score;
        }

        if (flappyBird.y>boardHeight){
            gameOver=true;
        }

    }

    //making the pipes
    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;

        Image img;
        boolean passed = false; //if the bird has passed the pipe

        //constructor for the pipe
        Pipe(Image img){
            this.img=img;
        }
    }

    ArrayList<Pipe> pipes;
    Random r = new Random();

        public void makePipe(){
            int rPipeY = (int) (pipeY-pipeHeight/4 -Math.random()*(pipeHeight/2));

            int birdBound = boardHeight/4;

            Pipe topPipe = new Pipe(pipe1);
            topPipe.y = rPipeY;
            pipes.add(topPipe);

            Pipe bottomPipe = new Pipe(pipe2);
            bottomPipe.y = (topPipe.y + pipeHeight + birdBound);
            pipes.add(bottomPipe);
        }

    public GamePanel(String playerName){
        this.playerName = playerName;
        setPreferredSize(new Dimension(360,640));
        loadImages();
        setFocusable(true);
        addKeyListener(this);

        flappyBird = new Bird(bird);
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();

        pipes = new ArrayList<Pipe>();

        makePipe = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                makePipe();
            }
        });
        makePipe.start();
    }

    private void loadImages() {

        background = new ImageIcon(getClass().getResource("/flappybirdbg.png")).getImage();
        bird = new ImageIcon(getClass().getResource("/flappybird.png")).getImage();
        pipe1 = new ImageIcon(getClass().getResource("/toppipe.png")).getImage();
        pipe2 = new ImageIcon(getClass().getResource("/bottompipe.png")).getImage();
        gameOverBackground = new ImageIcon(getClass().getResource("/exit.png")).getImage();
        
        // Verify images loaded correctly
        if (background == null || bird == null || pipe1 == null || pipe2 == null || gameOverBackground == null) {
            JOptionPane.showMessageDialog(this, "Error loading game images!");
            System.exit(1);
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public boolean collision(Bird birb, Pipe pip){
        boolean collided;
        collided = ((birb.x < pip.x+pip.width) && (birb.x + birb.width > pip.x) && (birb.y < pip.y+pip.height) && (birb.y+birb.height>pip.y));
        return collided;
    }

    public void draw(Graphics g){
        System.out.println("tite");
        //drawing the background
        g.drawImage(background, 0, 0, 360,640, null);
        
        //drawing the bird
        g.drawImage(bird, flappyBird.x, flappyBird.y, flappyBird.width, flappyBird.height, null);
    
        //drawing the pipes
        for (int i = 0; i<pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));

        if (gameOver){
        	// Semi-transparent black overlay
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, boardWidth, boardHeight);
            
            // Draw the custom background
            g.drawImage(gameOverBackground, 0, 0, 360, 640, null);

            g.setFont(new Font("04b_08_", Font.BOLD, 18));
            g.setColor(Color.WHITE);
            g.drawString(playerName+ "'s Score: " + (int)score, 92, 332);

            showRestartPrompt = true;
        } else {
            g.drawString("Score: " + String.valueOf((int) score), 10, 35);
        }
        
        // show player name
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Player: " + playerName, 10, 620);

    }
    
    public void restartGame() {
        birdX = boardWidth/8;
        birdY = boardWidth/2;
        speedY = 0;
        speedX = -4;
        lastSpeedUpScore = 0;
        score = 0;
        gameOver = false;
        showRestartPrompt = false;

        flappyBird = new Bird(bird);
        pipes.clear();

        gameLoop.start();
        makePipe.start();

        requestFocusInWindow(); // regain focus for key events
        repaint();
    }
    
    public void saveGame() {
        File gameData = new File("bird.txt");
        int highScore = 0;
    
        // First, try to read the existing high score
        try {
            if (gameData.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(gameData));
                String line = br.readLine();
                if (line != null && !line.isEmpty()) {
                    highScore = Integer.parseInt(line.trim());
                }
                br.close();
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading high score: " + e.getMessage());
            highScore = 0; // Reset if there's an error reading
        }
    
        // Only save if current score is higher
        if ((int)score > highScore) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(gameData));
                bw.write(String.valueOf((int)score));
                bw.close();
                System.out.println("New high score saved: " + (int)score);
            } catch (IOException e) {
                System.err.println("Error saving high score: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if(gameOver){
            makePipe.stop();
            gameLoop.stop();
            saveGame();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
            if (e.getKeyCode()==KeyEvent.VK_SPACE){
                speedY = -9;
            }
        } else if (gameOver && showRestartPrompt) {
            if (e.getKeyCode() == KeyEvent.VK_R) {
                restartGame();
            }
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        }
	}
