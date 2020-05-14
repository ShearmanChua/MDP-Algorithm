package mdp.simulation.view;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class RunControlPanel extends JPanel {
    
    private static final Color _BG_COLOR = new Color(128, 128, 128);
    
    private JButton _explorationBtn;
    private JButton _ToWayPointBtn;
//    private JButton _combinedBtn;
    private JButton _wayPointToGoalBtn;
    private JButton _shortestPathWPBtn;
    private JButton _shortestPathBtn;
    private JButton _TestWayPointBtn;
    private JTextField _exePeriod;
    private JButton _ImagePathBtn;
    private JButton _SingleImageBtn;
    private JButton _explorationWImageBtn;
    
    public RunControlPanel() {
        // config
        this.setBackground(_BG_COLOR);
        
        // children
        _explorationBtn = new JButton("Exploration");
        _ToWayPointBtn = new JButton("ToWayPoint");
        _shortestPathWPBtn = new JButton("Shortest Path (WayPoint)");
        _shortestPathBtn = new JButton("Shortest Path");
        _TestWayPointBtn = new JButton("Test WayPoint");
        _ImagePathBtn =  new JButton("Image Path");
        _SingleImageBtn = new JButton(" Take Image Single  ");
        
        _explorationWImageBtn = new JButton("Explore(Image)");
//        _combinedBtn = new JButton("Combined");
        _exePeriod = new JTextField("100", 5);
        this.add(_explorationBtn);
        //this.add(_ToWayPointBtn);       
        this.add(_shortestPathWPBtn);
        this.add(_shortestPathBtn);
        this.add(_explorationWImageBtn);
      //this.add(_TestWayPointBtn);
//        this.add(_combinedBtn);
        this.add(_ImagePathBtn);
        this.add(_SingleImageBtn);
        this.add(_exePeriod);
        JLabel exePeriodLabel = new JLabel("s/action");
        exePeriodLabel.setForeground(Color.WHITE);
        this.add(exePeriodLabel);
    }
    
    public JButton getExplorationBtn() {
        return _explorationBtn;
    }

    public JButton getToWayPointBtn() {
        return _ToWayPointBtn;
    }    
    
    public JButton getShortestPathWPBtn() {
        return _shortestPathWPBtn;
    }
    public JButton getShortestPathBtn() {
        return _shortestPathBtn;
    }
    
    public JButton getTestWayPointBtn() {
    	return _TestWayPointBtn;
    }

//    public JButton getCombinedBtn() {
//        return _combinedBtn;
//    }
    
    public JButton getImagePathBtn() {
    	return _ImagePathBtn;
    }
    
    public JButton getSingleImageBtn() {
    	return _SingleImageBtn;
    }

    public JTextField getExePeriod() {
        return _exePeriod;
    }
    
    public JButton getExplorationWImageBtn() {
    	return _explorationWImageBtn;
    }
    
    
}
