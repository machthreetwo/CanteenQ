package com.canteenq.jdbc;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcLedger {

    private final String url;
    private final String user;
    private final String password;

    public JdbcLedger(@Value("${spring.datasource.url}") String url,
                      @Value("${spring.datasource.username}") String user,
                      @Value("${spring.datasource.password}") String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection open() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @PostConstruct
    public void createTables() throws SQLException {
        try (Connection con = open(); Statement st = con.createStatement()) {
            st.execute("CREATE SEQUENCE IF NOT EXISTS closing_seq START WITH 1 INCREMENT BY 1");
            st.execute("CREATE TABLE IF NOT EXISTS day_closing ("
                    + "closing_no INT PRIMARY KEY, "
                    + "closing_date DATE UNIQUE, "
                    + "orders_count INT, "
                    + "cash_total DECIMAL(10,2), "
                    + "closed_at TIMESTAMP)");
        }
    }

    public LocalDate serverDate() throws SQLException {
        try (Connection con = open();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT CURRENT_DATE FROM DUAL")) {
            rs.next();
            return rs.getDate(1).toLocalDate();
        }
    }

    public Map<String, Object> closeDay(LocalDate day) throws SQLException {
        LocalDate date = day == null ? serverDate() : day;
        try (Connection con = open()) {
            con.setAutoCommit(false);
            try {
                if (alreadyClosed(con, date)) {
                    throw new IllegalStateException("Accounts for " + date + " are already closed");
                }
                int orders;
                double cash;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT COUNT(*), COALESCE(SUM(total), 0) FROM food_orders WHERE order_date = ?")) {
                    ps.setDate(1, Date.valueOf(date));
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        orders = rs.getInt(1);
                        cash = rs.getDouble(2);
                    }
                }
                if (orders == 0) {
                    throw new IllegalStateException("No orders found on " + date + ", nothing to close");
                }
                int closingNo;
                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery("SELECT NEXT VALUE FOR closing_seq FROM DUAL")) {
                    rs.next();
                    closingNo = rs.getInt(1);
                }
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO day_closing VALUES (?, ?, ?, ?, ?)")) {
                    ps.setInt(1, closingNo);
                    ps.setDate(2, Date.valueOf(date));
                    ps.setInt(3, orders);
                    ps.setDouble(4, cash);
                    ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                    ps.executeUpdate();
                }
                con.commit();

                Map<String, Object> row = new LinkedHashMap<>();
                row.put("closingNo", closingNo);
                row.put("date", date);
                row.put("orders", orders);
                row.put("cash", cash);
                return row;
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public List<Map<String, Object>> closings() throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection con = open();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM day_closing ORDER BY closing_no")) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("closingNo", rs.getInt("closing_no"));
                row.put("date", rs.getDate("closing_date").toLocalDate());
                row.put("orders", rs.getInt("orders_count"));
                row.put("cash", rs.getBigDecimal("cash_total"));
                row.put("closedAt", rs.getTimestamp("closed_at").toLocalDateTime().withNano(0));
                rows.add(row);
            }
        }
        return rows;
    }

    public List<String> lowStock(int below) throws SQLException {
        List<String> list = new ArrayList<>();
        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT name, stock FROM menu_items WHERE stock < ? ORDER BY stock")) {
            ps.setInt(1, below);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("name") + " - " + rs.getInt("stock") + " left");
                }
            }
        }
        return list;
    }

    private boolean alreadyClosed(Connection con, LocalDate date) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM day_closing WHERE closing_date = ?")) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }
}
