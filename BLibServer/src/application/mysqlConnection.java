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
import javafx.application.Platform;
import javafx.scene.control.Alert;
import enteties.Librarian;
import enteties.Loan;
import enteties.Order;

/**
 * Handles MySQL database connection and operations.
 */
public class mysqlConnection {

	private static Connection conn;

	/**
     * Establishes a connection to the MySQL database.
     */
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

	/**
     * Retrieves all books from the database.
     *
     * @return ArrayList of Book objects.
     */
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

	 /**
     * Places an order for a book.
     *
     * @param msg Order object containing subscriber ID and book barcode.
     * @return Success message if the order was placed successfully.
     */
	public static String orderBook(Object msg) {
	    Order orderBook = (Order) msg;
	    String barcode = orderBook.getBarcode();
	    String id = orderBook.getSubscriberID();

	    String result = null;
	    PreparedStatement pstmt = null;

	    try {
	        String query = "INSERT INTO orders (subscriberID, requestDate, barcode) VALUES (?, ?, ?)";
	        pstmt = conn.prepareStatement(query);

	        // Format: yyyy-MM-dd HH:mm:ss
	        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String requestDateString = formatter.format(new Date()); // Now

	        pstmt.setString(1, id);
	        pstmt.setString(2, requestDateString);
	        pstmt.setString(3, barcode);

	        int rowsInserted = pstmt.executeUpdate();
	        if (rowsInserted > 0) {
	            result = "success";
	            
	            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
				String requestDateOnly = dateFormatter.format(new Date());

				String bookTitleQuery = "SELECT title FROM books WHERE barcode = ?";
				String orderHistoryQuery = "INSERT INTO orderhistory (orderDate, subscriberID, bookTitle) VALUES (?, ?, ?)";

				PreparedStatement bookTitleStmt = null;
				PreparedStatement orderHistoryStmt = null;
	            
				bookTitleStmt = conn.prepareStatement(bookTitleQuery);
				bookTitleStmt.setString(1, barcode);
				ResultSet rs = bookTitleStmt.executeQuery();

				String bookTitle = null;
				if (rs.next()) {
					bookTitle = rs.getString("title");
				} else {
					throw new SQLException("No book found for the given barcode: " + barcode);
				}

				orderHistoryStmt = conn.prepareStatement(orderHistoryQuery);
				orderHistoryStmt.setString(1, requestDateOnly);
				orderHistoryStmt.setString(2, id);
				orderHistoryStmt.setString(3, bookTitle);
				orderHistoryStmt.executeUpdate();
	            
	        } else {
	            result = "error";
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        // You could explicitly set result to "error" in case of an exception
	        result = "error";
	    } finally {
	        try {
	            if (pstmt != null)
	                pstmt.close();
	        } catch (SQLException se) {
	            se.printStackTrace();
	        }
	    }
	    return result;
	}
	

	/**
     * Checks if a book has already been ordered by the subscriber.
     *
     * @param msg Order object containing subscriber ID and book barcode.
     * @return Status of the order.
     */
	public static String checkOrderedBook(Object msg) {
	    Order orderBook = (Order) msg;
	    String barcode = orderBook.getBarcode();
	    String id = orderBook.getSubscriberID();
	    
	    String result = null;
	    

	    String queryCheckLoan = "SELECT dueDate FROM loan WHERE barcode = ? AND id = ? AND returnDate IS NULL";

	    String queryCheckOrder = "SELECT 1 FROM orders WHERE barcode = ? AND subscriberID = ? LIMIT 1";

	    try (
	        PreparedStatement pstmtLoan = conn.prepareStatement(queryCheckLoan);
	        PreparedStatement pstmtOrder = conn.prepareStatement(queryCheckOrder)
	    ) {
	        pstmtLoan.setString(1, barcode);
	        pstmtLoan.setString(2, id);
	        try (ResultSet loanRS = pstmtLoan.executeQuery()) {
	            if (loanRS.next()) {
	                // There is an active loan for this book (returnDate is null)
	                result = "UserHasActiveLoan";
	                return result;
	            }
	        }

	        pstmtOrder.setString(1, barcode);
	        pstmtOrder.setString(2, id);
	        try (ResultSet orderRS = pstmtOrder.executeQuery()) {
	            if (orderRS.next()) {
	            	result = "OrderExists";
	                return result;
	            }
	        }

	        result = "success";
            
	    } catch (SQLException e) {
	        e.printStackTrace();
	        result = "error";
	    }

	    return result;
	}


	/**
     * Removes orders that are older than 2 days.
     */
	public static void removeOrder() {
		PreparedStatement pstmt = null;

		try {
			// SQL query to delete orders older than 2 days
			String query = "DELETE FROM orders WHERE requestDate < NOW() - INTERVAL 2 DAY";
			pstmt = conn.prepareStatement(query);

			// Execute the deletion
			int affectedRows = pstmt.executeUpdate();
			System.out.println(affectedRows + " old orders removed successfully.");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
     * Retrieves the top 5 loaned books.
     *
     * @return ArrayList of the top 5 loaned Book objects.
     */
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

	 /**
     * Gets the earliest return date for a book.
     *
     * @param barcode Book barcode.
     * @return Earliest return date in string format.
     */
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

	
	/**
     * Updates values in the database based on provided data.
     *
     * @param msg List containing update details.
     */
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

	
	/**
     * Saves a new subscriber to the database.
     *
     * @param msg Subscriber object.
     * @return Map containing the result of the operation.
     */
	public static Map<String, String> saveNewSubscriber(Object msg) {
		Subscriber sub = (Subscriber) msg;
		Map<String, String> result = new HashMap<>();
		String checkQuery = "SELECT COUNT(*) FROM users WHERE id = ? OR email = ?";
		try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
			checkStmt.setString(1, sub.getID());
			checkStmt.setString(2, sub.getEmail());
			ResultSet rs = checkStmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {

				result.put("type", "UserExists");
				result.put("message", "Duplicate ReadCard or Email found in database.");
				return result;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			result.put("type", "error");
			result.put("message", "error in register. try again later.");
			return result;
		}

		String subscriberQuery = "INSERT INTO users (id, email, password, username, phoneNumber, type, status) "
				+ "VALUES (?, ?, ?, ?, ?, 'Subscriber', 'Active')";

		try (PreparedStatement subscriberStmt = conn.prepareStatement(subscriberQuery)) {
			subscriberStmt.setString(1, sub.getID());
			subscriberStmt.setString(2, sub.getEmail());
			subscriberStmt.setString(3, sub.getPassword());
			subscriberStmt.setString(4, sub.getName());
			subscriberStmt.setString(5, sub.getPhoneNumber());

			subscriberStmt.executeUpdate();

			result.put("type", "success");
			result.put("message", "Registration process completed successfully");
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			result.put("type", "error");
			result.put("message", "error in register. try again later.");
			return result;
		}
	}

	
	 /**
     * Saves user information to the database.
     *
     * @param msg User details.
     */
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

	 /**
     * Updates subscriber details.
     *
     * @param msg Subscriber details.
     */
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

	
	/**
     * Retrieves card details if the card exists.
     *
     * @param cardNum Card number.
     * @return Map containing card details.
     */
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

				// Step 3: Fetch loan history for the cardNum, joining with the books table
				String loanHistoryQuery = "SELECT b.title AS bookTitle, l.dueDate, l.borrowDate " + "FROM loan l "
						+ "JOIN books b ON l.barcode = b.barcode " + "WHERE l.id = ?";
				preparedStatement = conn.prepareStatement(loanHistoryQuery);
				preparedStatement.setString(1, cardNum);

				resultSet = preparedStatement.executeQuery();

				List<Map<String, Object>> loanHistory = new ArrayList<>();

				while (resultSet.next()) {
					Map<String, Object> row = new HashMap<>();
					row.put("title", resultSet.getString("bookTitle")); // Get the title from the books table

					// Add 1 day to the dates
					LocalDate dueDate = resultSet.getDate("dueDate").toLocalDate().plusDays(1);
					LocalDate borrowDate = resultSet.getDate("borrowDate").toLocalDate().plusDays(1);

					row.put("returnDate", dueDate);
					row.put("borrowDate", borrowDate);
					loanHistory.add(row);
				}

				result.put("loanHistory", loanHistory);

				// Close resources
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
					String barcode = resultSet.getString("barcode");

					row.put("issueType", resultSet.getString("issueType"));
					row.put("issueDate", resultSet.getDate("issueDate"));

					String bookTitleQuery = "SELECT title FROM books WHERE barcode = ?";
					try (PreparedStatement bookStatement = conn.prepareStatement(bookTitleQuery)) {
						bookStatement.setString(1, barcode);
						try (ResultSet bookResultSet = bookStatement.executeQuery()) {
							if (bookResultSet.next()) {
								row.put("title", bookResultSet.getString("title"));
							} else {
								row.put("title", "Unknown Title");
							}
						}
					}
					issuesHistory.add(row);
				}

				System.out.println("issuesHistory MAP = " + issuesHistory.toString());

				result.put("issuesHistory", issuesHistory);

				// Close resources
				resultSet.close();
				preparedStatement.close();

				// order history
				String orderHistoryQuery = "SELECT orderDate, bookTitle FROM orderhistory WHERE subscriberId = ?";
				preparedStatement = conn.prepareStatement(orderHistoryQuery);
				preparedStatement.setString(1, cardNum);

				resultSet = preparedStatement.executeQuery();

				List<Map<String, Object>> orderHistory = new ArrayList<>();

				while (resultSet.next()) {
					Map<String, Object> row = new HashMap<>();
					row.put("orderDate", resultSet.getString("orderDate"));
					row.put("bookTitle", resultSet.getString("bookTitle"));
					orderHistory.add(row);
				}

				System.out.println("orderHistory MAP = " + orderHistory.toString());

				result.put("orderHistory", orderHistory);

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
		System.out.println("result MAP = " + result.toString());
		return result;
	}


	
	 /**
     * Updates a subscriber's email and phone number.
     *
     * @param email Email address.
     * @param phoneNumber Phone number.
     * @param cardNum Card number.
     * @return True if the update was successful, false otherwise.
     */
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

	/**
     * Updates the return date of a book loan.
     *
     * @param messageData Loan details.
     * @return True if the update was successful, false otherwise.
     */
	// key: Loan,,, subscriber[0] -> ID, librarian[1] -> ID, book[2] -> name
	public static boolean updateReturnDate(Object messageData) {

	    Map<Loan, ArrayList<Object>> updatedDetails = (Map<Loan, ArrayList<Object>>) messageData;

	    String barcodeQuery = "SELECT barcode FROM books WHERE title = ?";
	    // Set the returnDate column, rather than dueDate
	    String updateLoanQuery = 
	          "UPDATE loan "
	        + "   SET dueDate = ?, "
	        + "       librarianUserName = ?, "
	        + "       updateReturnDate = ? "
	        + " WHERE id = ? "
	        + "   AND borrowDate = ? "
	        + "   AND barcode = ?";
	    String getLibrarianNameQuery = 
	          "SELECT username FROM users WHERE id = ? AND type = 'Librarian'";

	    LocalDate currentDate = LocalDate.now();
	    String currentDateStr = currentDate.toString();

	    for (Loan loan : updatedDetails.keySet()) {
	        ArrayList<Object> value = updatedDetails.get(loan);
	        Subscriber subscriber = new Subscriber(((Subscriber) value.get(0)).getID());
	        Librarian librarian = new Librarian(((Librarian) value.get(1)).getID());
	        Book book = new Book(((Book) value.get(2)).getTitle());

	        try (PreparedStatement barcodeStmt = conn.prepareStatement(barcodeQuery);
	             PreparedStatement updateLoanStmt = conn.prepareStatement(updateLoanQuery);
	             PreparedStatement updateLibrarianNameStmt = conn.prepareStatement(getLibrarianNameQuery)) {

	            // 1. Find the book’s barcode based on its title
	            barcodeStmt.setString(1, book.getTitle());
	            ResultSet rs = barcodeStmt.executeQuery();

	            if (rs.next()) {
	                book.setBarcode(rs.getString("barcode"));
	            } else {
	                System.err.println("Barcode not found for bookTitle: " + book.getTitle());
	                continue;
	            }

	            // 2. Retrieve the librarian's username
	            updateLibrarianNameStmt.setString(1, librarian.getID());
	            rs = updateLibrarianNameStmt.executeQuery();

	            if (rs.next()) {
	                librarian.setName(rs.getString("username"));
	            } else {
	                System.err.println("librarian name invalid");
	                continue;
	            }

	            // Debugging prints
	            System.out.println("loan.getReturnDate(): " + loan.getReturnDate());
	            System.out.println("librarian.getName(): " + librarian.getName());
	            System.out.println("currentDateStr: " + currentDateStr);
	            System.out.println("subscriber.getID(): " + subscriber.getID());
	            System.out.println("loan.getBorrowDate(): " + loan.getBorrowDate());
	            System.out.println("book.getBarcode(): " + book.getBarcode());

	            // 3. Update the row in the loan table
	            updateLoanStmt.setString(1, loan.getReturnDate());     // returnDate
	            updateLoanStmt.setString(2, librarian.getName());      // librarianUserName
	            updateLoanStmt.setString(3, currentDateStr);           // updateReturnDate
	            updateLoanStmt.setString(4, subscriber.getID());       // WHERE id = ?
	            updateLoanStmt.setString(5, loan.getBorrowDate());     // WHERE borrowDate = ?
	            updateLoanStmt.setString(6, book.getBarcode());        // WHERE barcode = ?

	            int rowsUpdated = updateLoanStmt.executeUpdate();
	            if (rowsUpdated == 0) {
	                System.err.println("Failed to update loan for book: " + book.getTitle());
	            } else {
	                System.out.println("Successfully updated loan for book: " + book.getTitle());
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }

	    return true;
	}


	/**
     * Retrieves books with extended return dates.
     *
     * @param number Subscriber card number.
     * @return Map containing book titles and their extended return dates.
     */
	public static Map<String, String> getExtendedBooks(Object number) {
		String cardNum = (String) number;
		Map<String, String> loanMap = new LinkedHashMap<>(); // Using LinkedHashMap to maintain insertion order
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		// Updated query to join the 'loan' and 'books' tables using barcode
		String query = "SELECT b.title, l.dueDate FROM loan l " + "JOIN books b ON l.barcode = b.barcode " + 
				"WHERE l.id = ? AND l.dueDate BETWEEN CURDATE() AND CURDATE() + INTERVAL 7 DAY ";

		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, cardNum); // Set the cardNum parameter

			// Execute the query
			resultSet = preparedStatement.executeQuery();

			// Process the result set
			while (resultSet.next()) {
				String bookTitle = resultSet.getString("title");
				String returnDate = resultSet.getDate("dueDate").toLocalDate().plusDays(1).toString();

				// Store in the map
				loanMap.put(bookTitle, returnDate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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

	 /**
     * Updates the extension return date of a book loan.
     *
     * @param messageData Map containing the subscriber card number, book title, and return date.
     * @return True if the extension update was successful, false otherwise.
     */
	public static boolean updateExtensionReturnDate(Object messageData) {
		Map<String, String> data = (Map<String, String>) messageData;

		String cardNum = data.get("cardNum");
		String bookTitle = data.get("bookTitle");
		String returnDate = data.get("returnDate");

		try {
			// SQL query to get the barcode from the books table based on bookTitle
			String getBarcodeQuery = "SELECT barcode FROM books WHERE title = ?";
			PreparedStatement getBarcodeStatement = conn.prepareStatement(getBarcodeQuery);
			getBarcodeStatement.setString(1, bookTitle);

			// Execute the query to get the barcode
			ResultSet barcodeResult = getBarcodeStatement.executeQuery();
			String barcode = null;

			if (barcodeResult.next()) {
				barcode = barcodeResult.getString("barcode");
			} else {
				// If no matching bookTitle found, return false
				return false;
			}

			// SQL query to check if there is an order for the same barcode
			String checkOrderQuery = "SELECT COUNT(*) FROM orders WHERE barcode = ?";
			PreparedStatement checkOrderStatement = conn.prepareStatement(checkOrderQuery);
			checkOrderStatement.setString(1, barcode);

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
			String updateQuery = "UPDATE loan SET dueDate = ? WHERE id = ? AND barcode = ? AND dueDate = ?";
			PreparedStatement updateStatement = conn.prepareStatement(updateQuery);

			// Set the parameters for the query
			updateStatement.setString(1, updatedReturnDate); // Set the new returnDate
			updateStatement.setString(2, cardNum); // Set cardNum
			updateStatement.setString(3, barcode); // Set barcode (not bookTitle)
			updateStatement.setString(4, returnDate);

			// Execute the update query
			int rowsUpdated = updateStatement.executeUpdate();

			// If rowsUpdated > 0, the update was successful
			
			boolean  success = rowsUpdated > 0;
			
			if (success) {
			    Platform.runLater(() -> {
			        Alert alert = new Alert(Alert.AlertType.INFORMATION);
			        alert.setTitle("Librarian Message");
			        alert.setHeaderText(null);
			        alert.setContentText("Subscriber: " + cardNum + " extended book: " + bookTitle);
			        alert.showAndWait();
			    });
			}

			
			return success;
			
			
			

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			System.err.println("Error parsing or updating date: " + e.getMessage());
			return false;
		}
	}

	
	/**
     * Logs in a user.
     *
     * @param message Login credentials.
     * @return User object if login is successful.
     */
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
					boolean status = rs.getString("status").equals("Active");
					user = new Subscriber(rs.getString("id"), rs.getString("username"), rs.getString("phoneNumber"),
							rs.getString("email"), status);
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

	
	/**
     * Processes the return of a borrowed book.
     *
     * @param message Return details.
     * @return Map with return status and message.
     */
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
				String insertLateBookSQL = "INSERT INTO issuehistory (id, issueType, issueDate, barcode) VALUES (?, ?, ?, ?)";
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

			String selectOrderSQL = "SELECT id FROM orders WHERE barcode = ? AND subscriberID = ? AND notification = 0 "
					+ "ORDER BY requestDate ASC " + "LIMIT 1";

			pstmt.close();
			pstmt = conn.prepareStatement(selectOrderSQL);
			pstmt.setString(1, bookBarcode);
			pstmt.setString(2, readerCard);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				String orderId = rs.getString("id");
				String updateOrderSQL = "UPDATE orders " + "SET notification = 1 " + "WHERE id = ?";
				pstmt.close();
				pstmt = conn.prepareStatement(updateOrderSQL);
				pstmt.setString(1, orderId);
				pstmt.executeUpdate();

				 Platform.runLater(() -> {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Book Arrived");
				alert.setHeaderText(null);
				alert.setContentText("Email has sent to: " + readerCard + "\n" + bookBarcode
						+ " has been arrived and waiting for you.");
				alert.showAndWait();
				 });

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

	
	/**
     * Checks the subscriber's status.
     *
     * @param message Subscriber ID.
     * @return Map containing the subscriber's status.
     */
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

	
	/**
     * Creates a new loan for a book.
     *
     * @param message Loan details.
     * @return Map with loan creation status.
     */
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
		PreparedStatement pstmtCheckLoanExists = null;
	    ResultSet rsCheckLoanExists = null;

		try {
			ArrayList<Object> msg = (ArrayList<Object>) message;
			Loan loan = (Loan) msg.get(0);
			Subscriber subscriber = (Subscriber) msg.get(1);

			String subscriberID = subscriber.getID();
			String bookBarcode = loan.getBarcode();
			String borrowDateStr = loan.getBorrowDate();
			String dueDateStr = loan.getReturnDate();
			
			
			
			String sqlCheckDuplicateLoan = "SELECT * FROM loan WHERE id = ? AND barcode = ? AND borrowDate = ?";
	        pstmtCheckLoanExists = conn.prepareStatement(sqlCheckDuplicateLoan);
	        pstmtCheckLoanExists.setString(1, subscriberID);
	        pstmtCheckLoanExists.setString(2, bookBarcode);
	        pstmtCheckLoanExists.setDate(3, java.sql.Date.valueOf(borrowDateStr));
	        rsCheckLoanExists = pstmtCheckLoanExists.executeQuery();
	        if (rsCheckLoanExists.next()) {
	            result.put("type", "unsuccessful");
	            result.put("message", "Loan was not successful. A loan with the same subscriber, barcode, and borrow date already exists.");
	            return result;
	        }
	        rsCheckLoanExists.close();
	        pstmtCheckLoanExists.close();
			
			

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
				ordersList.add(new Order(rsOrders.getString("subscriberID"), rsOrders.getTimestamp("requestDate"),
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

				String sqlDeleteOrder = "DELETE FROM orders WHERE subscriberID = ? AND barcode = ?";
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

	
	 /**
     * Retrieves loan data for generating reports.
     *
     * @param message Report parameters including month and year.
     * @return Map with categorized loan data.
     */
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

	
	/**
     * Retrieves status data for generating reports.
     *
     * @param message Report parameters including month and year.
     * @return Map containing active and frozen subscriber counts.
     */
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


	/**
     * Performs end-of-month processing for loan reports.
     */
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

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	 /**
     * Performs end-of-month processing for subscriber status reports.
     */
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

	
	/**
     * Retrieves a connection to the database.
     *
     * @return Connection object.
     */
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

	
	 /**
     * Checks due dates and sends SMS notifications for book returns.
     *
     * @return True if SMS notifications were sent, false otherwise.
     */
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
