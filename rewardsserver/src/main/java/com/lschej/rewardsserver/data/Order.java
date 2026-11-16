package com.lschej.rewardsserver.data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Order {

    private int id;
    private int orderNumber;
    private LocalDate orderDate;
    private BigDecimal orderTotal;
    private int pointsAwarded;
    private int pointsUsed;

    public Order(int orderNumber, LocalDate orderDate, BigDecimal orderTotal) {
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.orderTotal = orderTotal;
        pointsAwarded = orderTotal.multiply(BigDecimal.TEN).intValue(); //Temp
    }

    public Order(int id, int orderNumber, LocalDate orderDate, BigDecimal orderTotal, int pointsAwarded, int pointsUsed) {
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.orderTotal = orderTotal;
        this.pointsAwarded = pointsAwarded;
        this.pointsUsed = pointsUsed;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public BigDecimal getOrderTotal() {
        return orderTotal;
    }

    public int getPoints() {
        return pointsAwarded - pointsUsed;
    }

    public int usePoints(int pointsUsed) {
        this.pointsUsed += pointsUsed;
        return -(this.pointsAwarded - this.pointsUsed);
    }

    public int getPointsAwarded() {
        return pointsAwarded;
    }

    public int getPointsUsed() {
        return pointsUsed;
    }
}
