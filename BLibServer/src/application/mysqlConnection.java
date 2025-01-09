package application;

import java.sql.Blob;
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
	
	
	
	public static ArrayList<Book> getAllValues(Object msg) {
        Statement stmt = null;
        ResultSet resultSet = null;
        String tableName = (String) msg;
        ArrayList<Book> books = new ArrayList<>();

        try {
            String query = "SELECT * FROM " + tableName;
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery(query);

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
	
	
	public static ArrayList<Book> getTop5LoanedBooks() {
        Statement stmt = null;
        ResultSet resultSet = null;
        ArrayList<Book> books = new ArrayList<>();
        
        try {
            String query = ""
                + "SELECT b.*\n"
                + "FROM books b\n"
                + "JOIN (\n"
                + "    SELECT t.*, (@rank := @rank + 1) AS rn\n"
                + "    FROM top5LoanedBooks t, (SELECT @rank := 0) r\n"
                + "    ORDER BY t.loanedCount DESC, t.barcode ASC\n"
                + ") AS ranked_books ON b.barcode = ranked_books.barcode\n"
                + "WHERE ranked_books.rn <= 5\n"
                + "ORDER BY ranked_books.loanedCount DESC, ranked_books.barcode ASC;";

            stmt = conn.createStatement();
            resultSet = stmt.executeQuery(query);

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


