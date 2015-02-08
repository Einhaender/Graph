package einhaenderMath;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ErrorFrame
  extends JFrame
{
	private static final long	serialVersionUID	= 1826251667086737784L;
	private JLabel lblError;
  private JButton buttOK;
  private ErrorFrame tis;
  
  public ErrorFrame(String text)
  {
    this.tis = this;
    setSize(200, 100);
    if (text.length() > 30) {
      setSize(text.length() * 8, 100);
    }
    setLocation(500, 400);
    setDefaultCloseOperation(3);
    setBackground(Color.RED);
    setTitle("Error");
    this.lblError = new JLabel(text);
    this.lblError.setHorizontalAlignment(0);
    this.lblError.setForeground(Color.RED);
    this.buttOK = new JButton("OK");
    this.buttOK.addActionListener(new listenerButtEvaluate());
    setLayout(new BorderLayout());
    add(this.lblError, "North");
    add(this.buttOK, "South");
    setAlwaysOnTop(true);
    setVisible(true);
  }
  
  private class listenerButtEvaluate
    implements ActionListener
  {
    private listenerButtEvaluate() {}
    
    public void actionPerformed(ActionEvent e)
    {
      ErrorFrame.this.tis.dispose();
    }
  }
}