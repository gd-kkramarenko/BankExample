package operations;

import domain.Client;

public class WithdrawOperation extends BankOperation {

    private Client client;

    private long amount;

    public WithdrawOperation(Client client, long amount) {
        this.client = client;
        this.amount = amount;
    }

    @Override
    public void doOperation() {
        synchronized (client) {
            long currentBalance = client.getAccount().getBalance();

            client.getAccount().setBalance(currentBalance - amount);

            for (long i = 1, m = 1; i <= 15000; i++) {
                m *= i;
            }
        }
    }


    @Override
    public void run() {
        doOperation();
    }
}
