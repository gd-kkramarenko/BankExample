import domain.Account;
import domain.Client;
import operations.BankOperation;
import operations.DepositOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

    static List<Client> clients;

    static List<BankOperation> bankOperations;

    public static void main(String[] args) {
        setupClients();
        setupBankOperations();

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (BankOperation operation : bankOperations) {
            executorService.execute(operation);
        }

        executorService.shutdown();

    }

    public static void setupClients() {
        List<String> names = List.of("Michael", "Christopher", "Jessica", "Matthew", "Ashley", "Jennifer", "Joshua",
                "Amanda", "Daniel", "David", "James", "Robert", "John", "Joseph", "Andrew", "Ryan", "Brandon",
                "Jason", "Justin", "Sarah");

        clients = new ArrayList<>();

        final double MIN_BALANCE = 100;
        final double MAX_BALANCE = 1000;

        for (int i = 0; i < names.size(); i++) {
            double balance = MIN_BALANCE + new Random().nextDouble() * (MAX_BALANCE - MIN_BALANCE);
            Client client = new Client(names.get(i), new Account(i, balance));
            clients.add(client);
        }

        System.out.println(clients);
    }

    public static void setupBankOperations() {
        bankOperations = new ArrayList<>();

        final double MIN_DEPOSIT = 100;
        final double MAX_DEPOSIT = 300;


        for (int i = 0; i < 10; i++) {
            int randomClientIndex = new Random().nextInt(clients.size());
            Client client = clients.get(randomClientIndex);
            double depositAmount = MIN_DEPOSIT + new Random().nextDouble() * (MAX_DEPOSIT - MIN_DEPOSIT);

            BankOperation operation = new DepositOperation(client, depositAmount);
            bankOperations.add(operation);
        }
    }
}
