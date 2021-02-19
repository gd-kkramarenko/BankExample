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

    @Override
    public void doOperation() throws InsufficientBalanceException {
        synchronized (client) {
            double currentBalance = client.getAccount().getBalance();

            if ((currentBalance - amount) < 0) {
                throw new InsufficientBalanceException("Insufficient balance:\n" +
                        client.getFirstName() + " - Balance - " + currentBalance + "$\n" +
                        "Not able to withdraw " + amount + "$");
            }

            client.getAccount().setBalance(currentBalance - amount);

        }
    }


    @Override
    public void run() {
        try {
            doOperation();
        } catch (InsufficientBalanceException e) {
//            System.out.println(e.getMessage());
        }
    }
}
