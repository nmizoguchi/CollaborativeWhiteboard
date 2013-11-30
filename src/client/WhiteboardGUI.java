package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
    private final JButton colorButton = new JButton();
    private final JColorChooser colorOptions = new JColorChooser();
    private final JList onlineUserList;
    private final JList whiteboardsList;
    private final JScrollPane onlineUserScroller;
    private final JScrollPane whiteboardsScroller;
    private final OnlineUserListModel activeUsersData;
    private final WhiteboardListModel activeWhiteboardsData;
    
    public WhiteboardGUI(ApplicationClient client) {
        /********** Initialize attributes **********/
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
        colorButton.setBackground(Color.BLACK);
        
        activeUsersData = client.getActiveUsers();
        activeWhiteboardsData = client.getActiveWhiteboards();
        
        onlineUserList = new JList(activeUsersData);
        whiteboardsList = new JList(activeWhiteboardsData);
        
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
        
        // this is for the eraser sizes
        buttonsMenu.setLayout(new GridLayout(20, 1));
        colorOptions.setPreviewPanel(new JPanel());
        
        onlineUserList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        onlineUserList.setLayoutOrientation(JList.VERTICAL);
        onlineUserList.setVisibleRowCount(-1);
        onlineUserScroller = new JScrollPane(onlineUserList);
        onlineUserScroller.setPreferredSize(new Dimension(200, 80));
        
        whiteboardsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        whiteboardsList.setLayoutOrientation(JList.VERTICAL);
        whiteboardsList.setVisibleRowCount(-1);
        whiteboardsScroller = new JScrollPane(whiteboardsList);
        whiteboardsScroller.setPreferredSize(new Dimension(200, 80));
        
        /********** Initialize listeners **********/
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

        /********** Initialize Layout **********/
        buttonsMenu.add(eraser);
        buttonsMenu.add(freehand);
        buttonsMenu.add(drawLine);
        buttonsMenu.add(drawRect);
        buttonsMenu.add(brushSizes);
        buttonsMenu.add(colorButton);
        this.add(canvas, BorderLayout.CENTER);
        this.add(buttonsMenu, BorderLayout.WEST);
        this.add(onlineUserScroller, BorderLayout.EAST);
        this.add(whiteboardsScroller, BorderLayout.NORTH);
        
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
    }

    public void updateModelView(String command) {
        // set up the UI (on the event-handling thread)
        SwingUtilities.invokeLater(new CanvasPainter(canvas, command));
    }
}
