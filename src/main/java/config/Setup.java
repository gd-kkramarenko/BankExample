package config;

import domain.Account;
import domain.Client;
import operations.BankOperation;
import operations.DepositOperation;
import operations.TransferOperation;
import operations.WithdrawOperation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Setup {

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

        try (BufferedReader reader = new BufferedReader(new FileReader("names.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("clients.txt", false))) {

            System.out.println("----- Started creating clients.txt file -----");

            Random random = new Random();

            String name;
            while ((name = reader.readLine()) != null) {
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


    public static List<Client> setupClients() {
        List<Client> clients = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("clients.txt"))) {

            String clientInfo;
            int accountNumber = 1;
            while ((clientInfo = reader.readLine()) != null) {
                String[] info = clientInfo.split(" ");
                Client client = new Client(info[0], new Account(accountNumber, Double.parseDouble(info[1])));
                clients.add(client);
                accountNumber++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return clients;
    }


    public static void createOperationsTxtFile(int numberOfOperations, List<Client> clients) {
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


    public static List<BankOperation> setupBankOperations(List<Client> clients) {
        List<BankOperation> bankOperations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("operations.txt"))) {
            String operationInfo;
            while ((operationInfo = reader.readLine()) != null) {
                String[] info = operationInfo.split(" ");
                switch (info[0]) {
                    case "DEPOSIT":
                        BankOperation depositOperation = new DepositOperation(getClientByName(clients, info[1]),
                                Double.parseDouble(info[2]));
                        bankOperations.add(depositOperation);
                        break;

                    case "WITHDRAW":
                        BankOperation withdrawOperation = new WithdrawOperation(getClientByName(clients, info[1]),
                                Double.parseDouble(info[2]));
                        bankOperations.add(withdrawOperation);
                        break;

                    case "TRANSFER":
                        BankOperation transferOperation = new TransferOperation(getClientByName(clients, info[1]),
                                getClientByName(clients, info[2]),
                                Double.parseDouble(info[3]));
                        bankOperations.add(transferOperation);
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bankOperations;
    }

    //    todo exception if client not found
    public static Client getClientByName(List<Client> clients, String name) {
        for (Client client : clients) {
            if (name.equals(client.getFirstName())) {
                return client;
            }
        }

        return null;
    }


    public static void createResultTxtFile(List<Client> clients, int numberOfOperations,
                                           int numberOfThreads, long elapsedTime, String prefix) {
        List<Client> sortedClientsAfterAllOperations = clients.stream()
                .sorted(Comparator.comparing(Client::getFirstName))
                .collect(Collectors.toList());

        StringBuilder filenameBuilder = new StringBuilder(prefix);
        filenameBuilder.append(numberOfOperations / 1000);
        filenameBuilder.append("k-operations-");
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


            for (Client client : sortedClientsAfterAllOperations) {
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
