package shop.storage;

import shop.Customer;

import java.sql.*;

// TODO: Task 2.2 c)
public class H2StoreImpl implements CustomerStore, CustomerStoreQuery {
    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:./customerDatabase";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    private Connection connection = null;

    @Override
    public void open() {
        try {
            Class.forName(DB_DRIVER);
            this.connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            this.connection.setAutoCommit(false);
            createTables();
            this.connection.commit();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTables() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
        Statement stmt = conn.createStatement();

        // Drop existing tables if they exist
        try {
            stmt.execute("DROP TABLE IF EXISTS ORDERITEMS");
            stmt.execute("DROP TABLE IF EXISTS ORDERS");
            stmt.execute("DROP TABLE IF EXISTS PRODUCTS");
            stmt.execute("DROP TABLE IF EXISTS CUSTOMERS");
        } catch (SQLException e) {
            // Tables might not exist, continue
        }

        // Create tables
        String createCustomer =
                "CREATE TABLE CUSTOMERS(id int primary key, name varchar(255))";
        String createOrder =
                "CREATE TABLE ORDERS(oid int primary key, customerId int, shippingAddress varchar(255))";
        String createProduct =
                "CREATE TABLE PRODUCTS(pid int primary key, pname varchar(255))";
        String createOrderItem =
                "CREATE TABLE ORDERITEMS(otid int auto_increment, orderId int, productId int)";

        stmt.execute(createCustomer);
        stmt.execute(createOrder);
        stmt.execute(createProduct);
        stmt.execute(createOrderItem);

        stmt.close();
        conn.close();
    }

    @Override
    public void insertCustomer(Customer customer) {
        try {
            String insertQuery = "INSERT INTO CUSTOMERS (id, name) VALUES (?, ?)";
            PreparedStatement pstmt = this.connection.prepareStatement(insertQuery);
            pstmt.setInt(1, customer.getCustomerId());
            pstmt.setString(2, customer.getUserName());
            pstmt.executeUpdate();
            connection.commit();
            pstmt.close();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanUp() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            // Delete all database files
            org.h2.tools.DeleteDbFiles.execute(".", "shopDB", true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryAllUsers() {
        try {
            String selectQuery = "SELECT * FROM CUSTOMERS";
            PreparedStatement pstmt = connection.prepareStatement(selectQuery);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("All Users:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void queryTopProduct() {
        try {
            String selectQuery =
                    "SELECT p.pid, p.pname, COUNT(oi.productId) as order_count " +
                            "FROM PRODUCTS p " +
                            "JOIN ORDERITEMS oi ON p.pid = oi.productId " +
                            "GROUP BY p.pid, p.pname " +
                            "ORDER BY order_count DESC " +
                            "LIMIT 1";

            PreparedStatement pstmt = connection.prepareStatement(selectQuery);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Top Product:");
                System.out.println("ID: " + rs.getInt("pid") +
                        ", Name: " + rs.getString("pname") +
                        ", Orders: " + rs.getInt("order_count"));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
