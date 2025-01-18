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
	@FXML
	private BarChart<String, Number> chartTwo;

	@FXML
	private CategoryAxis xTwoAxis;

	@FXML
	private NumberAxis yTwoAxis;

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
		xOneAxis.setTickLabelRotation(45); // For the first chart
		xTwoAxis.setTickLabelRotation(45); // For the second chart

		xOneAxis.setTickLabelFont(Font.font("Arial", FontWeight.NORMAL, 10));
		xTwoAxis.setTickLabelFont(Font.font("Arial", FontWeight.NORMAL, 10));

		errorMonthOne.setText("");
		errorYearOne.setText("");
		errorMonthTwo.setText("");
		errorYearTwo.setText("");

	}

	// Handlers for buttons
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
			System.out.println("Error: Invalid month format.");
			return;
		}

		// Check if the year is in four-digit format
		if (!yearOne.matches("^[0-9]{4}$")) {
			errorYearOne.setText("Invalid year! Enter a four-digit year (e.g., 2021).");
			System.out.println("Error: Invalid year format.");
			return;
		}

		// Clear any previous error messages if inputs are valid
		errorMonthOne.setText("");
		errorYearOne.setText("");

		// Save the data in the collection
		chartOneData.put("month", monthOne);
		chartOneData.put("year", yearOne);

		Message sendToServer = new Message(MessageType.loanReport, chartOneData);
		ClientUI.chat.accept(sendToServer);

	}

	public void showLoanReportData(Map<String, String> loanReportMap) {

		Platform.runLater(() -> {
			// Clear existing data from the chart
			chartOne.getData().clear();

			// Create a new series for the chart
			XYChart.Series<String, Number> series = new XYChart.Series<>();
			series.setName("Late Returns");

			// Populate the series with data from the map
			for (Map.Entry<String, String> entry : loanReportMap.entrySet()) {
				String bookTitle = entry.getKey();
				String totalLateCountStr = entry.getValue();

				try {
					int totalLateCount = Integer.parseInt(totalLateCountStr);
					series.getData().add(new XYChart.Data<>(bookTitle, totalLateCount));
				} catch (NumberFormatException e) {
					System.err.println("Invalid totalLateCount for book: " + bookTitle);
				}
			}

			// Add the series to the chart
			chartOne.getData().add(series);
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

	public void showStatusReportData(Map<String, String> statusReportMap) {
		Platform.runLater(() -> {
			// Clear existing data from the chart
			chartTwo.getData().clear();

			// Create a new series for the chart
			XYChart.Series<String, Number> series = new XYChart.Series<>();
			series.setName("Status Report");

			// Populate the series with data from the map
			for (Map.Entry<String, String> entry : statusReportMap.entrySet()) {
				String status = entry.getKey();
				String count = entry.getValue();

				try {
					int totalCount = Integer.parseInt(count);
					XYChart.Data<String, Number> data = new XYChart.Data<>(status, totalCount);
					series.getData().add(data);

					// Add a listener to style the node when it becomes available
					data.nodeProperty().addListener((observable, oldValue, newValue) -> {
						if (newValue != null) {
							// Apply a custom style based on the status
							if ("active".equalsIgnoreCase(status)) {
								newValue.setStyle("-fx-bar-fill: #4caf50;"); // Green for active
							} else if ("frozen".equalsIgnoreCase(status)) {
								newValue.setStyle("-fx-bar-fill: #f44336;"); // Red for frozen
							}
						}
					});
				} catch (NumberFormatException e) {
					System.err.println("Invalid totalCount for status: " + status);
				}
			}

			// Add the series to the chart
			chartTwo.getData().add(series);
		});
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
