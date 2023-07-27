import javax.sound.sampled.*;
import java.awt.*;
import java.io.IOException;
import java.util.Random;


public class Ball {

    private static final int MAX_RADIUS = 80; // the maximum radius of a ball
    private static final int MIN_RADIUS = 10; // the minimum radius of a ball.

    protected int radius = 10;
    private int dir = 1; // direction of scaling (1: increasing, -1: decreasing)
    protected Vector center; // center position of the ball
    protected Vector velocity; // velocity of the ball
    protected Color color; // color of the ball
    protected static int ballCount = 0; // the count of balls created


    Clip clip;

    public void playSound() {
        if (!GameFrame.playSound) return;
        clip.setFramePosition(0);
        FloatControl pitchControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
        pitchControl.setValue(pitchControl.getMaximum() / (float) (velocity.magnitude() / 1000.0 + 1.0));
        clip.start();
    }


    public static Color getRandomColor() {
        Random random = new Random();
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    /**
     * Updates the position of the ball based on its velocity and handles collision with the boundaries of the screen.
     */
    public void move(int width, int height) {
        velocity = new Vector(velocity.x, velocity.y + GameFrame.gravity); //change speed due to gravity
        boolean scale = false;

        if (center.x - radius < 0) {
            playSound();
            setCenter(radius, center.getY());
            scale = true;
            velocity.flipHorizontal();
        } else if (center.x + radius > width) {
            playSound();
            setCenter(width - radius, center.getY());
            scale = true;
            velocity.flipHorizontal();
        } else if (center.y - radius < 0) {
            playSound();
            setCenter(center.getX(), radius + 1);
            scale = true;
            velocity.flipVertical();
        } else if (center.y + radius >= height) {
            setCenter(center.getX(), height - radius);
            scale = true;
            if (velocity.magnitude() < 3) {
                velocity.scale(0);
            } else {
                playSound();
                velocity = new Vector(velocity.x, velocity.y - GameFrame.gravity);
                velocity.flipVertical();
                velocity.scale(0.8);
            }
        }
        center.add(velocity);
        if (scale) { //slow down
            velocity.scale(Math.pow(9, 1.0 / 6) / Math.pow(radius, 1.0 / 6));
        }
    }

    /**
     * Initializes the ball with the given center position and color.
     */
    Ball(int xCenter, int yCenter, Color color) {

        ++ballCount;
        center = new Vector(xCenter, yCenter);
        this.color = color;
        velocity = new Vector(0, 0);

        if (GameFrame.playSound) {
            initSound();
        }

    }

    private void initSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(GameFrame.soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the center position of the ball to the given coordinate
     */
    public void setCenter(int x, int y) {
        center = new Vector(x, y);
    }

    /**
     * Sets the velocity of the ball to the given values
     */
    public void setVelocity(int x, int y) {
        velocity = new Vector(x, y);
    }

    /**
     * Calculates and returns the speed of the ball based on its velocity magnitude
     */
    double speed() {
        return velocity.magnitude();
    }


    /**
     * Changes the size (radius) of the ball based on the current direction (dir).
     * The size change alternates between increasing and decreasing within the specified minimum and maximum radius limits.
     */
    public void changeSize() {
        if (radius == MIN_RADIUS)
            dir = 1;

        if (radius == MAX_RADIUS)
            dir = -1;

        radius += dir;
    }


    /**
     * Attributes:
     * <p>
     * private double x: Represents the x-coordinate of the vector.
     * private double y: Represents the y-coordinate of the vector.
     * <p>
     * Methods:
     * <p>
     * public int getX(): Returns the x-coordinate of the vector.
     * public int getY(): Returns the y-coordinate of the vector.
     * private ??? scale(double d): Scales the vector by the given factor d.
     * public Vector(double x, double y): Constructor that initializes the vector with the given x and y coordinates.
     * private ??? add(Vector that): Adds the given vector that to the current vector.
     * private ??? flipVertical(): Flips the direction of the vector vertically.
     * private ??? flipHorizontal(): Flips the direction of the vector horizontally.
     * private ??? magnitude(): Calculates and returns the magnitude (length) of the vector.
     */
    protected static class Vector {

        private double x;
        private double y;

        public int getX() {
            return (int) x;
        }

        public int getY() {
            return (int) y;
        }

        private void scale(double d) {
            x *= d;
            y *= d;
        }

        public Vector(double x, double y) {
            this.x = x;
            this.y = y;
        }

        private void add(Vector that) {
            x += that.x;
            y += that.y;
        }

        private void flipVertical() {
            y *= -1;
        }

        private void flipHorizontal() {
            x *= -1;
        }

        private double magnitude() {
            return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        }


    }


}
