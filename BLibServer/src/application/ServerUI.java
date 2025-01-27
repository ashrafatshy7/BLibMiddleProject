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

/**
 * The ServerUI class is responsible for initializing and managing the
 * server-side application. It sets up scheduled tasks for maintenance
 * operations and provides methods to start and stop the server.
 */
public class ServerUI extends Application {

	/** The server instance. */
	private static EchoServer server;

	private static ScheduledExecutorService reportScheduler, removeOrderScheduler, EmailSmsScheduler, removeFrozenAccount;

	/**
	 * The main entry point of the application.
	 *
	 * @param args The command-line arguments.
	 * @throws Exception If an error occurs during startup.
	 */
	public static void main(String args[]) throws Exception {
		launch(args);
	} // end main

	/**
	 * Starts the JavaFX application and initializes the server UI.
	 *
	 * @param primaryStage The primary stage for the application.
	 * @throws Exception If an error occurs during initialization.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		mysqlConnection.connectToDB();
		ServerFrameController aFrame = new ServerFrameController();

		aFrame.start(primaryStage);
	}

	/**
	 * Starts the server on the specified port.
	 *
	 * @param p The port number to start the server on.
	 */
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
			startSwitchToActiveTask();

		} catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}

	}

	/**
	 * Stops the server if it is running.
	 */
	public static void stopServer() {
		if (server != null) {
			try {
				server.close(); // Fully shuts down the server
				reportScheduler.shutdown();
				removeOrderScheduler.shutdown();
				EmailSmsScheduler.shutdown();
				removeFrozenAccount.shutdown();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns the current server instance.
	 *
	 * @return The running EchoServer instance.
	 */
	public static EchoServer getServer() {
		return server;
	}

	/**
	 * Called when the application is about to exit.
	 *
	 * @throws Exception If an error occurs during shutdown.
	 */
	@Override
	public void stop() throws Exception {
		// This method is called when the application is about to exit
		super.stop();
	}

	/**
	 * Starts a scheduled task that runs at the end of each month to process loan
	 * and status reports.
	 */
	private static void startEndOfMonthTask() {
		reportScheduler = Executors.newSingleThreadScheduledExecutor();

		Runnable task = () -> {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());

			// Check if it's the last day of the month at midnight
			System.out.println("End of month detected. Calling mysqlConnection method...");
			try {
				mysqlConnection.endOfMonthProcessingLoanReport();
				mysqlConnection.endOfMonthProcessingStatusReport();
			} catch (Exception e) {
				e.printStackTrace();
			}

		};

		reportScheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.MINUTES);
	}
	
	
	private static void startSwitchToActiveTask() {
		removeFrozenAccount = Executors.newSingleThreadScheduledExecutor();

		Runnable task = () -> {
			try {
				mysqlConnection.switchToActive();
			} catch (Exception e) {
				e.printStackTrace();
			}

		};

		removeFrozenAccount.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);
	}

	/**
	 * Starts a scheduled task to check for midnight and remove expired orders.
	 */
	private static void startMidnightTask() {
		removeOrderScheduler = Executors.newSingleThreadScheduledExecutor();
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

		removeOrderScheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.MINUTES);
	}

	/**
	 * Starts a scheduled task to check loan due dates and send SMS notifications.
	 */
	private static void startDailyDueDateCheckTask() {
		EmailSmsScheduler = Executors.newSingleThreadScheduledExecutor();

		Runnable task = () -> {
			try {
				// Call the method to check due dates and send SMS
				boolean smsSent = mysqlConnection.checkDueDateAndSendSMS();

				if (smsSent) {
					// Show a popup message indicating success
					Platform.runLater(() -> {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("SMS/Emal Notification");
						alert.setHeaderText(null);
						alert.setContentText("SMS/Email for loans due tomorrow were sent successfully.");
						alert.showAndWait();
					});
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		EmailSmsScheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.MINUTES);

	}

}
