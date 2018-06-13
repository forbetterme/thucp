package model.CPMRM;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {

	    /**
	     * 获取Connection
	     */
	    public static Connection getConnection(String className,String url,String userName,String password ) throws ClassNotFoundException, SQLException {
	        Connection conn = null;
	         Class.forName(className);
	         conn = DriverManager.getConnection(url, userName, password);
	        return conn;
	    }
	    /**
	     * 关闭ResultSet
	     * @param rs
	     */
	    public static void closeResultSet(ResultSet rs) {
	        if (rs != null) {
	            try {
	                rs.close();
	            } catch (SQLException e) {
	                System.out.println(e.getMessage());
	            }
	        }
	    }
	    /**
	     * 关闭Statement
	     * @param stmt
	     */
	    public static void closeStatement(Statement stmt) {
	        if (stmt != null) {
	            try {
	                stmt.close();
	            }
	            catch (Exception e) {
	                System.out.println(e.getMessage());
	            }
	        }
	    }
	    /**
	     * 关闭ResultSet、Statement
	     * @param rs
	     * @param stmt
	     */
	    public static void closeStatement(ResultSet rs, Statement stmt) {
	        closeResultSet(rs);
	        closeStatement(stmt);
	    }
	    /**
	     * 关闭PreparedStatement
	     * @param pstmt
	     * @throws SQLException
	     */
	    public static void fastcloseStmt(PreparedStatement pstmt) throws SQLException
	    {
	        pstmt.close();
	    }
	    /**
	     * 关闭ResultSet、PreparedStatement
	     * @param rs
	     * @param pstmt
	     * @throws SQLException
	     */
	    public static void fastcloseStmt(ResultSet rs, PreparedStatement pstmt) throws SQLException
	    {
	        rs.close();
	        pstmt.close();
	    }
	    /**
	     * 关闭ResultSet、Statement、Connection
	     * @param rs
	     * @param stmt
	     * @param con
	     */
	    public static void closeConnection(ResultSet rs, Statement stmt, Connection con) {
	        closeResultSet(rs);
	        closeStatement(stmt);
	        closeConnection(con);
	    }
	    /**
	     * 关闭Statement、Connection
	     * @param stmt
	     * @param con
	     */
	    public static void closeConnection(Statement stmt, Connection con) {
	        closeStatement(stmt);
	        closeConnection(con);
	    }
	    /**
	     * 关闭Connection
	     * @param con
	     */
	    public static void closeConnection(Connection con) {
	        if (con != null) {
	            try {
	               con.close();
	            }
	            catch (Exception e) {
	                System.out.println(e.getMessage());
	            }
	        }
	    }
}
