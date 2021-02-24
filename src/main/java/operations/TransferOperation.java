package operations;

import domain.Client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TransferOperation extends BankOperation {

    private Client sender;
    private Client recipient;

    private long amount;

    public TransferOperation(Client sender, Client recipient, long amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    @Override
    public void doOperation() {
        List<Client> clientsToSyncBy = new ArrayList<>();
        clientsToSyncBy.add(sender);
        clientsToSyncBy.add(recipient);

        clientsToSyncBy.sort(Comparator.comparing(Client::getFirstName));

        synchronized (clientsToSyncBy.get(0)) {
            synchronized (clientsToSyncBy.get(1)) {

                long senderCurrentBalance = sender.getAccount().getBalance();
                long recipientCurrentBalance = recipient.getAccount().getBalance();


                sender.getAccount().setBalance(senderCurrentBalance - amount);
                recipient.getAccount().setBalance(recipientCurrentBalance + amount);

                for (long i = 1, m = 1; i <= 15000; i++) {
                    m *= i;
                }

            }
        }
    }


    @Override
    public void run() {
        doOperation();
    }
}
