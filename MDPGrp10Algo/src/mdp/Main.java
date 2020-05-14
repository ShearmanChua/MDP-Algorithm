package mdp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

import mdp.communication.Translator;
import mdp.robot.Robot;
import mdp.communication.ITranslatable;
import mdp.simulation.GUI;
import mdp.simulation.event.EventHandler;
import mdp.simulation.event.GUIClickEvent;
import mdp.simulation.IGUIUpdatable;
import mdp.solver.exploration.ActionFormulator;
import mdp.solver.exploration.CalibrationType;

public class Main {

    private static IGUIUpdatable _gui;
    private static ITranslatable _rpi;
    
    //To test if format for waypoint is correct
    public static String wayPoint="waypoint(11,12)";

    private static boolean _isSimulating = false;

    public static void main(String[] args) throws IOException {
        // run simulation
    	
    	//"C:\Users\Nitro5\Desktop\models2\research\object_detection\Object_detection_image.py"
        System.out.println("Initiating GUI...");
        startGUI();
        
        /*
        String bat_dir = "C:\\ProgramData\\Anaconda3\\Scripts\\activate.bat";
    	String conda_cmd = "conda activate mdp";
    	String change_dir = "cd /d C:\\Users\\Nitro5\\Desktop\\models2\\research\\object_detection";
    	String run_python = "python Object_detection_image.py";
        Runtime rt = Runtime.getRuntime();
        try {
        	//rt.exec("cmd /c \""+bat_dir+"\" &" +conda_cmd + "&"+ change_dir + "&"+ run_python + "& start cmd.exe");
        	rt.exec("cmd.exe /c \""+bat_dir+"\" &" +conda_cmd + "&"+ change_dir); // + "&"+ run_python
        } catch(IOException e)
        {
        	e.printStackTrace();
        }
        */


        // testing (if needed)
//        connectToRpi();
    }

    public static boolean isSimulating() {
        return _isSimulating;
    }

    public static void isSimulating(boolean isSimulating) {
        _isSimulating = isSimulating;
    }

    public static IGUIUpdatable getGUI() {
        return _gui;
    }

    public static ITranslatable getRpi() {
        return _rpi;
    }

    public static void startGUI() {
        SwingUtilities.invokeLater(() -> {
            _gui = new GUI();
        });
    }

    public static void connectToRpi() throws IOException {
        _rpi = new Translator();
        _rpi.connect(() -> {
            try {
                _listenToRPi();
//                /*Test.run();*/
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private static void _listenToRPi() throws IOException {
    	
    	
    	String bat_dir = "C:\\ProgramData\\Anaconda3\\Scripts\\activate.bat";
    	String conda_cmd = "conda activate mdp";
    	String change_dir = "cd /d C:\\Users\\Nitro5\\Desktop\\models2\\research\\object_detection";
    	String run_python = "python Object_detection_image.py";
    	
        _rpi.listen(() -> {
            String inStr = _rpi.getInputBuffer();
            System.out.println("inStr = " + inStr);
            switch (inStr) {
                // Android start commands
                case "e":
                    System.out.println("Triggering Exploration");
                    _gui.trigger(GUIClickEvent.OnExploration);
                    
                    /*
                    Runtime rt = Runtime.getRuntime();
                    try {
                    	rt.exec("cmd /c \""+bat_dir+"\" &" +conda_cmd + "&"+ change_dir + "&"+ run_python + "& start cmd.exe");
                    } catch(IOException e)
                    {
                    	e.printStackTrace();
                    }*/
                    
                    break;
                case "z":
                    System.out.println("Triggering Calibration");
                    Main.getRpi().sendCalibrationCommand(CalibrationType.Front);
                    break;
                case "g":
                    System.out.println("Triggering Stop Button");
                    _gui.trigger(GUIClickEvent.OnStop);
                    //System.out.println("TESTTTTTTTTTTTTTTTTTTTT");
                    break;
                case "s":
                    System.out.println("Triggering ShortestPath");
                    _gui.trigger(GUIClickEvent.OnShortestPathWP);
                    break;
                //new case for startpos to waypoint    
                case "x":
                    System.out.println("Triggering to WayPoint");
                    _gui.trigger(GUIClickEvent.OnToWayPoint);
                    break;
                case "c":
                    System.out.println("Triggering Combined");
                    _gui.trigger(GUIClickEvent.OnCombined);
                    break;
                case "p":
                    System.out.println("Triggering Image Path");
                    _gui.trigger(GUIClickEvent.OnImagePath); 
                    break;
                case "D":
                    //Robot.actionCompletedCallBack();
                    ActionFormulator.calibrationCompletedCallBack();
                    break;
                case "a": //a for accuracy , calibration

                    break;
                case "u":
                	Robot.picTakenCallBack();
                	System.out.println("Image Taken By RPI(From RPI)");     
                	break;
                case "W":
                	ActionFormulator.calibrating = false;
                	System.out.println("C DONE");
                	break;
                case "I":
                	EventHandler.doneFinalCal = false;
                	System.out.println("image done");
                	break;
                case "K":
                	System.out.println("k Gaby");
                    _gui.trigger(GUIClickEvent.OnSingleImage);
                	break;
                default:
                	//From arduino
                    if (inStr.length() == 6) {
                        System.out.println("Analyzing sensing information");
                        ActionFormulator.sensingDataCallback(inStr);
                        Robot.sensingDataCallback(inStr);
                        Robot.actionCompletedCallBack();
                    } 
                    //Message from andriod way	point 'waypoint(x,y)'
                    else if(inStr.charAt(0) == 'w' && inStr.charAt(3) == 'p')
                    {   
                    	wayPoint = inStr;
                    	System.out.println("Receiving and updating waypoint coordinates");
                    	_gui.trigger(GUIClickEvent.OnSetWayPoint);
                    }
                           
                    else {
                        System.out.println(inStr);
                    	System.out.println("Unrecognized input");
                    }
                    break;
            }
        });
    }

}
