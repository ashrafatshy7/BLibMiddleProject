package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Duration;
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
			startMidnightTask();
			startEndOfMonthTask();
			startDailyDueDateCheckTask();

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

//        long oneMonthInMinutes = 30L * 24L * 60L;
//        scheduler.scheduleAtFixedRate(task, oneMonthInMinutes, 1, TimeUnit.MINUTES);

	}

	private static void startMidnightTask() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		Runnable task = () -> {
			LocalDateTime now = LocalDateTime.now();
			System.out.println("Checking for midnight task at: " + now);

			// Check if it's exactly midnight
			if (now.toLocalTime().equals(LocalTime.MIDNIGHT)) {
				System.out.println("Midnight detected. Calling mysqlConnection.removeOrder()...");
				try {
					mysqlConnection.removeOrder();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Not midnight yet.");
			}
		};

		long initialDelay = computeInitialDelay();
		long period = TimeUnit.DAYS.toSeconds(1); // 24 hours

		// Schedule the task to run daily at midnight
		scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
	}

	private static long computeInitialDelay() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nextMidnight = now.toLocalDate().atStartOfDay().plusDays(1);
		Duration duration = Duration.between(now, nextMidnight);
		return duration.getSeconds();
	}

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

}
