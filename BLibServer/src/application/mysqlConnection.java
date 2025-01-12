package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.mysql.cj.protocol.Message;

import enteties.Loan;
import message.MessageType;

public class mysqlConnection {

	private static Connection conn;

	public static void connectToDB() {
		System.out.println("in connectToDB");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			System.out.println("Driver definition succeed");
		} catch (Exception ex) {
			/* handle the error */
			System.out.println("Driver definition failed");
		}

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/middleproject?serverTimezone=IST", "root",
					"Aa123456");
			System.out.println("SQL connection succeed");

		} catch (SQLException ex) {/* handle any errors */
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

	// subscribers
	public static ArrayList<Map<String, Object>> getAllValues(Object msg) {
		Statement stmt;
		ResultSet resultSet;
		String tableName = (String) msg;
		ArrayList<Map<String, Object>> tableData = new ArrayList<>();

		try {
			String query = "SELECT * FROM " + tableName;
			stmt = conn.createStatement();
			resultSet = stmt.executeQuery(query);

			while (resultSet.next()) {
				Map<String, Object> row = new HashMap<>();
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();

				for (int i = 1; i <= columnCount; i++) {
					String columnName = metaData.getColumnName(i);
					Object value = resultSet.getObject(i);
					row.put(columnName, value);
				}
				tableData.add(row);
			}

			resultSet.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tableData;
	}

	// stmt.executeUpdate("UPDATE subscriber SET phoneNumber='333333',
	// email='kian@gmail.com' WHERE subscriber_id='211613708'");
	public static void updateValues(Object msg) {
		PreparedStatement pstmt = null;
		ArrayList<String> data = (ArrayList<String>) msg;
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE " + data.get(0) + " SET ");
		for (int i = 2; i < data.size(); i += 2) {
			sb.append(data.get(i) + "='" + data.get(i + 1) + "'");
			if (i != data.size() - 2)
				sb.append(", ");
			else
				sb.append(" ");
		}
		if (data.get(0).equals("subscribers"))
			sb.append("WHERE subscriber_id='" + data.get(1) + "'");
		try {
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void updateSubscriberDetails(Object msg) {
		PreparedStatement pstmt = null;
		try {
			ArrayList<String> userData = (ArrayList<String>) msg;
			String subscriberId = userData.get(0);

			// List to hold individual column assignments
			List<String> assignments = new ArrayList<>();

			// Iterate over the key-value pairs starting from index 1
			for (int i = 1; i < userData.size(); i += 2) {
				String field = userData.get(i);
				String value = userData.get(i + 1);
				String dbField = null;

				switch (field) {
				case "phoneNumber":
					dbField = "subscriber_phone_number";
					break;
				case "email":
					dbField = "subscriber_email";
					break;
				default:
					// Handle unknown fields or skip
					continue;
				}

				assignments.add(dbField + " = ?");
			}

			String setClause = String.join(", ", assignments);

			String query = "UPDATE subscribers SET " + setClause + " WHERE subscriber_id = ?";
			pstmt = conn.prepareStatement(query);

			int paramIndex = 1;
			for (int i = 1; i < userData.size(); i += 2) {
				String field = userData.get(i);
				String value = userData.get(i + 1);

				switch (field) {
				case "phoneNumber":
				case "email":
					pstmt.setString(paramIndex++, value);
					break;
				}
			}

			// Set the subscriber_id parameter
			pstmt.setLong(paramIndex, Long.parseLong(subscriberId));

			// Execute the update
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close the PreparedStatement
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void saveUserToDB(Object msg) {
		Statement stmt;
		try {
			ArrayList<String> userData = (ArrayList<String>) msg;
			String userName = userData.get(0);
			String id = userData.get(1);
			String department = userData.get(2);
			String tel = userData.get(3);
			String query = "INSERT INTO users (UserName, ID, Department, Tel) VALUES ('" + userName + "', " + id + ", '"
					+ department + "', '" + tel + "')";

			stmt = conn.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Map<String, Object> getCardDetailsIfExists(String cardNum) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Map<String, Object> result = new HashMap<>(); // Contains the existence check, card details, and table data

		try {
			// Step 1: Check if the cardNum exists
			String checkQuery = "SELECT COUNT(*) FROM users WHERE id = ?";
			preparedStatement = conn.prepareStatement(checkQuery);
			preparedStatement.setString(1, cardNum);

			resultSet = preparedStatement.executeQuery();
			boolean exists = false;

			if (resultSet.next()) {
				exists = resultSet.getInt(1) > 0; // Check if the cardNum exists
			}

			result.put("exists", exists); // Add the existence check to the result

			resultSet.close(); // Close previous resultSet
			preparedStatement.close(); // Close previous preparedStatement

			// Step 2: If cardNum exists, fetch the card details
			if (exists) {
				String detailsQuery = "SELECT id, userName, phoneNumber, email FROM users WHERE id = ?";

				preparedStatement = conn.prepareStatement(detailsQuery);
				preparedStatement.setString(1, cardNum);
				result.put("cardNum", cardNum);

				resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {
					ResultSetMetaData metaData = resultSet.getMetaData();
					int columnCount = metaData.getColumnCount();

					// Fetch all columns for the cardNum
					for (int i = 2; i <= columnCount; i++) {
						String columnName = metaData.getColumnName(i);
						Object value = resultSet.getObject(i);
						result.put(columnName, value);
					}

					System.out.println("shown details : " + result.toString());
				}

				resultSet.close();
				preparedStatement.close();

				// Step 3: Fetch loan history for the cardNum
				String loanHistoryQuery = "SELECT bookTitle, returnDate, borrowDate FROM loan WHERE id = ?";
				preparedStatement = conn.prepareStatement(loanHistoryQuery);
				preparedStatement.setString(1, cardNum);

				resultSet = preparedStatement.executeQuery();

				List<Map<String, Object>> loanHistory = new ArrayList<>();

				while (resultSet.next()) {
					Map<String, Object> row = new HashMap<>();
					row.put("bookTitle", resultSet.getString("bookTitle"));
					row.put("returnDate", resultSet.getDate("returnDate"));
					row.put("borrowDate", resultSet.getDate("borrowDate"));
					loanHistory.add(row);
				}

				result.put("loanHistory", loanHistory);

				resultSet.close();
				preparedStatement.close();

				// Step 4: Fetch issues history for the cardNum
				String issuesHistoryQuery = "SELECT issueType, issueDate, issueDescription FROM issuehistory WHERE cardNum = ?";
				preparedStatement = conn.prepareStatement(issuesHistoryQuery);
				preparedStatement.setString(1, cardNum);

				resultSet = preparedStatement.executeQuery();

				List<Map<String, Object>> issuesHistory = new ArrayList<>();

				while (resultSet.next()) {
					Map<String, Object> row = new HashMap<>();
					row.put("issueType", resultSet.getString("issueType"));
					row.put("issueDate", resultSet.getDate("issueDate"));
					row.put("issueDescription", resultSet.getString("issueDescription"));
					issuesHistory.add(row);
				}

				result.put("issuesHistory", issuesHistory);

				System.out.println("after issue history : " + result.toString());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close resources to prevent memory leaks
			try {
				if (resultSet != null)
					resultSet.close();
				if (preparedStatement != null)
					preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static boolean updateSubscriberEmailAndPhoneNumber(String email, String phoneNumber, String cardNum) {
		String sql = "UPDATE users SET email = ?, phoneNumber = ? WHERE id = ?";
		PreparedStatement stmt = null;

		try {
			// Prepare the SQL statement
			stmt = conn.prepareStatement(sql);

			// Bind parameters
			stmt.setString(1, email);
			stmt.setString(2, phoneNumber);
			stmt.setString(3, cardNum);

			// Execute the update
			int rowsUpdated = stmt.executeUpdate();

			// Return true if at least one row was updated, otherwise false
			return rowsUpdated > 0;

		} catch (SQLException e) {
			// Log the exception and return false
			e.printStackTrace();
			return false;

		} finally {
			// Close the statement
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean updateReturnDate(Object messageData) {
		// Cast the messageData to the expected Map format
		Map<String, String> temp = (Map<String, String>) messageData;

		// Check if the map is empty
		if (temp == null || temp.isEmpty()) {
			return false;
		}

		// Query to update the return date based on the book title
		String query = "UPDATE loan SET returnDate = ? WHERE bookTitle = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			// Iterate through the map entries
			for (Map.Entry<String, String> entry : temp.entrySet()) {
				System.out.println("book title and return date : " + entry.toString());
				String bookTitle = entry.getKey(); // Get the book title (key)
				String returnDate = entry.getValue(); // Get the return date (value)

				// Set parameters in the prepared statement
				stmt.setString(1, returnDate);
				stmt.setString(2, bookTitle);

				// Execute the update
				int rowsUpdated = stmt.executeUpdate();
				if (rowsUpdated == 0) {
					System.err.println("Failed to update book: " + bookTitle);
				} else {
					System.out.println("Successfully updated book: " + bookTitle);
				}
			}

			return true; // All updates completed successfully
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Method to retrieve loans for books with return dates after two days
	public static ArrayList<Loan> getExtendedBooks(Object number) {
		String cardNum = (String) number;
		ArrayList<Loan> loanList = new ArrayList<>();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String query = "SELECT bookTitle, returnDate FROM loan WHERE id = ? AND returnDate > CURDATE() + INTERVAL 2 DAY";

		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, cardNum); // Set the cardNum parameter

			// Execute the query
			resultSet = preparedStatement.executeQuery();

			// Process the result set
			while (resultSet.next()) {
				String bookTitle = resultSet.getString("bookTitle");
				String returnDate = resultSet.getDate("returnDate").toString();
				Loan loan = new Loan(bookTitle, returnDate);
				loanList.add(loan);
			}

			System.out.println("Loan list = " + loanList.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close the ResultSet and PreparedStatement to prevent resource leaks
			try {
				if (resultSet != null)
					resultSet.close();
				if (preparedStatement != null)
					preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return loanList;
	}

	public static boolean updateExtensionReturnDate(Object messageData) {
		// Cast messageData to Map<String, String>
		Map<String, String> data = (Map<String, String>) messageData;

		// Get the values from the map
		String cardNum = data.get("cardNum");
		String bookTitle = data.get("bookTitle");
		String returnDate = data.get("returnDate");

		// SQL query to update the returnDate in the loan table
		String query = "UPDATE loan SET returnDate = ? WHERE cardNum = ? AND bookTitle = ?";

		try {
			// Prepare the statement
			PreparedStatement preparedStatement = conn.prepareStatement(query);

			// Set the parameters for the query
			preparedStatement.setString(1, returnDate); // Set returnDate
			preparedStatement.setString(2, cardNum); // Set cardNum
			preparedStatement.setString(3, bookTitle); // Set bookTitle

			// Execute the update query
			int rowsUpdated = preparedStatement.executeUpdate();

			// If rowsUpdated > 0, the update was successful
			return rowsUpdated > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
