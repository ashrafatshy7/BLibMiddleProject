package gui.bounderies;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.ChatClient;
import application.ClientUI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
		// xOneAxis.setTickLabelRotation(35);
		xOneAxis.setPrefWidth(1200);
		chartOne.setMaxWidth(1079);

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
		Platform.runLater(() -> {
			// Clear existing data from the chart
			chartOne.getData().clear();

			// Predefined categories for the xAxis
			List<String> categories = Arrays.asList("Science Fiction", "Romance", "Political Satire",
					"Historical Fiction", "Gothic Fiction", "Fantasy", "Epic Poetry", "Dystopian", "Coming-of-Age",
					"Adventure");

			// Set categories on the xAxis
			xOneAxis.setCategories(FXCollections.observableArrayList(categories));

			// Create two series for the chart: Borrowed and Late Returns
			XYChart.Series<String, Number> borrowedSeries = new XYChart.Series<>();
			borrowedSeries.setName("Borrowed Books");

			XYChart.Series<String, Number> lateReturnsSeries = new XYChart.Series<>();
			lateReturnsSeries.setName("Late Returns");

			// Populate the series with data for each category
			for (String category : categories) {
				Map<String, String> data = loanReportMap.getOrDefault(category, new HashMap<>());

				// Add data for borrowed books
				int borrowed = Integer.parseInt(data.getOrDefault("borrowed", "0"));
				borrowedSeries.getData().add(new XYChart.Data<>(category, borrowed));

				// Add data for late returns
				int lateReturn = Integer.parseInt(data.getOrDefault("lateReturn", "0"));
				lateReturnsSeries.getData().add(new XYChart.Data<>(category, lateReturn));
			}

			// Add the series to the chart
			chartOne.getData().addAll(borrowedSeries, lateReturnsSeries);

			// Style the series after the nodes are created
			borrowedSeries.nodeProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null) {
					newValue.setStyle("-fx-bar-fill: orange;");
				}
			});

			lateReturnsSeries.nodeProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null) {
					newValue.setStyle("-fx-bar-fill: red;");
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