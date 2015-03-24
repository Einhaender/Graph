package einhaenderMath;

import helper.MessageFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUIGraph extends JPanel {
	private static final long	serialVersionUID		= 6812517344141364091L;
	private JButton						buttGraph;
	private JPanel						frameGraphSouth;
	private JPanel						frameInfo;
	private JPanel						frameInfoNorthLabels;
	private JPanel						frameInfoCenterFields;
	private JLabel						lblEquation;
	private JLabel						lblXMin;
	private JLabel						lblXMax;
	private JLabel						lblYMin;
	private JLabel						lblYMax;
	private JTextField				textEquation;
	private JTextField				textXMin;
	private JTextField				textXMax;
	private JTextField				textYMin;
	private JTextField				textYMax;
	private double						xMin;
	private double						xMax;
	private double						yMin;
	private double						yMax;
	private int								frameGraphXSizeDONOTUSE;
	private int								frameGraphYSizeDONOTUSE;
	private Equation					eq;
	private final double[]		frameXValueDONOTUSE	= { 0.0D };
	private final Color				COLORBACKROUND			= Color.LIGHT_GRAY;
	
	private class listenerButtEvaluate implements ActionListener {
		private listenerButtEvaluate() {
		}
		
		public void actionPerformed(ActionEvent e) {
			String txtError = "";
			try {
				GUIGraph.this.xMin = Double.valueOf(GUIGraph.this.textXMin.getText()).doubleValue();
				GUIGraph.this.xMax = Double.valueOf(GUIGraph.this.textXMax.getText()).doubleValue();
				GUIGraph.this.yMin = Double.valueOf(GUIGraph.this.textYMin.getText()).doubleValue();
				GUIGraph.this.yMax = Double.valueOf(GUIGraph.this.textYMax.getText()).doubleValue();
				if ((GUIGraph.this.xMin >= GUIGraph.this.xMax) || (GUIGraph.this.yMin >= GUIGraph.this.yMax)) {
					txtError = txtError + "the maximum window bound must be greater than the minimum; ";
				}
			} catch (Exception e2) {
				txtError = txtError + "window bounds must be numbers; ";
			}
			if (!txtError.isEmpty()) {
				new MessageFrame(txtError.substring(0, txtError.length() - 2), true);
			}
			char[] temp = { 'x' };
			try {
				GUIGraph.this.eq = new Equation(GUIGraph.this.textEquation.getText(), temp);
			} catch (Exception e1) {
				new MessageFrame(e1.getMessage(), true);
				e1.printStackTrace();
			}
			GUIGraph.this.graph();
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Graph");
		frame.setSize(500, 600);
		frame.setLocation(100, 20);
		frame.setDefaultCloseOperation(3);
		frame.setContentPane(new GUIGraph());
		frame.setVisible(true);
	}
	
	public GUIGraph() {
		setLayout(new BorderLayout());
		
		setupFrameGraphCenter();
		add(this.frameGraphSouth, "Center");
		
		setupFrameInfo();
		add(this.frameInfo, "North");
	}
	
	private void drawAxis(Graphics g) {
		double yAxisxPercent = (0.0D - this.xMin) / (this.xMax - this.xMin);
		
		int originYCoord = valueToPixelY(0.0D);
		int originXCoord = (int) (yAxisxPercent * this.frameGraphXSizeDONOTUSE);
		
		Color c = g.getColor();
		g.setColor(Color.ORANGE);
		
		g.drawLine(0, originYCoord - 1, this.frameGraphXSizeDONOTUSE, originYCoord - 1);
		g.drawLine(0, originYCoord, this.frameGraphXSizeDONOTUSE, originYCoord);
		g.drawLine(0, originYCoord + 1, this.frameGraphXSizeDONOTUSE, originYCoord + 1);
		
		g.drawLine(originXCoord - 1, valueToPixelY(this.yMin), originXCoord - 1, valueToPixelY(this.yMax));
		g.drawLine(originXCoord, valueToPixelY(this.yMin), originXCoord, valueToPixelY(this.yMax));
		g.drawLine(originXCoord + 1, valueToPixelY(this.yMin), originXCoord + 1, valueToPixelY(this.yMax));
		
		g.setColor(c);
	}
	
	private void graph() {
		this.frameGraphXSizeDONOTUSE = this.frameGraphSouth.getSize().width;
		this.frameGraphYSizeDONOTUSE = this.frameGraphSouth.getSize().height;
		Graphics g = this.frameGraphSouth.getGraphics();
		g.setColor(this.COLORBACKROUND);
		g.fillRect(0, 0, this.frameGraphXSizeDONOTUSE, this.frameGraphYSizeDONOTUSE);
		g.setColor(Color.BLACK);
		
		drawAxis(g);
		
		char[] tempVars = { 'x' };
		try {
			this.eq = new Equation(this.textEquation.getText(), tempVars);
		} catch (Exception e1) {
			System.out.println("caught exeption when initiating pixel -1; ignoring...");
			e1.printStackTrace();
		}
		double lastY = 0.0D;
		try {
			lastY = this.eq.evaluate(pixelToGraphX(-1));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for (int x = 0; x <= this.frameGraphXSizeDONOTUSE + 1; x++) {
			double thisY = 0.0D;
			try {
				thisY = this.eq.evaluate(pixelToGraphX(x));
			} catch (Exception e) {
				System.err.println("caught exeption during graphing... halting proccess.");
				new MessageFrame(e.getMessage());
				e.printStackTrace();
				break;
			}
			g.drawLine(x - 1, valueToPixelY(lastY), x, valueToPixelY(thisY));
			lastY = thisY;
		}
	}
	
	private double[] pixelToGraphX(int x) {
		this.frameXValueDONOTUSE[0] = ((this.xMax - this.xMin) / this.frameGraphXSizeDONOTUSE * x + this.xMin);
		return this.frameXValueDONOTUSE;
	}
	
	private void setupFrameGraphCenter() {
		this.frameGraphSouth = new JPanel();
		this.frameGraphSouth.setSize(500, 500);
		this.frameGraphSouth.setBackground(this.COLORBACKROUND);
	}
	
	private void setupFrameInfo() {
		this.frameInfo = new JPanel();
		this.frameInfo.setLayout(new BorderLayout());
		
		this.frameInfoNorthLabels = new JPanel();
		this.frameInfoNorthLabels.setLayout(new FlowLayout());
		
		this.lblEquation = new JLabel("Equation");
		this.frameInfoNorthLabels.add(this.lblEquation);
		
		this.lblXMin = new JLabel("X Min");
		this.frameInfoNorthLabels.add(this.lblXMin);
		
		this.lblXMax = new JLabel("X Max");
		this.frameInfoNorthLabels.add(this.lblXMax);
		
		this.lblYMin = new JLabel("Y Min");
		this.frameInfoNorthLabels.add(this.lblYMin);
		
		this.lblYMax = new JLabel("Y Max");
		this.frameInfoNorthLabels.add(this.lblYMax);
		
		this.frameInfoCenterFields = new JPanel();
		this.frameInfoCenterFields.setLayout(new FlowLayout());
		
		this.textEquation = new JTextField(10);
		this.frameInfoCenterFields.add(this.textEquation);
		
		this.textXMin = new JTextField("-10", 3);
		this.frameInfoCenterFields.add(this.textXMin);
		
		this.textXMax = new JTextField("10", 3);
		this.frameInfoCenterFields.add(this.textXMax);
		
		this.textYMin = new JTextField("-10", 3);
		this.frameInfoCenterFields.add(this.textYMin);
		
		this.textYMax = new JTextField("10", 3);
		this.frameInfoCenterFields.add(this.textYMax);
		
		this.frameInfo.add(this.frameInfoNorthLabels, "North");
		this.frameInfo.add(this.frameInfoCenterFields, "Center");
		
		this.buttGraph = new JButton("graph");
		this.buttGraph.addActionListener(new listenerButtEvaluate());
		this.buttGraph.setVerticalAlignment(0);
		this.frameInfo.add(this.buttGraph, "East");
	}
	
	private int valueToPixelY(double yValue) {
		return (int) (this.frameGraphYSizeDONOTUSE / (this.yMin - this.yMax) * (yValue - this.yMax));
	}
}