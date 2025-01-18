package gui.bounderies;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class TwoChartsController {

	// First chart and its input fields
	@FXML
	private BarChart<?, ?> chartOne;

	@FXML
	private TextField tfMonthOne;

	@FXML
	private TextField tfYearOne;

	@FXML
	private Button btnGenerateOne;

	// Second chart and its input fields
	@FXML
	private BarChart<?, ?> chartTwo;

	@FXML
	private TextField tfMonthTwo;

	@FXML
	private TextField tfYearTwo;

	@FXML
	private Button btnGenerateTwo;

	// Back button
	@FXML
	private Button btnBack;

	// Handlers for buttons
	@FXML
	private void btnGenerateOneClicked() {
		// To be implemented
	}

	@FXML
	private void btnGenerateTwoClicked() {
		// To be implemented
	}

	@FXML
	private void btnBackClicked() {
		// To be implemented
	}

}
