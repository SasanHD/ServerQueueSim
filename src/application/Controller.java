package application;

import java.io.IOException;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.application.*;
import java.util.*;
import java.util.stream.Collectors;

public class Controller implements Initializable {

	@FXML
	Label myLabel,myLabelTwo,myLabelThree,lableQueue,serverOneTime,serverTwoTime,serverThreeTime,timerLabel;
	@FXML
	Button startButton,resetButton;
	
	private Parent root;
	private Stage stage;
	private Scene scene;
	
	//to check if a server is available.
	boolean serviceOne=true;
	boolean serviceTwo=true;
	boolean serviceThree=true;
	
	ArrayList<Integer> allJobs = new ArrayList<Integer>(); //list of all Job IDs.
	ArrayList<Integer> serverOneJobs = new ArrayList<Integer>(); //list of Job IDs processed by server one.
	ArrayList<Integer> serverTwoJobs = new ArrayList<Integer>(); //list of Job IDs processed by server two.
	ArrayList<Integer> serverThreeJobs = new ArrayList<Integer>(); //list of Job IDs processed by server three.
	ArrayList<Integer> potentialFirstTryJobs = new ArrayList<Integer>(); //list of Job IDs that may have been processed on the first try.
	ArrayList<Integer> requeuedOnceJobs = new ArrayList<Integer>();	//list of Job IDs that were re-queued once.
	ArrayList<Integer> jobsSentBackToQueue = new ArrayList<Integer>(); //list of Job IDs that were re-queued.
	ArrayList<Integer> refAllJobs = new ArrayList<Integer>(); //list of Job IDs processed as reference.
	ArrayList<Integer> refAllJobsTime = new ArrayList<Integer>(); //list of Job ID times processed as reference.
	
	//counter to keep track of jobs processed by server.
	int serverOneTotal=0;
	int serverTwoTotal=0;
	int serverThreeTotal=0;
	int jobsOnFirstTry=0;
	int jobsRequeuedOnce=0;
	int jobsRequeuedTwice=0;
	
	//current index and random time.
	int getIndex=0;
	int duration=0;
	
	//timer counter.
	static int time=0;
	
	Random rand = new Random();
	
	//generate list of Job IDs method.
	public void JobsGenerator() {
			
	    for (int i=1; i<=300; i++) {
	        allJobs.add(i);
	     }
	     
	    Collections.shuffle(allJobs);    
	}
		
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		myLabel.setText("Available");
		myLabelTwo.setText("Available");
		myLabelThree.setText("Available");	
	}
	
	//reset method.
	public void resetQueue (ActionEvent e) {
		resetButton.setDisable(true);
		startButton.setDisable(false);
		
		//clear ArrayLists.
		allJobs.clear();
		serverOneJobs.clear();
		serverTwoJobs.clear();
		serverThreeJobs.clear();
		potentialFirstTryJobs.clear();
		requeuedOnceJobs.clear();
		jobsSentBackToQueue.clear();
		refAllJobs.clear();
		refAllJobsTime.clear();
		
		//reset counters.
		serverOneTotal=0;
		serverTwoTotal=0;
		serverThreeTotal=0;
		jobsOnFirstTry=0;
		jobsRequeuedOnce=0;
		jobsRequeuedTwice=0;
		time = 0;
		
		getIndex=0;
		duration=0;
		
		//reset labels.
		lableQueue.setText("[ No Jobs Currently In Queue ]");
		myLabel.setText("Available");
		myLabelTwo.setText("Available");
		myLabelThree.setText("Available");
		serverOneTime.setText("0 secs");
		serverTwoTime.setText("0 secs");
		serverThreeTime.setText("0 secs");
		timerLabel.setText(Integer.toString(time)+" secs");
		
	}
	
	//start simulation method.
	public void updateLabel(ActionEvent e)  {
		
		//generate a random list of Job IDs without duplicates.
		JobsGenerator();
		
		//a new thread to branch out of the main FX thread.
		new Thread(()->{ 

			startButton.setDisable(true);
			ArrayList<Integer> jobs = new ArrayList<Integer>(); //list of jobs in queue.
			ArrayList<Integer> times = new ArrayList<Integer>(); //list of times of respective jobs.

			//a 3 second delay before adding first job to the queue. 
			try {
					Thread.sleep(1000);
					time=time+1;
					Platform.runLater(() -> {
						lableQueue.setText(jobs.toString());
						timerLabel.setText(Integer.toString(time)+" secs");
					});
					
					Thread.sleep(1000);
					time=time+1;
					Platform.runLater(() -> {
						lableQueue.setText(jobs.toString());
						timerLabel.setText(Integer.toString(time)+" secs");
	    			
					});
					Thread.sleep(1000);
					time=time+1;
					Platform.runLater(() -> {
						lableQueue.setText(jobs.toString());
						timerLabel.setText(Integer.toString(time)+" secs");
					});
			} catch (InterruptedException ex) {ex.printStackTrace();}
    	    
			//for-loop used as a program timer.
			for(int i=0;i<=14;i++) {
				
				//a random time (5-19 seconds) is assigned to a Job ID.
				duration= 5 + rand.nextInt(15);
	    	    jobs.add(allJobs.get(getIndex));
	    	    times.add(duration);
	    	    
	    	    //add the job and its time to the reference list.
	    	    refAllJobs.add(allJobs.get(getIndex));
	    	    refAllJobsTime.add(duration);
	    	    
	    	    //if a job has a time lower than or equal 8 seconds, add it to the list of potential jobs that will be processed once.
	    	    if(times.get(times.size()-1)<=8) {
	    	    	potentialFirstTryJobs.add(jobs.get(jobs.size()-1));
	    	    }
	    	    
	    	    ++getIndex;
	    	    
	            //update the queue.
	    		Platform.runLater(() -> {
	    			lableQueue.setText(jobs.toString());
	    		   });
	    
	    		
	    	    //if server one is available, give it a job from the queue.
	    	    if(serviceOne==true && jobs.isEmpty()!=true) {
	    	    	
	    	    	Platform.runLater(() -> {
	    	 
	    	    		myLabel.setText(String.valueOf(jobs.get(0)));
	    	    		
	    	    		//remove ID and time from the queue of jobs.
	    	    		int jobRemoved=jobs.get(0);
	    	    		int timeOfJobRemoved=times.get(0);
	    	    		jobs.remove(0);
	    	    		times.remove(0);
	    	    		
	    	    		//server one is no longer available.
	    	    		serviceOne=false;
	    	    		
	    	    		//Update the queue.
	    	    	    lableQueue.setText(jobs.toString());
	    	    	    
	    	    		new Thread(()->{ //a new thread for handling Server 1.
	    	    			
	    	    			//if the time is 8 seconds or under.
	    	    			if(timeOfJobRemoved<=8) {
	    		        	++serverOneTotal;
	    		        	serverOneJobs.add(jobRemoved);
	    		        	
	    		        	//determine time to delay by seconds.
	    		        	switch (timeOfJobRemoved) {
	    		        	case 1: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 2: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 3: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 4: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 5: try {	
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));	
	    		        	} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 6: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break; 	
	    		        	case 7: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	
	    		        	case 8: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	
	    		        	} //end switch.
	    		        
	    		        }
	    	    			//if the time is greater than 8 seconds, and equal to or under 16 seconds.
	    	    			else if(timeOfJobRemoved>8 && timeOfJobRemoved<=16) {
	    		        	
	    	    			Platform.runLater(() -> requeuedOnceJobs.add(jobRemoved));
	    		        	jobsSentBackToQueue.add(jobRemoved); 
	    		        	
	    		        	//determine time to delay by seconds.
	    		        	switch (timeOfJobRemoved) {
	    		        	case 9: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 10: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 11: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 12: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 13: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 14: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 15: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 16: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	
	    		        	} //end switch.
	    		        
	    		        }
	    	    			//if the time is greater than 16 seconds and smaller than or equal to 19 seconds.
	    	    			else if(timeOfJobRemoved>16) {
	    		        	jobsSentBackToQueue.add(jobRemoved); 
	    		        	
	    		        	//determine time to delay by seconds.
	    		        	switch (timeOfJobRemoved) {
	    		        	case 17: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 18: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 19: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;

	    		        	} //end switch.
	    		        } 
	   	    		
	   	    			 //set availability after processing ID.
	   	    			 Platform.runLater(() -> {      
	   		    	   			myLabel.setText("Available");
	   		    	   			serviceOne=true;
	   		    	   			
	   		    	   			//Update the queue.
	   		    	   			lableQueue.setText(jobs.toString()); 
	   		    	        });	   	    	    	        
	    	    		}).start();
	    	        });

	    	    }
	    	    
	    	    //if server two is available, give it a job from the queue.
	    	    else if (serviceTwo==true && jobs.isEmpty()!=true) {
	    	    	
	    	    	Platform.runLater(() -> {
	    	    		myLabelTwo.setText(String.valueOf(jobs.get(0)));
	    	    		
	    	    		//remove ID and time from the queue of jobs.
	    	    		int jobRemoved=jobs.get(0);
	    	    		int timeOfJobRemoved=times.get(0);
	    	    		jobs.remove(0);
	    	    		times.remove(0);
	    	    		
	    	    		//server one is no longer available.
	    	    		serviceTwo=false;
	    	    		
	    	    		//Update the queue.
	    	    	    lableQueue.setText(jobs.toString());
	    	    	    
	    	    		
	    	    		new Thread(()->{ //a new thread for handling Server 2.
	    		        
	    	    			//if the time is 8 seconds or under.
	    	    			if(timeOfJobRemoved<=8) {
	    		        	++serverTwoTotal;
	    		        	serverTwoJobs.add(jobRemoved);
	    		        	
	    		        	switch (timeOfJobRemoved) {
	    		        	case 1: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 2: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 3: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 4: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 5: try {	
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        	} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 6: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		        	case 7: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		        	case 8: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		          } //end switch statement.	    		        
	    		        }
	    	    			//if the time is greater than 8 seconds, and equal to or under 16 seconds.
	    	    			else if(timeOfJobRemoved>8 && timeOfJobRemoved<=16) {
	    		        	Platform.runLater(() -> requeuedOnceJobs.add(jobRemoved));
	    		        	jobsSentBackToQueue.add(jobRemoved);
	    		        	
	    		        	switch (timeOfJobRemoved) {
	    		        	case 9: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 10: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 11: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 12: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 13: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 14: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 15: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 16: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		          } //end switch statement.	
	    		        
	    		        }
	    	    			//if the time is greater than 16 seconds and smaller than or equal to 19 seconds.
	    	    			else if(timeOfJobRemoved>16) {
	    		        	jobsSentBackToQueue.add(jobRemoved); 
	    		        	
	    		        	switch (timeOfJobRemoved) {
	    		        	case 17: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 18: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 19: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		          } //end switch statement.	
	    		        }
	   	    			  	    			
	   	    			 Platform.runLater(() -> {
	   		    	        
	   	    				 	//set availability after processing ID.
	   		    	   			myLabelTwo.setText("Available");
	   		    	   			serviceTwo=true;
	   		    	   			
	   		    	   			//update the queue.
	   		    	   			lableQueue.setText(jobs.toString());
		    	    	    
	   		    	        });	   	    	    	        
	    	    		}).start();
	    	        });
	    	    }
	    	    
	    	    //if server three is available, give it a job from the queue.
	    	    else if(serviceThree==true && jobs.isEmpty()!=true) {
	    	    	Platform.runLater(() -> {
	    	    		myLabelThree.setText(String.valueOf(jobs.get(0)));
	    	    		
	    	    		//remove ID and time from the queue of jobs.
	    	    		int jobRemoved=jobs.get(0);
	    	    		int timeOfJobRemoved=times.get(0);
	    	    		jobs.remove(0);
	    	    		times.remove(0);
	    	    		
	    	    		//server one is no longer available.
	    	    		serviceThree=false;
	    	    		
	    	    		//Update the queue.
	    	    	    lableQueue.setText(jobs.toString());
	    	    	    
	    	    		
	    	    		new Thread(()->{ //a new thread for handling Server 3.
	    	    			
	    	    			//if the time is 8 seconds or under.
	    	    			if(timeOfJobRemoved<=8) {
	    		        	++serverThreeTotal;
	    		        	serverThreeJobs.add(jobRemoved);
	    		        	
	    		        	switch (timeOfJobRemoved) {
	    		        	case 1: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 2: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 3: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 4: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 5: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));	
	    		        	} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		        	case 6: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		        	case 7: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		        	case 8: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		          } //end switch statement.	    		        
	    		        }
	    	    			//if the time is greater than 8 seconds, and equal to or under 16 seconds.
	    	    			else if(timeOfJobRemoved>8 && timeOfJobRemoved<=16) {
	    		        	Platform.runLater(() -> requeuedOnceJobs.add(jobRemoved));
	    		        	jobsSentBackToQueue.add(jobRemoved); 
	    		        	
	    		        	switch (timeOfJobRemoved) {
	    		        	case 9: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 10: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 11: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 12: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 13: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 14: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 15: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 16: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		          } //end switch statement.	    		        
	    		        }
	    	    			//if the time is greater than 16 seconds and smaller than or equal to 19 seconds.
	    	    			else if(timeOfJobRemoved>16) {
	    		        	jobsSentBackToQueue.add(jobRemoved); 
	    		        	
	    		        	switch (timeOfJobRemoved) {
	    		        	case 17: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 18: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 19: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		          } //end switch statement.	
	    		        }

	   	    			 Platform.runLater(() -> {     
	   		    	   			
	   	    				 	//set availability after processing ID.
	   	    				 	myLabelThree.setText("Available");
	   		    	   			serviceThree=true;
	   		    	   			
	   		    	   			//update the queue.
	   		    	   			lableQueue.setText(jobs.toString());
		    	    	    
	   		    	        });	   	    	    	        
	    	    		}).start();
	    	        });
	    	    } //end of first If-statement. 
	    	    
	    	    
	    	    //wait 3 seconds before adding a new job.
	    	    try {
					Thread.sleep(1000);
					time=time+1;
					Platform.runLater(() -> {
						lableQueue.setText(jobs.toString());
						timerLabel.setText(Integer.toString(time)+" secs");	    			
					});
					
					Thread.sleep(1000);
					time=time+1;
					Platform.runLater(() -> {
						lableQueue.setText(jobs.toString());
						timerLabel.setText(Integer.toString(time)+" secs");
	    			
					});
					
					Thread.sleep(1000);
					time=time+1;
					Platform.runLater(() -> {
						lableQueue.setText(jobs.toString());
						timerLabel.setText(Integer.toString(time)+" secs");
					});
	    	    } catch (InterruptedException ex) {ex.printStackTrace();}
	    	    
	    		//add another job and time to the queue.
	    	    duration= 5 + rand.nextInt(15);
	    	    jobs.add(allJobs.get(getIndex));
	    	    times.add(duration);
	    	    
	    	    //add the job and its time to the reference list.
	    	    refAllJobs.add(allJobs.get(getIndex));
	    	    refAllJobsTime.add(duration);
	    	    
	    	    if(times.get(times.size()-1)<=8) {
	    	    	potentialFirstTryJobs.add(jobs.get(jobs.size()-1));
	    	    }
	    	    
	    	    ++getIndex;
 
	    	    //Update the queue.
	    		Platform.runLater(() -> {
	    				lableQueue.setText(jobs.toString());
	    		   });
	    	    
	    	    //2nd if statement starts here.
	    	    //if server one is available, give it a job from the queue.
	    	    if(serviceOne==true && jobs.isEmpty()!=true) {
	    	    	
	    	    	Platform.runLater(() -> {
	    	            
	    	    		myLabel.setText(String.valueOf(jobs.get(0)));
	    	    		
	    	    		//remove ID and time from the queue of jobs.
	    	    		int jobRemoved=jobs.get(0);
	    	    		int timeOfJobRemoved=times.get(0);
	    	    		jobs.remove(0);
	    	    		times.remove(0);
	    	    		
	    	    		//server one is no longer available.
	    	    		serviceOne=false;
	    	    		
	    	    		//Update the queue.
	    	    	    lableQueue.setText(jobs.toString());
	
	    	    		new Thread(()->{ //a new thread for handling Server 1.
	    		        
	    	    			//if the time is 8 seconds or under.
	    	    			if(timeOfJobRemoved<=8) {
	    		        	++serverOneTotal;
	    		        	serverOneJobs.add(jobRemoved);
	    		        	
	    		        	//determine time to delay by seconds.
	    		        	switch (timeOfJobRemoved) {
	    		        	case 1: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 2: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 3: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 4: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 5: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));	
	    		        	} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		        	case 6: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		        	case 7: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	  		        	
	    		        	case 8: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	
	    		          }//end switch.
	    		        
	    		        }
	    	    			//if the time is greater than 8 seconds, and equal to or under 16 seconds.
	    	    			else if(timeOfJobRemoved>8 && timeOfJobRemoved<=16) {
	    	    				
	    		        	Platform.runLater(() -> requeuedOnceJobs.add(jobRemoved));
	    		        	jobsSentBackToQueue.add(jobRemoved); 
	    		        	
	    		        	//determine time to delay by seconds.
	    		        	switch (timeOfJobRemoved) {
	    		        	case 9: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 10: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 11: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 12: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 13: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 14: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 15: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 16: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	
	    		          }//end switch.
	    		        
	    		        }
	    	    			//if the time is greater than 16 seconds and smaller than or equal to 19 seconds.
	    	    			else if(timeOfJobRemoved>16) {
	    		        	jobsSentBackToQueue.add(jobRemoved); 
	    		        	
	    		        	//determine time to delay by seconds.
	    		        	switch (timeOfJobRemoved) {
	    		        	case 17: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 18: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 19: try {
	    		        		Platform.runLater(() -> serverOneTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverOneTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;

	    		          } //end switch.
	    		        }
	   	    			   	    			
	   	    			 Platform.runLater(() -> {
	   	    				 	//set availability after processing ID.
	   		    	   			myLabel.setText("Available");
	   		    	   			serviceOne=true;
	   		    	   			
	   		    	   			//Update the queue.
	   		    	   			lableQueue.setText(jobs.toString());  
	   		    	        });
	    	    		}).start();
	    	        });
	    	    }
	    	    
	    	    //if server two is available, give it a job from the queue.
	    	    else if (serviceTwo==true && jobs.isEmpty()!=true) {
	    	    	Platform.runLater(() -> {
	    	    		myLabelTwo.setText(String.valueOf(jobs.get(0)));
	    	    		
	    	    		//remove ID and time from the queue of jobs.
	    	    		int jobRemoved=jobs.get(0);
	    	    		int timeOfJobRemoved=times.get(0);
	    	    		jobs.remove(0);
	    	    		times.remove(0);
	    	    		
	    	    		//server one is no longer available.
	    	    		serviceTwo=false;
	    	    		
	    	    		//Update the queue.
	    	    	    lableQueue.setText(jobs.toString());
	    	    	   
	    	    		new Thread(()->{ //a new thread for handling Server 2.
	    		        
	    	    			//if the time is 8 seconds or under.
	    	    			if(timeOfJobRemoved<=8) {
	    		        	++serverTwoTotal;
	    		        	serverTwoJobs.add(jobRemoved);
	    		        	
	    		        	switch (timeOfJobRemoved) {
	    		        	case 1: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 2: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 3: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 4: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 5: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        	} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		        	case 6: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;    		        	
	    		        	case 7: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;   		        	
	    		        	case 8: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		          } //end switch statement.	    		        
	    		        }
	    	    			//if the time is greater than 8 seconds, and equal to or under 16 seconds.
	    	    			else if(timeOfJobRemoved>8 && timeOfJobRemoved<=16) {
	    		        	Platform.runLater(() -> requeuedOnceJobs.add(jobRemoved));
	    		        	jobsSentBackToQueue.add(jobRemoved);
	    		        	
	    		        	switch (timeOfJobRemoved) {
	    		        	case 9: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 10: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 11: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 12: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 13: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 14: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 15: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 16: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		          } //end switch statement.	    		        
	    		        }
	    	    			//if the time is greater than 16 seconds and smaller than or equal to 19 seconds.
	    	    			else if(timeOfJobRemoved>16) {
	    		        	jobsSentBackToQueue.add(jobRemoved); 
	    		        	
	    		        	switch (timeOfJobRemoved) {
	    		        	case 17: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 18: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 19: try {
	    		        		Platform.runLater(() -> serverTwoTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverTwoTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		          } //end switch statement.	
	    		        }
	   	    			   	    			
	   	    			 Platform.runLater(() -> {
	   		    	          
	   	    				 	//set availability after processing ID.
	   		    	   			myLabelTwo.setText("Available");
	   		    	   			serviceTwo=true;
	   		    	   			
	   		    	   			//Update the queue.
	   		    	   			lableQueue.setText(jobs.toString());		    	    	    
	   		    	        });	   	    	    	        
	    	    		}).start();
	    	        });
	    	    }
	    	    
	    	    //if server three is available, give it a job from the queue.
	    	    else if(serviceThree==true && jobs.isEmpty()!=true) {
	    	    	Platform.runLater(() -> {	    	            
	    	    		myLabelThree.setText(String.valueOf(jobs.get(0)));
	    	    		
	    	    		//remove ID and time from the queue of jobs.
	    	    		int jobRemoved=jobs.get(0);
	    	    		int timeOfJobRemoved=times.get(0);
	    	    		jobs.remove(0);
	    	    		times.remove(0);
	    	    		
	    	    		//server three is no longer available.
	    	    		serviceThree=false;
	    	    		
	    	    		//Update the queue.
	    	    	    lableQueue.setText(jobs.toString());
	    	    	    	
	    	    		new Thread(()->{ //a new thread for handling Server 3.
	    		        
	    	    			//if the time is 8 seconds or under.
	    	    			if(timeOfJobRemoved<=8) {
	    		        	++serverThreeTotal;
	    		        	serverThreeJobs.add(jobRemoved);
	    		        	
	    		        	switch (timeOfJobRemoved) {
	    		        	case 1: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 2: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 3: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 4: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 5: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        	} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		        	case 6: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		        	case 7: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		        	case 8: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	    		        	
	    		          } //end switch statement.	    		        
	    		        }
	    	    			//if the time is greater than 8 seconds, and equal to or under 16 seconds.
	    	    			else if(timeOfJobRemoved>8 && timeOfJobRemoved<=16) {
	    		        	Platform.runLater(() -> requeuedOnceJobs.add(jobRemoved));
	    		        	jobsSentBackToQueue.add(jobRemoved); 
	    		        	
	    		        	switch (timeOfJobRemoved) {
	    		        	case 9: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 10: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 11: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 12: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 13: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 14: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 15: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 16: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;	   		        	
	    		          } //end switch statement.	    		        
	    		        }
	    	    			//if the time is greater than 16 seconds and smaller than or equal to 19 seconds.
	    	    			else if(timeOfJobRemoved>16) {
	    		        	jobsSentBackToQueue.add(jobRemoved); 
	    		        	
	    		        	switch (timeOfJobRemoved) {
	    		        	case 17: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 18: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		        	case 19: try {
	    		        		Platform.runLater(() -> serverThreeTime.setText("1 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("2 secs"));		        		
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("3 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("4 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("5 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("6 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("7 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("8 secs"));
	    		        		Thread.sleep(1000);
	    		        		Platform.runLater(() -> serverThreeTime.setText("0 secs"));
	    		        		jobs.add(jobRemoved);
	    		        		times.add(timeOfJobRemoved-8);
	    		        		} catch (InterruptedException ex) {ex.printStackTrace();} 
	    		        	break;
	    		          } //end switch statement.	
	    		        }
	   	    			   	    			
	   	    			 Platform.runLater(() -> {     
	   	    				 	//set availability after processing ID.
	   	    				 	myLabelThree.setText("Available");
	   	    				 	serviceThree=true;
	   	    				 	
	   	    				 	//Update the queue.
	   	    				 	lableQueue.setText(jobs.toString());		    	    	    
	   		    	        });   	    	    	        
	    	    		}).start();
	    	        });
	    	    }
	    	    
	    	    
	    	    //update the queue.
	    		Platform.runLater(() -> {
	    				lableQueue.setText(jobs.toString());	 		
	    		   });
	    	    
	    	    //wait 3 seconds before adding a new job.	 
	    		try {
					Thread.sleep(1000);
					time=time+1;
					Platform.runLater(() -> {
						lableQueue.setText(jobs.toString());
						timerLabel.setText(Integer.toString(time)+" secs");	    			
					});
					
					Thread.sleep(1000);
					time=time+1;
					Platform.runLater(() -> {
						lableQueue.setText(jobs.toString());
						timerLabel.setText(Integer.toString(time)+" secs");	    			
					});
					
					Thread.sleep(1000);
					time=time+1;
					Platform.runLater(() -> {
						lableQueue.setText(jobs.toString());
						timerLabel.setText(Integer.toString(time)+" secs");    			
					});
	    	    } catch (InterruptedException ex) {ex.printStackTrace();}
	    	    
	    	    //update the queue.
	    		Platform.runLater(() -> {
	    				lableQueue.setText(jobs.toString());	    		
	    		   });
 	        	 
			} //end of for-loop.
			
			//wait 5 seconds before enabling reset button.
			try {
					Thread.sleep(5000);		
					resetButton.setDisable(false); 
				} catch (InterruptedException ex) { ex.printStackTrace();} 
		
			ArrayList<Integer> allProJobs = new ArrayList<Integer>(); //list of all processed jobs.
			ArrayList<Integer> firstTryJobs = new ArrayList<Integer>(); //list of jobs processed on first try.
			
			//merging all processed server jobs into one list.
			allProJobs.addAll(serverOneJobs);
			allProJobs.addAll(serverTwoJobs);
			allProJobs.addAll(serverThreeJobs);
			
			//identifying jobs that were processed on the first try.
			allProJobs.retainAll(potentialFirstTryJobs);
			firstTryJobs.addAll(allProJobs);
			
			//removing duplicates of first try job IDs.
	        List<Integer> list = new ArrayList<>(firstTryJobs);
	        List<Integer> newList = list.stream().distinct().collect(Collectors.toList());
			
			ArrayList<Integer> onceSentBack = new ArrayList<Integer>(); //jobs sent back to the queue once.
			ArrayList<Integer> twiceSentBack = new ArrayList<Integer>(); //jobs sent back to the queue twice.
			
			//separating and allocating job IDs to each list.
			for(int i=0;i<jobsSentBackToQueue.size();i++) {
				
				int counter=0;
				for(int j=0;j<jobsSentBackToQueue.size();j++) {
					if(jobsSentBackToQueue.get(i).equals(jobsSentBackToQueue.get(j))) {
						counter++;
					}
				}
				
				if(counter==1) {
					onceSentBack.add(jobsSentBackToQueue.get(i));
				}
				else if(counter==2) {
					twiceSentBack.add(jobsSentBackToQueue.get(i));
				}
			}
			
			//removing duplicates of jobs sent back twice.
	        List<Integer> listTwo = new ArrayList<>(twiceSentBack);
	        List<Integer> newListTwo = listTwo.stream().distinct().collect(Collectors.toList());
			
	        //generate the final report.
			Platform.runLater(() -> {
				
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/Report.fxml"));
					root= loader.load();
					ReportController report = loader.getController();
					report.setReport(serverOneTotal+serverTwoTotal+serverThreeTotal,serverOneTotal,serverTwoTotal,serverThreeTotal,newList.size(),onceSentBack.size(),newListTwo.size(),serverOneJobs,serverTwoJobs,serverThreeJobs);
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				stage = new Stage();
				scene = new Scene(root);
			    scene.getStylesheets().add(getClass().getResource("report.css").toExternalForm());
				stage.setScene(scene);
				stage.setTitle("Final Report");
				stage.setResizable(false);
			    stage.getIcons().add(new Image(Main.class.getResourceAsStream("queue.png")));	
				stage.show();
			   });
			
    	   }).start();
		

    } //end start simulation method.
	
	public void aboutServer (ActionEvent event) throws IOException {
		
			root = FXMLLoader.load(getClass().getResource("about.fxml"));
			stage = new Stage();
			scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("about.css").toExternalForm());
			stage.setScene(scene);
			stage.setTitle("About");
			stage.setResizable(false);
		    stage.getIcons().add(new Image(Main.class.getResourceAsStream("queue.png")));
			stage.show();

	}
	
	//close application method.
	public void closeServer (ActionEvent event) {
		Platform.exit();
		System.exit(0);
	}
		
}
