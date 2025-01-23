package application;

import java.sql.Blob;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import enteties.Book;
import enteties.Issue;
import enteties.Subscriber;
import enteties.User;
import enteties.Librarian;
import enteties.Loan;
import enteties.Order;

public class mysqlConnection {

	private static Connection conn;

	public static void connectToDB() {
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

	public static ArrayList<Book> getAllBooks() {
		Statement stmt = null;
		ResultSet resultSet = null;
		ArrayList<Book> books = new ArrayList<>();

		try {
			// Retrieve all books
			String query = "SELECT * FROM books";
			stmt = conn.createStatement();
			resultSet = stmt.executeQuery(query);

			// Map to hold books by barcode
			Map<String, Book> bookMap = new HashMap<>();

			while (resultSet.next()) {
				String barcode = resultSet.getString("barcode");
				String title = resultSet.getString("title");
				String author = resultSet.getString("author");
				String category = resultSet.getString("category");
				String description = resultSet.getString("description");
				int availableCopies = resultSet.getInt("availableCopies");

				byte[] imageBytes = null;
				Blob imageBlob = resultSet.getBlob("image");
				if (imageBlob != null) {
					imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
				}

				// Create a new Book object
				Book book = new Book(barcode, title, author, category, description, availableCopies, imageBytes);

				books.add(book);
				bookMap.put(barcode, book);
			}

			// Retrieve all shelves
			String shelfQuery = "SELECT barcode, shelf FROM bookshelf";
			ResultSet shelfResultSet = stmt.executeQuery(shelfQuery);

			while (shelfResultSet.next()) {
				String barcode = shelfResultSet.getString("barcode");
				String shelf = shelfResultSet.getString("shelf");
				Book book = bookMap.get(barcode);
				if (book != null) {
					book.getShelfs().add(shelf);
				}
			}

			shelfResultSet.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return books;
	}

	public static boolean orderBook(Object details) {
		boolean ordered = false;
		ArrayList<String> det = (ArrayList<String>) details;
		String barcode = det.get(0);
		String id = det.get(1);

		PreparedStatement pstmt = null;

		try {
			String query = "INSERT INTO orders (id, requestDate, barcode) VALUES (?, ?, ?)";
			pstmt = conn.prepareStatement(query);

			// Format: yyyy-MM-dd HH:mm:ss
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String requestDateString = formatter.format(new Date()); // Now

			pstmt.setString(1, id);
			pstmt.setString(2, requestDateString); // store as string
			pstmt.setString(3, barcode);

			pstmt.executeUpdate();
			// If you're using manual commit mode, you need: conn.commit();
			ordered = true;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return ordered;
	}

	public static boolean checkOrderedBook(Object details) {
		ArrayList<String> det = (ArrayList<String>) details;
		String barcode = det.get(0);
		String id = det.get(1);

		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		boolean exists = false;

		try {
			String query = "SELECT 1 FROM orders WHERE barcode = ? AND id = ? LIMIT 1";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, barcode);
			pstmt.setString(2, id);
			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {
				exists = true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}

		return exists;
	}

	public static ArrayList<Book> getTop5LoanedBooks() {
		Statement stmt = null;
		ResultSet resultSet = null;
		ArrayList<Book> books = new ArrayList<>();

		try {
			// Retrieve top 5 loaned books
			String query = "" + "SELECT b.*\n" + "FROM (\n" + "    SELECT lb.barcode,\n"
					+ "           lb.loanedCount,\n" + "           @rownum := @rownum + 1 AS rn\n"
					+ "    FROM loanedbooks lb\n" + "    CROSS JOIN (SELECT @rownum := 0) init\n"
					+ "    ORDER BY lb.loanedCount DESC, lb.barcode ASC\n" + ") AS lb_ranked\n"
					+ "JOIN books b ON b.barcode = lb_ranked.barcode\n" + "WHERE lb_ranked.rn <= 5\n"
					+ "ORDER BY lb_ranked.loanedCount DESC, lb_ranked.barcode ASC;";

			stmt = conn.createStatement();
			resultSet = stmt.executeQuery(query);

			// Map to hold books by barcode
			Map<String, Book> bookMap = new HashMap<>();

			while (resultSet.next()) {
				// Extract data from the current row
				String barcode = resultSet.getString("barcode");
				String title = resultSet.getString("title");
				String author = resultSet.getString("author");
				String category = resultSet.getString("category");
				String description = resultSet.getString("description");
				int availableCopies = resultSet.getInt("availableCopies");

				// Assuming the image is stored in a column named "image" as a BLOB
				byte[] imageBytes = null;
				Blob imageBlob = resultSet.getBlob("image");

				if (imageBlob != null) {
					imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
				}

				// Create a new Book object
				Book book = new Book(barcode, title, author, category, description, availableCopies, imageBytes);

				books.add(book);
				bookMap.put(barcode, book);
			}

			// Retrieve shelves for the top 5 books
			if (!bookMap.isEmpty()) {
				StringBuilder barcodeList = new StringBuilder();
				for (String barcode : bookMap.keySet()) {
					barcodeList.append("'").append(barcode).append("',");
				}
				// Remove the trailing comma
				barcodeList.setLength(barcodeList.length() - 1);

				String shelfQuery = "SELECT barcode, shelf FROM bookshelf WHERE barcode IN (" + barcodeList.toString()
						+ ")";
				ResultSet shelfResultSet = stmt.executeQuery(shelfQuery);

				while (shelfResultSet.next()) {
					String barcode = shelfResultSet.getString("barcode");
					String shelf = shelfResultSet.getString("shelf");
					Book book = bookMap.get(barcode);
					if (book != null) {
						book.getShelfs().add(shelf);
					}
				}

				shelfResultSet.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close resources in reverse order of their opening
			try {
				if (resultSet != null)
					resultSet.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}

		return books;
	}

	public static String getEarliestReturnDate(Object barcode) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String earliestDate = null;
		Date exactDate = null;
		String barcodeStr = barcode.toString();
		System.out.println("barcode: " + barcodeStr);

		try {
			String query = "SELECT dueDate FROM loan WHERE barcode = ? ORDER BY dueDate ASC LIMIT 1";

			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, barcodeStr);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				Date date = rs.getDate("dueDate");

				if (date != null) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					calendar.add(Calendar.DATE, 1);
					exactDate = calendar.getTime();
					SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
					earliestDate = formatter.format(exactDate); // Format Date to String as DD-MM-YYYY
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close the ResultSet and PreparedStatement to free resources
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return earliestDate;
	}

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

	// String readCard, String email, String password, String username, String phone
	public static boolean saveNewSubscriber(Object msg) {
		ArrayList<Object> subscriber = (ArrayList<Object>) msg;
		Subscriber sub = (Subscriber) subscriber.get(0);
		String password = (String) subscriber.get(1);
		String checkQuery = "SELECT COUNT(*) FROM users WHERE id = ? OR email = ?";
		try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
			checkStmt.setString(1, sub.getID());
			checkStmt.setString(2, sub.getEmail());
			ResultSet rs = checkStmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				System.out.println("Duplicate ReadCard or Email found in database.");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		// הוספת מנוי לטבלה
		String subscriberQuery = "INSERT INTO users (id, email, password, username, phoneNumber, type, status) "
				+ "VALUES (?, ?, ?, ?, ?, 'student', 'active')";

		try (PreparedStatement subscriberStmt = conn.prepareStatement(subscriberQuery)) {
			// הגדרת הפרמטרים לפי הסדר הנכון:
			subscriberStmt.setString(1, sub.getID());
			subscriberStmt.setString(2, sub.getEmail());
			subscriberStmt.setString(3, password);
			subscriberStmt.setString(4, sub.getName());
			subscriberStmt.setString(5, sub.getPhoneNumber());

			subscriberStmt.executeUpdate();
			return true; // הצלחה
		} catch (SQLException e) {
			e.printStackTrace();
			return false; // שגיאה
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
				}

				resultSet.close();
				preparedStatement.close();

				// Step 3: Fetch loan history for the cardNum
				String loanHistoryQuery = "SELECT bookTitle, dueDate, borrowDate FROM loan WHERE id = ?";
				preparedStatement = conn.prepareStatement(loanHistoryQuery);
				preparedStatement.setString(1, cardNum);

				resultSet = preparedStatement.executeQuery();

				List<Map<String, Object>> loanHistory = new ArrayList<>();

				while (resultSet.next()) {
					Map<String, Object> row = new HashMap<>();
					row.put("bookTitle", resultSet.getString("bookTitle"));
					row.put("returnDate", resultSet.getDate("dueDate"));
					row.put("borrowDate", resultSet.getDate("borrowDate"));
					loanHistory.add(row);
				}

				result.put("loanHistory", loanHistory);

				resultSet.close();
				preparedStatement.close();

				// Step 4: Fetch issues history for the cardNum
				String issuesHistoryQuery = "SELECT issueType, issueDate, barcode FROM issuehistory WHERE id = ?";
				preparedStatement = conn.prepareStatement(issuesHistoryQuery);
				preparedStatement.setString(1, cardNum);

				resultSet = preparedStatement.executeQuery();

				List<Map<String, Object>> issuesHistory = new ArrayList<>();

				while (resultSet.next()) {
					Map<String, Object> row = new HashMap<>();
					row.put("issueType", resultSet.getString("issueType"));
					row.put("issueDate", resultSet.getDate("issueDate"));
					row.put("barcode", resultSet.getString("barcode"));
					issuesHistory.add(row);
				}

				result.put("issuesHistory", issuesHistory);
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
		System.out.println("resultresultresultresult = " + result.toString());
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
		String query = "UPDATE loan SET dueDate = ? WHERE bookTitle = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			// Iterate through the map entries
			for (Map.Entry<String, String> entry : temp.entrySet()) {
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

	// Method to retrieve loans for books with return dates after 1 week
	public static Map<String, String> getExtendedBooks(Object number) {
		String cardNum = (String) number;
		Map<String, String> loanMap = new LinkedHashMap<>(); // Using LinkedHashMap to maintain insertion order
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String query = "SELECT bookTitle, dueDate FROM loan WHERE id = ? AND dueDate BETWEEN CURDATE() AND CURDATE() + INTERVAL 7 DAY ";

		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, cardNum); // Set the cardNum parameter

			// Execute the query
			resultSet = preparedStatement.executeQuery();

			// Process the result set
			while (resultSet.next()) {
				String bookTitle = resultSet.getString("bookTitle");
				String returnDate = resultSet.getDate("dueDate").toString();

				// Store in the map
				loanMap.put(bookTitle, returnDate);
			}
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

		return loanMap;
	}

	public static boolean updateExtensionReturnDate(Object messageData) {
		Map<String, String> data = (Map<String, String>) messageData;

		String cardNum = data.get("cardNum");
		String bookTitle = data.get("bookTitle");
		String returnDate = data.get("dueDate");

		try {
			// SQL query to check if there is an order for the same book
			String checkOrderQuery = "SELECT COUNT(*) FROM orders WHERE bookTitle = ?";
			PreparedStatement checkOrderStatement = conn.prepareStatement(checkOrderQuery);
			checkOrderStatement.setString(1, bookTitle);

			// Execute the query
			ResultSet orderResult = checkOrderStatement.executeQuery();
			if (orderResult.next() && orderResult.getInt(1) > 0) {
				// If there are orders for the book, return false
				return false;
			}

			// Parse the returnDate string to LocalDate
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate currentReturnDate = LocalDate.parse(returnDate, formatter);

			// Add one week to the current return date
			LocalDate newReturnDate = currentReturnDate.plusWeeks(2);
			String updatedReturnDate = newReturnDate.format(formatter); // Convert back to string

			// SQL query to update the returnDate in the loan table
			String updateQuery = "UPDATE loan SET dueDate = ? WHERE id = ? AND bookTitle = ?";
			PreparedStatement updateStatement = conn.prepareStatement(updateQuery);

			// Set the parameters for the query
			updateStatement.setString(1, updatedReturnDate); // Set the new returnDate
			updateStatement.setString(2, cardNum); // Set cardNum
			updateStatement.setString(3, bookTitle); // Set bookTitle

			// Execute the update query
			int rowsUpdated = updateStatement.executeUpdate();

			// If rowsUpdated > 0, the update was successful
			return rowsUpdated > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			System.err.println("Error parsing or updating date: " + e.getMessage());
			return false;
		}
	}

	public static User login(Object message) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		User user = null; // Assuming a User class exists to map the result
		ArrayList<String> loginDetails = (ArrayList<String>) message;

		String email = loginDetails.get(0);
		String password = loginDetails.get(1);

		try {
			String query = "SELECT * FROM users WHERE email = ? AND password = ?";

			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, email);
			pstmt.setString(2, password);

			rs = pstmt.executeQuery();

			if (rs.next()) {

				String type = rs.getString("type");
				System.out.println(type);
				if (type.equals("Subscriber")) {
					user = new Subscriber(rs.getString("id"), rs.getString("username"), rs.getString("phoneNumber"),
							rs.getString("email"));
				} else if (type.equals("Librarian")) {
					user = new Librarian(rs.getString("id"), rs.getString("username"), rs.getString("phoneNumber"),
							rs.getString("email"));
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close the ResultSet and PreparedStatement
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		System.out.println(user);
		return user;
	}

	public static Map<String, String> returnBook(Object message) {
		Map<String, String> result = new HashMap<>();
		PreparedStatement pstmt = null;
		PreparedStatement updateStatusStmt = null;
		ResultSet rs = null;

		ArrayList<Object> returnDetails = (ArrayList<Object>) message;
		String bookBarcode = (String) returnDetails.get(0);
		String readerCard = (String) returnDetails.get(1);
		Issue issue = (Issue) returnDetails.get(2);
		boolean lateWithoutFreeze = false;
		try {
			String queryCheckLoan = "SELECT dueDate FROM loan WHERE barcode = ? AND id = ? AND returnDate IS NULL";
			pstmt = conn.prepareStatement(queryCheckLoan);
			pstmt.setString(1, bookBarcode);
			pstmt.setString(2, readerCard);
			rs = pstmt.executeQuery();

			if (!rs.next()) {
				result.put("type", "noLoan");
				result.put("message", "No loan found for this user and book.");
				return result;
			}

			java.sql.Date dueDate = rs.getDate("dueDate");
			java.sql.Date currentSqlDate = new java.sql.Date(System.currentTimeMillis());
			long diffInMillis = currentSqlDate.getTime() - dueDate.getTime();
			long diffInDays = diffInMillis / (1000L * 60 * 60 * 24);

			boolean isFrozen = false;
			if (diffInDays >= 7 && issue.getType().equals("No Issue")) {
				String freezeQuery = "UPDATE users SET status = 'Frozen' WHERE id = ?";
				updateStatusStmt = conn.prepareStatement(freezeQuery);
				updateStatusStmt.setString(1, readerCard);
				updateStatusStmt.executeUpdate();
				isFrozen = true;
			} else if (diffInDays >= 1 && diffInDays < 7 && issue.getType().equals("No Issue")) {
				lateWithoutFreeze = true;
			}

			if (isFrozen || lateWithoutFreeze) {
				String insertLateBookSQL = "INSERT INTO issuehistory (subscriberId, type, date, bookBarcode) VALUES (?, ?, ?, ?)";
				pstmt.close();
				pstmt = conn.prepareStatement(insertLateBookSQL);
				pstmt.setString(1, readerCard);
				pstmt.setString(2, "Late");
				pstmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
				pstmt.setString(4, bookBarcode);
				pstmt.executeUpdate();
			}

			String updateReturnDateSQL = "UPDATE loan SET returnDate = ? WHERE barcode = ? AND id = ? AND returnDate IS NULL";
			pstmt.close();
			pstmt = conn.prepareStatement(updateReturnDateSQL);
			pstmt.setDate(1, currentSqlDate);
			pstmt.setString(2, bookBarcode);
			pstmt.setString(3, readerCard);
			pstmt.executeUpdate();

			if (issue.equals("Lost")) {
				String insertLostBookSQL = "INSERT INTO issuehistory (subscriberId, type, date, bookBarcode) VALUES (?, ?, ?, ?)";
				pstmt.close();
				pstmt = conn.prepareStatement(insertLostBookSQL);
				pstmt.setString(1, readerCard);
				pstmt.setString(2, "Lost");
				pstmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
				pstmt.setString(4, bookBarcode);
				pstmt.executeUpdate();

				result.put("type", "lost");
				result.put("message", "Book marked as lost and recorded in issue history.");
				return result;
			}

			String updateBookCopiesQuery = "UPDATE books SET availableCopies = availableCopies + 1 WHERE barcode = ?";
			pstmt.close();
			pstmt = conn.prepareStatement(updateBookCopiesQuery);
			pstmt.setString(1, bookBarcode);
			pstmt.executeUpdate();

			// Find the first empty bookshelf slot and place the book there
			String findEmptyShelfQuery = "SELECT id FROM bookshelf WHERE barcode IS NULL LIMIT 1";
			pstmt.close();
			pstmt = conn.prepareStatement(findEmptyShelfQuery);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				int shelfId = rs.getInt("id");
				String updateShelfQuery = "UPDATE bookshelf SET barcode = ? WHERE id = ?";
				pstmt.close();
				pstmt = conn.prepareStatement(updateShelfQuery);
				pstmt.setString(1, bookBarcode);
				pstmt.setInt(2, shelfId);
				pstmt.executeUpdate();
			}

			if (lateWithoutFreeze) {
				result.put("type", "lateWithoutFreeze");
				result.put("message", "The book was returned late, but the user's account has not been frozen.");
			} else if (isFrozen) {
				result.put("type", "frozen");
				result.put("message", "User has been frozen due to late return. Book return processed successfully.");
			} else {
				result.put("type", "success");
				result.put("message", "Return processed successfully.");
			}
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			result.put("type", "error");
			result.put("message", "Error returning book: " + e.getMessage());
			return result;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (updateStatusStmt != null)
					updateStatusStmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static Map<String, String> checkSubscriberStatus(Object message) {
		Map<String, String> result = new HashMap<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String readerCard = ((Subscriber) message).getID();

		try {

			String query = "SELECT status FROM users WHERE id = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, readerCard);

			rs = pstmt.executeQuery();

			if (!rs.next()) {
				result.put("type", "notFound");
				result.put("message", "No user found for the given reader card.");
				return result;
			}

			String status = rs.getString("status");

			result.put("type", "found");
			result.put("status", status);
			result.put("message", "User found with status: " + status);

			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			result.put("type", "error");
			result.put("message", "Error checking subscriber status: " + e.getMessage());
			return result;
		} finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {

			}
		}
	}

	public static Map<String, String> createLoan(Object message) {
		Map<String, String> result = new HashMap<>();
		PreparedStatement pstmt = null;
		PreparedStatement pstmtOrders = null;
		PreparedStatement pstmtInsertLoan = null;
		PreparedStatement pstmtDeleteOrder = null;
		PreparedStatement pstmtUpdateCopies = null;
		PreparedStatement pstmtUpdateLoanedCount = null;
		PreparedStatement pstmtDeleteBookshelf = null; // New statement to remove from bookshelf
		ResultSet rs = null;
		ResultSet rsOrders = null;

		try {
			ArrayList<Object> msg = (ArrayList<Object>) message;
			Loan loan = (Loan) msg.get(0);
			Subscriber subscriber = (Subscriber) msg.get(1);

			String subscriberID = subscriber.getID();
			String bookBarcode = loan.getBarcode();
			String borrowDateStr = loan.getBorrowDate();
			String dueDateStr = loan.getReturnDate();

			// 1) Check if the book exists
			String sqlCheckBook = "SELECT availableCopies FROM books WHERE barcode = ?";
			pstmt = conn.prepareStatement(sqlCheckBook);
			pstmt.setString(1, bookBarcode);
			rs = pstmt.executeQuery();
			if (!rs.next()) {
				result.put("type", "notFound");
				result.put("message", "No book found with the given barcode.");
				return result;
			}
			int availableCopies = rs.getInt("availableCopies");
			rs.close();
			pstmt.close();

			// 2) Check if the subscriber already has this book on loan (unreturned)
			String queryCheckLoan = "SELECT dueDate FROM loan WHERE barcode = ? AND id = ? AND returnDate IS NULL";
			pstmt = conn.prepareStatement(queryCheckLoan);
			pstmt.setString(1, bookBarcode);
			pstmt.setString(2, subscriberID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				result.put("type", "alreadyLoaned");
				result.put("message",
						"Subscriber already has this book on loan and cannot borrow again until it is returned.");
				return result;
			}
			rs.close();
			pstmt.close();

			// 3) Gather all orders for this book, sorted by requestDate (earliest first)
			String sqlSelectAllOrders = "SELECT * FROM orders WHERE barcode = ? ORDER BY requestDate ASC";
			pstmtOrders = conn.prepareStatement(sqlSelectAllOrders);
			pstmtOrders.setString(1, bookBarcode);
			rsOrders = pstmtOrders.executeQuery();

			List<Order> ordersList = new ArrayList<>();
			while (rsOrders.next()) {
				ordersList.add(new Order(rsOrders.getString("id"), rsOrders.getTimestamp("requestDate"),
						rsOrders.getString("barcode")));
			}
			rsOrders.close();
			pstmtOrders.close();

			int subscriberPosition = -1;

			for (int i = 0; i < ordersList.size(); i++) {
				if (ordersList.get(i).getSubscriberID().equals(subscriberID)) {
					subscriberPosition = i + 1;
					break;
				}
			}

			if (subscriberPosition != -1) {
				if (availableCopies < subscriberPosition) {
					result.put("type", "unsuccessful");
					result.put("message", "Not enough copies are available yet to cover all earlier reservations.");
					return result;
				}

				String sqlInsertLoan = "INSERT INTO loan (id, barcode, borrowDate, dueDate) VALUES (?, ?, ?, ?)";
				pstmtInsertLoan = conn.prepareStatement(sqlInsertLoan);
				pstmtInsertLoan.setString(1, subscriberID);
				pstmtInsertLoan.setString(2, bookBarcode);
				pstmtInsertLoan.setDate(3, java.sql.Date.valueOf(borrowDateStr));
				pstmtInsertLoan.setDate(4, java.sql.Date.valueOf(dueDateStr));
				pstmtInsertLoan.executeUpdate();

				String updateBookCopies = "UPDATE books SET availableCopies = availableCopies - 1 WHERE barcode = ?";
				pstmtUpdateCopies = conn.prepareStatement(updateBookCopies);
				pstmtUpdateCopies.setString(1, bookBarcode);
				pstmtUpdateCopies.executeUpdate();

				String sqlUpdateLoanedCount = "UPDATE loanedbooks SET loanedCount = loanedCount + 1 WHERE barcode = ?";
				pstmtUpdateLoanedCount = conn.prepareStatement(sqlUpdateLoanedCount);
				pstmtUpdateLoanedCount.setString(1, bookBarcode);
				pstmtUpdateLoanedCount.executeUpdate();

				String sqlDeleteOrder = "DELETE FROM orders WHERE id = ? AND barcode = ?";
				pstmtDeleteOrder = conn.prepareStatement(sqlDeleteOrder);
				pstmtDeleteOrder.setString(1, subscriberID);
				pstmtDeleteOrder.setString(2, bookBarcode);
				pstmtDeleteOrder.executeUpdate();

				String sqlDeleteBookshelf = "UPDATE bookshelf SET barcode = NULL WHERE barcode = ? LIMIT 1";
				pstmtDeleteBookshelf = conn.prepareStatement(sqlDeleteBookshelf);
				pstmtDeleteBookshelf.setString(1, bookBarcode);
				pstmtDeleteBookshelf.executeUpdate();

				result.put("type", "success");
				result.put("message", "Loan created (subscriber had an order).");
				return result;
			} else {
				if (availableCopies <= ordersList.size()) {
					result.put("type", "unsuccessful");
					result.put("message", "Not enough copies are left after covering existing reservations.");
					return result;
				}

				String sqlInsertLoan = "INSERT INTO loan (id, barcode, borrowDate, dueDate) VALUES (?, ?, ?, ?)";
				pstmtInsertLoan = conn.prepareStatement(sqlInsertLoan);
				pstmtInsertLoan.setString(1, subscriberID);
				pstmtInsertLoan.setString(2, bookBarcode);
				pstmtInsertLoan.setDate(3, java.sql.Date.valueOf(borrowDateStr));
				pstmtInsertLoan.setDate(4, java.sql.Date.valueOf(dueDateStr));
				pstmtInsertLoan.executeUpdate();

				String updateBookCopies = "UPDATE books SET availableCopies = availableCopies - 1 WHERE barcode = ?";
				pstmtUpdateCopies = conn.prepareStatement(updateBookCopies);
				pstmtUpdateCopies.setString(1, bookBarcode);
				pstmtUpdateCopies.executeUpdate();

				String sqlUpdateLoanedCount = "UPDATE loanedbooks SET loanedCount = loanedCount + 1 WHERE barcode = ?";
				pstmtUpdateLoanedCount = conn.prepareStatement(sqlUpdateLoanedCount);
				pstmtUpdateLoanedCount.setString(1, bookBarcode);
				pstmtUpdateLoanedCount.executeUpdate();

				String sqlDeleteBookshelf = "UPDATE bookshelf SET barcode = NULL WHERE barcode = ? LIMIT 1";
				pstmtDeleteBookshelf = conn.prepareStatement(sqlDeleteBookshelf);
				pstmtDeleteBookshelf.setString(1, bookBarcode);
				pstmtDeleteBookshelf.executeUpdate();

				result.put("type", "success");
				result.put("message", "Loan created (subscriber was not in queue).");
				return result;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			result.put("type", "error");
			result.put("message", "Error creating loan: " + e.getMessage());
			return result;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				/* ignore */}
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				/* ignore */}
			try {
				if (pstmtOrders != null)
					pstmtOrders.close();
			} catch (SQLException e) {
				/* ignore */}
			try {
				if (rsOrders != null)
					rsOrders.close();
			} catch (SQLException e) {
				/* ignore */}
			try {
				if (pstmtInsertLoan != null)
					pstmtInsertLoan.close();
			} catch (SQLException e) {
				/* ignore */}
			try {
				if (pstmtDeleteOrder != null)
					pstmtDeleteOrder.close();
			} catch (SQLException e) {
				/* ignore */}
			try {
				if (pstmtUpdateCopies != null)
					pstmtUpdateCopies.close();
			} catch (SQLException e) {
				/* ignore */}
			try {
				if (pstmtUpdateLoanedCount != null)
					pstmtUpdateLoanedCount.close();
			} catch (SQLException e) {
				/* ignore */}
			try {
				if (pstmtDeleteBookshelf != null)
					pstmtDeleteBookshelf.close();
			} catch (SQLException e) {
				/* ignore */}
		}
	}

	// REPORTS
//	public static Map<String, Map<String, String>> fetchLoanDataForReport(Object message) {
//		// Cast the input object to a Map<String, String>
//		Map<String, String> loanMonthAndYear = (Map<String, String>) message;
//
//		// Extract the "month" and "year" values from the map
//		String month = loanMonthAndYear.get("month");
//		String year = loanMonthAndYear.get("year");
//
//		// Query to filter the LoanReports table by month and year
//		String query = "SELECT week, borrowed, lateReturn FROM loanreport WHERE month = ? AND year = ?";
//
//		// Create a Map to hold the results
//		Map<String, Map<String, String>> loanData = new HashMap<>();
//
//		try (PreparedStatement stmt = conn.prepareStatement(query)) {
//			// Set the parameters for the query
//			stmt.setString(1, month);
//			stmt.setString(2, year);
//
//			// Execute the query
//			try (ResultSet rs = stmt.executeQuery()) {
//				// Process the result set
//				while (rs.next()) {
//					// Fetch the values
//					String week = rs.getString("week");
//					String borrowed = rs.getString("borrowed");
//					String lateReturn = rs.getString("lateReturn");
//
//					// Create or update the week-specific map
//					Map<String, String> weekData = new HashMap<>();
//					weekData.put("borrowed", borrowed);
//					weekData.put("lateReturn", lateReturn);
//
//					// Add to the main map with week as the key
//					loanData.put("Week " + week, weekData);
//				}
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//		System.out.println("Loan Data Map: " + loanData);
//
//		return loanData;
//	}

	// REPORTS
	public static Map<String, Map<String, String>> fetchLoanDataForReport(Object message) {
		System.out.println("calling endOfMonthProcessing()");
		// Cast the input object to a Map<String, String>
		Map<String, String> loanMonthAndYear = (Map<String, String>) message;

		// Extract the "month" and "year" values from the map
		String month = loanMonthAndYear.get("month");
		String year = loanMonthAndYear.get("year");

		// Query to filter the LoanReports table by month and year
		String query = "SELECT category, borrowed, lateReturn FROM loanreport WHERE month = ? AND year = ?";

		// Create a Map to hold the results
		Map<String, Map<String, String>> loanData = new HashMap<>();

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			// Set the parameters for the query
			stmt.setString(1, month);
			stmt.setString(2, year);

			// Execute the query
			try (ResultSet rs = stmt.executeQuery()) {
				// Process the result set
				while (rs.next()) {
					// Fetch the values
					String category = rs.getString("category");
					String borrowed = rs.getString("borrowed");
					String lateReturn = rs.getString("lateReturn");

					// Create or update the category-specific map
					Map<String, String> categoryData = new HashMap<>();
					categoryData.put("borrowed", borrowed);
					categoryData.put("lateReturn", lateReturn);

					// Add to the main map with category as the key
					loanData.put(category, categoryData);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Loan Data Map: " + loanData);

		return loanData;
	}

	public static Map<String, String> fetchStatusDataForReport(Object message) {
		// Cast the input object to a Map<String, String>
		Map<String, String> loanMonthAndYear = (Map<String, String>) message;

		// Extract the "month" and "year" values from the map
		String month = loanMonthAndYear.get("month");
		String year = loanMonthAndYear.get("year");

		// SQL query to retrieve active and frozen counts for the given month and year
		String query = "SELECT active, frozen " + "FROM statusreport "
				+ "WHERE MONTH(monthAndYear) = ? AND YEAR(monthAndYear) = ?";

		// Create a Map to hold the results
		Map<String, String> statusData = new HashMap<>();

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			// Set the parameters for the query
			stmt.setInt(1, Integer.parseInt(month));
			stmt.setInt(2, Integer.parseInt(year));

			// Execute the query
			try (ResultSet rs = stmt.executeQuery()) {
				// Process the result set
				if (rs.next()) {
					String activeCount = rs.getString("active"); // Get the active count
					String frozenCount = rs.getString("frozen"); // Get the frozen count
					statusData.put("active", activeCount); // Add active count to the map
					statusData.put("frozen", frozenCount); // Add frozen count to the map
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Status data fetched from database: " + statusData.toString());

		return statusData;
	}

//	public static void endOfMonthProcessingLoanReport() {
//		PreparedStatement insertStmt = null;
//		PreparedStatement checkStmt = null;
//		ResultSet rs = null;
//
//		List<String> categories = Arrays.asList("Science Fiction", "Romance", "Political Satire", "Historical Fiction",
//				"Gothic Fiction", "Fantasy", "Epic Poetry", "Dystopian", "Coming-of-Age", "Adventure");
//
//		try {
//			// Query distinct years and months from the loan table
//			String distinctYearMonthQuery = "SELECT DISTINCT YEAR(borrowDate) AS year,MONTH(borrowDate) AS month FROM loan";
//			Statement stmt = conn.createStatement();
//			rs = stmt.executeQuery(distinctYearMonthQuery);
//
//			// Prepare the insertion query for loanreport (without loanReportId)
//			String insertQuery = "INSERT INTO loanreport (month, year, category, borrowed, lateReturn) VALUES (?, ?, ?, ?, ?)";
//			insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
//
//			// Prepare the query to check borrowed and late return counts
//			String checkQuery = "SELECT COUNT(*) AS borrowed,SUM(CASE WHEN returnDate > dueDate THEN 1 ELSE 0 END) AS lateReturn FROM loan l JOIN books b ON l.barcode = b.barcode WHERE MONTH(l.borrowDate) = ? AND YEAR(l.borrowDate) = ? AND b.category = ?";
//			checkStmt = conn.prepareStatement(checkQuery);
//
//			// Iterate over each distinct year and month
//			while (rs.next()) {
//				int year = rs.getInt("year");
//				int month = rs.getInt("month");
//				String monthFormatted = String.format("%02d", month); // Format as 01, 02, etc.
//
//				// For each category, check borrowed and lateReturn counts
//				for (String category : categories) {
//					checkStmt.setInt(1, month); // Set month parameter
//					checkStmt.setInt(2, year); // Set year parameter
//					checkStmt.setString(3, category); // Set category parameter
//
//					ResultSet checkRs = checkStmt.executeQuery();
//					int borrowed = 0;
//					int lateReturn = 0;
//
//					if (checkRs.next()) {
//						borrowed = checkRs.getInt("borrowed");
//						lateReturn = checkRs.getInt("lateReturn");
//					}
//
//					// Insert the data into loanreport table
//					insertStmt.setString(1, monthFormatted);
//					insertStmt.setString(2, String.valueOf(year));
//					insertStmt.setString(3, category);
//					insertStmt.setInt(4, borrowed);
//					insertStmt.setInt(5, lateReturn);
//
//					insertStmt.executeUpdate();
//
//					// Retrieve the auto-generated primary key using getGeneratedKeys()
//					ResultSet generatedKeys = insertStmt.getGeneratedKeys();
//					if (generatedKeys.next()) {
//						int loanReportId = generatedKeys.getInt(1);
//						System.out.println("Loan report inserted with ID: " + loanReportId);
//					}
//				}
//			}
//
//			System.out.println("End-of-month processing completed. Data inserted into loanreport table.");
//			
//			
//			
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			// Clean up resources
//			try {
//				if (rs != null)
//					rs.close();
//				if (insertStmt != null)
//					insertStmt.close();
//				if (checkStmt != null)
//					checkStmt.close();
//				if (conn != null)
//					conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	public static void endOfMonthProcessingLoanReport() {
		PreparedStatement insertStmt = null;
		PreparedStatement checkStmt = null;
		ResultSet rs = null;

		List<String> categories = Arrays.asList("Science Fiction", "Romance", "Political Satire", "Historical Fiction",
				"Gothic Fiction", "Fantasy", "Epic Poetry", "Dystopian", "Coming-of-Age", "Adventure");

		try {

			// Query distinct years and months from the loan table
			String distinctYearMonthQuery = "SELECT DISTINCT YEAR(borrowDate) AS year,MONTH(borrowDate) AS month FROM loan";
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(distinctYearMonthQuery);

			// Prepare the insertion query for loanreport (without loanReportId)
			String insertQuery = "INSERT INTO loanreport (month, year, category, borrowed, lateReturn) VALUES (?, ?, ?, ?, ?)";
			insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);

			// Prepare the query to check borrowed and late return counts
			String checkQuery = "SELECT COUNT(*) AS borrowed,SUM(CASE WHEN returnDate > dueDate THEN 1 ELSE 0 END) AS lateReturn FROM loan l JOIN books b ON l.barcode = b.barcode WHERE MONTH(l.borrowDate) = ? AND YEAR(l.borrowDate) = ? AND b.category = ?";
			checkStmt = conn.prepareStatement(checkQuery);

			// Iterate over each distinct year and month
			while (rs.next()) {
				int year = rs.getInt("year");
				int month = rs.getInt("month");
				String monthFormatted = String.format("%02d", month); // Format as 01, 02, etc.

				// For each category, check borrowed and lateReturn counts
				for (String category : categories) {
					checkStmt.setInt(1, month); // Set month parameter
					checkStmt.setInt(2, year); // Set year parameter
					checkStmt.setString(3, category); // Set category parameter

					ResultSet checkRs = checkStmt.executeQuery();
					int borrowed = 0;
					int lateReturn = 0;

					if (checkRs.next()) {
						borrowed = checkRs.getInt("borrowed");
						lateReturn = checkRs.getInt("lateReturn");
					}

					// Insert the data into loanreport table
					insertStmt.setString(1, monthFormatted);
					insertStmt.setString(2, String.valueOf(year));
					insertStmt.setString(3, category);
					insertStmt.setInt(4, borrowed);
					insertStmt.setInt(5, lateReturn);

					insertStmt.executeUpdate();

					// Retrieve the auto-generated primary key using getGeneratedKeys()
					ResultSet generatedKeys = insertStmt.getGeneratedKeys();
					if (generatedKeys.next()) {
						int loanReportId = generatedKeys.getInt(1);
						System.out.println("Loan report inserted with ID: " + loanReportId);
					}
				}
			}

			System.out.println("End-of-month processing completed. Data inserted into loanreport table.");

		} catch (SQLException e) {
			// Handle SQL exceptions
			e.printStackTrace();
			if (e instanceof SQLNonTransientConnectionException) {
				System.err.println("Failed to execute loan report due to connection issue. Attempting to reconnect...");
				if (conn != null) {
					// Retry the operation if reconnection is successful
					endOfMonthProcessingLoanReport(); // Recursive call to retry
				} else {
					System.err.println("Reconnection failed. Loan report processing skipped.");
				}
			}
		} finally {
			// Clean up resources
			try {
				if (rs != null)
					rs.close();
				if (insertStmt != null)
					insertStmt.close();
				if (checkStmt != null)
					checkStmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void endOfMonthProcessingStatusReport() {
	    PreparedStatement insertStmt = null;
	    ResultSet rs = null;

	    try {
	        // Get the date of the previous month
	        Calendar cal = Calendar.getInstance();
	        cal.add(Calendar.MONTH, -1); // Go back to the previous month
	        int previousMonth = cal.get(Calendar.MONTH) + 1; // Adjust for 0-based month index
	        int previousYear = cal.get(Calendar.YEAR);
	        String previousMonthFormatted = String.format("%02d", previousMonth);
	        String dateFormatted = previousYear + "-" + previousMonthFormatted + "-01"; // Format as yyyy-mm-01

	        // Queries for counting users
	        String activeUsersQuery = "SELECT COUNT(*) AS count FROM users WHERE LOWER(status) = 'active'";
	        String frozenUsersQuery = "SELECT COUNT(*) AS count FROM users WHERE LOWER(status) = 'frozen'";

	        // Prepare and execute the active users query
	        PreparedStatement activeStmt = conn.prepareStatement(activeUsersQuery);
	        rs = activeStmt.executeQuery();
	        int activeCount = 0;
	        if (rs.next()) {
	            activeCount = rs.getInt("count");
	        }
	        rs.close();
	        activeStmt.close();

	        // Prepare and execute the frozen users query
	        PreparedStatement frozenStmt = conn.prepareStatement(frozenUsersQuery);
	        rs = frozenStmt.executeQuery();
	        int frozenCount = 0;
	        if (rs.next()) {
	            frozenCount = rs.getInt("count");
	        }
	        rs.close();
	        frozenStmt.close();

	        System.out.println("Active users: " + activeCount + ", Frozen users: " + frozenCount);

	        // Insert the data into the statusreport table (using ON DUPLICATE KEY UPDATE)
	        String insertQuery = "INSERT INTO statusreport (monthAndYear, active, frozen) VALUES (?, ?, ?) "
	                + "ON DUPLICATE KEY UPDATE active = VALUES(active), frozen = VALUES(frozen)";
	        insertStmt = conn.prepareStatement(insertQuery);
	        insertStmt.setString(1, dateFormatted); // Set the previous month's date
	        insertStmt.setString(2, String.valueOf(activeCount)); // Convert active count to string
	        insertStmt.setString(3, String.valueOf(frozenCount)); // Convert frozen count to string
	        insertStmt.executeUpdate();

	        System.out.println(
	                "Inserted status report for previous month (" + previousMonthFormatted + "/" + previousYear + "):");
	        System.out.println("Active Count: " + activeCount + ", Frozen Count: " + frozenCount);

	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        // Clean up resources
	        try {
	            if (rs != null)
	                rs.close();
	            if (insertStmt != null)
	                insertStmt.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}


//	public static void endOfMonthProcessingStatusReport() {
//	    PreparedStatement insertStmt = null;
//	    PreparedStatement userStatusStmt = null;  // Use PreparedStatement for the SELECT query
//	    ResultSet rs = null;
//
//	    try {
//	        Calendar cal = Calendar.getInstance();
//	        cal.add(Calendar.MONTH, -1); // Go back to previous month
//	        int previousMonth = cal.get(Calendar.MONTH) + 1; // Adjust for 0-based month index
//	        int previousYear = cal.get(Calendar.YEAR);
//	        String previousMonthFormatted = String.format("%02d", previousMonth);
//	        String dateFormatted = previousYear + "-" + previousMonthFormatted + "-01"; // Format to yyyy-mm-dd
//
//	        // Prepare the insertion query for statusreport
//	        String insertQuery = "INSERT INTO statusreport (monthAndYear, active, frozen) VALUES (?, ?, ?)";
//	        String updateQuery = "UPDATE statusreport SET active = ?, frozen = ? WHERE monthAndYear = ?";
//
//	        // Prepare the query to get active and frozen user counts
//	        String userStatusQuery = "SELECT COUNT(*) AS count FROM users WHERE status = ?";
//
//	        // Prepare the user status statement for active users
//	        userStatusStmt = conn.prepareStatement(userStatusQuery);
//
//	        // Count active users
//	        userStatusStmt.setString(1, "active");
//	        rs = userStatusStmt.executeQuery();
//	        int activeCount = 0;
//	        if (rs.next()) {
//	            activeCount = rs.getInt("count");
//	            System.out.println("Active Count: " + activeCount);
//	        }
//
//	        // Check if the record for the monthAndYear already exists
//	        String checkExistenceQuery = "SELECT COUNT(*) AS count FROM statusreport WHERE monthAndYear = ?";
//	        PreparedStatement checkExistenceStmt = conn.prepareStatement(checkExistenceQuery);
//	        checkExistenceStmt.setString(1, dateFormatted);
//	        rs = checkExistenceStmt.executeQuery();
//	        int existingCount = 0;
//	        if (rs.next()) {
//	            existingCount = rs.getInt("count");
//	        }
//
//	        if (existingCount > 0) {
//	            // If the record exists, update it
//	            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
//	            updateStmt.setInt(1, activeCount);
//	            updateStmt.setInt(2, 0);  // Set frozen count to 0 for now
//	            updateStmt.setString(3, dateFormatted);
//	            updateStmt.executeUpdate();
//	        } else {
//	            // If the record does not exist, insert it
//	            insertStmt = conn.prepareStatement(insertQuery);
//	            insertStmt.setString(1, dateFormatted);
//	            insertStmt.setInt(2, activeCount);
//	            insertStmt.setInt(3, 0);  // Initialize frozen count to 0
//	            insertStmt.executeUpdate();
//	        }
//
//	        // Count frozen users
//	        userStatusStmt.setString(1, "frozen");
//	        rs = userStatusStmt.executeQuery();
//	        int frozenCount = 0;
//	        if (rs.next()) {
//	            frozenCount = rs.getInt("count");
//	        }
//
//	        if (existingCount > 0) {
//	            // If the record exists, update it with frozen count
//	            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
//	            updateStmt.setInt(1, 0);  // Set active count to 0 for now
//	            updateStmt.setInt(2, frozenCount);
//	            updateStmt.setString(3, dateFormatted);
//	            updateStmt.executeUpdate();
//	        } else {
//	            // If the record does not exist, insert frozen user count as well
//	            insertStmt.setString(1, dateFormatted);  // Set date again
//	            insertStmt.setInt(2, 0); // Set active count to 0
//	            insertStmt.setInt(3, frozenCount);
//	            insertStmt.executeUpdate();
//	        }
//
//	        System.out.println("Status report processed for previous month (" + previousMonthFormatted + "/" + previousYear + ").");
//
//	    } catch (SQLException e) {
//	        e.printStackTrace();
//	    } finally {
//	        // Clean up resources
//	        try {
//	            if (rs != null) rs.close();
//	            if (insertStmt != null) insertStmt.close();
//	            if (userStatusStmt != null) userStatusStmt.close();
//	        } catch (SQLException e) {
//	            e.printStackTrace();
//	        }
//	    }
//	}

//	public static void endOfMonthProcessingStatusReport() {
//		PreparedStatement insertStmt = null;
//		ResultSet rs = null;
//
//		try {
//			Calendar cal = Calendar.getInstance();
//			cal.add(Calendar.MONTH, -1); // Go back to previous month
//			int previousMonth = cal.get(Calendar.MONTH) + 1; // Adjust for 0-based month index
//			int previousYear = cal.get(Calendar.YEAR);
//			String previousMonthFormatted = String.format("%02d", previousMonth);
//
//			// Prepare the insertion query for statusreport
//			String insertQuery = "INSERT INTO statusreport (monthAndYear, active, frozen) VALUES (?, ?, ?)";
//			insertStmt = conn.prepareStatement(insertQuery);
//
//			// Prepare the query to get active and frozen user counts
//			String userStatusQuery = "SELECT COUNT(*) AS count FROM users WHERE status = ?";
//
//			// Count active users
//			insertStmt.setString(1, previousYear + "-" + previousMonthFormatted);
//			insertStmt.setString(2, "active");
//			rs = conn.createStatement().executeQuery(userStatusQuery);
//			int activeCount = 0;
//			if (rs.next()) {
//				activeCount = rs.getInt("count");
//			}
//			insertStmt.setInt(3, activeCount);
//			insertStmt.executeUpdate();
//
//			// Count frozen users (repeat the query with status='frozen')
//			rs.close(); // Close the result set before re-using
//			insertStmt.setString(2, "frozen");
//			rs = conn.createStatement().executeQuery(userStatusQuery);
//			int frozenCount = 0;
//			if (rs.next()) {
//				frozenCount = rs.getInt("count");
//			}
//			insertStmt.setInt(3, frozenCount);
//			insertStmt.executeUpdate();
//
//			System.out.println(
//					"Status report inserted for previous month (" + previousMonthFormatted + "/" + previousYear + ").");
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			// Clean up resources
//			try {
//				if (rs != null)
//					rs.close();
//				if (insertStmt != null)
//					insertStmt.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//	}

//	public static void endOfMonthProcessingStatusReport() {
//		PreparedStatement insertStmt = null;
//		ResultSet rs = null;
//
//		try {
//			Calendar cal = Calendar.getInstance();
//			cal.add(Calendar.MONTH, -1); // Go back to previous month
//			int previousMonth = cal.get(Calendar.MONTH) + 1; // Adjust for 0-based month index
//			int previousYear = cal.get(Calendar.YEAR);
//			String previousMonthFormatted = String.format("%02d", previousMonth);
//
//			// Prepare the insertion query for statusreport
//			String insertQuery = "INSERT INTO statusreport (monthAndYear, active, frozen) VALUES (?, ?, ?)";
//			insertStmt = conn.prepareStatement(insertQuery);
//
//			// Prepare the query to get active and frozen user counts
//			String userStatusQuery = "SELECT COUNT(*) AS count FROM users WHERE status = ?";
//
//			// Count active users
//			insertStmt.setString(1, previousYear + "-" + previousMonthFormatted);
//			insertStmt.setString(2, "active");
//			rs = conn.createStatement().executeQuery(userStatusQuery);
//			int activeCount = 0;
//			if (rs.next()) {
//				activeCount = rs.getInt("count");
//			}
//			insertStmt.setInt(3, activeCount);
//			insertStmt.executeUpdate();
//
//			// Count frozen users (repeat the query with status='frozen')
//			rs.close(); // Close the result set before re-using
//			insertStmt.setString(2, "frozen");
//			rs = conn.createStatement().executeQuery(userStatusQuery);
//			int frozenCount = 0;
//			if (rs.next()) {
//				frozenCount = rs.getInt("count");
//			}
//			insertStmt.setInt(3, frozenCount);
//			insertStmt.executeUpdate();
//
//			System.out.println(
//					"Status report inserted for previous month (" + previousMonthFormatted + "/" + previousYear + ").");
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//			if (e instanceof SQLNonTransientConnectionException) {
//				System.err
//						.println("Failed to execute status report due to connection issue. Attempting to reconnect...");
//				conn = getConnection(); // Implement reconnection logic here
//				if (conn != null) {
//					// Retry the operation if reconnection is successful
//					endOfMonthProcessingStatusReport(); // Recursive call to retry
//				} else {
//					System.err.println("Reconnection failed. Status report processing skipped.");
//				}
//			}
//		} finally {
//			// Clean up resources
//			try {
//				if (rs != null)
//					rs.close();
//				if (insertStmt != null)
//					insertStmt.close();
//				if (conn != null)
//					conn.close(); // Close connection even if reconnection failed
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost/middleproject?serverTimezone=IST", "root",
					"Aa123456");
			System.out.println("Connection to database established.");
		} catch (Exception ex) {
			System.err.println("Failed to establish connection to database: " + ex.getMessage());
		}
		return conn;
	}

	public static boolean checkDueDateAndSendSMS() {
		boolean smsSent = false;

		try {
			// Calculate tomorrow's date
			LocalDate tomorrow = LocalDate.now().plusDays(1);

			// SQL query to find loans due on tomorrow's date
			String query = "SELECT dueDate FROM loan WHERE dueDate = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setDate(1, java.sql.Date.valueOf(tomorrow)); // Use tomorrow's date here

			// Execute the query
			ResultSet resultSet = statement.executeQuery();

			// Process the results
			if (!resultSet.isBeforeFirst()) {
				System.out.println("No loans due tomorrow (" + tomorrow + ").");
			} else {
				System.out.println("Loans due tomorrow (" + tomorrow + ") found. Sending SMS notifications...");

				while (resultSet.next()) {
					// Fetch loan data
					LocalDate dueDate = resultSet.getDate("dueDate").toLocalDate();

					// Simulate sending SMS (replace this with your actual SMS API)
					System.out.println("Sending SMS for loan due on: " + dueDate);
				}

				smsSent = true; // Set flag to true if notifications were sent
			}

			// Close resources
			resultSet.close();
			statement.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return smsSent;
	}

}
