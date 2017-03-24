package interfaz;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Ellipse2D;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 *
 * @author Giuliano
 */
public class WindowBubble extends JFrame {

    private boolean posFija = false;
    private boolean escritorio = false;
    private JPanel principal;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                WindowBubble w = new WindowBubble();
                w.setVisible(true);
            }
        });
    }

    public WindowBubble() {
        super("ShapedWindow");
        setLayout(new GridBagLayout());
        setType(JFrame.Type.UTILITY);

        // It is best practice to set the window's shape in
        // the componentResized method.  Then, if the window
        // changes size, the shape will be correctly recalculated.
        addComponentListener(new ComponentAdapter() {
            // Give the window an elliptical shape.
            // If the window is resized, the shape is recalculated here.
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new Ellipse2D.Double(0, 0, getWidth(), getHeight()));
            }
        });
        addWindowListener(new WindowBubbleListener(this));
        addFocusListener(new WindowBubbleListener(this));

        setUndecorated(true);
        setSize(50, 50);
        setOpacity(0.8f);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        principal = new JPanel();
        principal.setBackground(Color.BLACK);
        principal.setLayout(new GridBagLayout());
        add(principal);

        JLabel folder = new JLabel(new ImageIcon(getClass().getResource("/recursos/folder.png")));
        principal.add(folder);

        JPopupMenu opciones = new JPopupMenu();
        JMenuItem opcionCerrar = new JMenuItem("Cerrar");
        opcionCerrar.addActionListener(new CerrarListener(this));
        opciones.add(opcionCerrar);
        JCheckBoxMenuItem opcionFijar = new JCheckBoxMenuItem("Fijar posicion");
        opcionFijar.addActionListener(new FijarListener(this));
        opciones.add(opcionFijar);
        JCheckBoxMenuItem opcionModoEscritorio = new JCheckBoxMenuItem("Modo escritorio");
        opcionModoEscritorio.addActionListener(new ModoEscritorioListener(this));
        opcionModoEscritorio.setEnabled(false);
        opciones.add(opcionModoEscritorio);
        principal.setComponentPopupMenu(opciones);

        WindowBubbleMouseListener mover = new WindowBubbleMouseListener(this);
        principal.addMouseListener(mover);
        principal.addMouseMotionListener(mover);
    }

    public boolean isPosFija() {
        return posFija;
    }

    public void setPosFija() {
        this.posFija = !posFija;
    }

    public boolean isEscritorio() {
        return escritorio;
    }

    public void setEscritorio() {
        this.escritorio = !escritorio;
    }

    public JPanel getPrincipal() {
        return principal;
    }

    class CerrarListener implements ActionListener {

        private WindowBubble bubble;

        public CerrarListener(WindowBubble bubble) {
            this.bubble = bubble;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            bubble.dispose();
        }
    }

    class FijarListener implements ActionListener {

        private WindowBubble bubble;

        public FijarListener(WindowBubble bubble) {
            this.bubble = bubble;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            bubble.setPosFija();
        }
    }

    class ModoEscritorioListener implements ActionListener {

        private WindowBubble bubble;

        public ModoEscritorioListener(WindowBubble bubble) {
            this.bubble = bubble;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            bubble.setEscritorio();
            if (bubble.isEscritorio()) {
                //Boton mostrar escritorio hace desaparecer JFrame
                //bubble.setAlwaysOnTop(false);
                //bubble.toBack();
            } else {
                bubble.setAlwaysOnTop(true);
                bubble.toFront();
            }
        }
    }

    class WindowBubbleListener implements WindowListener, FocusListener {

        private WindowBubble bubble;

        public WindowBubbleListener(WindowBubble bubble) {
            this.bubble = bubble;
        }

        @Override
        public void windowOpened(WindowEvent e) {

        }

        @Override
        public void windowClosing(WindowEvent e) {

        }

        @Override
        public void windowClosed(WindowEvent e) {

        }

        @Override
        public void windowIconified(WindowEvent e) {

        }

        @Override
        public void windowDeiconified(WindowEvent e) {

        }

        @Override
        public void windowActivated(WindowEvent e) {

        }

        @Override
        public void windowDeactivated(WindowEvent e) {

        }

        @Override
        public void focusGained(FocusEvent e) {

        }

        @Override
        public void focusLost(FocusEvent e) {

        }

    }

    class WindowBubbleMouseListener implements MouseMotionListener, MouseListener {

        private int x;
        private int y;
        private WindowBubble bubble;

        public WindowBubbleMouseListener(WindowBubble bubble) {
            this.bubble = bubble;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!bubble.isPosFija()) {
                Point point = MouseInfo.getPointerInfo().getLocation();
                bubble.setLocation(point.x - x, point.y - y);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                WindowBox box;
                box = new WindowBox(bubble.getLocation(), bubble);
                box.setVisible(true);
                bubble.setVisible(false);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            x = e.getX();
            y = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (bubble.getLocation().x < 0) {
                setLocation(0, bubble.getLocation().y);
            } else if (bubble.getLocation().x + bubble.getWidth() > Toolkit.getDefaultToolkit().getScreenSize().getWidth()) {
                setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - bubble.getWidth()), bubble.getLocation().y);
            }
            if (bubble.getLocation().y < 0) {
                setLocation(bubble.getLocation().x, 0);
            } else if (bubble.getLocation().y + bubble.getHeight() > Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
                setLocation(bubble.getLocation().x, (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - bubble.getHeight()));
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            bubble.getPrincipal().setBackground(new Color(20,20,20));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            bubble.getPrincipal().setBackground(Color.BLACK);
        }

    }

}
