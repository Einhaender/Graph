package com.silentsalamander.AGE;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.silentsalamander.helper.equation.Equation;

public class TabEquations extends JScrollPane {
  private static final long              serialVersionUID   = -7547335977750370332L;
  protected AGEFrame                     age;
  protected ArrayList<GraphableEquation> graphableEquations = new ArrayList<>();
  protected JPanel                       mainPanel;
  protected JPanel                       panelAddGraph;
  
  public TabEquations(AGEFrame age) {
    this.age = age;
    panelAddGraph = new JPanel();
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
        TabEquations.this.age.graph();
      }
    });
    panelAddGraph.add(buttonAdd);
    panelAddGraph.add(buttonGraph);
    
    mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayout(0, 1));
    mainPanel.add(panelAddGraph);
    
    addEquation();
    
    setViewportView(mainPanel);
  }
  
  protected void addEquation() {
    GraphableEquation ge = new GraphableEquation();
    ge.addDeleteListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!(e.getSource() instanceof GraphableEquation)) {
          throw new Error("this should NEVER happen.");
        }
        removeEquation(graphableEquations.indexOf(e.getSource()));
      }
    });
    graphableEquations.add(ge);
    mainPanel.add(ge.getPanel());
    age.resetDividerLocation();
    revalidate();
    repaint();
  }
  
  
  public Color getColor(int i) {
    Color old = graphableEquations.get(i).getColor();
    return new Color(old.getRGB());
  }
  
  public Equation[] getEquations() {
    if (graphableEquations == null || graphableEquations.size() == 0) {
      return null;
    }
    Equation[] arr = new Equation[graphableEquations.size()];
    for (int i = 0; i < graphableEquations.size(); i++) {
      arr[i] = graphableEquations.get(i).getEquation();
    }
    return arr;
  }
  
  public ArrayList<GraphableEquation> getGraphableEquations() {
    return graphableEquations;
  }
  
  protected void removeEquation(final int i) {
    graphableEquations.get(i).close();
    graphableEquations.remove(i);
    mainPanel.remove(i + 1);// +1 because index 0 is the add/graph panel
    age.resetDividerLocation();
    revalidate();
    repaint();
  }
}
