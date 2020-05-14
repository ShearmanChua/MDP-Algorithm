package mdp.robot;

import java.io.IOException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ArrayList;

import mdp.common.Direction;
import mdp.common.Vector2;
import mdp.communication.Translator;
import mdp.solver.exploration.ActionFormulator;
import mdp.solver.exploration.ExplorationSolver;
import mdp.solver.exploration.MapViewer;
import mdp.solver.exploration.SensingData;
import mdp.Main;
import mdp.map.Map;
import mdp.map.WPObstacleState;
import mdp.simulation.event.EventHandler;

public class Robot {

    private Vector2 _position;
    private Direction _orientation;

    private static volatile int calibrationCounter = 0;
    private static volatile boolean actionCompleted = false;
    private static volatile boolean picTaken = false;
    private static volatile String sensingDataFromRPI;
    private static volatile boolean robotVisitedBefore = false;
    
    private static LinkedList<RobotAction> bufferedActions = new LinkedList<>();
    private ArrayList<String> imageFileNames = new ArrayList<String>(); 
    private MapViewer mapViewer;
    private ActionFormulator actionFormulator;
    private long executionStartTime = System.currentTimeMillis();
    private long executionEndTime ;
    public Robot() {
        this(new Vector2(1, 1), Direction.Right); //right is up in this case lmao
    }

    public Robot(Vector2 position, Direction direction) {
        _position = position;
        _orientation = direction;
    }
     

    public Robot(Vector2 position, Direction direction, MapViewer mv , ActionFormulator ac) {
        _position = position;
        _orientation = direction;
        mapViewer = mv;
        actionFormulator = ac;
    }

    public boolean checkIfRobotVisitedBefore(){
        
        return robotVisitedBefore;
    }
    public Vector2 position() {
        return _position;
    }

    public Direction orientation() {
        return _orientation;
    }

    public void position(Vector2 position) {
        _position = position;
    }

    public void orientation(Direction direction) {
        _orientation = direction;
    }

    public void execute(RobotAction action) {
        Vector2 dirVector = _orientation.toVector2();
        switch (action) {
            case MoveForward:
                // RPI call

                _position.add(dirVector);
                break;
            case MoveBackward:
                // RPI call
                dirVector.multiply(-1);

                _position.add(dirVector);
                break;
            case RotateLeft:
                // RPI call
                _orientation = _orientation.getLeft();
                break;
            case RotateRight:
                // RPI call
                _orientation = _orientation.getRight();
                break;
            case TakeImage:
                // RPI call
                System.out.println("Taking Image in GUI");
                break;
                
            
        }
    }

    public boolean bufferAction(RobotAction action) {
        return bufferedActions.add(action);
    }
    
    
    public int checkBufferActionSize() {
        return bufferedActions.size();
    }
    
    public void cleanBufferedActions(){
    		bufferedActions.clear();
    		
    }
    
    public static void actionCompletedCallBack() {
        actionCompleted = true;

    }
    
    
    public static void picTakenCallBack() {
       picTaken = true;

    }
    
    
    public void setActionCompleteFalse() {
        actionCompleted = false;

    }
    
    public void executeBufferActions(int sleepPeriod) throws IOException {
    	
    	
    	Map this_map = mapViewer.getSubjectiveMap();
    	Vector2 robot_pos = this._position;
    	Direction cur_direction = this._orientation;
    	Vector2 obstacle_pos = new Vector2(robot_pos.i(),robot_pos.j()); //default is center
    	Vector2 obstacle_pos_f = new Vector2(robot_pos.i(),robot_pos.j()+1); //default is center
    	Vector2 obstacle_pos_b = new Vector2(robot_pos.i(),robot_pos.j()-1); //default is center
    	String camera_pos_from_obs = "CameraPosFromObs";
    	String image_file_name = "thisfilename";
    	Vector2 obstacle_wall = new Vector2(robot_pos.i(),robot_pos.j()+2); // default is checking north wall
    	
    	/*
    	Vector2 SpecialObs = new Vector2(0,8);
    	Vector2 SpecialDesiredRobotPos = new Vector2(1,7);
    	String SpecialCameraPosFromObs = "South";
    	int specialflag = 1;*/

    	//System.out.println(cur_direction);
    	
			
    	if(EventHandler.isShortestPath() == false) //if exploration
    	{
    		if(cur_direction == Direction.Right) //facing North
        	{
        		obstacle_pos = new Vector2(robot_pos.i()-2,robot_pos.j()); // Robot on the right of obs
        		obstacle_pos_f = new Vector2(robot_pos.i()-2,robot_pos.j()+1);
        		obstacle_pos_b = new Vector2(robot_pos.i()-2,robot_pos.j()-1);
        		camera_pos_from_obs = "East";

        	}
        	
        	if(cur_direction == Direction.Down) // facing East
        	{
        		obstacle_pos = new Vector2(robot_pos.i(),robot_pos.j()+2);	// Robot on the south of obs
        		obstacle_pos_f = new Vector2(robot_pos.i()-1,robot_pos.j()+2);
        		obstacle_pos_b = new Vector2(robot_pos.i()+1,robot_pos.j()+2);
        		camera_pos_from_obs = "South";
        	}
        	
        	if(cur_direction == Direction.Left) //facing South
        	{
        		obstacle_pos = new Vector2(robot_pos.i()+2,robot_pos.j()); // Robot on the left of obs
        		obstacle_pos_f = new Vector2(robot_pos.i()+2,robot_pos.j()-1);
        		obstacle_pos_b = new Vector2(robot_pos.i()+2,robot_pos.j()+1);
        		camera_pos_from_obs = "West";
        	}
        	
        	if(cur_direction == Direction.Up) // Facing West
        	{
        		obstacle_pos = new Vector2(robot_pos.i(),robot_pos.j()-2); // Robot on the north of obs
        		obstacle_pos_f = new Vector2(robot_pos.i()-1,robot_pos.j()-2);
        		obstacle_pos_b = new Vector2(robot_pos.i()+1,robot_pos.j()-2);
        		camera_pos_from_obs = "North";
        	}
        	
        	
        	//Filename
    		//System.out.println("SHortestPath isFalse");
     	
        	/*
        	if(specialflag == 1)
        	{
        	
        		if(robot_pos == SpecialDesiredRobotPos 
        				//&& SpecialCameraPosFromObs == camera_pos_from_obs 
            			//&&  (this_map.getPoint(SpecialObs).obstacleState().equals(WPObstacleState.IsActualObstacle))
            					
            			)
            	{
        			System.out.println("Testing special");
        			
            		while (ActionFormulator.calibrating == true)
    				{
    					// Lets wait for calibrating to be done
    				}
            		
            		System.out.println("Special obstacle");
            		image_file_name = SpecialObs.toRpiImageFileString()+ camera_pos_from_obs + "special";
            		
            		if(!imageFileNames.contains(image_file_name))
        			{
        				imageFileNames.add(image_file_name);
        				System.out.println(imageFileNames);
        				bufferedActions.addFirst(RobotAction.TakeImage);
            			System.out.println(bufferedActions);
            			calibrationCounter --;
            			if (!Main.isSimulating()) {
            				Main.getRpi().RpiActions(image_file_name); //get Rpi to take photo   
            				System.out.println("Taking Image: " + image_file_name);
            				while (!picTaken)
            				{
            					
            				}
            				picTaken = false;
            				
            			}
            			
            			if (Main.isSimulating()) {
            				//Main.getRpi().RpiActions(image_file_name); //get Rpi to take photo   
            				System.out.println("Simulating Taking Image Special: " + image_file_name);
            			}
            			//If we detect an obstacle lets take a photo using bufferedaction
            			//Should i remove 1x calibration counter
            			
            		}   		
            	}
        	}*/
        	
        	
        	
        	
        	
        		
        	
        	
    		
    		if(obstacle_pos.i() >=0 && obstacle_pos.i() < Map.DIM_I &&
        			obstacle_pos.j() >=0 && obstacle_pos.j() < Map.DIM_J)
        	{
    			if(
    					
    					((this_map.getPoint(obstacle_pos).obstacleState().equals(WPObstacleState.IsActualObstacle)) && //
    					
    					(!(
    					this_map.getPoint(obstacle_pos_f).obstacleState().equals(WPObstacleState.IsActualObstacle) &&
    					this_map.getPoint(obstacle_pos_b).obstacleState().equals(WPObstacleState.IsActualObstacle) &&
    					this_map.getPoint(obstacle_pos).obstacleState().equals(WPObstacleState.IsActualObstacle)
    					))) ||    	// if only center is obs
    					
    					((this_map.getPoint(obstacle_pos_f).obstacleState().equals(WPObstacleState.IsActualObstacle)) && //
    	    					
    	    					(!(
    	    					this_map.getPoint(obstacle_pos_f).obstacleState().equals(WPObstacleState.IsActualObstacle) &&
    	    					this_map.getPoint(obstacle_pos_b).obstacleState().equals(WPObstacleState.IsActualObstacle) &&
    	    					this_map.getPoint(obstacle_pos).obstacleState().equals(WPObstacleState.IsActualObstacle)
    	    					))) || // if only left top is obs
    					
    					
    					
    					((this_map.getPoint(obstacle_pos_b).obstacleState().equals(WPObstacleState.IsActualObstacle)) && //
    	    					
    	    					(!(
    	    					this_map.getPoint(obstacle_pos_f).obstacleState().equals(WPObstacleState.IsActualObstacle) &&
    	    					this_map.getPoint(obstacle_pos_b).obstacleState().equals(WPObstacleState.IsActualObstacle) &&
    	    					this_map.getPoint(obstacle_pos).obstacleState().equals(WPObstacleState.IsActualObstacle)
    	    					))) // if only btm left is obs
    					
  							  					
    					
    					) //if obstacle on LHS and within boundary
        		{
    				while (ActionFormulator.calibrating == true)
    				{
    					// Lets wait for calibrating to be done
    				}
        			System.out.println("LHS has an obstacle");
        			image_file_name = obstacle_pos.toRpiImageFileString()+ camera_pos_from_obs;
        			
        			if ((this_map.getPoint(obstacle_pos_f).obstacleState().equals(WPObstacleState.IsActualObstacle)) && //
	    					//if pos_f
	    					(!(
	    					this_map.getPoint(obstacle_pos_f).obstacleState().equals(WPObstacleState.IsActualObstacle) &&
	    					this_map.getPoint(obstacle_pos_b).obstacleState().equals(WPObstacleState.IsActualObstacle) &&
	    					this_map.getPoint(obstacle_pos).obstacleState().equals(WPObstacleState.IsActualObstacle)
	    					)) &&
	    					
	    					(!this_map.getPoint(obstacle_pos).obstacleState().equals(WPObstacleState.IsActualObstacle) &&
	    					!this_map.getPoint(obstacle_pos_b).obstacleState().equals(WPObstacleState.IsActualObstacle))
	    					
        					
        					)
        				
        				
        			{
        				image_file_name = obstacle_pos_f.toRpiImageFileString()+ camera_pos_from_obs;
        			}
        			
        			if ((this_map.getPoint(obstacle_pos_b).obstacleState().equals(WPObstacleState.IsActualObstacle)) && //
	    					
	    					(!(
	    					this_map.getPoint(obstacle_pos_f).obstacleState().equals(WPObstacleState.IsActualObstacle) &&
	    					this_map.getPoint(obstacle_pos_b).obstacleState().equals(WPObstacleState.IsActualObstacle) &&
	    					this_map.getPoint(obstacle_pos).obstacleState().equals(WPObstacleState.IsActualObstacle)
	    					)) &&
	    					
	    					(!this_map.getPoint(obstacle_pos_f).obstacleState().equals(WPObstacleState.IsActualObstacle) &&
	    	    					!this_map.getPoint(obstacle_pos).obstacleState().equals(WPObstacleState.IsActualObstacle))
	    					
        					
        					)
        			{
        				image_file_name = obstacle_pos_b.toRpiImageFileString()+ camera_pos_from_obs;
        			}
        			
        			
        			//if image_file_name not in linkedlist, add to unique linkedlist. Not unique means already took picture.
        			if(!imageFileNames.contains(image_file_name))
        			{
        				imageFileNames.add(image_file_name);
        				System.out.println(imageFileNames);
        				bufferedActions.addFirst(RobotAction.TakeImage);
            			System.out.println(bufferedActions);
            			calibrationCounter --;
            			if (!Main.isSimulating()) {
            				Main.getRpi().RpiActions(image_file_name); //get Rpi to take photo   
            				System.out.println("Taking Image: " + image_file_name);
            				while (!picTaken)
            				{
            					
            				}
            				picTaken = false;
            				
            			}
            			
            			if (Main.isSimulating()) {
            				//Main.getRpi().RpiActions(image_file_name); //get Rpi to take photo   
            				System.out.println("Simulating Taking Image: " + image_file_name);
            			}
            			//If we detect an obstacle lets take a photo using bufferedaction
            			//Should i remove 1x calibration counter
            			
            		} 
        		}
    			  			
        	}
    		
    		
    	}
    	//  	
    	
        try {      	
            ExplorationSolver.setPermitTerminationState(false);

            if (!Main.isSimulating()) {
            		executionEndTime = System.currentTimeMillis();
            		System.out.println("Computational time for next movement"+ (executionStartTime- executionEndTime) + "ms");
        		//while(EventHandler.doneFinalCal) {
        			
        		//}
            	Main.getRpi().sendMoveCommand(bufferedActions, Translator.MODE_0);
                
                while (!actionCompleted) {
                }
                executionStartTime = System.currentTimeMillis();
                Map map = mapViewer.getSubjectiveMap();
                int[][] explored = mapViewer.getExplored();
                
                // send info to android
                Main.getRpi().sendInfoToAndroid(map, explored, bufferedActions);

                //System.out.println("Actions completed");
                actionCompleted = false;
                //increment calibrationCounter
                calibrationCounter += bufferedActions.size();
            }
      
            int first = 0;
            int index =0;
            for (RobotAction action : bufferedActions) {
                
                execute(action);
                if(mapViewer.checkRobotVisited(_position)){
                    robotVisitedBefore= true;
                    
                }
                else 
                    robotVisitedBefore = false;
                mapViewer.markRobotVisited(_position);
                
                //System.out.println("Execute action: "+action.toString());
                /*f(first == 0 && mapViewer.detectCircle(_position, _orientation)!=-1){
                		first = 1;
                		index = mapViewer.detectCircle(_position, _orientation);
                }
                mapViewer.markRobotHistory(_position, _orientation);*/
                Main.getGUI().update(this);
                
            if (Main.isSimulating()) {
                    Thread.sleep(sleepPeriod);
                } 
            } 
            
            
            /*
            if(first!= 0){
            		
            		
            	    actionFormulator.reverseToThePoint(mapViewer.getRobotMovementHistory().get(index), this);
            	    int i = mapViewer.robotMovementHistory.size()-index -1 ;
            	    while( i >0 )
            		{
            			mapViewer.robotMovementHistory.removeLast();
            			i--;
            		}
            	    
            }
            	*/	
            
            
            bufferedActions.clear();
            ExplorationSolver.setPermitTerminationState(true);   
        } catch (InterruptedException e) {
            System.out.println("Robot execution interrupted");
        }
    }
    
    public void executeAction(int sleepPeriod,RobotAction action) throws IOException {
        try {
            
            ExplorationSolver.setPermitTerminationState(false);    
            if (!Main.isSimulating()) {
            		executionEndTime = System.currentTimeMillis();
            		System.out.println("Computational time for next movement"+ (executionStartTime- executionEndTime) + "ms");
            		
            		LinkedList<RobotAction> rAction = new LinkedList<>();
            		rAction.add(0, action);
            		Main.getRpi().sendMoveCommand(rAction, Translator.MODE_0);
            		//Main.getRpi().sendSensingRequest();
            			
            	//Main.getRpi().sendMoveCommand(bufferedActions, Translator.MODE_0);
                
                while (!actionCompleted) {
                }
                executionStartTime = System.currentTimeMillis();
                Map map = mapViewer.getSubjectiveMap();
                int[][] explored = mapViewer.getExplored();

                // send info to android
                Main.getRpi().sendInfoToAndroid(map, explored, rAction);
               
                //System.out.println("Actions completed");
                actionCompleted = false;
                //increment calibrationCounter
                calibrationCounter +=  rAction.size();
            }
      
            LinkedList<RobotAction> rAction = new LinkedList<>();
			rAction.add(0, action);
            int first = 0;
            int index =0;
            for (RobotAction act :  rAction) {
                
                execute(act);
                if(mapViewer.checkRobotVisited(_position)){
                    robotVisitedBefore= true;
                    
                }
                else 
                    robotVisitedBefore = false;
                mapViewer.markRobotVisited(_position);
                
                //System.out.println("Execute action: "+action.toString());
                /*f(first == 0 && mapViewer.detectCircle(_position, _orientation)!=-1){
                		first = 1;
                		index = mapViewer.detectCircle(_position, _orientation);
                }
                mapViewer.markRobotHistory(_position, _orientation);*/
                Main.getGUI().update(this);
                
            if (Main.isSimulating()) {
                    Thread.sleep(sleepPeriod);
                } 
            } 
                        
            rAction.clear();
            ExplorationSolver.setPermitTerminationState(true);   
        } catch (InterruptedException e) {
            System.out.println("Robot execution interrupted");
        }
    }

    public boolean checkIfHavingBufferActions() {
        return !bufferedActions.isEmpty();
    }

    public boolean checkIfCalibrationCounterReached() {
        return (calibrationCounter >= 6);
    }

    public boolean clearCalibrationCounter() {
        calibrationCounter = 0;
        return true;
    }
    
    public  LinkedList<RobotAction> getBufferedActions(){
        return bufferedActions;
    }
    
    public boolean checkifObstacleAhead() {
    	SensingData s = new SensingData();
    	s.front_l = Integer.parseInt(Character.toString(sensingDataFromRPI.charAt(0)));
        s.front_m = Integer.parseInt(Character.toString(sensingDataFromRPI.charAt(1)));
        s.front_r = Integer.parseInt(Character.toString(sensingDataFromRPI.charAt(2)));
        if(s.front_l==1 || s.front_m==1 || s.front_r==1) {
        	return true;
        }
        return false;
    }
    public static void sensingDataCallback(String input) {
        sensingDataFromRPI = input;
    }

}
