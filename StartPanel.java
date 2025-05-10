import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StartPanel extends JPanel implements KeyListener {
    private Image background;
    private String playerName = "";
    private Runnable onStart;

    public StartPanel(Runnable onStart) {
        loadImage();
        setPreferredSize(new Dimension(360, 640));
        this.onStart = onStart;
        setFocusable(true);
        setRequestFocusEnabled(true);
        addKeyListener(this);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("StartPanel gained focus");
            }
        });
    }

    public void loadImage(){
        background = new ImageIcon(getClass().getResource("/startPanel.png")).getImage();

            //check if background loaded correctly 
            if (background == null){
                JOptionPane.showMessageDialog(this, "Error loading game images!");
                System.exit(1);
            }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background (you can draw an image later here)
        System.out.println("tite");
        draw(g);
    }

    public void draw(Graphics g){
        g.drawImage(background, 0, 0, 360,640, null);

        // Set font and color for drawing text
        g.setColor(Color.WHITE);
        g.setFont(new Font("04b08", Font.BOLD, 24));

        // Draw current input
        g.drawString(playerName + "|", 100, 440);  // the "|" gives a typing feel
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (Character.isLetterOrDigit(c) || c == ' ') {
            playerName += c;
        } else if (c == '\b' && playerName.length() > 0) {
            playerName = playerName.substring(0, playerName.length() - 1);
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Key pressed in StartPanel: " + e.getKeyCode());

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            System.out.println("ENTER detected, player name: " + playerName);

            if (playerName.trim().isEmpty()) {
                playerName = "Anonymous"; // ✅ DEFAULT NAME
                System.out.println("No name entered, using default: " + playerName);
            }

            onStart.run(); // Start game
        }
    }
    
    public String getPlayerName() {
    	return playerName.isEmpty() ? "Anonymous" : playerName;
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
}
