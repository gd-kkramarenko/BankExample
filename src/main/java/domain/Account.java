package domain;

public class Account {

    private int number;

    private long balance;

    public Account(int number, long balance) {
        this.number = number;
        this.balance = balance;
    }

    public int getNumber() {
        return number;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "domain.Account{" +
                "number=" + number +
                ", balance=" + balance +
                '}';
    }
}
