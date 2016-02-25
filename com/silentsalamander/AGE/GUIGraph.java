// This file is part of AGE
//
// AGE Graphs Equations (AGE) is a collection of small java programs
// Copyright (C) 2016 Ivan Johnson:
// fire.4@cox.net
//
// AGE is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package com.silentsalamander.AGE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.silentsalamander.helper.PrettyLogger;
import com.silentsalamander.helper.equation.Equation;

public class GUIGraph extends JPanel {
  private class listenerButtEvaluate implements ActionListener {
    private listenerButtEvaluate() {
    }
    
    // TODO try using java to compile equation as class with java.Math imported? auto replace
    // sin(blah) with Math.sin if necessary.
    @Override
    public void actionPerformed(ActionEvent e) {
      String txtError = "";
      try {
        xMin = Double.valueOf(textXMin.getText()).doubleValue();
        xMax = Double.valueOf(textXMax.getText()).doubleValue();
        yMin = Double.valueOf(textYMin.getText()).doubleValue();
        yMax = Double.valueOf(textYMax.getText()).doubleValue();
        if ((xMin >= xMax) || (yMin >= yMax)) {
          txtError = txtError + "the maximum window bound must be greater than the minimum; ";
        }
      } catch (Exception e2) {
        txtError = txtError + "window bounds must be numbers; ";
      }
      if (!txtError.isEmpty()) {
        JOptionPane.showMessageDialog(null, txtError.substring(0, txtError.length() - 2), "Error",
          JOptionPane.ERROR_MESSAGE);
      }
      String[] temp = { "x" };
      try {
        eq = new Equation(textEquation.getText(), temp);
      } catch (Exception e1) {
        JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e1.printStackTrace();
      }
      graph();
    }
  }
  
  private static final long serialVersionUID = 6812517344141364091L;
  
  public static void main(String[] args) {
    log = PrettyLogger.getPrimaryLogger();
    JFrame frame = new JFrame("Graph");
    frame.setSize(500, 600);
    frame.setLocation(100, 20);
    frame.setDefaultCloseOperation(3);
    frame.setContentPane(new GUIGraph());
    frame.addWindowListener(new WindowListener() {
      @Override
      public void windowOpened(WindowEvent e) {
      }
      
      @Override
      public void windowIconified(WindowEvent e) {
      }
      
      @Override
      public void windowDeiconified(WindowEvent e) {
      }
      
      @Override
      public void windowDeactivated(WindowEvent e) {
      }
      
      @Override
      public void windowClosing(WindowEvent e) {
      }
      
      @Override
      public void windowClosed(WindowEvent e) {
        log.info("closing logger.");
        log.close();
      }
      
      @Override
      public void windowActivated(WindowEvent e) {
      }
    });
    frame.setVisible(true);
  }
  
  private JButton            buttGraph;
  private final Color        COLORBACKROUND      = Color.LIGHT_GRAY;
  private Equation           eq;
  private JPanel             frameGraphSouth;
  private int                frameGraphXSizeDONOTUSE;
  private int                frameGraphYSizeDONOTUSE;
  private JPanel             frameInfo;
  private JPanel             frameInfoCenterFields;
  private JPanel             frameInfoNorthLabels;
  private final double[]     frameXValueDONOTUSE = { 0.0D };
  private JLabel             lblEquation;
  private JLabel             lblXMax;
  private JLabel             lblXMin;
  private JLabel             lblYMax;
  private JLabel             lblYMin;
  private JTextField         textEquation;
  private JTextField         textXMax;
  private JTextField         textXMin;
  private JTextField         textYMax;
  private JTextField         textYMin;
  private double             xMax;
  private double             xMin;
                             
  private double             yMax;
                             
  private double             yMin;
                             
  public static PrettyLogger log;
                             
  public GUIGraph() {
    setLayout(new BorderLayout());
    
    setupFrameGraphCenter();
    add(frameGraphSouth, "Center");
    
    setupFrameInfo();
    add(frameInfo, "North");
  }
  
  private void drawAxis(Graphics g) {
    double yAxisxPercent = (0.0D - xMin) / (xMax - xMin);
    
    int originYCoord = valueToPixelY(0.0D);
    int originXCoord = (int) (yAxisxPercent * frameGraphXSizeDONOTUSE);
    
    Color c = g.getColor();
    g.setColor(Color.ORANGE);
    
    g.drawLine(0, originYCoord - 1, frameGraphXSizeDONOTUSE, originYCoord - 1);
    g.drawLine(0, originYCoord, frameGraphXSizeDONOTUSE, originYCoord);
    g.drawLine(0, originYCoord + 1, frameGraphXSizeDONOTUSE, originYCoord + 1);
    
    g.drawLine(originXCoord - 1, valueToPixelY(yMin), originXCoord - 1, valueToPixelY(yMax));
    g.drawLine(originXCoord, valueToPixelY(yMin), originXCoord, valueToPixelY(yMax));
    g.drawLine(originXCoord + 1, valueToPixelY(yMin), originXCoord + 1, valueToPixelY(yMax));
    
    g.setColor(c);
  }
  
  private void graph() {
    frameGraphXSizeDONOTUSE = frameGraphSouth.getSize().width;
    frameGraphYSizeDONOTUSE = frameGraphSouth.getSize().height;
    Graphics g = frameGraphSouth.getGraphics();
    g.setColor(COLORBACKROUND);
    g.fillRect(0, 0, frameGraphXSizeDONOTUSE, frameGraphYSizeDONOTUSE);
    g.setColor(Color.BLACK);
    
    drawAxis(g);
    
    String[] tempVars = { "x" };
    try {
      eq = new Equation(textEquation.getText(), tempVars);
    } catch (Exception e1) {
      log.warning("caught exeption when initiating pixel -1; ignoring...");
      e1.printStackTrace();
    }
    double lastY = 0.0D;
    try {
      lastY = eq.evaluate(pixelToGraphX(-1));
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    for (int x = 0; x <= frameGraphXSizeDONOTUSE + 1; x++) {
      double thisY = 0.0;
      try {
        thisY = eq.evaluate(pixelToGraphX(x));
      } catch (Exception e) {
        log.severe("caught exeption during graphing... halting proccess.");
        JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        break;
      }
      g.drawLine(x - 1, valueToPixelY(lastY), x, valueToPixelY(thisY));
      lastY = thisY;
    }
  }
  
  private double[] pixelToGraphX(int x) {
    frameXValueDONOTUSE[0] = ((xMax - xMin) / frameGraphXSizeDONOTUSE * x + xMin);
    return frameXValueDONOTUSE;
  }
  
  private void setupFrameGraphCenter() {
    frameGraphSouth = new JPanel();
    frameGraphSouth.setSize(500, 500);
    frameGraphSouth.setBackground(COLORBACKROUND);
  }
  
  private void setupFrameInfo() {
    frameInfo = new JPanel();
    frameInfo.setLayout(new BorderLayout());
    
    frameInfoNorthLabels = new JPanel();
    frameInfoNorthLabels.setLayout(new FlowLayout());
    
    lblEquation = new JLabel("Equation");
    frameInfoNorthLabels.add(lblEquation);
    
    lblXMin = new JLabel("X Min");
    frameInfoNorthLabels.add(lblXMin);
    
    lblXMax = new JLabel("X Max");
    frameInfoNorthLabels.add(lblXMax);
    
    lblYMin = new JLabel("Y Min");
    frameInfoNorthLabels.add(lblYMin);
    
    lblYMax = new JLabel("Y Max");
    frameInfoNorthLabels.add(lblYMax);
    
    frameInfoCenterFields = new JPanel();
    frameInfoCenterFields.setLayout(new FlowLayout());
    
    textEquation = new JTextField(10);
    frameInfoCenterFields.add(textEquation);
    
    textXMin = new JTextField("-10", 3);
    frameInfoCenterFields.add(textXMin);
    
    textXMax = new JTextField("10", 3);
    frameInfoCenterFields.add(textXMax);
    
    textYMin = new JTextField("-10", 3);
    frameInfoCenterFields.add(textYMin);
    
    textYMax = new JTextField("10", 3);
    frameInfoCenterFields.add(textYMax);
    
    frameInfo.add(frameInfoNorthLabels, "North");
    frameInfo.add(frameInfoCenterFields, "Center");
    
    buttGraph = new JButton("graph");
    buttGraph.addActionListener(new listenerButtEvaluate());
    buttGraph.setVerticalAlignment(0);
    frameInfo.add(buttGraph, "East");
  }
  
  private int valueToPixelY(double yValue) {
    return (int) (frameGraphYSizeDONOTUSE / (yMin - yMax) * (yValue - yMax));
  }
}
