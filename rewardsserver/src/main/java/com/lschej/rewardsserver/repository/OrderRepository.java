package com.lschej.rewardsserver.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.lschej.rewardsserver.data.Order;
import com.lschej.rewardsserver.database.DatabaseController;

public class OrderRepository {

    Logger logger = Logger.getLogger(OrderRepository.class.getName());

    public void create(Order order, String phoneNumber) {
        String sql = """
            INSERT INTO Orders
                (MemberID, LocalOrderID, OrderDate, Total, PointsAwarded, PointsUsed)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection connection = DatabaseController.getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, phoneNumber);
            statement.setInt(2, order.getOrderNumber());
            statement.setObject(3, order.getOrderDate());
            statement.setBigDecimal(4, order.getOrderTotal());
            statement.setInt(5, order.getOrderTotal().multiply(BigDecimal.TEN).intValue());
            statement.setInt(6, 0);

            statement.executeUpdate();

            logger.info("Order row created.");
        } catch (Exception e) {
            String message = "SQL Exception " + e;
            logger.warning(message);
        }
    }

    public List<Order> retireveAll(String phoneNumber) {
        String sql = """
            SELECT ID, LocalOrderID, OrderDate, Total, PointsAwarded, PointsUsed
            FROM Orders
            WHERE MemberID = ?
        """;

        List<Order> orders = new ArrayList<>();

        try (Connection connection = DatabaseController.getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Order order = new Order(
                            results.getInt("ID"),
                            results.getInt("OrderID"),
                            results.getObject("OrderDate", LocalDate.class),
                            results.getBigDecimal("Total"),
                            results.getInt("PointsAwarded"),
                            results.getInt("PointsUsed"));
                    orders.add(order);
                }
            }

            logger.info("Order Rows retrieved.");
            return orders;

        } catch (Exception e) {
            String message = "SQL Exception: " + e + "Member " + phoneNumber;
            logger.warning(message);

            return orders;
        }
    }

    public Optional<Order> retrieve(int uniqueId) {
        String sql = """
            SELECT ID, LocalOrderID, OrderDate, Total, PointsAwarded, PointsUsed
            FROM Orders
            WHERE MemberID = ?
        """;

        try (Connection connection = DatabaseController.getConnection(); PreparedStatement statement = connection.prepareStatement(sql);) {
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    Order order = new Order(
                            results.getInt("ID"),
                            results.getInt("OrderID"),
                            results.getObject("OrderDate", LocalDate.class),
                            results.getBigDecimal("Total"),
                            results.getInt("PointsAwarded"),
                            results.getInt("PointsUsed"));
                    logger.info("Order row retrieved.");
                    return Optional.of(order);
                } else {
                    String message = "Orders searched. No id found: " + uniqueId;
                    logger.warning(message);
                    return Optional.empty();
                }
            }

        } catch (Exception e) {
            String message = "SQL Exception: " + e + "Member " + uniqueId;
            logger.warning(message);

            return Optional.empty();
        }
    }

    public void update(int uniqueId) {
        String sql = """
            UPDATE Orders
            SET MemberID,
                LocalOrderID,
                OrderDate,
                Total,
                PointsAwarded,
                PointsUsed
            WHERE ID = ?
        """;

        try (Connection connection = DatabaseController.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, uniqueId);

            statement.executeUpdate();

            logger.info("Order row updated");

        } catch (SQLException e) {
            String message = "SQL Exception " + e + "Order row update failed.";
            logger.warning(message);
        }
    }

    public void delete(int uniqueId) {
        String sql = """
            DELETE FROM Orders
            WHERE ID = ?
        """;

        try (Connection connection = DatabaseController.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, uniqueId);

            statement.executeUpdate();

            logger.info("Order row deleted");

        } catch (SQLException e) {
            String message = "SQL Exception " + e + "Order row deletion failed.";
            logger.warning(message);
        }
    }
}
