// This file is part of AGE
//
// AGE Graphs Equations (AGE) is a java program that graphs equations
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

import java.awt.Color;
import java.awt.Graphics;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.silentsalamander.helper.PrettyLogger;
import com.silentsalamander.helper.equation.Equation;

class PanelGraph extends JPanel {// only visible within this package
  // This is all code I wrote years ago when I was much less experienced with java, so:
  // TODO try to clean up this big mess of a class
  
  
  protected static PrettyLogger log;
  private static final long     serialVersionUID = 8666758096652663747L;
  static {
    log = PrettyLogger.getPrimaryLogger();
  }
  
  private final Color  COLORBACKROUND = Color.LIGHT_GRAY;
  
  protected AGEFrame graphFrame;
  
  protected double     xMin, xMax, yMin, yMax;
  
  public PanelGraph(AGEFrame graphFrame) {
    this.graphFrame = graphFrame;
  }
  
  private void drawAxis(Graphics g) {
    double yAxisxPercent = (0 - xMin) / (xMax - xMin);
    
    int originYCoord = valueToPixelY(0.0D);
    int originXCoord = (int) (yAxisxPercent * getWidth());
    
    Color c = g.getColor();
    g.setColor(Color.ORANGE);
    
    g.drawLine(0, originYCoord - 1, getWidth(), originYCoord - 1);
    g.drawLine(0, originYCoord, getWidth(), originYCoord);
    g.drawLine(0, originYCoord + 1, getWidth(), originYCoord + 1);
    
    g.drawLine(originXCoord - 1, valueToPixelY(yMin), originXCoord - 1, valueToPixelY(yMax));
    g.drawLine(originXCoord, valueToPixelY(yMin), originXCoord, valueToPixelY(yMax));
    g.drawLine(originXCoord + 1, valueToPixelY(yMin), originXCoord + 1, valueToPixelY(yMax));
    
    g.setColor(c);
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    drawAxis(g);
    
    g.setColor(COLORBACKROUND);
    g.fillRect(0, 0, getWidth(), getHeight());
    g.setColor(Color.BLACK);
    
    drawAxis(g);
    
    Equation[] equations = graphFrame.getEquation();
    if (equations == null || equations.length == 0) {
      return;
    }
    
    double lastY, thisY;
    double[] arrX = { 0 };
    Equation eq;
    for (int i = 0; i < equations.length; i++) {
      eq = equations[i];
      if (eq == null) {
        continue;// this happens when there is an empty text field
      }
      g.setColor(graphFrame.getColor(i));
      arrX[0] = pixelToGraphX(-1);
      lastY = eq.evaluate(arrX);
      for (int x = 0; x <= getWidth() + 1; x++) {
        try {
          arrX[0] = pixelToGraphX(x);
          thisY = eq.evaluate(arrX);
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          log.log(Level.WARNING, "caught exeption during graphing... halting proccess.", e);
          break;
        }
        g.drawLine(x - 1, valueToPixelY(lastY), x, valueToPixelY(thisY));
        lastY = thisY;
      }
    }
  }
  
  private double pixelToGraphX(int x) {
    return (xMax - xMin) / getWidth() * x + xMin;
  }
  
  public void setXMax(double xMax) {
    this.xMax = xMax;
  }
  
  public void setXMin(double xMin) {
    this.xMin = xMin;
  }
  
  public void setYMax(double yMax) {
    this.yMax = yMax;
  }
  
  public void setYMin(double yMin) {
    this.yMin = yMin;
  }
  
  private int valueToPixelY(double yValue) {
    return (int) (getHeight() / (yMin - yMax) * (yValue - yMax));
  }
}
