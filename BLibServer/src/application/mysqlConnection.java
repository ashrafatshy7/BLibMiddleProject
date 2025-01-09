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

import javafx.scene.control.Alert;


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
	
	
	// stmt.executeUpdate("UPDATE subscriber SET phoneNumber='333333', email='kian@gmail.com' WHERE subscriber_id='211613708'");
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

	
	public static boolean isFieldUnique(String fieldName, String value) {
	    String query = "SELECT COUNT(*) FROM users WHERE " + fieldName + " = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setString(1, value);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt(1) == 0; // אם לא נמצאו ערכים זה אומר שהשדה ייחודי
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // אם יש שגיאה, נחשב לא ייחודי
	}

	public static boolean saveNewSubscriber(String readCard, String email, String password, String username, String phone) {
	    // בדיקה אם ReadCard או Email כבר קיימים
	    String checkQuery = "SELECT COUNT(*) FROM users WHERE id = ? OR email = ?";
	    try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
	        checkStmt.setString(1, readCard);
	        checkStmt.setString(2, email);
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
	    String subscriberQuery = 
	        "INSERT INTO users (id, email, password, username, phoneNumber, type, status) "
	        + "VALUES (?, ?, ?, ?, ?, 'student', 'active')";

	    try (PreparedStatement subscriberStmt = conn.prepareStatement(subscriberQuery)) {
	        // הגדרת הפרמטרים לפי הסדר הנכון:
	        subscriberStmt.setString(1, readCard);   // id
	        subscriberStmt.setString(2, email);      // email
	        subscriberStmt.setString(3, password);   // password
	        subscriberStmt.setString(4, username);   // username
	        subscriberStmt.setString(5, phone);      // phoneNumber

	        subscriberStmt.executeUpdate();
	        return true; // הצלחה
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; // שגיאה
	    }
	}


	
	
	
	
	// בדיקה אם subscriber_id קיים
	public static boolean isSubscriberIdExists(String id) {
	    String query = "SELECT 1 FROM users WHERE id = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setString(1, id);
	        ResultSet rs = pstmt.executeQuery();
	        return rs.next(); // מחזיר true אם קיים ID
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	// בדיקה אם email קיים
	public static boolean isEmailExists(String email) {
	    String query = "SELECT 1 FROM users WHERE email = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        ResultSet rs = pstmt.executeQuery();
	        return rs.next(); // מחזיר true אם קיים Email
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	
}