package client.gui.canvas.tools;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import Protocol.CWPMessage;

public interface ToolController extends MouseListener, MouseMotionListener {
    public void paint(CWPMessage message);
}
