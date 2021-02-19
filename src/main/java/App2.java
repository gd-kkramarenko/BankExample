import domain.Bank;
import domain.Client;
import operations.BankOperation;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static config.Setup.countPresentOperations;
import static config.Setup.createOperationsTxtFile;
import static config.Setup.createResultTxtFile;
import static config.Setup.setupBankOperations;
import static config.Setup.setupClients;

public class App2 {

    static List<Client> clients;

    static List<BankOperation> bankOperations;

    static int numberOfOperations;
    static int numberOfThreads;

    public static void main(String[] args) throws InterruptedException {
//        FIXME 1 & multiple thread runs yield different results!
//        That's due to order of operations:
//        acc1(50$) - deposit 400$, then withdraw 300$ works OK -> yields acc1(150$)
//        acc1(50$) - withdraw 300$, then deposit 400$ works Different -> yields acc1(450$) (1st operation declined)
        clients = setupClients();


        Scanner scanner = new Scanner(System.in);
        numberOfOperations = countPresentOperations();

        System.out.println("operations.txt file is present and contains " + numberOfOperations + " operations.\n" +
                "Do you want to create a new file with operations? y/n");

        String wantToCreateAnswer = scanner.next();
        if (wantToCreateAnswer.equals("y")) {
            System.out.println("Write wanted number of operations (ex. 10000):");
            numberOfOperations = scanner.nextInt();
            createOperationsTxtFile(numberOfOperations, clients);
        }

        System.out.println("Write number of wanted threads (ex. 5):");
        numberOfThreads = scanner.nextInt();

        bankOperations = setupBankOperations(clients);


        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        long startTime = System.currentTimeMillis();
        if (numberOfThreads == 1) {
            Bank bank = new Bank(bankOperations);
            executorService.execute(bank);
        } else {
            int range = numberOfOperations / numberOfThreads;
            for (int i = 0; i < numberOfThreads; i++) {
                int startIndex = range * i;
                int endIndex = range * (i + 1);
                List<BankOperation> partialList = bankOperations.subList(startIndex, endIndex);
                Bank bank = new Bank(partialList);
                executorService.execute(bank);
            }
        }

        executorService.shutdown();


        executorService.awaitTermination(50, TimeUnit.SECONDS);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        createResultTxtFile(clients, numberOfOperations, numberOfThreads, elapsedTime, "resultwithpartiallist");


        System.out.println("\n\n\n\n\n");
        System.out.println("Processing " + numberOfOperations + " operations with " + numberOfThreads +
                " thread(s) took " + elapsedTime + " MILLISECONDS");

    }
}
