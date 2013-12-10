package client.gui;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import Protocol.CWPMessage;

/**
 * This menu bar (located at the top of the GUI) contains a submenu called "File" with a menu item called "Save".
 * Clicking "Save" or entering CTRL+S would save a
 * screenshot of the whiteboard as a png file in the user-selected directory
 * 
 * @author rcha
 */
public class MenuNorth extends JMenuBar {

	private final JMenu file;
	private final JMenuItem save;
	private final JFileChooser saveFile;
	private final WhiteboardGUI gui;
	
	public MenuNorth(final WhiteboardGUI gui) {
		//initiates a menu and its submenu
		saveFile = new JFileChooser();
		saveFile.setToolTipText("Must add .png to the end of file");
		saveFile.setDialogTitle("Save screenshot of whiteboard");
		file = new JMenu();
		file.setText("File");
		save = new JMenuItem();
		save.setText("Save As..");
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		file.add(save);
		this.gui = gui;
		/*
		 * When this menu item is activated, it opens a save dialog
		 * that allows the user to save a file and the program would save
		 *  a png file at the indicated file directory
		 */
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = saveFile.showSaveDialog(MenuNorth.this);
				if (returnVal == saveFile.APPROVE_OPTION){
					File file;
					String fileTemp = saveFile.getSelectedFile().getPath();
					if (fileTemp.contains(".png")){
						file = new File(fileTemp);
					}
					else {
						file = new File(saveFile.getSelectedFile().getPath() + ".png");
					}
					try{
						synchronized(gui){
							BufferedImage img = new BufferedImage(gui.getWidth(), gui.getHeight(), BufferedImage.TYPE_INT_RGB);
							gui.paint(img.getGraphics());
							ImageIO.write(img, "png", file);
						}
					} catch (IOException ex){
					}

				}
			}
		});
		
		this.add(file);
	}

}
