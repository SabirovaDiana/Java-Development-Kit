import client.domain.ClientController;
import client.ui.ClientGUI;
import server.domain.ServerController;
import server.repository.FileStorage;
import server.ui.ServerWindow;

public class Main {
    public static void main(String[] args) {
        ServerController serverController = new ServerController(new ServerWindow(), new FileStorage());

        new ClientController(new ClientGUI(), serverController);
        new ClientController(new ClientGUI(), serverController);
    }
}
