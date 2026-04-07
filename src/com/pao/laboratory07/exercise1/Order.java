package com.pao.laboratory07.exercise1;

import com.pao.laboratory07.exercise1.exceptions.CannotCancelFinalOrderException;
import com.pao.laboratory07.exercise1.exceptions.CannotRevertInitialOrderStateException;
import com.pao.laboratory07.exercise1.exceptions.OrderIsAlreadyFinalException;

import java.util.ArrayDeque;
import java.util.Deque;

public class Order {
    private OrderState state;
    private final Deque<OrderState> history;

    public Order(OrderState initialState) {
        this.state = initialState;
        this.history = new ArrayDeque<>();
    }

    public void nextState() throws OrderIsAlreadyFinalException {
        if (state.isFinal()) {
            throw new OrderIsAlreadyFinalException();
        }

        history.push(state);
        state = state.next();
        System.out.println("Order state updated to: " + state);
    }

    public void cancel() throws CannotCancelFinalOrderException {
        if (state.isFinal()) {
            throw new CannotCancelFinalOrderException();
        }

        history.push(state);
        state = OrderState.CANCELED;
        System.out.println("Order has been canceled.");
    }

    public void undoState() throws CannotRevertInitialOrderStateException {
        if (history.isEmpty()) {
            throw new CannotRevertInitialOrderStateException();
        }

        state = history.pop();
        System.out.println("Order state reverted to: " + state);
    }
}
