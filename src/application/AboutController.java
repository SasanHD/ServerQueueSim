package application;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class AboutController implements Initializable {
	@FXML
	Label labelOne,labelTwo,labelThree,labelFour,labelFive,labelSix;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		labelOne.setText("- This application simulates a Queue of Jobs being served.");	
		labelTwo.setText("- There are Three Servers available.");	
		labelThree.setText("- The program runs for a minute and 33 seconds.");		
		labelFour.setText("- A random Job ID is added to the queue after every 3 seconds.");		
		labelFive.setText("- A Job ID spends no more than 8 seconds on a server. Anymore than that, it is requeued.");		
		labelSix.setText("- A final report is displayed after all Job ID's are served.");	
	}
}
