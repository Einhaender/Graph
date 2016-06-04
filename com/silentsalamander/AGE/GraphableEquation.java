package com.silentsalamander.AGE;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.silentsalamander.helper.Colors;
import com.silentsalamander.helper.PrettyLogger;
import com.silentsalamander.helper.equation.Equation;

public class GraphableEquation {
  private static final String[]                EQUATIONARGUMENTS  = { "x" };
  private static ArrayList<GraphableEquation>  allGraphables      = new ArrayList<>();
  private static int                           numGenerated       = 0;
  private static ArrayList<Color>              prettyColors       = Colors.getPrettyColors();
  private static Comparator<GraphableEquation> jtextComparator    = new Comparator<GraphableEquation>() {
                                                                    //@formatter:off
                                                                    @Override
                                                                    public int compare(GraphableEquation o1, GraphableEquation o2) {
                                                                      return o1.textName.getText().compareTo(o2.textName.getText());
                                                                    }
                                                                    //@formatter:on
                                                                  };
  private static Color                         jtextDisabledColor = Color.YELLOW;
  private static final Color                   jtextDefaultColor  = (new JTextField())
    .getBackground();
  private static PrettyLogger                  log;
  
  static {
    log = PrettyLogger.getPrimaryLogger();
    for (int x = 0; x < prettyColors.size(); x++) {
      if (prettyColors.get(x).equals(PanelGraph.COLORBACKROUND)) {
        prettyColors.remove(x);
        x--;
      }
    }
  }
  
  private static void onNameChange() {
    allGraphables.sort(jtextComparator);
    if (allGraphables.size() == 0)
      return;
    boolean[] duplicateName = new boolean[allGraphables.size()];
    String lastText = allGraphables.get(0).textName.getText();
    for (int i = 1; i < allGraphables.size(); i++) {
      if (allGraphables.get(i).textName.getText().equals(lastText)) {
        duplicateName[i] = true;
        duplicateName[i - 1] = true;
      }
      lastText = allGraphables.get(i).textName.getText();
    }
    for (int i = 0; i < duplicateName.length; i++) {
      if (duplicateName[i]) {
        allGraphables.get(i).textName.setBackground(jtextDisabledColor);
        if (allGraphables.get(i).equation != null)
          allGraphables.get(i).equation.setGlobalFunc(false);
      } else {
        allGraphables.get(i).textName.setBackground(jtextDefaultColor);
        if (allGraphables.get(i).equation != null)
          allGraphables.get(i).equation.setGlobalFunc(true);
      }
    }
  }
  
  private JButton                   buttonColor;
  private JButton                   buttonDelete;
  private JTextField                textName;
  private JColorChooser             colorChooser;
  private ArrayList<ActionListener> deleteListeners;
  private Equation                  equation;
  private JPanel                    primaryPanel;
  private JTextField                textFieldEquation;
  private Component                 labelEquality = new JLabel("(x) = ");
  
  public GraphableEquation() {
    equation = null;// deliberately not initiated so that GraphPanel doesn't try to graph it.
    allGraphables.add(this);
    
    textName = new JTextField(String.valueOf((char) ('a' + numGenerated)));
    textName.setHorizontalAlignment(SwingConstants.CENTER);
    textName.getDocument().addDocumentListener(new DocumentListener() {
      private void onNameChange() {
        if (GraphableEquation.this.equation != null)
          try {
            GraphableEquation.this.equation.setName(textName.getText());
          } catch (IllegalArgumentException e) {
            log.warning("User entered invalid equation name \"" + textName.getText() + "\"");
            JOptionPane.showMessageDialog(primaryPanel, e.getMessage(), "WARNING:",
              JOptionPane.WARNING_MESSAGE);
          }
        GraphableEquation.onNameChange();
      }
      
      @Override
      public void removeUpdate(DocumentEvent e) {
        this.onNameChange();
      }
      
      @Override
      public void insertUpdate(DocumentEvent e) {
        this.onNameChange();
      }
      
      @Override
      public void changedUpdate(DocumentEvent e) {
        this.onNameChange();
      }
    });
    
    textFieldEquation = new JTextField(10);
    textFieldEquation.setText("");
    
    int randomColorIndex = (int) (Math.random() * prettyColors.size());
    colorChooser = new JColorChooser(prettyColors.get(randomColorIndex));
    colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        if (buttonColor != null) {
          buttonColor.setBackground(colorChooser.getColor());
        } else {
          throw new Error("An equation's color has changed, but the color button is null."
            + "This should NEVER happen.");
        }
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
    primaryPanel.add(textName);
    primaryPanel.add(labelEquality);
    primaryPanel.add(textFieldEquation);
    primaryPanel.add(buttonColor);
    primaryPanel.add(buttonDelete);
    
    numGenerated++;
  }
  
  public void addDeleteListener(ActionListener actionListener) {
    if (deleteListeners == null) {
      deleteListeners = new ArrayList<>();
    }
    deleteListeners.add(actionListener);
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
  
  public Color getColor() {
    return colorChooser.getColor();
  }
  
  public Equation getEquation() {
    return equation;
  }
  
  public Component getPanel() {
    return primaryPanel;
  }
  
  public void update() {
    String newText = textFieldEquation.getText();
    if (newText == null || newText.trim().isEmpty()) {
      close();
      return;
    }
    if (equation == null) {
      equation = new Equation(newText, EQUATIONARGUMENTS, textName.getText());
      // equation.setGlobalFunc(true);//can't just set to global func, because another equation
      // might have the same name. Could write code to do this specifically, but calling
      // onNameChange will have the same effect.
      onNameChange();
    } else {
      equation.setEquation(newText);
    }
  }
}
