package operations;

import domain.Client;

public class DepositOperation extends BankOperation {

    private final Client client;

    private final double amount;

    public DepositOperation(Client client, double amount) {
        this.client = client;
        this.amount = amount;
    }

    @Override
    public void doOperation() {
        synchronized (client) {
            double currentBalance = client.getAccount().getBalance();
            client.getAccount().setBalance(currentBalance + amount);

        }
    }


    @Override
    public void run() {
        doOperation();
    }
}
