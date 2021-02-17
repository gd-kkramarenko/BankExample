package operations;

import domain.Client;
import exceptions.InsufficientBalanceException;

public class WithdrawOperation extends BankOperation {

    private Client client;

    private double amount;

    public WithdrawOperation(Client client, double amount) {
        this.client = client;
        this.amount = amount;
    }

    private void withdraw() throws InsufficientBalanceException {
        synchronized (client) {
            double currentBalance = client.getAccount().getBalance();

            if ((currentBalance - amount) < 0) {
                throw new InsufficientBalanceException("Insufficient balance:\n" +
                        client.getFirstName() + " - Balance - " + currentBalance + "$\n" +
                        "Not able to withdraw " + amount + "$");
            }

            client.getAccount().setBalance(currentBalance - amount);
            System.out.println(
                    "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n" +
                            "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n" +
                            client.getFirstName() + " - CURRENT BALANCE: " + currentBalance + "$\n" +
                            client.getFirstName() + " - successfully withdrew " + amount + "$\n" +
                            client.getFirstName() + " - NEW BALANCE: " + client.getAccount().getBalance() + "$\n"
            );
        }
    }

    @Override
    public void run() {
        try {
            withdraw();
        } catch (InsufficientBalanceException e) {
            System.out.println(e.getMessage());
        }
    }
}
