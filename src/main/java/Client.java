public class Client {

    private String firstName;

    private Account account;

    public Client(String firstName, Account account) {
        this.firstName = firstName;
        this.account = account;
    }

    public String getFirstName() {
        return firstName;
    }

    public Account getAccount() {
        return account;
    }
}
