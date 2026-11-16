package com.lschej.rewardsserver.data;

import java.time.LocalDate;

public class Member {

    private String PhoneNumber;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private String emailAddress;
    private OrderHistory orderHistory;
    private int points;

    public Member(String number, String fName, String lName, String birthday, String email) {
        PhoneNumber = number;
        firstName = fName;
        lastName = lName;
        this.birthday = LocalDate.parse(birthday);
        emailAddress = email;
        orderHistory = new OrderHistory();
        rewards = new MemberRewards();
    }

    public Member(String number, String fName, String lName, LocalDate birthday, String email) {
        PhoneNumber = number;
        firstName = fName;
        lastName = lName;
        this.birthday = birthday;
        emailAddress = email;
        orderHistory = new OrderHistory();
        rewards = new MemberRewards();
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.PhoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    private void certifyRewards() {
        // Need to refactor this method
        // while (rewards.isExpired()) {
        //     if (orderHistory.getOrderCount() == 0) {
        //         rewards.updatePoints(0, LocalDate.now().plusDays(1));
        //         break;
        //     }
        //     orderHistory.removeOldest();
        //     rewards.updatePoints(orderHistory.collectPoints(), orderHistory.getOldesDate());
        // }
        // rewards.updatePoints(orderHistory.collectPoints(), orderHistory.getOldesDate());
    }

    public void addOrder(Order order) {
        orderHistory.addOrder(order);
        certifyRewards();
    }

    public int getPoints() {
        certifyRewards();
        return points;
    }

    public OrderHistory getOrderHistory() {
        return orderHistory;
    }

    public boolean hasSufficientBalance(int pointsToRedeem) {
        certifyRewards();
        return (points - pointsToRedeem) >= 0;
    }

    public void redeemPoints(int points) {
        if (hasSufficientBalance(points)) {
            orderHistory.redeemPoints(points);
            points = orderHistory.collectPoints();
        } else {
            throw new InsufficientBalanceError("B: " + points + ", P: " + points);
        }
    }
}
