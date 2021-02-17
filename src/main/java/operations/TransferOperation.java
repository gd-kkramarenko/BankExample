package operations;

import domain.Client;
import exceptions.InsufficientBalanceException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TransferOperation extends BankOperation {

    private Client sender;
    private Client recipient;

    private double amount;

    public TransferOperation(Client sender, Client recipient, double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    private void transfer() throws InsufficientBalanceException {
        List<Client> clientsToSyncBy = new ArrayList<>();
        clientsToSyncBy.add(sender);
        clientsToSyncBy.add(recipient);

        clientsToSyncBy.sort(Comparator.comparing(Client::getFirstName));

        synchronized (clientsToSyncBy.get(0)) {
            synchronized (clientsToSyncBy.get(1)) {

                double senderCurrentBalance = sender.getAccount().getBalance();
                double recipientCurrentBalance = recipient.getAccount().getBalance();

                if ((senderCurrentBalance - amount) < 0) {
                    throw new InsufficientBalanceException("Insufficient balance:\n" +
                            sender.getFirstName() + " - Balance - " + senderCurrentBalance + "$\n" +
                            "Not able to transfer " + amount + "$");
                }

                sender.getAccount().setBalance(senderCurrentBalance - amount);
                recipient.getAccount().setBalance(recipientCurrentBalance + amount);

                System.out.println(
                        "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n" +
                                "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n" +
                                sender.getFirstName() + " - SENDER - CURRENT BALANCE: " +
                                                                    senderCurrentBalance + "$\n" +
                                recipient.getFirstName() + " - RECIPIENT - CURRENT BALANCE: " +
                                                                    recipientCurrentBalance + "$\n" +
                                sender.getFirstName() + " - successfully transfered " + amount + "$ to " +
                                                                    recipient.getFirstName() + "\n" +
                                recipient.getFirstName() + " - successfully received " + amount + "$ from " +
                                                                    sender.getFirstName() + "\n" +
                                sender.getFirstName() + " - NEW BALANCE: " + sender.getAccount().getBalance() + "$\n" +
                                recipient.getFirstName() + " - NEW BALANCE: " +
                                                        recipient.getAccount().getBalance() + "$\n"
                );
            }
        }
    }
    @Override
    public void run() {
        try {
            transfer();
        } catch (InsufficientBalanceException e) {
            System.out.println(e.getMessage());
        }
    }
}
