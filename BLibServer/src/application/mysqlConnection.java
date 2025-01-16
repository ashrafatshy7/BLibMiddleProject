package application;

import java.sql.Blob;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import enteties.Book;
import enteties.Subscriber;
import enteties.User;
import enteties.Librarian;

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
			// Your INSERT query
			String query = "INSERT INTO `order` (id, requestDate, barcode) VALUES (?, ?, ?)";

			// Prepare the statement
			pstmt = conn.prepareStatement(query);

			// Format the date as yyyy-MM-dd (MySQL standard)
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String requestDate = formatter.format(new Date());

			// Bind parameters
			pstmt.setString(1, id);
			pstmt.setString(2, requestDate);
			pstmt.setString(3, barcode);

			// Execute
			pstmt.executeUpdate();
			ordered = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
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
			String query = "SELECT 1 FROM `order` WHERE barcode = ? AND id = ? LIMIT 1";
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
			String query = "" + "SELECT b.*\n" + "FROM books b\n" + "JOIN (\n"
					+ "    SELECT t.*, (@rank := @rank + 1) AS rn\n" + "    FROM loanedbooks t, (SELECT @rank := 0) r\n"
					+ "    ORDER BY t.loanedCount DESC, t.barcode ASC\n"
					+ ") AS ranked_books ON b.barcode = ranked_books.barcode\n" + "WHERE ranked_books.rn <= 5\n"
					+ "ORDER BY ranked_books.loanedCount DESC, ranked_books.barcode ASC;";

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
			String query = "SELECT returnDate FROM loan WHERE barcode = ? ORDER BY returnDate ASC LIMIT 1";

			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, barcodeStr);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				Date date = rs.getDate("returnDate");

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
		String query = "SELECT bookTitle, returnDate FROM loan WHERE id = ? AND returnDate BETWEEN CURDATE() AND CURDATE() + INTERVAL 7 DAY ";

		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, cardNum); // Set the cardNum parameter

			// Execute the query
			resultSet = preparedStatement.executeQuery();

			// Process the result set
			while (resultSet.next()) {
				String bookTitle = resultSet.getString("bookTitle");
				String returnDate = resultSet.getDate("returnDate").toString();

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
		// Cast messageData to Map<String, String>
		Map<String, String> data = (Map<String, String>) messageData;

		// Get the values from the map
		String cardNum = data.get("cardNum");
		String bookTitle = data.get("bookTitle");
		String returnDate = data.get("returnDate");

		try {
			// SQL query to check if there is an order for the same book
			String checkOrderQuery = "SELECT COUNT(*) FROM `order` WHERE bookTitle = ?";
			PreparedStatement checkOrderStatement = conn.prepareStatement(checkOrderQuery);
			checkOrderStatement.setString(1, bookTitle);

			// Execute the query
			ResultSet orderResult = checkOrderStatement.executeQuery();
			if (orderResult.next() && orderResult.getInt(1) > 0) {
				// If there are orders for the book, return false
				return false;
			}

			// Parse the returnDate string to LocalDate
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Adjust format as needed
			LocalDate currentReturnDate = LocalDate.parse(returnDate, formatter);

			// Add one week to the current return date
			LocalDate newReturnDate = currentReturnDate.plusWeeks(2);
			String updatedReturnDate = newReturnDate.format(formatter); // Convert back to string

			// SQL query to update the returnDate in the loan table
			String updateQuery = "UPDATE loan SET returnDate = ? WHERE id = ? AND bookTitle = ?";
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

	public static boolean returnBook(Object message) {
	    PreparedStatement pstmt = null;
	    PreparedStatement updateStatusStmt = null;
	    ResultSet rs = null;
	    boolean isSuccess = false;
	    
	    ArrayList<String> returnDetails = (ArrayList<String>) message;

	    String bookBarcode = returnDetails.get(0);
	    String readerCard = returnDetails.get(1);
	    
	    try {
	        // 1) Check the returnDate in the loan table
	        String queryCheckReturnDate = 
	            "SELECT returnDate FROM loan WHERE barcode = ? AND id = ?";
	        pstmt = conn.prepareStatement(queryCheckReturnDate);
	        pstmt.setString(1, bookBarcode);
	        pstmt.setString(2, readerCard);
	        rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            java.sql.Date returnDate = rs.getDate("returnDate");
	            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
	            
	            long diffInMillis = currentDate.getTime() - returnDate.getTime();
	            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
	            
	            // 2) If more than 7 days late, freeze user
	            if (diffInDays >= 7) {
	                String freezeQuery = 
	                    "UPDATE users " +
	                    "SET status = 'Frozen' " +
	                    "WHERE id = ?";
	                updateStatusStmt = conn.prepareStatement(freezeQuery);
	                updateStatusStmt.setString(1, readerCard);
	                updateStatusStmt.executeUpdate();
	            }
	        }
	        
	        // 3) Increment the availableCopies in books
	        String updateBookCopiesQuery = 
	            "UPDATE books SET availableCopies = availableCopies + 1 " +
	            "WHERE barcode = ?";
	        pstmt = conn.prepareStatement(updateBookCopiesQuery);
	        pstmt.setString(1, bookBarcode);
	        
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            isSuccess = true; 
	        }
	        
	        
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        // Close resources
	        try {
	            if (rs != null) rs.close();
	        } catch (SQLException se) {
	            se.printStackTrace();
	        }
	        try {
	            if (pstmt != null) pstmt.close();
	        } catch (SQLException se) {
	            se.printStackTrace();
	        }
	        try {
	            if (updateStatusStmt != null) updateStatusStmt.close();
	        } catch (SQLException se) {
	            se.printStackTrace();
	        }
	       
	    }

	    return isSuccess;
	}



}
