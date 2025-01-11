package application;

import java.sql.Blob;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import enteties.Book;






public class mysqlConnection {
	
	
	private static Connection conn;

	public static void connectToDB() 
	{
		try 
		{
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            System.out.println("Driver definition succeed");
        } catch (Exception ex) {
        	/* handle the error*/
        	 System.out.println("Driver definition failed");
        	 }
        
        try 
        {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/middleproject?serverTimezone=IST","root","Aa123456");
            System.out.println("SQL connection succeed");
          
            
            
     	} catch (SQLException ex) 
     	    {/* handle any errors*/
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
	            Book book = new Book(
	                barcode,
	                title,
	                author,
	                category,
	                description,
	                availableCopies,
	                imageBytes
	            );

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
	            if (resultSet != null) resultSet.close();
	        } catch (SQLException se) {
	            se.printStackTrace();
	        }
	        try {
	            if (stmt != null) stmt.close();
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
	            if (resultSet != null) resultSet.close();
	        } catch (SQLException se) {
	            se.printStackTrace();
	        }
	        try {
	            if (pstmt != null) pstmt.close();
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
	        String query = ""
	            + "SELECT b.*\n"
	            + "FROM books b\n"
	            + "JOIN (\n"
	            + "    SELECT t.*, (@rank := @rank + 1) AS rn\n"
	            + "    FROM loanedbooks t, (SELECT @rank := 0) r\n"
	            + "    ORDER BY t.loanedCount DESC, t.barcode ASC\n"
	            + ") AS ranked_books ON b.barcode = ranked_books.barcode\n"
	            + "WHERE ranked_books.rn <= 5\n"
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
	            Book book = new Book(
	                barcode,
	                title,
	                author,
	                category,
	                description,
	                availableCopies,
	                imageBytes
	            );

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

	            String shelfQuery = "SELECT barcode, shelf FROM bookshelf WHERE barcode IN (" + barcodeList.toString() + ")";
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
	            if (resultSet != null) resultSet.close();
	        } catch (SQLException se) {
	            se.printStackTrace();
	        }
	        try {
	            if (stmt != null) stmt.close();
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
	            if (rs != null) rs.close();
	        } catch (SQLException se) {
	            se.printStackTrace();
	        }
	        try {
	            if (pstmt != null) pstmt.close();
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
	    sb.append("UPDATE "+data.get(0)+" SET ");
	    for(int i=2; i<data.size(); i+=2) {
	    	sb.append(data.get(i)+"='"+data.get(i+1)+"'");
	    	if(i!=data.size()-2) sb.append(", ");
	    	else sb.append(" ");
	    }
	    if(data.get(0).equals("subscribers"))
	    	sb.append("WHERE subscriber_id='"+data.get(1)+"'");
	    try {
	    pstmt = conn.prepareStatement(sb.toString());
	    pstmt.executeUpdate();
	    pstmt.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
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
	        String query = "INSERT INTO users (UserName, ID, Department, Tel) VALUES ('"+ userName + "', "+ id + ", '"+ department + "', '"+ tel + "')";

	        stmt = conn.createStatement();
	        stmt.executeUpdate(query);
	        stmt.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	
	
}


