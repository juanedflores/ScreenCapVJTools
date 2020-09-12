package com.juaned;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class GUI extends JFrame {
  private static final long serialVersionUID = 1L;
  /// #cleaned [GUI Elements]
  /*
   * [GUI]
   */
  public static GUI mainGUI;
  public static JLayeredPane gridCenterPanel;
  public static JLayeredPane centerPanel;
  public static Menu menu;
  public static Notification notification;
  public static Gridpage gridPage;
  public static JPanel editPanel;
  public static JFrame processingFrame;
  public static ProcessingSketch myProcessingSketch;
  public static JPanel screenShotPanel;
  public static ScreenCaptureDisplay screenCapDisplay;
  public static ScreenCapInfo screenCapInfo;
  public static JPanel sketchMapperPanel;
  public static SketchMapper sketchMapper;
  // #endregion

  public GUI() {
    setTitle("Luminaria");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setResizable(false);
    // Handles Look and Feel of GUI elements.
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }
    /*
     * [Set up Processing Sketch]
     */
    /// #cleaned [Processing]
    myProcessingSketch = new ProcessingSketch(); /// instantiate the Processing Sketch object.
    myProcessingSketch.init();
    // Processing JFrame
    processingFrame = new JFrame("Processing");
    processingFrame.setResizable(false); /// there is no need to have window resizable.
    processingFrame.setFocusableWindowState(false); /// prevent this window to be focusable window.
    processingFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); /// closing the Processing window will do
    /// nothing.
    processingFrame.add(GUI.myProcessingSketch); /// add myProcessingSketch to the JFrame called processingFrame.
    // #endregion
    /*
     * [Set up Button Panel]
     */
    /// #cleaned [Button Panel]
    gridPage = new Gridpage();
    gridPage.setBounds(600, 0, 600, 600);
    // #endregion
    /*
     * [Set up Screenshot Panel]
     */
    /// #cleaned [Screenshot Panel]
    screenShotPanel = new JPanel();
    screenShotPanel.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.WHITE));
    screenShotPanel.setLayout(new BorderLayout());
    screenShotPanel.setOpaque(true);
    screenShotPanel.setBounds(0, 0, 1200, 600);
    screenShotPanel.setVisible(false);
    screenShotPanel.setBackground(Color.ORANGE);
    // the captured screen and info panel
    screenCapDisplay = new ScreenCaptureDisplay();
    screenCapInfo = new ScreenCapInfo();
    // add to panel
    screenShotPanel.add(screenCapDisplay, BorderLayout.CENTER);
    screenShotPanel.add(screenCapInfo, BorderLayout.EAST);
    // #endregion
    /*
     * [Set up Menu]
     */
    /// #cleaned [Menu]
    menu = new Menu();
    // #endregion
    /*
     * [Set notifications]
     */
    /// #cleaned [Notifications]
    notification = new Notification();
    menu.userInput.addActionListener(notification.notificationsInput);
    notification.setMessage("Hello world", new Color(255, 255, 255), true);
    // #endregion
    /*
     * [Set up Edit Panel]
     */
    /// #cleaned [Edit Panel]
    editPanel = new JPanel();
    editPanel.setBackground(Color.BLACK);
    editPanel.setBounds(0, 0, 600, 600);
    editPanel.setLayout(null);
    editPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.WHITE));
    // #endregion
    /*
     * [Sketch Mapper]
     */
    /// #cleaned [Sketch Mapper]
    sketchMapperPanel = new JPanel();
    sketchMapperPanel.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.WHITE));
    sketchMapperPanel.setLayout(null);
    sketchMapperPanel.setOpaque(true);
    sketchMapperPanel.setBounds(0, 0, 1200, 600);
    sketchMapperPanel.setVisible(false);
    sketchMapperPanel.setBackground(Color.ORANGE);
    // add the sketch mapper
    sketchMapper = new SketchMapper();
    sketchMapperPanel.add(sketchMapper, BorderLayout.CENTER);
    sketchMapperPanel.add(GUI.sketchMapper.sketchMapperInfoPanel, BorderLayout.EAST);
    // #endregion
    /*
     * [Set up centerPanel and add objects]
     */
    /// #cleaned [Center Panel]
    centerPanel = new JLayeredPane();
    centerPanel.setBackground(Color.BLACK);
    centerPanel.setPreferredSize(new Dimension(1200, 600));
    // add the editPanel.
    centerPanel.add(editPanel);
    centerPanel.setLayer(editPanel, 1);
    // add the menu.
    centerPanel.add(menu.menuScrollPane);
    centerPanel.setLayer(menu.menuScrollPane, 2);
    // add the button panel.
    centerPanel.add(gridPage);
    centerPanel.setLayer(gridPage, 3);
    // add the screenshot panel.
    centerPanel.add(screenShotPanel);
    centerPanel.setLayer(screenShotPanel, 4);
    // add the sketchmapper panel.
    centerPanel.add(sketchMapperPanel);
    centerPanel.setLayer(sketchMapperPanel, 5);
    // add the notification panel.
    centerPanel.add(notification);
    centerPanel.setLayer(notification, 10);
    // #endregion
    /*
     * [Add to Window]
     */
    add(centerPanel, BorderLayout.CENTER);
    add(menu.topPanel, BorderLayout.NORTH);
    add(menu.bottomPanel, BorderLayout.SOUTH);
  }

  public static void main(String[] args) {
    // create a GUI and pack all elements in it.
    mainGUI = new GUI();
    mainGUI.pack();
    // position the window in the correct area.
    centerWindow(mainGUI);
    // pack all elements in the processingFrame.
    processingFrame.pack();
    // make windows visible.
    mainGUI.setVisible(true);
    processingFrame.setVisible(true);
    // focus on userInput text field.
    menu.userInput.requestFocusInWindow();
  }

  public static void centerWindow(Window frame) { /// [DONE]
    /*
     * method is to properly start the program that is most convenient to the
     * situation. if it is started with no connected monitors, it will start it and
     * position it in a convenient side of the screen. if it is started with three
     * monitors, it means I am editing code in my home studio, so it will position
     * the program in the second monitor screen.
     */
    // get the info of the currently connected monitors.
    GraphicsDevice myScreen;
    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] allScreens = env.getScreenDevices();
    int x;
    int y;
    // if there are three monitors, the GUI will start in second monitor. [For
    // coding while I am home]
    if (allScreens.length > 1) {
      myScreen = allScreens[1];
      x = frame.getWidth();
      y = frame.getHeight();
      frame.setLocation(myScreen.getDefaultConfiguration().getBounds().width * 3 / 2 - x,
          myScreen.getDefaultConfiguration().getBounds().height * 4 / 5 - y);
    }
    // if I am not connected to any other monitors, the GUI will start on right side
    // of default monitor. [Because the left side of my computer is broken]
    else {
      myScreen = allScreens[0];
      Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
      x = (int) ((dimension.getWidth() - frame.getWidth()) - 50);
      y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
      frame.setLocation(x, y);
    }
  }

  public static void makeProcessingFullScreen() { /// [DONE]
    /*
     * sets the processing window JFrame to fullscreen and creates a new mainCanvas
     * in the Processing sketch that corresponds to the fullscreen dimensions.
     */
    // detect if there is more than one monitor being used.
    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment(); /// returns the local Graphics
    /// Environment
    GraphicsDevice[] allScreens = env.getScreenDevices();
    // check to see which monitor the processing JFrame is in.
    GraphicsConfiguration config = processingFrame.getGraphicsConfiguration();
    GraphicsDevice currentDevice = config.getDevice();
    // this loop is just to double check if screen index is in the right order.
    int myScreenIndex = -1;
    for (int i = 0; i < allScreens.length; i++) {
      if (allScreens[i].equals(currentDevice)) {
        myScreenIndex = i;
        break;
      }
    }
    // determine what monitor the window is in and get the dimensions of it.
    currentDevice = allScreens[myScreenIndex];
    int fw = currentDevice.getDefaultConfiguration().getBounds().width;
    int fh = currentDevice.getDefaultConfiguration().getBounds().height;
    gridCenterPanel.setBounds(0, 0, fw, fh);
    // change the mainCanvas of the Processing Sketch before we change the size of
    // the JFrame.
    GUI.myProcessingSketch.changeCanvasSize(fw / 2, fh);
    GUI.myProcessingSketch.frameW = fw / 2;
    GUI.myProcessingSketch.frameH = fh;
    GUI.myProcessingSketch.foregroundCanvas.beginDraw();
    GUI.myProcessingSketch.foregroundCanvas.background(0, 0);
    GUI.myProcessingSketch.foregroundCanvas.endDraw();
    GUI.myProcessingSketch.buttonCanvas = GUI.myProcessingSketch.createGraphics(fw / 2, fh);
    GUI.myProcessingSketch.buttonCanvas.beginDraw();
    GUI.myProcessingSketch.buttonCanvas.background(0);
    GUI.myProcessingSketch.buttonCanvas.endDraw();
    // @ put the buttongrid on the right side of the screen.
    gridPage.setBounds(0, 0, fw / 2, fh);
    gridPage.setBackground(Color.BLACK);
    gridCenterPanel.add(myProcessingSketch);
    gridCenterPanel.add(gridPage);
    // @ add to center panel.
    processingFrame.add(gridCenterPanel);
    // if there is more than one monitor, program will maximize the window in the
    // current display that it is in.
    if (allScreens.length > 1) {
      processingFrame.dispose();
      processingFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
      processingFrame.setUndecorated(true);
      processingFrame.pack();
      processingFrame.setVisible(true);
    }
    // else, display notice that there needs to be a second monitor to go
    // fullscreen.
    else {
      notification.setMessage("Connect a second monitor", new Color(255, 100, 140), true);
    }
    // request focus to be on the userInput JTextField.
    menu.userInput.requestFocus();
  }

  public static void makeProcessingNormalScreen() { /// [DONE]
    /*
     * sets the window size of the Processing window to the default size of 1080 x
     * 742.
     */
    // change the mainCanvas size in Processing to the new dimensions.
    GUI.myProcessingSketch.changeCanvasSize(1080, 742);
    // make the Processing JFrame the default window size.
    processingFrame.dispose(); /// causes the JFrame window to be destroyed and cleaned up by the operating
    /// system.
    processingFrame.setExtendedState(JFrame.NORMAL); /// will set the JFrame window to NORMAL.
    processingFrame.setUndecorated(false); /// will set the JFrame window to have a bar on top.
    processingFrame.pack(); /// pack all the elements to automatically fit inside the window.
    processingFrame.setVisible(true); /// make the new window visible.
    // request focus to be on the userInput JTextField.
    menu.userInput.requestFocus();
  }
}
