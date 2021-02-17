package domain;

import operations.BankOperation;

public class Bank {

    void processOperation(BankOperation operation) {
        operation.run();
    }
}
