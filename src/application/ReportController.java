package application;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ReportController {
	
	@FXML
	Label totalJobs,totalServerOne,totalServerTwo,totalServerThree,firstJobs,requeueOnce,requeueTwice,jobIdServerOne,jobIdServerTwo,jobIdServerThree;
	@FXML
	PieChart pieChart,pieChartTwo;
	@FXML
	Button closeButton;
	
	public void setReport(int totalJ,int totalServerO,int totalServerTw,int totalServerTh,int firstJ,int rOnce,int rTwice,ArrayList<Integer> oneIds,ArrayList<Integer> twoIds,ArrayList<Integer> threeIds) {
		
		totalJobs.setText(String.valueOf(totalJ)); //total number of jobs processed.
		totalServerOne.setText(String.valueOf(totalServerO)); //total number of jobs processed by server one.
		totalServerTwo.setText(String.valueOf(totalServerTw)); //total number of jobs processed by server two.
		totalServerThree.setText(String.valueOf(totalServerTh)); //total number of jobs processed by server three.
		firstJobs.setText(String.valueOf(firstJ)); //total number of jobs processed in the first try.
		requeueOnce.setText(String.valueOf(rOnce)); //total number of jobs re-queued once.
		requeueTwice.setText(String.valueOf(rTwice)); //total number of jobs re-queued twice.
		jobIdServerOne.setText(oneIds.toString()); //job IDs processed by server one.
		jobIdServerTwo.setText(twoIds.toString()); //job IDs processed by server two.
		jobIdServerThree.setText(threeIds.toString()); //job IDs processed by server three.
		
		ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
				new PieChart.Data("Server One", totalServerO),
				new PieChart.Data("Server Two", totalServerTw),
				new PieChart.Data("Server Three", totalServerTh));
		
		pieChart.setData(pieData);
		
		ObservableList<PieChart.Data> pieDataTwo = FXCollections.observableArrayList(
				new PieChart.Data("On The First Try", firstJ),
				new PieChart.Data("Rest Of The Jobs", totalJ-firstJ));
		
		pieChartTwo.setData(pieDataTwo);
	
	}
	
	public void closeReport (ActionEvent event) {
		Stage stage = (Stage) closeButton.getScene().getWindow();
	    stage.close();
	}
	

}
