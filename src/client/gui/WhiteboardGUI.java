package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
<<<<<<< HEAD:src/client/WhiteboardGUI.java
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
=======
import javax.swing.JPanel;
import javax.swing.JToggleButton;
>>>>>>> 3425202e03f6f826c7856ece2655bb0bb7d05bd6:src/client/gui/WhiteboardGUI.java
import javax.swing.SwingUtilities;

import client.ClientApplication;
import client.RunnableChangeboard;
import client.gui.canvas.Canvas;
import client.gui.canvas.CanvasPainter;

public class WhiteboardGUI extends JFrame {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private final ClientApplication client;
    private final Canvas canvas;
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
    private final JMenu brushSizes;
    private final JButton size5 = new JButton("5");
    private final JButton size20 = new JButton("20");
    private final JButton colorButton = new JButton();
    private final JColorChooser colorOptions = new JColorChooser();
    private final MenuEast menuEast;

    public WhiteboardGUI(ClientApplication client) {
        
        this.client = client;
        
        this.menuEast = new MenuEast(this);
        
        /********** Initialize attributes **********/
        // creates eraser and drawLine buttons with icons
        canvas = new Canvas(800, 600, client);

        brushSizes = new JMenu("Size: " + canvas.getBrushSize());
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
        freehand = new JToggleButton(drawIcon);
        freehand.setBackground(Color.WHITE);
        drawLine = new JToggleButton(drawLineIcon);
        drawLine.setBackground(Color.WHITE);
        drawRect = new JToggleButton(rectangleIcon);
        drawRect.setBackground(Color.WHITE);
        colorButton.setBackground(Color.BLACK);

        
        //adding tool tip texts to each icon
        brushSizes.setToolTipText("Click to select size");
        eraser.setToolTipText("Erase tool");
        freehand.setToolTipText("Free draw tool");
        drawLine.setToolTipText("Draw line tool");
        drawRect.setToolTipText("Draw rectangle tool");
        colorButton.setToolTipText("Click to select color");
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(new Rectangle(800, 600));

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
                canvas.setBrushSize(brushsize);
                brushSizes.setPopupMenuVisible(false);
                brushSizes.setText("Size: " + canvas.getBrushSize());
            }
        }
        size5.addActionListener(new sizeListener(5));
        size20.addActionListener(new sizeListener(20));

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
        brushSizes.add(size5);
        brushSizes.add(size20);
        buttonsMenu.add(eraser);
        buttonsMenu.add(freehand);
        buttonsMenu.add(drawLine);
        buttonsMenu.add(drawRect);
        buttonsMenu.add(brushSizes);
        buttonsMenu.add(colorButton);
        this.add(canvas, BorderLayout.CENTER);
        this.add(buttonsMenu, BorderLayout.WEST);
<<<<<<< HEAD:src/client/WhiteboardGUI.java
        this.add(onlineUserScroller, BorderLayout.EAST);
        this.add(whiteboardsScroller, BorderLayout.BEFORE_FIRST_LINE);
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        
        
        
//        whiteboardsList.addListSelectionListener(new ListSelectionListener() {
//            
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//
//                ListSelectionModel m = (ListSelectionModel)e.getSource();
//                for(int i = e.getFirstIndex(); i < e.getLastIndex(); i++) {
//                    if(m.isSelectedIndex(i)) {
//                        System.out.println("changeboard ");
//                    }
//                }
//            }
//        });
=======
        this.add(menuEast, BorderLayout.EAST);
>>>>>>> 3425202e03f6f826c7856ece2655bb0bb7d05bd6:src/client/gui/WhiteboardGUI.java
    }

    public void updateModelView(String command) {
        // set up the UI (on the event-handling thread)
        SwingUtilities.invokeLater(new CanvasPainter(canvas, command));
    }
    
    public void changeWhiteboard(String boardName) {
        SwingUtilities.invokeLater(new RunnableChangeboard(canvas,boardName));
    }
    
    public void addChatMessage(String user, String message) {
        menuEast.addMessage(user, message);
    }

    public ClientApplication getClient() {
        return client;
    }
}
