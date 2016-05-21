package com.silentsalamander.AGE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JScrollPane;

import com.silentsalamander.helper.FileManagement;
import com.silentsalamander.helper.config.ConfigPanel;
import com.silentsalamander.helper.config.ConfigValueBase;
import com.silentsalamander.helper.config.ConfigValueComboString;
import com.silentsalamander.helper.config.InvalidConfigFileException;

public class TabConfig extends JScrollPane {
  private static final long serialVersionUID = -293097432549174756L;
  private static String     fileLoc          = FileManagement.getDirectoryUserData()
    + File.separatorChar + AGEFrame.class.getPackage().getName();
  
  protected ConfigPanel     config;
  protected boolean         useRadians       = true;
  
  
  public TabConfig() throws IOException, InvalidConfigFileException {
    ArrayList<ConfigValueBase> configItems = new ArrayList<>();
    configItems.add(new ConfigValueComboString() {
      private final String[] options = { "Radians", "Degrees" };
      
      @Override
      public int getDefaultIndex() {
        return useRadians ? 0 : 1;// 0 by default
      }
      
      @Override
      public String getName() {
        return "Angle Measurement";
      }
      
      @Override
      public String[] getOptions() {
        return options;
      }
      
      @Override
      protected void saveIndex(int i) {
        useRadians = (i == 0);
      }
    });
    
    config = new ConfigPanel(configItems, true, fileLoc, null);
    
    setViewportView(config);
  }
  
  public boolean useRadians() {
    return useRadians;
  }
}
