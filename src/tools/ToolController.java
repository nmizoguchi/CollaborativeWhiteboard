package tools;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface ToolController extends MouseListener, MouseMotionListener {
    public void paint(String[] args);
}
