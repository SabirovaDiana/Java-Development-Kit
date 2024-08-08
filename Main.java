package dz_1;

public class Main {
    public static void main(String[] args) {

        ServerWindow serverWindow = new ServerWindow();      // создание экземпляра класса сервера
        new ClientGUI(serverWindow);                         // создание первого клиента
        new ClientGUI(serverWindow);                         // создание второго клиента
    }
}