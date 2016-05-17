package com.silentsalamander.AGE;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.silentsalamander.helper.Colors;
import com.silentsalamander.helper.equation.Equation;

public class GraphableEquation {
  private static final String[]     EQUATIONARGUMENTS = { "x" };
  private static int                numGenerated      = 0;
  private static ArrayList<Color>   prettyColors      = Colors.getPrettyColors();
  private Equation                  equation;
  private JPanel                    primaryPanel;
  private JLabel                    labelName;
  private JTextField                textFieldEquation;
  private JButton                   buttonColor;
  private JButton                   buttonDelete;
  private JColorChooser             colorChooser;
  private ArrayList<ActionListener> deleteListeners;
  
  public GraphableEquation() {
    equation = null;// deliberately not initiated so that GraphPanel doesn't try to graph it.
    
    labelName = new JLabel(((char) ('a' + numGenerated)) + "(x)");
    labelName.setHorizontalAlignment(SwingConstants.CENTER);
    
    textFieldEquation = new JTextField(10);
    textFieldEquation.setText("");
    
    colorChooser = new JColorChooser(prettyColors.get((int) (Math.random() * prettyColors.size())));
    colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        if (buttonColor != null)
          buttonColor.setBackground(colorChooser.getColor());
        else
          throw new Error("An equation's color has changed, but the color button is null."
            + "This should NEVER happen.");
      }
    });
    
    buttonDelete = new JButton("Delete Equation");
    buttonDelete.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        for (ActionListener actionListener : deleteListeners) {
          ActionEvent ae = new ActionEvent(GraphableEquation.this, -1, null);
          actionListener.actionPerformed(ae);
        }
      }
    });
    
    buttonColor = new JButton("Color");
    buttonColor.setBackground(colorChooser.getColor());
    buttonColor.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFrame f = new JFrame("Color chooser");
        f.setContentPane(colorChooser);
        f.pack();
        f.setLocationRelativeTo(null);// TODO is there an easy way to get this centered...?
        f.setVisible(true);
      }
    });
    
    primaryPanel = new JPanel();
    primaryPanel.setLayout(new BoxLayout(primaryPanel, BoxLayout.LINE_AXIS));
    primaryPanel.add(labelName);
    primaryPanel.add(textFieldEquation);
    primaryPanel.add(buttonColor);
    primaryPanel.add(buttonDelete);
    
    numGenerated++;
  }
  
  public Equation getEquation() {
    return equation;
  }
  
  public Color getColor() {
    return colorChooser.getColor();
  }
  
  /**
   * Preps the object to be "deleted"
   */
  public void close() {
    if (equation != null) {
      equation.setName(null);
      equation.setGlobalFunc(false);
      equation = null;
    }
  }
  
  public void update() {
    String newText = textFieldEquation.getText();
    if (newText == null || newText.trim().isEmpty()) {
      close();
      return;
    }
    if (equation == null) {
      String fullName = labelName.getText().trim();// fullName eg. "abc(x)"
      equation = new Equation(newText, EQUATIONARGUMENTS,
        fullName.substring(0, fullName.length() - 3));// actual name eg. "abc"
      equation.setGlobalFunc(true);
    } else
      equation.setEquation(newText);
  }
  
  public Component getPanel() {
    return primaryPanel;
  }
  
  public void addDeleteListener(ActionListener actionListener) {
    if (deleteListeners == null)
      deleteListeners = new ArrayList<>();
    deleteListeners.add(actionListener);
  }
}
