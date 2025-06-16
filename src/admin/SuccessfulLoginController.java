package admin;

import java.time.LocalDateTime;

import application.AppController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

public class SuccessfulLoginController {

	@FXML
	private Label benutzerLabel;
	@FXML
	private Label sitzungsDauerLabel;

	@FXML
	private BorderPane contentPane;

	private AppController appController;
	
	private Timeline timer;

	private boolean timerStarted = false;

	private int secondsElapsed;

	public void setAppController (AppController appController) {
		this.appController = appController;
	}
	
	public void logout(MouseEvent event) throws Exception {
		if (appController != null) {
			appController.logout();
		}
	}
	
	public void showSessionData(String user) {
		
		benutzerLabel.setText(user);
		
		stopSessionTimer();
		
		if (!timerStarted) {

			startSessionTimer();
			timerStarted = true;
		}
	}

	private void startSessionTimer() {

		secondsElapsed = 0;

		timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
			secondsElapsed++;

			int minutes = secondsElapsed / 60;
			int seconds = secondsElapsed % 60;

			sitzungsDauerLabel.setText(String.format("%02d:%02d", minutes, seconds));
		}));

		timer.setCycleCount(Timeline.INDEFINITE);
		timer.play();
	}
	
	public void resetTimerState() {
	    timerStarted = false;
	    secondsElapsed = 0;
	    sitzungsDauerLabel.setText("00:00");
	}

	public void stopSessionTimer() {
		if (timer != null) {
			timer.stop();
		}
	}

}
