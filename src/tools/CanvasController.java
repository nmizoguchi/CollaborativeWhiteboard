package tools;

import java.awt.Image;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface CanvasController extends MouseListener, MouseMotionListener {
    public void paint(String[] args);
}
