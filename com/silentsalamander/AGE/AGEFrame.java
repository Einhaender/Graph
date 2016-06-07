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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.silentsalamander.helper.AboutPanel;
import com.silentsalamander.helper.AboutPanel.LicenseInfo;
import com.silentsalamander.helper.AboutPanel.LicenseType;
import com.silentsalamander.helper.PrettyLogger;
import com.silentsalamander.helper.config.InvalidConfigFileException;
import com.silentsalamander.helper.equation.Equation;

public class AGEFrame extends JFrame {
  // TODO
  
  // AboutFrame
  
  // tick marks, even when axis off screen (labeled?)
  
  // config:
  // log scale axis?
  // x/y function, polar, parametric?
  // tabWindow + tick marks => config?
  
  // improve equation name auto-generation
  
  // setting for divider control: automatic, default, manual
  
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
  
  protected PanelGraph   panelGraph;
  protected JSplitPane   panelMaster;
  protected JTabbedPane  panelTop;
  protected TabConfig    tabConfig;
  protected TabEquations tabEquation;
  protected TabWindow    tabWindow;
  protected JScrollPane  tabAbout;
  protected boolean      dividerSetManually = false;
  protected boolean      dividerBeingSet    = false;
  
  public AGEFrame() {
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        super.componentResized(e);
        resetDividerLocation();
      }
    });
    
    tabEquation = new TabEquations(this);
    tabWindow = new TabWindow();
    try {
      tabConfig = new TabConfig();
    } catch (IOException | InvalidConfigFileException e1) {
      log.printStackTrace(e1);
      JOptionPane.showMessageDialog(this, "Could not load settings. Reverting to defaults.",
        "WARNING:", JOptionPane.WARNING_MESSAGE);
    }
    
    LicenseInfo info = new LicenseInfo();
    info.setApplicationName("AGE Graphs Equations (AGE)");
    info.setCopyrightContact("fire.4@cox.net");
    info.setCopyrightDates("2016");
    info.setCopyrightowner("Ivan Johnson");// If someone else were to contribute to this open
                                           // source project this would become:
                                           // "Ivan Johnson, et. al."
    info
      .setOneLineDescription("AGE Graphs Equations (AGE) is a java program that graphs equations");
    
    String aboutText = "AGE is a java program for graphing equations. It is free software, so everyone has access to the source code and can make their own versions of the program and distribute them as they like, so long as they distribute their own version as free software as well.";
    tabAbout = new JScrollPane(new AboutPanel(LicenseType.GPLv3, info, aboutText));
    tabAbout.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    
    panelTop = new JTabbedPane();
    panelTop.addTab("Equations", tabEquation);
    panelTop.addTab("Window", tabWindow);
    panelTop.addTab("Settings", tabConfig);
    panelTop.addTab("About", tabAbout);
    panelTop.addChangeListener(new ChangeListener() {// listens for change of active tab
      @Override
      public void stateChanged(ChangeEvent e) {
        dividerSetManually = false;// reset to automatic positioning of the divider on tab change
        resetDividerLocation();
      }
    });
    
    panelGraph = new PanelGraph(this);
    panelGraph.setMinimumCorner(tabWindow.getMinimumCorner());
    panelGraph.setMaximumCorner(tabWindow.getMaximumCorner());
    panelGraph.setPreferredSize(new Dimension(500, 500));// TODO make this less arbitrary
    
    panelMaster = new JSplitPane(JSplitPane.VERTICAL_SPLIT) {
      private static final long serialVersionUID = 3443044750030817169L;
      
      @Override
      public void setDividerLocation(double proportionalLocation) {
        // AGEFrame.this.dividerBeingSet//TODO
        super.setDividerLocation(proportionalLocation);
      }
    };
    panelMaster.setTopComponent(panelTop);
    panelMaster.setBottomComponent(panelGraph);
    panelMaster.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
      new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent pce) {
          dividerSetManually = dividerSetManually || !dividerBeingSet;
        }
      });
    
    setContentPane(panelMaster);
    
    pack();
  }
  
  public Color getColor(int i) {
    return tabEquation.getColor(i);
  }
  
  public Equation[] getEquation() {
    return tabEquation.getEquations();
  }
  
  public void graph() {
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
    
    ArrayList<GraphableEquation> graphableEquations = tabEquation.getGraphableEquations();
    for (int x = 0; x < graphableEquations.size(); x++) {
      try {
        graphableEquations.get(x).update();
      } catch (IllegalArgumentException e) {
        log.printStackTrace(e);
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error: ", JOptionPane.ERROR_MESSAGE);
        break;
      }
    }
    
    panelGraph.repaint();
  }
  
  boolean resetDividerLocation() {// package visibility so it can be accessed by TabEquations
    if (panelMaster == null || dividerSetManually) {
      return false;
    }
    
    final double MAXPERPORTIONALSIZE = 0.5;// the top panel will never use more than this fraction
                                           // of panelMaster's size
    final double DEFAULTPERPORTIONALSIZE = 0.333;// if the top panel can't fit in the MAXP.S.; it
                                                 // will gain a scroll bar and use this much space
    final int A_FEW_PIXELS;
    {// brackets are here to limit the scope of pixels
      int pixels = -1;// temp var with limited scope
      try {
        GraphableEquation ge = tabEquation.getGraphableEquations().get(0);
        pixels = (int) (ge.getPanel().getHeight() * 0.15);
      } catch (NullPointerException | IndexOutOfBoundsException e) {
        // do nothing
      } finally {
        if (pixels <= 0) {
          pixels = 15; // reasonable value for most screens
        }
      }
      A_FEW_PIXELS = pixels;
    }
    
    
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
    
    if (sizeTopUnscrolled < MAXPERPORTIONALSIZE * sizeMaster) {
      // sets the divider so that the top panel has all the space it needs; needs to add a few
      // pixels because reasons.
      setDivLoc(sizeTopUnscrolled + A_FEW_PIXELS);
    } else {
      setDivLoc(DEFAULTPERPORTIONALSIZE);
    }
    return true;
  }
  
  private void setDivLoc(double d) {
    dividerBeingSet = true;
    panelMaster.setDividerLocation(d);
    dividerBeingSet = false;
  }
  
  private void setDivLoc(int i) {
    dividerBeingSet = true;
    panelMaster.setDividerLocation(i);
    dividerBeingSet = false;
  }
  
  public boolean useRadians() {
    return tabConfig.useRadians();
  }
}
