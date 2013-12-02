package client.gui;

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

public class MenuNorth extends JMenuBar {

	private final JMenu file;
	private final JMenuItem save;
	private final JFileChooser saveFile;
	private final WhiteboardGUI gui;
	
	public MenuNorth(final WhiteboardGUI gui) {
		saveFile = new JFileChooser();
		file = new JMenu();
		file.setText("File");
		save = new JMenuItem();
		save.setText("Save");
		save.setAccelerator(KeyStroke.getKeyStroke(ActionEvent.CTRL_MASK, KeyEvent.VK_S));
		file.add(save);
		this.gui = gui;

		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = saveFile.showSaveDialog(MenuNorth.this);
				if (returnVal == saveFile.APPROVE_OPTION){
					File file = saveFile.getSelectedFile();
					try{
						BufferedImage img = new BufferedImage(gui.getWidth(), gui.getHeight(), BufferedImage.TYPE_INT_RGB);
						gui.paint(img.getGraphics());
						ImageIO.write(img, "png", file);
					} catch (IOException ex){
					}

				}
			}
		});
		this.add(file);
	}

}
