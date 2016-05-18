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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.silentsalamander.helper.PrettyLogger;
import com.silentsalamander.helper.equation.Equation;

public class AGEFrame extends JFrame {
  // TODO
  
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
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
      }
    });
  }
  
  protected ArrayList<GraphableEquation> graphableEquations = new ArrayList<>();
  protected PanelGraph                   panelGraph;
  protected JSplitPane                   panelMaster;
  protected JTabbedPane                  panelTop;
  protected JPanel                       panelTopEquation;
  protected JPanel                       panelTopEquationTop;
  protected TabWindow                    tabWindow;
  // protected JPanel panelTopWindow;
  private JScrollPane                    panelTopEquationScrollable;
  
  public AGEFrame() {
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        super.componentResized(e);
        resetDividerLocation();
      }
    });
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
        double xMin = tabWindow.getXMin();
        double xMax = tabWindow.getXMax();
        double yMin = tabWindow.getYMin();
        double yMax = tabWindow.getYMax();
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
    tabWindow = new TabWindow();
    
    
    panelTopEquationScrollable = new JScrollPane(panelTopEquation);
    panelTopEquationScrollable.getPreferredSize();
    
    panelTop = new JTabbedPane();
    panelTop.addTab("Equations", panelTopEquationScrollable);
    panelTop.addTab("Window", tabWindow);
    panelTop.addChangeListener(new ChangeListener() {// listens for change of active tab
      @Override
      public void stateChanged(ChangeEvent e) {
        resetDividerLocation();
      }
    });
    
    panelGraph = new PanelGraph(this);
    panelGraph.setMinimumCorner(tabWindow.getMinimumCorner());
    panelGraph.setMaximumCorner(tabWindow.getMaximumCorner());
    panelGraph.setPreferredSize(new Dimension(500, 500));// TODO make this less arbitrary
    
    // #####CREATES THE MASTER PANEL
    panelMaster = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    panelMaster.setTopComponent(panelTop);
    panelMaster.setBottomComponent(panelGraph);
    
    // #####duh
    setContentPane(panelMaster);
    
    pack();
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
    resetDividerLocation();
    revalidate();
    repaint();
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
    panelTopEquation.remove(i + 1);// +1 because index 0 is the add/graph panel
    resetDividerLocation();
    revalidate();
    repaint();
  }
  
  public Color getColor(int i) {
    Color old = graphableEquations.get(i).getColor();
    return new Color(old.getRGB());
  }
  
  private boolean resetDividerLocation() {
    final double MAXPERPORTIONALSIZE = 0.3;// the top panel will never use more than this fraction
                                           // of panelMaster
    final int A_FEW_PIXELS;
    {// brackets are here to limit the scope of pixels
      int pixels = -1;// temp var with limited scope
      try {
        GraphableEquation ge = graphableEquations.get(0);
        pixels = (int) (ge.getPanel().getHeight() * 0.15);
      } catch (NullPointerException | IndexOutOfBoundsException e) {
        // do nothing
      } finally {
        if (pixels <= 0)// if caught exception, or if the panel had a size of zero
          pixels = 15; // reasonable value for most screens
      }
      A_FEW_PIXELS = pixels;
    }
    
    if (panelMaster == null)
      return false;
    int sizeMaster = panelMaster.getHeight();
    int sizeTopUnscrolled;
    
    Component selectedTab = panelTop.getComponent(panelTop.getSelectedIndex());
    sizeTopUnscrolled = panelTop.getHeight() - selectedTab.getHeight();// size of the tabs
    
    if (selectedTab instanceof JScrollPane) {
      log.finer("IS AN INSTANCE OF JSP");
      JScrollPane jsp = (JScrollPane) selectedTab;
      sizeTopUnscrolled += jsp.getComponent(0).getPreferredSize().height;
    } else {
      log.finer("IS NOT AN INSTANCE OF JSP");
      sizeTopUnscrolled += selectedTab.getPreferredSize().height;
    }
    
    
    if (sizeTopUnscrolled < MAXPERPORTIONALSIZE * sizeMaster)
      // sets the divider so that the top panel has all the space it needs; needs to add a few
      // pixels because reasons.
      panelMaster.setDividerLocation(sizeTopUnscrolled + A_FEW_PIXELS);
    else
      panelMaster.setDividerLocation(MAXPERPORTIONALSIZE);
    return true;
  }
}
