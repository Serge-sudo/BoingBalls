import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class GameFrame extends JFrame {

    static boolean playSound = true;
    static File soundFile = new File("boink.wav");
    protected static double gravity = 0.5; //representing the gravity applied to the balls in the game.
    private final ArrayList<Ball> balls = new ArrayList<>();
    private Ball current = null; //object representing the currently active ball being manipulated.

    //last mouse press event.
    private int xPress;
    private int yPress;

    //coordinates of the previous mouse press event
    private int xPressOld;
    private int yPressOld;


    //total area of the balls in the game
    private double area = 0;

    //total speed of the balls in the game
    private double speed = 0;

    //whether the mouse button is currently pressed
    private boolean isPressed;

    //Panel representing the game area
    private final GamePanel gamePanel;


    /**
     * This class extends JPanel and is responsible for displaying the statistics of the game,
     * including the number of balls, total speed, and total area.
     * It overrides the paintComponent method to update the displayed statistics based on the current game state.
     */


    private class Statistics extends JPanel {
        private final JLabel ballCount;
        private final JLabel totalSpeed;
        private final JLabel totalArea;

        Statistics() {
            setLayout(new GridLayout(1, 3));
            ballCount = new JLabel();
            totalSpeed = new JLabel();
            totalArea = new JLabel();
            add(ballCount);
            add(totalSpeed);
            add(totalArea);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            ballCount.setText("Number of balls: " + Ball.ballCount);
            totalSpeed.setText("Total speed: " + (int) speed);
            totalArea.setText("Total area: " + (int) area);
            repaint();
        }
    }


    /**
     * Inner class GamePanel:
     * <p>
     * This class extends JPanel and is responsible for rendering and updating the game state.
     * It implements the MouseListener and MouseMotionListener interfaces to handle user mouse events.
     * It overrides the paintComponent method to render the balls on the panel.
     * It includes various methods to handle mouse events such as mousePressed, mouseReleased, mouseDragged, etc.
     */
    private class GamePanel extends JPanel implements MouseListener, MouseMotionListener {

        GamePanel() {
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            double tempSpeed = 0;

            for (var ball : balls) {
                if (ball != current) {
                    ball.move(getWidth(), getHeight());
                }
                tempSpeed += ball.speed();
                g.setColor(ball.color);
                g.fillOval(ball.center.getX() - ball.radius, ball.center.getY() - ball.radius, 2 * ball.radius, 2 * ball.radius);
                g.setColor(Color.BLACK);
                g.drawOval(ball.center.getX() - ball.radius, ball.center.getY() - ball.radius, 2 * ball.radius, 2 * ball.radius);

            }

            speed = tempSpeed;

            if (isPressed) {
                current.setCenter(xPress, yPress);
                current.changeSize();
            }

            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            repaint();
        }

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            //Nothing to do here
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            xPress = mouseEvent.getX();
            yPress = mouseEvent.getY();
            current = new Ball(xPress, yPress, Ball.getRandomColor());
            balls.add(current);
            isPressed = true;
            xPressOld = xPress;
            yPressOld = yPress;
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {

            area += Math.pow(current.radius, 2) * Math.PI;

            if (mouseEvent.getX() == xPressOld && mouseEvent.getY() == yPressOld) {
                current.setVelocity((int) (Math.random() * 10) - 5, 0);
            } else {
                current.setVelocity((mouseEvent.getX() - xPressOld) * 5, (mouseEvent.getY() - yPressOld) * 5);
            }
            current = null;
            isPressed = false;
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            //Nothing to do here
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            //Nothing to do here
        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent) {
            xPress = mouseEvent.getX();
            yPress = mouseEvent.getY();
        }


        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
            //Nothing to do here
        }
    }

    /**
     * Initializes the GameFrame object by setting the title, size, default close operation, and layout.
     * Creates an instance of the Statistics class and adds it to the top of the frame.
     * Creates an instance of the GamePanel class and adds it to the center of the frame.
     * Creates a menu bar with options for clearing the game and randomizing ball colors.
     * Uses an anonymous class to create an ActionListener object.
     * Sets the menu bar for the frame and makes it visible.
     */

    GameFrame() {

        super("Game");
        setSize(1280, 720);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        JPanel stat = new Statistics();
        addBorder(stat, "Statistics");
        add(stat, BorderLayout.NORTH);

        gamePanel = new GamePanel();
        addBorder(gamePanel, "Balls");
        add(gamePanel, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu options = new JMenu("options");
        menuBar.add(options);

        JMenuItem clear = new JMenuItem("clear");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cleanFrame();
            }
        });

        JMenuItem rand = new JMenuItem("randomize colors");
        rand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                randomize();
            }
        });

        options.add(clear);
        options.add(rand);

        setJMenuBar(menuBar);

        setVisible(true);
    }


    private void cleanFrame() {
        balls.clear();
        area = 0;
        speed = 0;
        current = null;
        isPressed = false;
        Ball.ballCount = 0;
    }

    private void randomize() {
        for (var ball : balls) {
            ball.color = Ball.getRandomColor();
        }
    }

    /**
     * Adds a border with a title to the specified component
     */
    private void addBorder(JComponent component, String title) {
        Border etch = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Border tb = BorderFactory.createTitledBorder(etch, title);
        component.setBorder(tb);
    }


    public static void main(String[] args) {

        GameFrame gameFrame = new GameFrame();

    }


}
