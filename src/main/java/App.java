import domain.Client;
import operations.BankOperation;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static config.Setup.countPresentOperations;
import static config.Setup.createClientsTxtFile;
import static config.Setup.createOperationsTxtFile;
import static config.Setup.createResultTxtFile;
import static config.Setup.setupBankOperations;
import static config.Setup.setupClients;

public class App {

    static List<Client> clients;

    static List<BankOperation> bankOperations;

    static int numberOfOperations;
    static int numberOfThreads;

    public static void main(String[] args) throws InterruptedException {

//        createClientsTxtFile();

        clients = setupClients();


        Scanner scanner = new Scanner(System.in);
        numberOfOperations = countPresentOperations();

        System.out.println("operations.txt file is present and contains " + numberOfOperations + " operations.\n" +
                "Do you want to create a new file with operations? y/n");

        String wantToCreateAnswer = scanner.next();
        if (wantToCreateAnswer.equals("y")) {
            System.out.println("Write wanted number of thousands of operations (ex. 10 will create 10000 operations):");
            numberOfOperations = scanner.nextInt() * 1000;
            createOperationsTxtFile(numberOfOperations, clients);
        }

        System.out.println("Write number of wanted threads (ex. 5):");
        numberOfThreads = scanner.nextInt();

        bankOperations = setupBankOperations(clients);


        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        long startTime = System.currentTimeMillis();
        for (BankOperation operation : bankOperations) {
            executorService.execute(operation);
        }
        executorService.shutdown();


        executorService.awaitTermination(50, TimeUnit.SECONDS);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        createResultTxtFile(clients, numberOfOperations, numberOfThreads, elapsedTime,
                "resultwithsingletaskexecution-");


        System.out.println("\n\n\n\n\n");
        System.out.println("Processing " + numberOfOperations + " operations with " + numberOfThreads +
                " thread(s) took " + elapsedTime + " MILLISECONDS");


    }



}
