public class Employee {

    private String name;

    public Employee(String name) {
        this.name = name;
    }

    // Employee deposits money to users account
    public void deposit(Account account, Double balance) {
        if(balance == null || balance <= 0) {
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Deposit not possible" + System.lineSeparator());
            return;
        }
        // don't need to use lock and condition
        synchronized (this){
            account.deposit(balance);
            System.out.println("Thread ID " + Thread.currentThread().getId() + "Employee Depositing £" + balance);
        }
    }

    public void withdraw(Account account, Double balance) throws InterruptedException {
        if(balance == null || balance <= 0) {
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Deposit not possible" + System.lineSeparator());
            return;
        }
        synchronized (this){
            account.withdraw(balance);
            System.out.println("Thread ID " + Thread.currentThread().getId() + " Employee Depositing £" + balance);
        }
    }

    public void transfer(Double balance, Account manager, Account receiver) throws InterruptedException {
        if(balance == null || balance <= 0) {
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Employee cannot transfer as no funds available" + System.lineSeparator());
            return;
        }
        synchronized (this) {
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": " + "\033[1;35m" + "Employee Transferring" + "\033[0m" + ": £" + balance);
            manager.transfer(balance, receiver);
        }
        }


    public void checkBalance(Account account) {
        System.out.println("Thread ID " + Thread.currentThread().getId() + ": Employee checking balance in new account");
        synchronized (this){
            account.checkBalance();
        }
    }


    public void editName(Account account, String newName) {
        // don't need to use lock and condition
    synchronized (this){
        account.editName(this.name, newName);
        System.out.println("Thread ID " + Thread.currentThread().getId() + ": Employee changing name to: " + newName);
        System.out.println("Thread ID " + Thread.currentThread().getId() + ": New Name: " + newName +System.lineSeparator());
        }
    }


    public void editPass(Account account, String newPass) {
        // don't need to use lock and condition
        synchronized (this){
            account.editPass("Admin", newPass);
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Employee changing pass to: " + newPass);
            System.out.println("Thread ID " + Thread.currentThread().getId() + ": New Pass: " + newPass + System.lineSeparator());
        }
    }
}
