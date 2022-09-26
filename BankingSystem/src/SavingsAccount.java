import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SavingsAccount implements Account {
    private final Lock lock;
    private final Condition condition;
    private String name;
    private String pass;
    private Double balance;
    private double interest= 0.10;

    public SavingsAccount(String name, String pass) {
        this.name = name;
        this.pass = pass;
        this.balance = 0.00;
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    /*
    ONLY DIFFERENCE HERE
        - Savings get added interest
        - Overdraft is not available as it is a savings account and will not go under £0.00
     */

    // Deposit money to account & interest is added (how a saving account works :D)
    @Override
    public void deposit(Double amount) {
        if(amount == null || amount <= 0) {
            System.out.println("Deposit not possible");
            return;
        }
        lock.lock();
        try {
            System.out.println("Thread ID " + Thread.currentThread().getId() + ":\033[1;35m" + " Depositing" + "\033[0m" + ": £" + amount);
            this.balance = amount + (amount * interest);
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Current Balance £" + this.balance + System.lineSeparator());
            condition.signalAll(); // release waiting threads
        } finally {
            lock.unlock();
        }
    }

    //withdraw money
    @Override
    public synchronized void withdraw(Double amount) {
        if (amount == null || amount <= 0) {
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Withdrawal not possible" + System.lineSeparator());
            return;
        }

        boolean waiting = true;
        lock.lock();
        try {
            while (amount > this.balance) {
                if (!waiting) Thread.currentThread().interrupt();
                else
                    System.out.println("Thread ID " + Thread.currentThread().getId() + ": Waiting..." + System.lineSeparator());
                waiting = condition.await(5, TimeUnit.SECONDS);
            }
            if (this.balance >= amount) {
                System.out.println("Thread ID " + Thread.currentThread().getId() + ": " + "\033[1;35m" + "Withdrawing" + "\033[0m: " + "£" + amount + " from current account");
                this.balance -= amount;
                System.out.println("Thread ID " + Thread.currentThread().getId() + ": Balance After Withdrawal: £" + this.balance + System.lineSeparator());
                condition.signalAll();
            }
        } catch (Exception e) {
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Insufficient funds for withdrawal" + System.lineSeparator());
        } finally {
            lock.unlock();
        }
    }

    // transfer money to receiver, if there is money in account
    @Override
    public void transfer(Double amount, Account receiver) {
        if (amount == null || amount <= 0) {
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Transfer not possible" + System.lineSeparator());
            return;
        }

        boolean waiting = true;
        lock.lock();
        try {
            while (amount > this.balance) {
                if (!waiting) Thread.currentThread().interrupt();
                else
                    System.out.println("Thread ID " + Thread.currentThread().getId() + ": Waiting..." + System.lineSeparator());
                waiting = condition.await(5, TimeUnit.SECONDS);
            }
            if (this.balance >= amount) {
                System.out.println("Thread ID " + Thread.currentThread().getId() + ":" + "\033[1;35m" + " Transferring" + "\033[0m" + ": £" + amount);
                //this.withdraw(amount);
                this.balance -= amount;
                this.withdraw(amount);
                receiver.deposit(amount);

                // System.out.println("Thread ID " + Thread.currentThread().getId() + ": Balance After Transferring: £" + this.balance + System.lineSeparator());
                condition.signalAll();
            }
        } catch (Exception e) {
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Insufficient funds for Transferring" + System.lineSeparator());
        } finally {
            lock.unlock();
        }
    }

    // check/print the current balance
    @Override
    public synchronized void checkBalance() {
        lock.lock();
        try {
            // System.out.println("BALANCE");
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Current Balance: £" + this.balance + System.lineSeparator());
        } finally {
            lock.unlock();
        }
    }

    // create/delete/edit the account
    // chosen to edit: change name and password of the account
    // employee should also be able to edit details
    @Override
    public synchronized void editName(String oldName, String newName) {
        System.out.println("Thread ID " + Thread.currentThread().getId() + ": Old Name: " + this.name);
        if(pass.equals(this.pass)) {
            this.name = newName;
        }
    }

    @Override
    public synchronized void editPass(String oldPass, String newPass) {
        System.out.println("Thread ID " + Thread.currentThread().getId() + ": Old Pass: " + this.pass);
        if(pass.equals(this.pass)) {
            this.pass = newPass;
        }
    }
}

