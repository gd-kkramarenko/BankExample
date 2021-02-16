package operations;

import domain.Client;

public class DepositOperation extends BankOperation {

    private Client client;

    private double amount;

    public DepositOperation(Client client, double amount) {
        this.client = client;
        this.amount = amount;
    }

    private void deposit() {
        synchronized (client) {
            double currentBalance = client.getAccount().getBalance();
            client.getAccount().setBalance(currentBalance + amount);
            System.out.println(
                    "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n" +
                            "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n" +
                            client.getFirstName() + " - CURRENT BALANCE: " + currentBalance + "$\n" +
                            client.getFirstName() + " - successfully deposited " + amount + "$\n" +
                            client.getFirstName() + " - NEW BALANCE: " + client.getAccount().getBalance() + "$\n"
                            );
        }
    }

    @Override
    public void run() {
        deposit();
    }
}
