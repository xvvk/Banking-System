interface Account {

    void deposit(Double balance);

    void withdraw(Double balance) throws InterruptedException;

    void transfer(Double balance, Account receiver) throws InterruptedException;

    void checkBalance();

    void editName(String newName, String pass);

    void editPass(String oldPass, String newPass);
}