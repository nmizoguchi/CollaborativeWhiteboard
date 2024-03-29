package client.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.SwingUtilities;

import client.gui.canvas.Canvas;

/**
 * This is a menu bar located on the west side of the GUI. 
 * It contains tool buttons that user selects to 
 * erase, freedraw, draw lines and draw rectangles. It also contains color and size options
 *  that allows the user to view and select their brush color/size using their mouse or the keyboard.
 * @author rcha
 *
 */
public class MenuWest extends JMenuBar{
	
		private final Canvas canvas;
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
	    private final JButton sizeDec;
	    private final JButton sizeInc;
	    private final JButton colorButton;
	    private final JToggleButton fillColor;
	    private final JColorChooser colorOptions = new JColorChooser();
	    private final JTextField enterSize;
	    
	    /**
	     * Initializes the components and their shortcuts and sets up the layout
	     * @param c is the Canvas that this menu bar is associated with
	     */
	public MenuWest(Canvas c) {
		this.canvas = c;
		brushSizes = new JLabel("Size:");
        eraserIcon = new ImageIcon(new ImageIcon(this.getClass().getResource("/Eraser.png"))
                .getImage().getScaledInstance(50, 50, 100));
        drawIcon = new ImageIcon(new ImageIcon(this.getClass().getResource("/brush.png")).getImage()
                .getScaledInstance(50, 50, 100));
        drawLineIcon = new ImageIcon(new ImageIcon(this.getClass().getResource("/Line.png")).getImage()
        		.getScaledInstance(50, 50, 100));
        rectangleIcon = new ImageIcon(new ImageIcon(this.getClass().getResource("/rectangle.png")).getImage()
        		.getScaledInstance(50, 50, 100));
        eraser = new JToggleButton(eraserIcon);
        eraser.setBackground(Color.WHITE);
        freehand = new JToggleButton(drawIcon);
        freehand.setBackground(Color.WHITE);
        drawLine = new JToggleButton(drawLineIcon);
        drawLine.setBackground(Color.WHITE);
        drawRect = new JToggleButton(rectangleIcon);
        drawRect.setBackground(Color.WHITE);
        enterSize = new JTextField();
        enterSize.setText(String.valueOf(canvas.getBrushSize()));
        sizeDec = new JButton("-");
        sizeInc = new JButton("+");
        colorButton = new JButton();
        colorButton.setBackground(Color.BLACK);
        colorButton.setOpaque(true);
        fillColor = new JToggleButton();
        fillColor.setSelected(true);
        fillColor.setOpaque(true);
        fillColor.setBackground(Color.BLACK);
        
        //adding tool tip texts to each icon
        eraser.setToolTipText("Erase tool");
        freehand.setToolTipText("Free draw tool");
        drawLine.setToolTipText("Draw line tool");
        drawRect.setToolTipText("Draw rectangle tool");
        colorButton.setToolTipText("Click to select color");
        fillColor.setToolTipText("Right click to select fill color. Left click to toggle");
        sizeDec.setToolTipText("Decreases the size by 1");
        sizeInc.setToolTipText("Increases the size by 1");
        
        /********** Initialize buttonGroup **********/
        // buttonGroup ensures that only one of the buttons is selected
        buttonGroup.add(eraser);
        buttonGroup.add(freehand);
        buttonGroup.add(drawLine);
        buttonGroup.add(drawRect);

        // default selected button
        freehand.setSelected(true);
        
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
					colorButton.setBackground(color);
				}
			}
		});
        fillColor.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				fill(arg0);
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				fill(arg0);
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				fill(arg0);
				
			}
			private void fill(MouseEvent arg0){
				if (SwingUtilities.isRightMouseButton(arg0)){
					Color color = JColorChooser.showDialog(colorOptions, "Choose a color", colorOptions.getColor());
					if (color != null){
						canvas.setFillColor(color.getRGB());
						fillColor.setBackground(color);
					}
				} else if (SwingUtilities.isLeftMouseButton(arg0)){
					
					canvas.setHasFill(fillColor.isSelected()? false : true);
				}
			}
        });
        
        
        class sizeListener implements ActionListener {
        	/*
             * sizeListener implements ActionListener and modifies the user's brush size according to their actions
             * When the sizeDec button is clicked or activated, the brush size will decrease
             * and when the sizeInc button is clicked or activated, the brush size will increase
             */
            private int brushsize;
            public sizeListener(int b) {
                brushsize = b;
            }
            @Override
            public void actionPerformed(ActionEvent e) {
            	int size = canvas.getBrushSize() + brushsize;
                canvas.setBrushSize(size > 0? size : 1);
                enterSize.setText(String.valueOf(canvas.getBrushSize()));
            }
        }
        sizeDec.addActionListener(new sizeListener(-1));
        sizeDec.setMnemonic(KeyEvent.VK_OPEN_BRACKET);
        sizeInc.addActionListener(new sizeListener(1));
        sizeInc.setMnemonic(KeyEvent.VK_CLOSE_BRACKET);
        
       
        enterSize.addActionListener(new ActionListener() {
        	 /*
             * The user is able to enter a number in the enterSize text box
             * This would set their brush size to their input.
             * If the user enters an invalid number, a dialog box would open
             */
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
				canvas.setBrushSize(Integer.parseInt(enterSize.getText()));
				enterSize.setText(String.valueOf(canvas.getBrushSize()));
				} catch (NumberFormatException e){
					JOptionPane.showMessageDialog(canvas, "Please enter a number.", "Size Input error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

        //Sets the drawing mode to the tool that the user selects.
        //Drawing modes are ERASE, FREEHAND, LINE, and RECTANGLE
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
        
        /********** Initialize Layout **********/
        GroupLayout layout = new GroupLayout(this);
        
        ParallelGroup customSize = layout.createParallelGroup();
        customSize.addComponent(sizeDec).addComponent(sizeInc);
        ParallelGroup currentSize = layout.createParallelGroup();
        currentSize.addComponent(brushSizes).addComponent(enterSize);
        ParallelGroup twoColors = layout.createParallelGroup()
        		.addComponent(colorButton).addComponent(fillColor);
        SequentialGroup vertical = layout.createSequentialGroup();
        vertical.addComponent(eraser).addComponent(freehand).addComponent(drawLine).addComponent(drawRect)
        	.addGroup(currentSize).addGroup(customSize).addGroup(twoColors).addGap(150);
        layout.setVerticalGroup(vertical);
        
        SequentialGroup current = layout.createSequentialGroup()
        		.addComponent(brushSizes).addComponent(enterSize);
        
        ParallelGroup topBottom1 = layout.createParallelGroup().addComponent(sizeDec).addComponent(colorButton);
        ParallelGroup topBottom2 = layout.createParallelGroup().addComponent(sizeInc).addComponent(fillColor);
        SequentialGroup custom = layout.createSequentialGroup().addGroup(topBottom1).addGroup(topBottom2);
        
        ParallelGroup horizontal = layout.createParallelGroup()
        		.addComponent(eraser).addComponent(freehand).addComponent(drawLine).addComponent(drawRect)
        		.addGroup(current).addGroup(custom);
        layout.setHorizontalGroup(horizontal);
        
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        
        this.setLayout(layout);
        
	}
	

}