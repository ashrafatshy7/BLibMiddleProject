package gui.bounderies;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import application.ChatClient;
import application.ClientUI;
import enteties.Loan;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import message.Message;
import message.MessageType;

public class TwoChartsController {
	private ChatClient chatClient;

	// First chart and its input fields
	@FXML
	private BarChart<String, Number> chartOne;

	@FXML
	private CategoryAxis xOneAxis; // Change from NumberAxis to CategoryAxis

	@FXML
	private NumberAxis yOneAxis;

	@FXML
	private TextField tfMonthOne;

	@FXML
	private TextField tfYearOne;

	@FXML
	private Button btnGenerateOne;

	@FXML
	private Label errorMonthOne;
	@FXML
	private Label errorYearOne;

	// Second chart and its input fields
//	@FXML
//	private BarChart<String, Number> chartTwo;
	@FXML
	private PieChart pieChart;
//	@FXML
//	private CategoryAxis xTwoAxis;
//
//	@FXML
//	private NumberAxis yTwoAxis;

	@FXML
	private Label errorMonthTwo;
	@FXML
	private Label errorYearTwo;

	@FXML
	private TextField tfMonthTwo;

	@FXML
	private TextField tfYearTwo;

	@FXML
	private Button btnGenerateTwo;

	// Back button
	@FXML
	private Button btnBack;

	public TwoChartsController() {
		chatClient = ClientUI.chat.getClient();
	}

	public void setChatClient(ChatClient chatClient) {
		this.chatClient = chatClient;
		this.chatClient.setTwoChartsController(this);
	}

	@FXML
	private void initialize() {
		errorMonthOne.setText("");
		errorYearOne.setText("");
		errorMonthTwo.setText("");
		errorYearTwo.setText("");

		// Adjust spacing between categories and bars for chartOne
		chartOne.setCategoryGap(10); // Spacing between categories (e.g., months)
		chartOne.setBarGap(5); // Spacing between bars within a category

	}

	@FXML
	private void btnGenerateOneClicked() {
		Map<String, String> chartOneData = new HashMap<>();

		// Retrieve the values from the text fields
		String monthOne = tfMonthOne.getText().trim();
		String yearOne = tfYearOne.getText().trim();

		if (monthOne.isEmpty() || yearOne.isEmpty()) {
			errorMonthOne.setText("Month is required (format: MM).");
			errorYearOne.setText("Year is required (format: YYYY).");
			return;
		}

		// Check if the month is in two-digit format
		if (!monthOne.matches("^0[1-9]|1[0-2]$")) {
			errorMonthOne.setText("Invalid month! Enter a value between 01 and 12.");
			return;
		}

		// Check if the year is in four-digit format
		if (!yearOne.matches("^[0-9]{4}$")) {
			errorYearOne.setText("Invalid year! Enter a four-digit year (e.g., 2021).");
			return;
		}

		// Clear any previous error messages if inputs are valid
		errorMonthOne.setText("");
		errorYearOne.setText("");

		// Save the data in the collection
		chartOneData.put("month", monthOne);
		chartOneData.put("year", yearOne);

		// Send the data request to the server
		Message sendToServer = new Message(MessageType.loanReport, chartOneData);
		ClientUI.chat.accept(sendToServer);
	}

	public void showLoanReportData(Map<String, Map<String, String>> loanReportMap) {
//		Platform.runLater(() -> {
//			// Clear existing data from the chart
//			chartOne.getData().clear();
//
//			// Create two series for the chart: Borrowed and Late Returns
//			XYChart.Series<String, Number> borrowedSeries = new XYChart.Series<>();
//			borrowedSeries.setName("Borrowed Books");
//
//			XYChart.Series<String, Number> lateReturnsSeries = new XYChart.Series<>();
//			lateReturnsSeries.setName("Late Returns");
//
//			// Populate the series with data for each week
//			for (Map.Entry<String, Map<String, String>> weekEntry : loanReportMap.entrySet()) {
//				String week = weekEntry.getKey(); // Week label (e.g., "Week 1", "Week 2")
//				Map<String, String> data = weekEntry.getValue();
//
//				// Parse borrowed and lateReturn as numbers and add to the chart
//				int borrowed = Integer.parseInt(data.getOrDefault("borrowed", "0"));
//				int lateReturn = Integer.parseInt(data.getOrDefault("lateReturn", "0"));
//
//				borrowedSeries.getData().add(new XYChart.Data<>(week, borrowed));
//				lateReturnsSeries.getData().add(new XYChart.Data<>(week, lateReturn));
//			}
//
//			// Add the series to the chart
//			chartOne.getData().addAll(borrowedSeries, lateReturnsSeries);
//
//			// Apply custom colors and adjust bar spacing
//			chartOne.setCategoryGap(10); // Adjust spacing between categories
//			chartOne.setBarGap(5); // Adjust spacing between bars within a category
//
//			// Assign custom CSS styles
//			borrowedSeries.getNode().setStyle("-fx-bar-fill: orange;"); // Set borrowed series to orange
//			lateReturnsSeries.getNode().setStyle("-fx-bar-fill: red;"); // Set late returns series to red
//		});

		Platform.runLater(() -> {
			// Clear existing data from the chart
			chartOne.getData().clear();

			// Create two series for the chart: Borrowed and Late Returns
			XYChart.Series<String, Number> borrowedSeries = new XYChart.Series<>();
			borrowedSeries.setName("Borrowed Books");

			XYChart.Series<String, Number> lateReturnsSeries = new XYChart.Series<>();
			lateReturnsSeries.setName("Late Returns");

			// Populate the series with data for each week
			for (Map.Entry<String, Map<String, String>> weekEntry : loanReportMap.entrySet()) {
				String week = weekEntry.getKey(); // Week label (e.g., "Week 1", "Week 2")
				Map<String, String> data = weekEntry.getValue();

				// Add data for borrowed books
				borrowedSeries.getData()
						.add(new XYChart.Data<>(week, Integer.parseInt(data.getOrDefault("borrowed", "0"))));

				// Add data for late returns
				lateReturnsSeries.getData()
						.add(new XYChart.Data<>(week, Integer.parseInt(data.getOrDefault("lateReturn", "0"))));
			}

			// Add the series to the chart
			chartOne.getData().addAll(borrowedSeries, lateReturnsSeries);

			// Add styles after the series nodes are created
			borrowedSeries.nodeProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null) {
					newValue.setStyle("-fx-bar-fill: orange;"); // Orange color for borrowed
				}
			});

			lateReturnsSeries.nodeProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null) {
					newValue.setStyle("-fx-bar-fill: red;"); // Red color for late returns
				}
			});
		});

	}

	@FXML
	private void btnGenerateTwoClicked() {
		Map<String, String> chartOneData = new HashMap<>();

		// Retrieve the values from the text fields
		String monthOne = tfMonthTwo.getText().trim();
		String yearOne = tfYearTwo.getText().trim();

		if (monthOne.isEmpty() || yearOne.isEmpty()) {
			errorMonthTwo.setText("Month is required (format: MM).");
			errorYearTwo.setText("Year is required (format: YYYY).");
			return;
		}

		// Check if the month is in two-digit format
		if (!monthOne.matches("^0[1-9]|1[0-2]$")) {
			errorMonthTwo.setText("Invalid month! Enter a value between 01 and 12.");
			System.out.println("Error: Invalid month format.");
			return;
		}

		// Check if the year is in four-digit format
		if (!yearOne.matches("^[0-9]{4}$")) {
			errorYearTwo.setText("Invalid year! Enter a four-digit year (e.g., 2021).");
			errorMonthTwo.setText("");
			System.out.println("Error: Invalid year format.");
			return;
		}

		// Clear any previous error messages if inputs are valid
		errorMonthTwo.setText("");
		errorYearTwo.setText("");

		// Save the data in the collection
		chartOneData.put("month", monthOne);
		chartOneData.put("year", yearOne);

		Message sendToServer = new Message(MessageType.StatusReport, chartOneData);
		ClientUI.chat.accept(sendToServer);
	}

//	public void showStatusReportData(Map<String, String> statusReportMap) {
//
//		Platform.runLater(() -> {
//			// Clear existing data from the chart
//			chartTwo.getData().clear();
//
//			// Create a new series for the chart
//			XYChart.Series<String, Number> series = new XYChart.Series<>();
//			series.setName("Status Report");
//
//			// Populate the series with data from the map
//			for (Map.Entry<String, String> entry : statusReportMap.entrySet()) {
//				String status = entry.getKey();
//				String count = entry.getValue();
//
//				try {
//					int totalCount = Integer.parseInt(count);
//					XYChart.Data<String, Number> data = new XYChart.Data<>(status, totalCount);
//					series.getData().add(data);
//
//					// Add a listener to style the node when it becomes available
//					data.nodeProperty().addListener((observable, oldValue, newValue) -> {
//						if (newValue != null) {
//							// Apply a custom style based on the status
//							if ("active".equalsIgnoreCase(status)) {
//								newValue.setStyle("-fx-bar-fill: #4caf50;"); // Green for active
//							} else if ("frozen".equalsIgnoreCase(status)) {
//								newValue.setStyle("-fx-bar-fill: #f44336;"); // Red for frozen
//							}
//						}
//					});
//				} catch (NumberFormatException e) {
//					System.err.println("Invalid totalCount for status: " + status);
//				}
//			}
//
//			// Add the series to the chart
//			chartTwo.getData().add(series);
//		});
//	}

//	public void showStatusReportData(Map<String, String> statusReportMap) {
//		// Clear any existing data in the pie chart
//		pieChart.getData().clear();
//
//		// Extract the data from the map
//		String activeAccounts = statusReportMap.getOrDefault("active", "0");
//		String frozenAccounts = statusReportMap.getOrDefault("frozen", "0");
//
//		// Convert the values to integers
//		int activeCount = Integer.parseInt(activeAccounts);
//		int frozenCount = Integer.parseInt(frozenAccounts);
//
//		// Add the data to the pie chart
//		PieChart.Data activeData = new PieChart.Data("Active Accounts", activeCount);
//		PieChart.Data frozenData = new PieChart.Data("Frozen Accounts", frozenCount);
//
//		pieChart.getData().addAll(activeData, frozenData);
//
//		// Set chart styling (optional)
//		pieChart.setTitle("Account Status Overview");
//		pieChart.setLegendVisible(true);
//		pieChart.setLabelsVisible(true);
//	}

	public void showStatusReportData(Map<String, String> statusReportMap) {
		// Clear existing data
		pieChart.getData().clear();

		// Extract data from the map
		String activeAccounts = statusReportMap.getOrDefault("active", "0");
		String frozenAccounts = statusReportMap.getOrDefault("frozen", "0");

		// Convert to integers
		int activeCount = Integer.parseInt(activeAccounts);
		int frozenCount = Integer.parseInt(frozenAccounts);
		int totalAccounts = activeCount + frozenCount;

		// Calculate percentages
		double activePercentage = totalAccounts > 0 ? (activeCount * 100.0 / totalAccounts) : 0;
		double frozenPercentage = totalAccounts > 0 ? (frozenCount * 100.0 / totalAccounts) : 0;

		// Add data to the chart
		pieChart.getData()
				.addAll(new PieChart.Data(
						"Active Accounts (" + activeCount + " / " + String.format("%.1f", activePercentage) + "%)",
						activeCount),
						new PieChart.Data("Frozen Accounts (" + frozenCount + " / "
								+ String.format("%.1f", frozenPercentage) + "%)", frozenCount));

		// Update chart styling
		pieChart.setTitle("Account Status Overview");
		pieChart.setLegendVisible(true);
		pieChart.setLabelsVisible(true);
	}

	@FXML
	private void btnBackClicked(ActionEvent event) {
		Stage primaryStage = new Stage();
		((Node) event.getSource()).getScene().getWindow().hide();
		HomeFrameController homeFrameController = new HomeFrameController();
		try {
			homeFrameController.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/TwoCharts.fxml"));
			Parent root = loader.load();
			TwoChartsController controller = loader.getController();
			if (this.chatClient != null) {
				controller.setChatClient(this.chatClient);
			}

			Scene scene = new Scene(root);
			primaryStage.setTitle("charts");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}