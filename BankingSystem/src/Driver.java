public class Driver {

    //loop the scenario 3 times for triple check the prints aren't printing wrong
    private static final int loop = 3;

    // returns a new current account
    private static CurrentAccount getCurrent() {
        return new CurrentAccount("Karmen", "Tsang");
    }

    // returns a new savings account -- check that it adds interest
    private static SavingsAccount getSaving() {
        return new SavingsAccount("Sudeep", "Kuro");
    }

    // returns a new overdraft account -- check that the balance can go negative
    private static Overdrafts getOverdraft() {
        return new Overdrafts("Mackie", "Burgess");
    }

    // use the gets to set the acc type for each scenario
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Looping each scenario " + loop + " times." + System.lineSeparator());

        // Scenario 1 -- Complete checkBalance()
        System.out.println("\033[1;34m" + "Scenario 1: Two account holders are trying to check the balance simultaneously." + "\033[0m" + System.lineSeparator());
        for (int i = 0; i < loop; i++) {
            scenario1(getCurrent());
        }

        // Scenario 2 -- Complete Deposit & Withdrawal
        System.out.println("\033[1;34m" + "Scenario 2: One account holder checks the balance while the other is depositing/withdrawing\n" +
                "money." + "\033[0m" + System.lineSeparator());
        for (int i = 0; i < loop; i++) {
            scenario2(getCurrent());
        }

        // Scenario 3
        System.out.println("\033[1;34m" + "Scenario 3: Two account holders simultaneously deposit/withdraw money & check the\n" +
                "balance." + "\033[0m" + System.lineSeparator());
        for (int i = 0; i < loop; i++) {
            scenario3(getCurrent());
        }

        // Scenario 4 -- Complete Employee
        System.out.println("\033[1;34m" + "Scenario 4: Same as 3, but at the same time a bank employee is in the process of completing a money\n" +
                "transfer in/out the account." + "\033[0m" + System.lineSeparator());
        for (int i = 0; i < loop; i++) {
            scenario4(getCurrent());
        }

        // Scenario 5
        System.out.println("\033[1;34m" + "Scenario 5: There are insufficient funds to complete a withdraw" + "\033[0m" + System.lineSeparator());
        for (int i = 0; i < loop; i++) {
            scenario5(getCurrent());
        }

        // Scenario 6 -- Create two new employees at runnable
        System.out.println("\033[1;34m" + "Scenario 6: Two bank employees are trying simultaneously to modify the details of a bank account." + "\033[0m" + System.lineSeparator());
        for (int i = 0; i < loop; i++) {
            scenario6(getCurrent());
        }

        // Scenario 7 -- Check money is deducted from account at £0 balance
        System.out.println("\033[1;34m" + "Scenario 7: Account goes into overdraft." + "\033[0m" + System.lineSeparator());
        for (int i = 0; i < loop; i++) {
            scenario5(getOverdraft());
        }

        // Scenario 8 -- Check money is gaining interest in savings
        System.out.println("\033[1;34m" + "Scenario 8: Interest is added to savings." + "\033[0m" + System.lineSeparator());
        for (int i = 0; i < loop; i++) {
            scenario2(getSaving());
        }
    }


    // Simulates scenario 1
    public static void scenario1(Account account) throws InterruptedException {
        Runnable x = account::checkBalance;
        Runnable y = account::checkBalance;

        Thread thread1 = new Thread(x);
        Thread thread2 = new Thread(y);

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println("-------------------------------------------------------------------" + System.lineSeparator());
    }

    // Simulates scenario 2 -- was buggy, double check before submission
    public static void scenario2(Account account) throws InterruptedException {
        Runnable x = () -> account.deposit(1.00);

        Runnable y = account::checkBalance;

        Thread thread1 = new Thread(x);
        Thread thread2 = new Thread(y);

        thread1.start();
        thread1.join();
        thread2.start();
        thread2.join();
        System.out.println("-------------------------------------------------------------------" + System.lineSeparator());
    }

    //Scenario 3
    public static void scenario3(Account account) throws InterruptedException {
        Runnable x = () -> {
            account.deposit(100.00);
            try {
                account.withdraw(10.00);
            } catch (InterruptedException e) {
            }
            account.checkBalance();
        };
        Runnable y = () -> {
            account.deposit(100.00);
            try {
                account.withdraw(10.00);
            } catch (InterruptedException e) {
            }
            account.checkBalance();
        };

        Thread thread1 = new Thread(x);
        Thread thread2 = new Thread(y);

        thread1.start();
        thread1.join();
        thread2.start();
        thread2.join();
        System.out.println("-------------------------------------------------------------------" + System.lineSeparator());
    }

    //Scenario 4
    public static void scenario4(Account account) throws InterruptedException {
        Employee employee = new Employee("employee");
        CurrentAccount newAcc = new CurrentAccount("GreatPerson", "NewAcc");

        Runnable x = () -> {
            account.deposit(100.00);
            try {
                account.withdraw(10.00);
            } catch (InterruptedException e) {
            }
            account.checkBalance();
        };
        Runnable y = () -> {
            account.deposit(100.00);
            try {
                account.withdraw(10.00);
            } catch (InterruptedException e) {
            }
            account.checkBalance();
        };
        Runnable z = () ->{
            try {
                employee.transfer(10.00, account, newAcc);
            } catch (InterruptedException e) {
            }
            employee.checkBalance(newAcc);
        };

        Thread thread1 = new Thread(x);
        Thread thread2 = new Thread(y);
        Thread thread3 = new Thread(z);

        thread1.start();
        thread1.join();
        thread2.start();
        thread2.join();
        thread3.start();
        thread3.join();
        System.out.println("-------------------------------------------------------------------" + System.lineSeparator());
    }



    //Scenario 5
    public static void scenario5(Account account) throws InterruptedException {
        // No money in account so the thread will wait for a deposit
        Runnable x = () -> {
            try {
                account.withdraw(10.00);
            } catch (InterruptedException e) {
            }
        };

        Thread thread1 = new Thread(x);

        thread1.start();
        thread1.join();
        System.out.println("-------------------------------------------------------------------" + System.lineSeparator());
    }


    //Scenario 6
    public static void scenario6(Account account) throws InterruptedException {
        Employee one = new Employee("one");
        Employee two = new Employee("two");

        Runnable x = () -> {
            one.editName(account, "Egg");
            one.editPass(account, "FriedEgg");

        };

        Runnable y = () -> {
            two.editName(account, "Sudeep");
            two.editPass(account, "Dhakal");
        };

        Thread thread1 = new Thread(x);
        Thread thread2 = new Thread(y);

        thread1.start();
        thread1.join();
        thread2.start();
        thread2.join();
        System.out.println("-------------------------------------------------------------------" + System.lineSeparator());
    }
}
