package tools;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


import canvas.Canvas;

public class SizeChangeListener implements KeyListener{
	private Canvas canvas;
	public SizeChangeListener(Canvas c) {
		canvas = c;
	}
	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("pressed");
		updateSize(e);
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		System.out.println("released");
		updateSize(e);
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
		System.out.println("typed");
		updateSize(e);
	}
	
	private void updateSize(KeyEvent arg0) {
		System.out.println(arg0.getKeyCode());
		System.out.println(arg0.VK_OPEN_BRACKET);
		if (arg0.getKeyCode() == arg0.VK_OPEN_BRACKET){
			canvas.setBrushSize(canvas.getBrushSize() - 1);
		}
		else if (arg0.getKeyCode() == arg0.VK_CLOSE_BRACKET){
			canvas.setBrushSize(canvas.getBrushSize() + 1);
		}
	}

}
