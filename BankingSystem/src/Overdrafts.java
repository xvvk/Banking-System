/*
    Overdraft Account:
        - Can overdraft funds within set limit
 */

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Overdrafts implements Account {
    private final Lock lock; //lock thread
    private final Condition condition; //suspend execution when there is enough money
    private final Double limit = 1000.00;
    private String name;
    private String pass;
    private Double balance;

    public Overdrafts(String name, String pass) {
        this.name     = name;
        this.balance  = 0.00;
        this.pass     = pass;
        lock = new ReentrantLock();
        condition = lock.newCondition();

    }
    @Override
    public void deposit(Double amount) {
        if (amount == null || amount < 0) {
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Deposit not possible" + System.lineSeparator());
            return;
        }

        lock.lock();
        try {
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": " + "\033[1;35m" + "Depositing" + "\033[0m" + ": " + amount);
            this.balance += amount;
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Balance After Deposit: £" + this.balance + System.lineSeparator());
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    // Withdraw money while in overdraft limit
    @Override
    public void withdraw(Double amount) {
        if (amount == null || amount < 0) {
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Withdrawal not possible" + System.lineSeparator());
            return;
        }

        boolean waiting = true;
        lock.lock();
        try {
            while (amount > this.balance + limit) {
                if (!waiting) Thread.currentThread().interrupt();
                else
                    System.out.println("Thread ID " + Thread.currentThread().getId() + ": Waiting..." + System.lineSeparator());
                waiting = condition.await(5, TimeUnit.SECONDS);
            }
            if (this.balance + limit >= amount) {
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

    @Override
    public void transfer(Double amount, Account receiver) {
        if (amount == null || amount < 0) {
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Transfer not possible" + System.lineSeparator());
            return;
        }

        boolean waiting = true;
        lock.lock();
        try {
            while (amount > this.balance + limit) {
                if (!waiting) Thread.currentThread().interrupt();
                else
                    System.out.println("Thread ID " + Thread.currentThread().getId() + ": Waiting..." + System.lineSeparator());
                waiting = condition.await(5, TimeUnit.SECONDS);
            }
            if (this.balance + limit >= amount) {
                System.out.println("Thread ID " + Thread.currentThread().getId() + ":" + "\033[1;35m" + " Transferring" + "\033[0m" + ": £" + amount);
                this.withdraw(amount);
                receiver.deposit(amount);
                condition.signalAll();
            }
        } catch (Exception e) {
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Insufficient funds for Transferring" + System.lineSeparator());
        } finally {
            lock.unlock();
        }
    }

    // Returns the account balance
    @Override
    public void checkBalance() {
        lock.lock();
        try {
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Current Balance £" + this.balance + System.lineSeparator());
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
