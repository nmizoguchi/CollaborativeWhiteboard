package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import client.ClientApplication;
import client.RunnableChangeboard;
import client.gui.canvas.Canvas;
import client.gui.canvas.CanvasPainter;

public class WhiteboardGUI extends JFrame {
    /**
	 * This is the display portion that a user directly interacts with.
	 * @author rcha
	 */
    private static final long serialVersionUID = 1L;

    private final ClientApplication client;
    private final Canvas canvas;
    private final JScrollPane canvasPane;
    private final JMenuBar buttonsMenu = new JMenuBar();
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final ImageIcon eraserIcon;
    private final ImageIcon drawIcon;
    private final ImageIcon drawLineIcon;
    private final ImageIcon rectangleIcon;
    private final JToggleButton eraser;
    private final JToggleButton freehand;
    private final JToggleButton drawLine;
    private final JToggleButton drawRect;
    private final JLabel brushSizes;
    private final JButton sizeDec = new JButton("Size decrease");
    private final JButton sizeInc = new JButton("Size increase");
    private final JButton colorButton = new JButton();
    private final JColorChooser colorOptions = new JColorChooser();
    private final MenuEast menuEast;
    private final JMenuBar menuBar;
    private final JTextField enterSize = new JTextField();

    public WhiteboardGUI(ClientApplication client) {
        
        this.client = client;
        
        
        /********** Initialize attributes **********/
        // creates eraser and drawLine buttons with icons
        canvas = new Canvas(800, 600, client);
        canvasPane = new JScrollPane(canvas);
        menuEast = new MenuEast(this);
        menuBar = new MenuNorth(this);

        brushSizes = new JLabel("Size: " + canvas.getBrushSize());
        eraserIcon = new ImageIcon(new ImageIcon("images/Eraser.png")

                .getImage().getScaledInstance(50, 50, 100));
        drawIcon = new ImageIcon(new ImageIcon("images/brush.png").getImage()
                .getScaledInstance(50, 50, 100));
        drawLineIcon = new ImageIcon(new ImageIcon("images/Line.png").getImage()
        		.getScaledInstance(50, 50, 100));
        rectangleIcon = new ImageIcon(new ImageIcon("images/rectangle.png").getImage()
        		.getScaledInstance(50, 50, 100));
        eraser = new JToggleButton(eraserIcon);
        eraser.setBackground(Color.WHITE);
        eraser.setMnemonic(KeyEvent.VK_E);
        freehand = new JToggleButton(drawIcon);
        freehand.setBackground(Color.WHITE);
        freehand.setMnemonic(KeyEvent.VK_B);
        drawLine = new JToggleButton(drawLineIcon);
        drawLine.setBackground(Color.WHITE);
        drawLine.setMnemonic(KeyEvent.VK_L);
        drawRect = new JToggleButton(rectangleIcon);
        drawRect.setBackground(Color.WHITE);
        drawRect.setMnemonic(KeyEvent.VK_R);
        colorButton.setBackground(Color.BLACK);
        colorButton.setMnemonic(KeyEvent.VK_C);
        
        
        //adding tool tip texts to each icon
        eraser.setToolTipText("Erase tool. ALT+E");
        freehand.setToolTipText("Free draw tool. ALT+B");
        drawLine.setToolTipText("Draw line tool. ALT+L");
        drawRect.setToolTipText("Draw rectangle tool. ALT+R");
        colorButton.setToolTipText("Click to select color. ALT+C");
        sizeDec.setToolTipText("Decreases the size by 1." + "\n\r" + "ALT+'['");
        sizeInc.setToolTipText("Increases the size by 1. ALT + ']'");
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(new Rectangle(1000, 600));

        /********** Initialize buttonGroup **********/
        // buttonGroup ensures that only one of the buttons is selected
        buttonGroup.add(eraser);
        buttonGroup.add(freehand);
        buttonGroup.add(drawLine);
        buttonGroup.add(drawRect);

        // default selected button
        freehand.setSelected(true);
        
        buttonsMenu.setLayout(new GridLayout(10, 1));
        
        /********** Initialize listeners **********/
        /*
         * Clicking the colorButton will open up a color palette. The user-selected color will then set the 
         * brush color and fill color along with changing the colorButton to that selected color.
         */
        colorButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Color color = JColorChooser.showDialog(colorOptions, "Choose a color", colorOptions.getColor());
				if (color != null){
					canvas.setBrushColor(color.getRGB());
					canvas.setFillColor(color.getRGB());
					colorButton.setBackground(color);
				}
			}
		});
        
        // listener that sets the brush size
        class sizeListener implements ActionListener {
            private int brushsize;
            public sizeListener(int b) {
                brushsize = b;
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setBrushSize(canvas.getBrushSize() + brushsize);
                brushSizes.setText("Size: " + canvas.getBrushSize());
            }
        }
        sizeDec.addActionListener(new sizeListener(-1));
        sizeDec.setMnemonic(KeyEvent.VK_OPEN_BRACKET);
        sizeInc.addActionListener(new sizeListener(1));
        sizeInc.setMnemonic(KeyEvent.VK_CLOSE_BRACKET);
        
        enterSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				canvas.setBrushSize(Integer.parseInt(enterSize.getText()));
				brushSizes.setText("Size: " + canvas.getBrushSize());
				enterSize.setText("Input size");
			}
		});

        // sets the mode and selects the button
        eraser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	canvas.setBrushSize(50);
            	brushSizes.setText("Size: " + canvas.getBrushSize());
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
        
        /********** Initialize Layout **********/
        buttonsMenu.add(eraser);
        buttonsMenu.add(freehand);
        buttonsMenu.add(drawLine);
        buttonsMenu.add(drawRect);
        buttonsMenu.add(brushSizes);
        buttonsMenu.add(sizeDec);
        buttonsMenu.add(sizeInc);
        buttonsMenu.add(enterSize);
        buttonsMenu.add(colorButton);
        
        this.add(menuBar, BorderLayout.NORTH);
        this.add(canvasPane, BorderLayout.CENTER);
        this.add(buttonsMenu, BorderLayout.WEST);
        this.add(menuEast, BorderLayout.EAST);
//        this.setLayout(layout);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
    }

    public void updateModelView(String command) {
        // set up the UI (on the event-handling thread)
        SwingUtilities.invokeLater(new CanvasPainter(canvas, command));
    }
    
    public void changeWhiteboard(String boardName) {
        SwingUtilities.invokeLater(new RunnableChangeboard(canvas, boardName));
    }
    
    public void addChatMessage(String message) {
        menuEast.addMessage(message);
    }

    public ClientApplication getClient() {
        return client;
    }
}
