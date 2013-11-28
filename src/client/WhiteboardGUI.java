package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import canvas.Canvas;
import canvas.CanvasPainter;

public class WhiteboardGUI extends JFrame {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private final Canvas canvas;
    private final JMenuBar buttonsMenu = new JMenuBar();
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final ImageIcon eraserIcon;
    private final ImageIcon drawIcon;
    private final JToggleButton eraser;
    private final JToggleButton freehand;
    private final JToggleButton drawLine;
    private final JToggleButton drawRect;
    private final JMenu brushSizes = new JMenu("brush sizes");
    private final JButton size5 = new JButton("5");
    private final JButton size20 = new JButton("20");

    public WhiteboardGUI(WhiteboardClient client) {
        // creates eraser and drawLine buttons with icons
        canvas = new Canvas(800, 600, client);
        eraserIcon = new ImageIcon(new ImageIcon("images/eraser.gif")
                .getImage().getScaledInstance(50, 50, 100));
        drawIcon = new ImageIcon(new ImageIcon("images/brush.png").getImage()
                .getScaledInstance(50, 50, 100));
        eraser = new JToggleButton(eraserIcon);
        eraser.setBackground(Color.WHITE);
        freehand = new JToggleButton(drawIcon);
        freehand.setBackground(Color.WHITE);
        drawLine = new JToggleButton(drawIcon);
        drawLine.setBackground(Color.WHITE);
        drawRect = new JToggleButton(drawIcon);
        drawRect.setBackground(Color.WHITE);
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(new Rectangle(800, 600));

        // buttonGroup ensures that only one of the buttons is selected
        buttonGroup.add(eraser);
        buttonGroup.add(freehand);
        buttonGroup.add(drawLine);
        buttonGroup.add(drawRect);
        // default selected button
        freehand.setSelected(true);
        // this is for the eraser sizes
        buttonsMenu.setLayout(new GridLayout(10, 2));

        // listener that sets the brush size
        class sizeListener implements ActionListener {
            private int brushsize;

            public sizeListener(int b) {
                brushsize = b;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setBrushSize(brushsize);
            }
        }
        size5.addActionListener(new sizeListener(5));
        size20.addActionListener(new sizeListener(20));
        brushSizes.add(size5);
        brushSizes.add(size20);

        // sets the mode and selects the button
        eraser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setMode(Canvas.MODE.ERASE);
            }
        });
        freehand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setMode(Canvas.MODE.FREEHAND);
            }
        });
        drawLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setMode(Canvas.MODE.LINE);
            }
        });
        drawRect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setMode(Canvas.MODE.RECTANGLE);
            }
        });

        // layout
        buttonsMenu.add(eraser);
        buttonsMenu.add(freehand);
        buttonsMenu.add(drawLine);
        buttonsMenu.add(drawRect);
        buttonsMenu.add(brushSizes);
        this.add(canvas, BorderLayout.CENTER);
        this.add(buttonsMenu, BorderLayout.WEST);
    }

    public void updateModelView(String command) {
        // set up the UI (on the event-handling thread)
        SwingUtilities.invokeLater(new CanvasPainter(canvas, command));
    }
}
