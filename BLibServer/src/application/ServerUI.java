package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import gui.bounderies.ServerFrameController;

public class ServerUI extends Application {

	private static EchoServer server;

	public static void main(String args[]) throws Exception {
		launch(args);
	} // end main

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Start the scheduled task for end-of-month processing
		startEndOfMonthTask();
		startDailyDueDateCheckTask();

		// TODO Auto-generated method stub
		mysqlConnection.connectToDB();
		ServerFrameController aFrame = new ServerFrameController();

		aFrame.start(primaryStage);
	}

	public static void runServer(String p) {

		int port = 0;

		try {
			port = Integer.parseInt(p);

		} catch (Throwable t) {
			System.out.println("ERROR - Could not connect!");
		}

		server = new EchoServer(port);

		try {
			server.listen(); // Start listening for connections

		} catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
	}

	public static void stopServer() {
		if (server != null) {
			try {
				server.close(); // Fully shuts down the server
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static EchoServer getServer() {
		return server;
	}

	@Override
	public void stop() throws Exception {
		// This method is called when the application is about to exit
		super.stop();
	}

	/**
	 * Scheduled task for generating end-of-month graphs.
	 */
	private static void startEndOfMonthTask() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

		Runnable task = () -> {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());

			// Check if it's the last day of the month at midnight
			if (now.toLocalDate().equals(lastDayOfMonth.toLocalDate()) && now.getHour() == 0 && now.getMinute() == 0) {
				System.out.println("End of month detected. Calling mysqlConnection method...");
				try {
					mysqlConnection.endOfMonthProcessingLoanReport();
					mysqlConnection.endOfMonthProcessingStatusReport();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Not the end of the month yet.");
			}
		};

		// Schedule the task to run after one minute and then every minute
		scheduler.scheduleAtFixedRate(task, 1, 1, TimeUnit.MINUTES);
	}

//	/**
//	 * Scheduled task for generating end-of-month graphs (simulated for testing).
//	 */
//	private static void startEndOfMonthTask() {
//		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//
//		Runnable task = () -> {
//			System.out.println("Calling mysqlConnection method after one minute...");
//			try {
//				mysqlConnection.endOfMonthProcessingLoanReport();
//				mysqlConnection.endOfMonthProcessingStatusReport();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		};
//
//		// Schedule the task to run after one minute and then every minute
//		scheduler.scheduleAtFixedRate(task, 1, 1, TimeUnit.MINUTES);
//	}

	/**
	 * Scheduled task for daily due date checking.
	 */
	private static void startDailyDueDateCheckTask() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

		Runnable task = () -> {
			try {
				// Call the method to check due dates and send SMS
				boolean smsSent = mysqlConnection.checkDueDateAndSendSMS();

				if (smsSent) {
					// Show a popup message indicating success
					Platform.runLater(() -> {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("SMS Notification");
						alert.setHeaderText(null);
						alert.setContentText("SMS notifications for loans due tomorrow were sent successfully.");
						alert.showAndWait();
					});
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		// Schedule the task to run after one minute and then every minute
		long initialDelay = 0; // Start immediately
		long oneMinute = TimeUnit.MINUTES.toMillis(1); // One minute in milliseconds

		scheduler.scheduleAtFixedRate(task, initialDelay, oneMinute, TimeUnit.MILLISECONDS);

//		// Schedule the task to run at a specific time daily (e.g., midnight)
//		long initialDelay = calculateInitialDelay();
//		long oneDay = TimeUnit.DAYS.toMillis(1); // One day in milliseconds
//
//		scheduler.scheduleAtFixedRate(task, initialDelay, oneDay, TimeUnit.MILLISECONDS);
	}

	/**
	 * Helper method to calculate the initial delay to the next midnight.
	 */
	private static long calculateInitialDelay() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nextMidnight = now.toLocalDate().atStartOfDay().plusDays(1);
		return java.time.Duration.between(now, nextMidnight).toMillis();
	}

}
