

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("all")
public class tess extends JPanel {

    public static void main(String[] args) {
        JFrame f = new JFrame();
        tess t = new tess();
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(t,BorderLayout.CENTER);
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    public tess() {
        setPreferredSize(new Dimension(300, 300));
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g.create();
        Rectangle2D rectangleNotToDrawIn = new Rectangle2D.Double(100, 100, 20, 30);
        Area outside = calculateRectOutside(rectangleNotToDrawIn);
        g2.setPaint(Color.white);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setPaint(Color.black);
        g2.setClip(outside);
        g2.drawLine(0, 0, getWidth(), getHeight());

    }


    private Area calculateRectOutside(Rectangle2D r) {
        Area outside = new Area(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        outside.subtract(new Area(r));
        return outside;
    }

}
