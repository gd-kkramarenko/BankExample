package domain;

import operations.BankOperation;

import java.util.List;

public class Bank implements Runnable{

    private List<BankOperation> operations;

    public Bank(List<BankOperation> operations) {
        this.operations = operations;
    }

    public void doOperations() {
        for(BankOperation operation: operations) {
            operation.doOperation();
        }
    }

    @Override
    public void run() {
        doOperations();
    }
}
