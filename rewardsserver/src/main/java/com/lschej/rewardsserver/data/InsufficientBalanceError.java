package com.lschej.rewardsserver.data;

public class InsufficientBalanceError extends RuntimeException {

    public InsufficientBalanceError(String message) {
        super(message);
    }
}
