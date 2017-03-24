package interfaz;

import estructura.Acceso;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;

/**
 *
 * @author Giuliano
 */
public class WindowBox extends JFrame {

    private EntityManager entityManager = Persistence.createEntityManagerFactory("BubblesPU").createEntityManager();
    private WindowBubble padre;
    private JPanel principal;
    private JPanel iconos;

    public WindowBox(Point posicion, WindowBubble padre) {
        super("ShapedWindow");
        this.padre = padre;
        setLayout(new GridBagLayout());
        setType(JFrame.Type.UTILITY);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
            }
        });

        addWindowFocusListener(new WindowBoxListener(this));

        setUndecorated(true);
        setSize(200, 200);
        setOpacity(0.95f);
        setLocation(posicion);
        setAlwaysOnTop(true);
        getContentPane().setBackground(Color.BLACK);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        principal = new JPanel();
        principal.setBackground(Color.BLACK);
        principal.setLayout(new BorderLayout());
        add(principal);
        
        CerrarButton cerrar = new CerrarButton();
        principal.add(cerrar, BorderLayout.PAGE_START);
        
        iconos = new JPanel();
        iconos.setBackground(Color.BLACK);
        principal.add(iconos);
        
        actualizarIconos();
    }

    //PROBANDO
    public void a1() {
        principal.remove(iconos);
        iconos.removeAll();
        iconos = new JPanel();
        iconos.setBackground(Color.BLACK);
        iconos.setLayout(new GridLayout(2, 2));
        iconos.setSize(32 * 2, 32 * 2);
        principal.add(iconos);
        
        Acceso prueba = new Acceso(4);
        prueba.setDireccion("www.google.com");
        prueba.setIconoDir("/recursos/default.png");
        iconos.add(new IconoButton(prueba));
        principal.repaint();
        iconos.repaint();
    }
    
    public void actualizarIconos() {
        
        Query query = entityManager.createNamedQuery("Acceso.findAll");
        Collection<Acceso> result = query.getResultList();
        
        int division = 1;
        if (result.size() + 1 == 1) {
            division = 1;
        } else if (result.size() + 1 <= 4) {
            division = 2;
        } else if (result.size() + 1 <= 9) {
            division = 3;
        } else if (result.size() + 1 <= 16) {
            division = 4;
        } else if (result.size() + 1 <= 25) {
            division = 5;
        } else if (result.size() <= 36) {
            division = 6;
        }
        principal.setSize(48 * division + 32, 48 * division + 42);
        setSize(48 * division + 32, 48 * division + 42);
        
        iconos.setLayout(new GridLayout(division, division));
        iconos.setSize(32 * division, 32 * division);

        for (Acceso next : result) {
            IconoButton icono = new IconoButton(next);
            iconos.add(icono);
        }

        if (result.size() < 36) {
            AgregarButton agregar = new AgregarButton();
            iconos.add(agregar);
        }
        
        principal.repaint();
        iconos.repaint();
    }

    public void cerrar() {
        padre.setVisible(true);
        this.dispose();
    }

    class WindowBoxListener implements WindowFocusListener {

        private WindowBox box;

        public WindowBoxListener(WindowBox box) {
            this.box = box;
        }

        @Override
        public void windowGainedFocus(WindowEvent e) {
            if (box.getLocation().x < 0) {
                setLocation(0, box.getLocation().y);
            } else if (box.getLocation().x + box.getWidth() > Toolkit.getDefaultToolkit().getScreenSize().getWidth()) {
                setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - box.getWidth()), box.getLocation().y);
            }
            if (box.getLocation().y < 0) {
                setLocation(box.getLocation().x, 0);
            } else if (box.getLocation().y + box.getHeight() > Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
                setLocation(box.getLocation().x, (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - box.getHeight()));
            }
        }

        @Override
        public void windowLostFocus(WindowEvent e) {
            //cerrar();
        }

    }

    class IconoButton extends JButton {

        public IconoButton(Acceso acc) {
            setIcon(new ImageIcon(getClass().getResource(acc.getIconoDir())));
            setContentAreaFilled(false);
            setSize(32, 32);
            setBorder(new RoundedBorder(10));
            setForeground(Color.BLACK);
            setFocusPainted(false);

            JPopupMenu opciones = new JPopupMenu();
            JMenuItem opcionEliminar = new JMenuItem("Eliminar");
            opcionEliminar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Acceso aElim = entityManager.find(Acceso.class, acc.getId());
                    entityManager.getTransaction().begin();
                    entityManager.remove(aElim);
                    entityManager.getTransaction().commit();
                    actualizarIconos();
                }
            });
            opciones.add(opcionEliminar);
            setComponentPopupMenu(opciones);

            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String direccion = acc.getDireccion();
                    try {
                        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + direccion);
                    } catch (Exception err) {
                        JOptionPane.showMessageDialog(null, "Error: " + err);
                    }
                }
            });
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    setForeground(Color.DARK_GRAY);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setForeground(Color.BLACK);
                }
            });
        }

    }

    class AgregarButton extends JButton {

        public AgregarButton() {
            setIcon(new ImageIcon(getClass().getResource("/recursos/agregar.png")));
            setContentAreaFilled(false);
            setSize(32, 32);
            setBorder(new RoundedBorder(10));
            setForeground(Color.BLACK);
            setFocusPainted(false);

            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //agregar
                    a1();
                    //actualizarIconos();
                }
            });
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    setForeground(Color.DARK_GRAY);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setForeground(Color.BLACK);
                }
            });
        }

    }

    class CerrarButton extends JButton {

        public CerrarButton() {
            setIcon(new ImageIcon(getClass().getResource("/recursos/cerrar.png")));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(new RoundedBorder(2));
            setForeground(Color.BLACK);

            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cerrar();
                }
            });
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    setForeground(Color.DARK_GRAY);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setForeground(Color.BLACK);
                }
            });
        }

    }

    class RoundedBorder implements Border {

        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}
