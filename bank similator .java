import java.sql.*;
import java.util.Scanner;

public class BankSimulator {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String USER = "root";
    private static final String PASS = "password";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            while (true) {
                System.out.println("\n*** BANKING SYSTEM ***");
                System.out.println("1. Create Account");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Choice: ");

                int choice = sc.nextInt();
                sc.nextLine();

                if (choice == 1) {
                    createAccount(conn, sc);
                } else if (choice == 2) {
                    loginAndShowMenu(conn, sc);
                } else if (choice == 3) {
                    System.out.println("Exiting...");
                    break;
                } else {
                    System.out.println("Invalid choice.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }

    private static void createAccount(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Set 4-digit PIN: ");
        int pin = sc.nextInt();
        sc.nextLine();

        String sql = "INSERT INTO account (name, pin, balance) VALUES (?, ?, 0)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, pin);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    System.out.println("Account Created! Acc No: " + rs.getInt(1));
                }
            }
        }
    }

    private static void loginAndShowMenu(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Acc No: ");
        int accNo = sc.nextInt();
        sc.nextLine();

        System.out.print("PIN: ");
        int pin = sc.nextInt();
        sc.nextLine();

        String sql = "SELECT * FROM account WHERE acc_no=? AND pin=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accNo);
            pstmt.setInt(2, pin);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Login successful! Welcome " + rs.getString("name"));
                    userMenu(conn, sc, accNo);
                } else {
                    System.out.println("Invalid credentials!");
                }
            }
        }
    }

    private static void userMenu(Connection conn, Scanner sc, int accNo) throws SQLException {
        while (true) {
            System.out.println("\n1. Deposit  2. Withdraw  3. Balance  4. Logout");
            System.out.print("Select: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                System.out.print("Enter Amount to Deposit: ");
                double amt = sc.nextDouble();
                sc.nextLine();

                String sql = "UPDATE account SET balance = balance + ? WHERE acc_no = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setDouble(1, amt);
                    pstmt.setInt(2, accNo);
                    pstmt.executeUpdate();
                }
                System.out.println("Amount Deposited.");
            } else if (choice == 2) {
                System.out.print("Enter Amount to Withdraw: ");
                double amt = sc.nextDouble();
                sc.nextLine();

                String chkSql = "SELECT balance FROM account WHERE acc_no=?";
                try (PreparedStatement chkStmt = conn.prepareStatement(chkSql)) {
                    chkStmt.setInt(1, accNo);
                    try (ResultSet rs = chkStmt.executeQuery()) {
                        if (rs.next()) {
                            double bal = rs.getDouble("balance");
                            if (bal >= amt) {
                                String sql = "UPDATE account SET balance = balance - ? WHERE acc_no = ?";
                                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                    pstmt.setDouble(1, amt);
                                    pstmt.setInt(2, accNo);
                                    pstmt.executeUpdate();
                                }
                                System.out.println("Please collect cash.");
                            } else {
                                System.out.println("Error: Insufficient Balance!");
                            }
                        }
                    }
                }
            } else if (choice == 3) {
                String sql = "SELECT balance FROM account WHERE acc_no=?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, accNo);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("Current Balance: Rs " + rs.getDouble("balance"));
                        }
                    }
                }
            } else {
                System.out.println("Logging out...");
                break;
            }
        }
    }
}
