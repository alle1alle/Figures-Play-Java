/*
Figure.java
Alexander Lindgren
alexander2000alle@gmail.com
2 april 2024
*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Interface som representerar en figur
interface Shape {
    void draw(Graphics g);
    boolean contains(int x, int y);
    void move(int dx, int dy);
    Rectangle getBoundingBox();
    Color getColor(); 
}

// Klass som representerar en triangel
class Triangle implements Shape {
    private int x, y, h;
    private Color color; 

    public Triangle(int x, int y, int h, Color color) { 
        this.x = x;
        this.y = y;
        this.h = h;
        this.color = color;
    }

    public void draw(Graphics g) {
        g.setColor(color); 
        g.fillPolygon(new int[] {x, x + h / 2, x - h / 2}, new int[] {y, y + h, y + h}, 3);
    }
    // Metod för att kontrollera om en given punkt (x, y) är inuti triangeln
    public boolean contains(int x, int y) {
        // Skapa en Polygon-instans som representerar triangelns form
        Polygon polygon = new Polygon(new int[] {this.x, this.x + h / 2, this.x - h / 2}, new int[] {this.y, this.y + h, this.y + h}, 3);
        return polygon.contains(x, y);
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(x - h / 2, y, h, h);
    }

    public Color getColor() {
        return color;
    }
}

// Klass som representerar en kvadrat
class Square implements Shape {
    private int x, y, sideLength;
    private Color color; 

    public Square(int x, int y, int sideLength, Color color) { 
        this.x = x;
        this.y = y;
        this.sideLength = sideLength;
        this.color = color;
    }

    public void draw(Graphics g) {
        g.setColor(color); 
        g.fillRect(x, y, sideLength, sideLength);
    }

    public boolean contains(int x, int y) {
        return x >= this.x && x <= this.x + sideLength &&
               y >= this.y && y <= this.y + sideLength;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, sideLength, sideLength);
    }

    public Color getColor() {
        return color;
    }
}

// Klass som representerar en cirkel
class Circle implements Shape {
    private int x, y, radius;
    private Color color; 

    public Circle(int x, int y, int radius, Color color) { 
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }

    public void draw(Graphics g) {
        g.setColor(color); 
        g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
    }

    public boolean contains(int x, int y) {
        return Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2) <= Math.pow(radius, 2);
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(x - radius, y - radius, 2 * radius, 2 * radius);
    }

    public Color getColor() {
        return color;
    }
}

// Klass som representerar huvudpanelen för att rita figurerna
public class Figure extends JPanel implements MouseListener, MouseMotionListener {
    private Shape[] shapes; // Array för att lagra figurerna
    private Shape selectedShape; // Den figur som är vald av användaren
    private int offsetX, offsetY; // Förskjutning mellan musens position och figurernas position
    private int selectedShapeIndex; // Index för den valda figuren

    // Konstruktor för huvudpanelen
    public Figure() {
        setBackground(Color.GRAY);
        setForeground(Color.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);

        // Skapa och lägg till olika figurer i arrayen
        shapes = new Shape[] {
            new Triangle(150, 50, 80, Color.GREEN),
            new Square(300, 50, 80, Color.YELLOW),
            new Circle(450, 90, 40, Color.RED),
            new Circle(500, 90, 40, Color.BLUE)
        };
    }

    // Metod för att rita figurerna
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Rita varje figur i arrayen
        for (int i = 0; i < shapes.length; i++) {
            if (selectedShape != null && i == selectedShapeIndex) continue; // Hoppa över att rita den valda figuren
            shapes[i].draw(g);
        }
        if (selectedShape != null) {
            selectedShape.draw(g); // Rita den valda figuren sist för att den ska hamna överst
        }
    }

    // Metod för att kontrollera vilken figur som innehåller en given punkt
    private Shape getShapeAtPoint(int x, int y) {
        // Loopa baklänges genom figurerna för att kontrollera den översta figuren först
        for (int i = shapes.length - 1; i >= 0; i--) {
            if (shapes[i].contains(x, y)) {
                selectedShapeIndex = i; // Spara index för den valda figuren
                return shapes[i];
            }
        }
        return null; // Returnera null om ingen figur innehåller punkten
    }

    // MouseListener-metoder
    public void mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        selectedShape = getShapeAtPoint(mouseX, mouseY); // Hämta den figur som musen klickade på

        if (selectedShape != null) {
            offsetX = mouseX - selectedShape.getBoundingBox().x; // Beräkna förskjutningen mellan musens position och figurernas position
            offsetY = mouseY - selectedShape.getBoundingBox().y;
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (selectedShape != null) {
            // Flytta den valda figuren till slutet av arrayen för att den ska hamna överst
            Shape temp = shapes[selectedShapeIndex];
            for (int i = selectedShapeIndex; i < shapes.length - 1; i++) {
                shapes[i] = shapes[i + 1];
            }
            shapes[shapes.length - 1] = temp;

            selectedShape = null; // Släpp den valda figuren när musknappen släpps
            repaint(); // Rita om panelen för att visa den uppdaterade positionen
        }
    }

    // MouseMotionListener-metoder
    public void mouseDragged(MouseEvent e) {
        if (selectedShape != null) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            selectedShape.move(mouseX - offsetX - selectedShape.getBoundingBox().x, mouseY - offsetY - selectedShape.getBoundingBox().y); // Flytta figuren med musen
            repaint(); 
        }
    }

    // Övriga MouseListener och MouseMotionListener
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}

    // Main method
    public static void main(String[] args) {
        JFrame frame = new JFrame("Figures");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        Figure panel = new Figure();
        frame.add(panel);
    
        panel.setPreferredSize(new Dimension(800, 600));
    
        frame.pack();
        frame.setVisible(true);
    }
}



