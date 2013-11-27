package canvas;

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

import canvas.Canvas.MODE;

public class WhiteboardGUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Canvas canvas = new Canvas(800, 600);
	private final JMenuBar buttonsMenu = new JMenuBar();
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final ImageIcon eraserIcon;
	private final ImageIcon drawIcon;
	private final JToggleButton eraser;
	private final JMenu brushSizes = new JMenu("brush sizes");
	private final JToggleButton drawLine;
	private final JButton size5 = new JButton("5");
	private final JButton size20 = new JButton("20");
	
	public WhiteboardGUI() {
		//creates eraser and drawLine buttons with icons
		eraserIcon =  new ImageIcon(new ImageIcon("images/eraser.gif").getImage().getScaledInstance(50, 50, 100));
		drawIcon = new ImageIcon(new ImageIcon("images/brush.png").getImage().getScaledInstance(50, 50, 100));
		eraser = new JToggleButton(eraserIcon);
		eraser.setBackground(Color.WHITE);
		drawLine = new JToggleButton(drawIcon);
		drawLine.setBackground(Color.WHITE);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setBounds(new Rectangle(800, 600));
		
		//buttonGroup ensures that only one of the buttons is selected
		buttonGroup.add(eraser);
		buttonGroup.add(drawLine);
		//default selected button
		drawLine.setSelected(true);
		//this is for the eraser sizes
		buttonsMenu.setLayout(new GridLayout(10, 2));
		
		//listener that sets the brush size
		class sizeListener implements ActionListener {
			private int brushsize;
			public sizeListener(int b){
				brushsize = b;
			}
			@Override
			public void actionPerformed(ActionEvent e){
				canvas.setBrushSize(brushsize);
			}
		}
		size5.addActionListener(new sizeListener(5));
		size20.addActionListener(new sizeListener(20));
		brushSizes.add(size5);
		brushSizes.add(size20);
		
		//sets the mode and selects the button
		eraser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canvas.setMode(MODE.ERASE);
			}
		});
		drawLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				canvas.setMode(MODE.DRAW_LINE);
			}
		});

		//layout
		buttonsMenu.add(eraser);
		buttonsMenu.add(drawLine);
		buttonsMenu.add(brushSizes);
		this.add(canvas, BorderLayout.CENTER);
		this.add(buttonsMenu, BorderLayout.WEST);
		
	}

    /*
     * Main program. Make a window containing a Canvas.
     */
    public static void main(String[] args) {

    	// set up the UI (on the event-handling thread)
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			WhiteboardGUI board = new WhiteboardGUI();
    			board.setVisible(true);
    		}
    	});
    }
}
