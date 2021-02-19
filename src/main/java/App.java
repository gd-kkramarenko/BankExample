import domain.Account;
import domain.Client;
import operations.BankOperation;
import operations.DepositOperation;
import operations.TransferOperation;
import operations.WithdrawOperation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class App {

    static List<Client> clients;

    static List<BankOperation> bankOperations;

    static int numberOfOperations;
    static int numberOfThreads;

    public static void main(String[] args) {

//        createClientsTxtFile();

        setupClients();


        Scanner scanner = new Scanner(System.in);
        numberOfOperations = countPresentOperations();

        System.out.println("operations.txt file is present and contains " + numberOfOperations +" operations.\n" +
                            "Do you want to create a new file with operations? y/n");

        String wantToCreateAnswer = scanner.next();
        if (wantToCreateAnswer.equals("y")) {
            System.out.println("Write wanted number of operations (ex. 10000):");
            numberOfOperations = scanner.nextInt();
            createOperationsTxtFile(numberOfOperations);
        }

        System.out.println("Write number of wanted threads (ex. 5):");
        numberOfThreads = scanner.nextInt();

        setupBankOperations();


        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        long startTime = System.currentTimeMillis();
        for (BankOperation operation : bankOperations) {
            executorService.execute(operation);
        }
        executorService.shutdown();

        try {
            executorService.awaitTermination(500, TimeUnit.SECONDS);
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;

            createResultTxtFile(elapsedTime);


            System.out.println("\n\n\n\n\n");
            System.out.println("Processing " + numberOfOperations + " operations with " + numberOfThreads +
                                                " thread(s) took " + elapsedTime + " MILLISECONDS");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }


    public static int countPresentOperations() {
        int lines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("operations.txt"))) {
          while (reader.readLine() != null) lines++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void createClientsTxtFile() {

        final double MIN_BALANCE = 100;
        final double MAX_BALANCE = 1000;

        try(BufferedReader reader = new BufferedReader(new FileReader("names.txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("clients.txt", false))) {

            System.out.println("----- Started creating clients.txt file -----");

            Random random = new Random();

            String name;
            while ((name = reader.readLine()) != null){
                double balance = MIN_BALANCE + random.nextDouble() * (MAX_BALANCE - MIN_BALANCE);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(name);
                stringBuilder.append(" ");
                stringBuilder.append(balance);

                writer.write(stringBuilder.toString());
                writer.newLine();
            }

            System.out.println("----- Done creating clients.txt file -----");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void setupClients() {
        try(BufferedReader reader = new BufferedReader(new FileReader("clients.txt"))) {
            clients = new ArrayList<>();

            String clientInfo;
            int accountNumber = 1;
            while ((clientInfo = reader.readLine()) != null){
                String[] info = clientInfo.split(" ");
                Client client = new Client(info[0], new Account(accountNumber, Double.parseDouble(info[1])));
                clients.add(client);
                accountNumber++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void createOperationsTxtFile(int numberOfOperations) {
        List<String> operationNames = List.of("DEPOSIT", "WITHDRAW", "TRANSFER");

        final double MIN_AMOUNT = 200;
        final double MAX_AMOUNT = 800;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("operations.txt", false))) {
            System.out.println("----- Started creating operations.txt file -----");

            Random random = new Random();

            for (int i = 0; i < numberOfOperations; i++) {
                int randomOperationIndex = random.nextInt(operationNames.size());
                String operationName = operationNames.get(randomOperationIndex);

                int randomClientIndex = random.nextInt(clients.size());
                String clientName = clients.get(randomClientIndex).getFirstName();

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(operationName);
                stringBuilder.append(" ");
                stringBuilder.append(clientName);
                stringBuilder.append(" ");

                if (operationName.equals("TRANSFER")) {
                    int randomSecondClientIndex = random.nextInt(clients.size());
                    String secondClientName = clients.get(randomSecondClientIndex).getFirstName();
                    while (secondClientName.equals(clientName)) {
                        randomSecondClientIndex = random.nextInt(clients.size());
                        secondClientName = clients.get(randomSecondClientIndex).getFirstName();
                    }
                    stringBuilder.append(secondClientName);
                    stringBuilder.append(" ");
                }

                double amount = MIN_AMOUNT + random.nextDouble() * (MAX_AMOUNT - MIN_AMOUNT);
                stringBuilder.append(amount);

                writer.write(stringBuilder.toString());
                writer.newLine();

            }
            System.out.println("----- Done creating operations.txt file -----");
        } catch (IOException e) {
            e.printStackTrace();
        }



    }



    public static void setupBankOperations() {
        bankOperations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("operations.txt"))) {
            String operationInfo;
            while ((operationInfo = reader.readLine()) != null) {
                String[] info = operationInfo.split(" ");
                switch (info[0]) {
                    case "DEPOSIT":
                        BankOperation depositOperation = new DepositOperation(getClientByName(info[1]),
                                                                        Double.parseDouble(info[2]));
                        bankOperations.add(depositOperation);
                        break;

                    case "WITHDRAW":
                        BankOperation withdrawOperation = new WithdrawOperation(getClientByName(info[1]),
                                                                        Double.parseDouble(info[2]));
                        bankOperations.add(withdrawOperation);
                        break;

                    case "TRANSFER":
                        BankOperation transferOperation = new TransferOperation(getClientByName(info[1]),
                                                                        getClientByName(info[2]),
                                                                        Double.parseDouble(info[3]));
                        bankOperations.add(transferOperation);
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    todo exception if client not found
    public static Client getClientByName(String name) {
        for (Client client: clients) {
            if (name.equals(client.getFirstName())) {
                return client;
            }
        }

        return null;
    }

    public static void createResultTxtFile(long elapsedTime) {
        List<Client> sortedClientsAfterAllOperations = clients.stream()
                .sorted(Comparator.comparing(Client::getFirstName))
                .collect(Collectors.toList());

        StringBuilder filenameBuilder = new StringBuilder("result-");
        filenameBuilder.append(numberOfThreads);
        if (numberOfThreads == 1) {
            filenameBuilder.append("-thread");
        } else {
            filenameBuilder.append("-threads");
        }
        filenameBuilder.append(".txt");

        String filename = filenameBuilder.toString();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) {
            System.out.println("----- Started creating result.txt file -----");

            writer.write("******* EXECUTION INFO *******");
            writer.newLine();
            writer.write("Number of operations: " + numberOfOperations);
            writer.newLine();
            writer.write("Number of threads: " + numberOfThreads);
            writer.newLine();
            writer.write("Execution time: " + elapsedTime + " milliseconds");
            writer.newLine();
            writer.write("******* EXECUTION INFO *******");
            writer.newLine();
            writer.newLine();
            writer.newLine();


            for (Client client: sortedClientsAfterAllOperations) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(client.getFirstName());
                stringBuilder.append(" ");
                stringBuilder.append(client.getAccount().getBalance());

                writer.write(stringBuilder.toString());
                writer.newLine();
            }
            System.out.println("----- Done creating result.txt file -----\n\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
