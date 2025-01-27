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
import javafx.geometry.Pos;
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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import message.Message;
import message.MessageType;


/**
 * Controller for handling the display of two charts: loan report and status report.
 */
public class TwoChartsController {
	 /** Chat client instance. */
    private ChatClient chatClient;

    /** Bar chart for displaying loan report data. */
    @FXML
    private BarChart<String, Number> chartOne;

    /** Category axis for the loan report x-axis. */
    @FXML
    private CategoryAxis xOneAxis;

    /** Number axis for the loan report y-axis. */
    @FXML
    private NumberAxis yOneAxis;

    /** TextField for entering the month of the loan report. */
    @FXML
    private TextField tfMonthOne;

    /** TextField for entering the year of the loan report. */
    @FXML
    private TextField tfYearOne;

    /** Button to generate the loan report. */
    @FXML
    private Button btnGenerateOne;

    /** Label to display month input error for the loan report. */
    @FXML
    private Label errorMonthOne;

    /** Label to display year input error for the loan report. */
    @FXML
    private Label errorYearOne;

    /** Pie chart for displaying account status report. */
    @FXML
    private PieChart pieChart;

    /** Label to display month input error for the status report. */
    @FXML
    private Label errorMonthTwo;

    /** Label to display year input error for the status report. */
    @FXML
    private Label errorYearTwo;

    /** TextField for entering the month of the status report. */
    @FXML
    private TextField tfMonthTwo;

    /** TextField for entering the year of the status report. */
    @FXML
    private TextField tfYearTwo;

    /** Button to generate the status report. */
    @FXML
    private Button btnGenerateTwo;

    /** Button to navigate back to the home screen. */
    @FXML
    private Button btnBack;

    
    /**
     * Default constructor that initializes the chat client.
     */
	public TwoChartsController() {
		chatClient = ClientUI.chat.getClient();
	}

	
	 /**
     * Sets the chat client instance.
     * @param chatClient The chat client to set.
     */
	public void setChatClient(ChatClient chatClient) {
		this.chatClient = chatClient;
		this.chatClient.setTwoChartsController(this);
	}

	
	/**
     * Initializes the UI components and default values.
     */
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

	/**
     * Handles the event of generating the loan report.
     */
	@FXML
	private void btnGenerateOneClicked() {
		Map<String, String> chartOneData = new HashMap<>();

		// Retrieve the values from the text fields
		String monthOne = tfMonthOne.getText().trim();
		String yearOne = tfYearOne.getText().trim();

		if (monthOne.isEmpty() || yearOne.isEmpty()) {
			errorMonthOne.setText("Month is required MM");
			errorYearOne.setText("Year is required YYYY");
			return;
		}

		// Check if the month is in two-digit format
		if (!monthOne.matches("^0[1-9]|1[0-2]$")) {
			errorMonthOne.setText("value between 01 and 12");
			return;
		}

		// Check if the year is in four-digit format
		if (!yearOne.matches("^[0-9]{4}$")) {
			errorYearOne.setText("Enter a 4 digit year");
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

	
	 /**
     * Displays loan report data in the bar chart.
     * @param loanReportMap The data map containing loan report details.
     */
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

				// Get data for borrowed and lateReturn
				int borrowed = Integer.parseInt(data.getOrDefault("borrowed", "0"));
				int lateReturn = Integer.parseInt(data.getOrDefault("lateReturn", "0"));

				// Add data to the series
				XYChart.Data<String, Number> borrowedData = new XYChart.Data<>(category, borrowed);
				borrowedSeries.getData().add(borrowedData);

				XYChart.Data<String, Number> lateReturnData = new XYChart.Data<>(category, lateReturn);
				lateReturnsSeries.getData().add(lateReturnData);

				// Add a label to display the number of borrowed books above the column
				borrowedData.nodeProperty().addListener((observable, oldValue, newValue) -> {
					if (newValue != null) {
						Label borrowedLabel = new Label(String.valueOf(borrowed));
						borrowedLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: black;");

						// Position the label above the borrowed column
						StackPane stackPane = (StackPane) newValue;
						stackPane.getChildren().add(borrowedLabel);
						StackPane.setAlignment(borrowedLabel, Pos.TOP_CENTER);
						borrowedLabel.setTranslateY(-10); // Adjust position slightly above the bar
					}
				});

				// Calculate the percentage of late returns
				if (borrowed > 0) {
					double percentage = (double) lateReturn / borrowed * 100;

					// Add a label to display the percentage above the late returns column
					lateReturnData.nodeProperty().addListener((observable, oldValue, newValue) -> {
						if (newValue != null) {
							Label lateReturnLabel = new Label(String.format("%.1f%%", percentage));
							lateReturnLabel
									.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: black;");

							// Position the label above the late returns column
							StackPane stackPane = (StackPane) newValue;
							stackPane.getChildren().add(lateReturnLabel);
							StackPane.setAlignment(lateReturnLabel, Pos.TOP_CENTER);
							lateReturnLabel.setTranslateY(-10); // Adjust position slightly above the bar
						}
					});
				}
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


	
	 /**
     * Handles the event of generating the status report.
     */
	@FXML
	private void btnGenerateTwoClicked() {
		Map<String, String> chartOneData = new HashMap<>();

		// Retrieve the values from the text fields
		String monthOne = tfMonthTwo.getText().trim();
		String yearOne = tfYearTwo.getText().trim();

		if (monthOne.isEmpty() || yearOne.isEmpty()) {
			errorMonthTwo.setText("Month is required: MM");
			errorYearTwo.setText("Year is required: YYYY");
			return;
		}

		// Check if the month is in two-digit format
		if (!monthOne.matches("^0[1-9]|1[0-2]$")) {
			errorMonthTwo.setText("value between 01 and 12.");
			return;
		}

		// Check if the year is in four-digit format
		if (!yearOne.matches("^[0-9]{4}$")) {
			errorYearTwo.setText("Enter a 4 digit year");
			errorMonthTwo.setText("");
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

	
	 /**
     * Displays status report data in the pie chart.
     * @param statusReportMap The data map containing status report details.
     */
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

	
	/**
     * Handles the event of navigating back to the home screen.
     * @param event The action event.
     */
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

	
	/**
     * Starts the charts frame.
     * @param primaryStage The primary stage.
     * @throws Exception If an error occurs while loading the frame.
     */
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