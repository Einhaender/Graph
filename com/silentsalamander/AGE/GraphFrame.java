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

import java.awt.BorderLayout;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.silentsalamander.helper.NumberTextField;
import com.silentsalamander.helper.PrettyLogger;
import com.silentsalamander.helper.equation.Equation;

public class GraphFrame extends JFrame {
  // TODO
  
  // fix the current issue with duplicate names after removing out of order...
  
  // allow renaming equations. This will involve checking for duplicate names on name change and
  // equation add. Will probably be forced to leave missing names if removing out of order.
  
  // allow one equation to call another
  
  // options. esp. radian or degree mode.
  
  // graph equations in different colors (maybe option to display a drop-down selection for each
  // equation?)
  
  protected static PrettyLogger log;
  private static final long     serialVersionUID = 2266856448062439731L;
  static {
    log = PrettyLogger.getPrimaryLogger();
  }
  
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JFrame f = new GraphFrame();
        f.pack();
        f.setVisible(true);
        f.setLocationRelativeTo(null);
      }
    });
  }
  
  protected ArrayList<Equation>   equationEquation;
  protected ArrayList<JLabel>     equationName;
  protected ArrayList<JTextField> equationText;
  protected JLabel                labelXMin, labelXMax, labelYMin, labelYMax;
  protected GraphPanel            panelGraph;
  protected JPanel                panelMaster;
  protected JTabbedPane           panelTop;
  protected JPanel                panelTopEquation;
  protected JPanel                panelTopWindow;
  protected NumberTextField       textXMin, textXMax, textYMin, textYMax;
  
  public GraphFrame() {
    // #####CREATES THE TOP PANEL
    // equation panel
    equationText = new ArrayList<>();
    equationName = new ArrayList<>();
    equationEquation = new ArrayList<>();
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
    panelGraph = new GraphPanel(this);
    panelGraph.setXMin(textXMin.getValueDouble());
    panelGraph.setXMax(textXMax.getValueDouble());
    panelGraph.setYMin(textYMin.getValueDouble());
    panelGraph.setYMax(textYMax.getValueDouble());
    panelGraph.setPreferredSize(new Dimension(500, 500));
    
    
    // #####CREATES THE MASTER PANEL
    panelMaster = new JPanel();
    panelMaster.setLayout(new BorderLayout());
    panelMaster.add(panelGraph, BorderLayout.CENTER);
    panelMaster.add(panelTop, BorderLayout.NORTH);
    
    // #####duh
    setContentPane(panelMaster);
  }
  
  protected void addEquation() {
    equationText.add(new JTextField(30));
    if (equationName.size() < equationText.size()) {
      equationName.add(new JLabel(((char) ('a' - 1 + equationText.size())) + "(x)"));
      equationName.get(equationName.size() - 1).setHorizontalAlignment(SwingConstants.CENTER);
    }
    // don't initialize Equation... It being null is how GraphPanel knows to not graph it
    equationEquation.add(null);
    rebuildTabEquation();
  }
  
  public Equation[] getEquation() {
    if (equationEquation == null || equationEquation.size() == 0) {
      return null;
    }
    Equation[] arr = new Equation[equationEquation.size()];
    return equationEquation.toArray(arr);
  }
  
  protected void rebuildEquations() {
    String[] arrX = { "x" };
    String text, eqName;
    
    
    // Only in unusual circumstances will this do stuff, and when it does it doesn't do much.
    // just here for good practice.
    equationEquation.ensureCapacity(equationText.size());
    
    
    while (equationEquation.size() < equationText.size()) {
      equationEquation.add(null);
    }
    
    for (int x = 0; x < equationText.size(); x++) {
      text = equationText.get(x).getText().trim();
      eqName = equationName.get(x).getText().trim();
      eqName = eqName.substring(0, eqName.length() - 3);
      if (equationEquation.get(x) == null) {
        if (text == null || text.isEmpty()) {
          continue;
        } else {
          try {
            equationEquation.set(x, new Equation(text, arrX, eqName));
          } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error: ",
              JOptionPane.ERROR_MESSAGE);
          }
          equationEquation.get(x).setGlobalFunc(true);
          continue;
        }
      }
      if (text == null || text.isEmpty()) {
        equationEquation.get(x).setGlobalFunc(false);
        equationEquation.get(x).setName(null);
        continue;
      }
      try {
        equationEquation.get(x).setEquation(text);
      } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error: ", JOptionPane.ERROR_MESSAGE);
      }
      equationEquation.get(x).setName(eqName);
      equationEquation.get(x).setGlobalFunc(true);
    }
  }
  
  /**
   * Called after adding or removing an equation to update the GUI
   */
  private void rebuildTabEquation() {
    // makes sure that panelTopEquation is ready
    if (panelTopEquation == null) {
      panelTopEquation = new JPanel();
      panelTopEquation.setLayout(new GridLayout(0, 3));// 0 will scale # of rows as needed
    } else {
      panelTopEquation.removeAll();
    }
    
    JButton tmpButton;
    // adds components for equations to panelTopEquation
    for (int x = 0; x < equationText.size(); x++) {
      panelTopEquation.add(equationName.get(x));
      panelTopEquation.add(equationText.get(x));
      final JTextField tmpVar = equationText.get(x);
      tmpButton = new JButton("Delete equation");
      tmpButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          removeEquation(tmpVar);
        }
      });
      panelTopEquation.add(tmpButton);
    }
    
    // adds the bottom row to panelTopEquation
    
    // TODO allow renaming & sorting equations
    // tmpButton = new JButton("Sort");
    // tmpButton.addActionListener(new ActionListener() {
    // @Override
    // public void actionPerformed(ActionEvent e) {
    // todo
    // }
    // });
    
    panelTopEquation.add(new JLabel());// srsly... Why can't I just do null.... :(
    tmpButton = new JButton("add equation");
    tmpButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addEquation();
      }
    });
    panelTopEquation.add(tmpButton);
    tmpButton = new JButton("Graph!");
    tmpButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        rebuildEquations();
        
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
        
        panelGraph.repaint();
      }
    });
    panelTopEquation.add(tmpButton);
    pack();
  }
  
  protected void removeEquation(int i) {
    if (equationEquation.get(i) != null) {
      equationEquation.get(i).setName(null);
      equationEquation.get(i).setGlobalFunc(false);
    }
    equationEquation.remove(i);
    equationText.remove(i);
    equationName.remove(i);
    rebuildTabEquation();
  }
  
  protected void removeEquation(JTextField equationTextField) {
    int index = equationText.indexOf(equationTextField);
    System.out.println("removing equation at index " + index);
    removeEquation(index);
  }
}
