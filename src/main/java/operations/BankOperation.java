package operations;

import exceptions.InsufficientBalanceException;

public abstract class BankOperation implements Runnable {
    public abstract void doOperation() throws InsufficientBalanceException;
}
