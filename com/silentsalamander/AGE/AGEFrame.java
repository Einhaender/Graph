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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import com.silentsalamander.helper.NumberTextField;
import com.silentsalamander.helper.PrettyLogger;
import com.silentsalamander.helper.equation.Equation;

public class AGEFrame extends JFrame {
  // TODO
  
  // scroll pane for when large number of equations
  
  // options. esp. radian or degree mode (Complete SettingsFrame w/ config? separate tab? mix?)
  
  // AboutFrame
  
  // tick marks, even when axis off screen (labeled?)
  
  // allow renaming equations. This will involve checking for duplicate names on name change and
  // equation add. Leave names unchanged, but disable them as global variables?
  // fix the current issue with duplicate names after removing out of order...
  
  
  protected static PrettyLogger log;
  private static final long     serialVersionUID = 2266856448062439731L;
  static {
    log = PrettyLogger.getPrimaryLogger();
  }
  
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JFrame f = new AGEFrame();
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
      }
    });
  }
  
  protected ArrayList<GraphableEquation> graphableEquations = new ArrayList<>();
  protected JLabel                       labelXMin, labelXMax, labelYMin, labelYMax;
  protected PanelGraph                   panelGraph;
  protected JSplitPane                   panelMaster;
  protected JTabbedPane                  panelTop;
  protected JPanel                       panelTopEquation;
  protected JPanel                       panelTopEquationTop;
  protected JPanel                       panelTopWindow;
  protected NumberTextField              textXMin, textXMax, textYMin, textYMax;
  
  public AGEFrame() {
    // #####CREATES THE TOP PANEL
    // equation panel
    panelTopEquationTop = new JPanel();
    JButton buttonAdd = new JButton("Add equation");
    buttonAdd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addEquation();
      }
    });
    JButton buttonGraph = new JButton("Graph!");
    buttonGraph.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        double xMin = textXMin.getValueDouble();
        double xMax = textXMax.getValueDouble();
        double yMin = textYMin.getValueDouble();
        double yMax = textYMax.getValueDouble();
        if ((xMin >= xMax) || (yMin >= yMax)) {
          JOptionPane.showMessageDialog(null,
            "The maximum window bound must be greater than the minimum", "Error",
            JOptionPane.ERROR_MESSAGE);
          return;
        }
        panelGraph.setXMin(xMin);
        panelGraph.setXMax(xMax);
        panelGraph.setYMin(yMin);
        panelGraph.setYMax(yMax);
        
        updateEquations();
        
        panelGraph.repaint();
      }
    });
    panelTopEquationTop.add(buttonAdd);
    panelTopEquationTop.add(buttonGraph);
    
    panelTopEquation = new JPanel();
    panelTopEquation.setLayout(new GridLayout(0, 1));
    panelTopEquation.add(panelTopEquationTop);
    
    addEquation();
    
    // window panel
    textXMin = new NumberTextField(-10);
    textXMax = new NumberTextField(10);
    textYMin = new NumberTextField(-10);
    textYMax = new NumberTextField(10);
    labelXMin = new JLabel("X-Min");
    labelXMin.setHorizontalAlignment(SwingConstants.CENTER);
    labelXMax = new JLabel("X-Max");
    labelXMax.setHorizontalAlignment(SwingConstants.CENTER);
    labelYMin = new JLabel("Y-Min");
    labelYMin.setHorizontalAlignment(SwingConstants.CENTER);
    labelYMax = new JLabel("Y-Max");
    labelYMax.setHorizontalAlignment(SwingConstants.CENTER);
    panelTopWindow = new JPanel();
    panelTopWindow.setLayout(new GridLayout(2, 5));
    panelTopWindow.add(labelXMin);
    panelTopWindow.add(labelXMax);
    panelTopWindow.add(labelYMin);
    panelTopWindow.add(labelYMax);
    panelTopWindow.add(textXMin);
    panelTopWindow.add(textXMax);
    panelTopWindow.add(textYMin);
    panelTopWindow.add(textYMax);
    
    
    panelTop = new JTabbedPane();
    panelTop.addTab("Equations", panelTopEquation);
    panelTop.addTab("Window", panelTopWindow);
    panelTop.setPreferredSize(new Dimension(500, 100));
    
    
    // #####CREATES THE GRAPH PANEL
    panelGraph = new PanelGraph(this);
    panelGraph.setXMin(textXMin.getValueDouble());
    panelGraph.setXMax(textXMax.getValueDouble());
    panelGraph.setYMin(textYMin.getValueDouble());
    panelGraph.setYMax(textYMax.getValueDouble());
    panelGraph.setPreferredSize(new Dimension(500, 500));
    
    
    // #####CREATES THE MASTER PANEL
    panelMaster = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    panelMaster.setTopComponent(panelTop);
    panelMaster.setBottomComponent(panelGraph);
    
    // #####duh
    setContentPane(panelMaster);
  }
  
  protected void addEquation() {
    GraphableEquation ge = new GraphableEquation();
    ge.addDeleteListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!(e.getSource() instanceof GraphableEquation))
          throw new Error("this should NEVER happen.");
        removeEquation(graphableEquations.indexOf(e.getSource()));
      }
    });
    graphableEquations.add(ge);
    panelTopEquation.add(ge.getPanel());
    if (panelMaster != null)
      panelMaster.setDividerLocation(-1);
  }
  
  public Equation[] getEquation() {
    if (graphableEquations == null || graphableEquations.size() == 0) {
      return null;
    }
    Equation[] arr = new Equation[graphableEquations.size()];
    for (int i = 0; i < graphableEquations.size(); i++) {
      arr[i] = graphableEquations.get(i).getEquation();
    }
    return arr;
  }
  
  protected void updateEquations() {
    for (int x = 0; x < graphableEquations.size(); x++) {
      try {
        graphableEquations.get(x).update();
      } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error: ", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
  
  protected void removeEquation(final int i) {
    graphableEquations.get(i).close();
    graphableEquations.remove(i);
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        panelTopEquation.remove(i + 1);// +1 because index 0 is the add/graph panel
        AGEFrame.this.pack();// TODO isn't there some other method for updating panels after
                             // removing components?
      }
    });
  }
  
  public Color getColor(int i) {
    Color old = graphableEquations.get(i).getColor();
    return new Color(old.getRGB());
  }
}
