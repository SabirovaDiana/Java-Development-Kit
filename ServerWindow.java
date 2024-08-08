package dz_1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


// создание серверного окна
public class ServerWindow extends JFrame {

    // параметры окна
    public static final int WIDTH = 500;
    public static final int HEIGHT = 400;
    public static final String LOG_PATH = "src/server/log.txt";  // прописываем путь для лог файла, в который будут записываться данные

    List<ClientGUI> clientGUIList;  // список


    // хранение пар
    Map<String, String> userWithPassword = Map.of(
                "Diana", "123",
                "Test", "321");

    JButton btnStart, btnStop;      // добавление двух кнопок(старт, стоп) на окне
    JTextArea log;  // область для ввода текста
    boolean is_running;

    // метод serverWindows, заполнение окна
    public ServerWindow(){
        clientGUIList = new ArrayList<>();

        setDefaultCloseOperation(EXIT_ON_CLOSE);     // определение действия при завершении программы
        setSize(WIDTH, HEIGHT);                      // определение размеров окна
        setResizable(false);                         // нельзя изменить размер окна
        setTitle("Chat server");                     // заголовок
        setLocationRelativeTo(null);                 // Для того чтобы центрировать окно JFrame, следует использовать этот метод после установки его размеров( центрирование для кор. расположения)

        createPanel();                               // создание панели

        setVisible(true);                            // отображение окна
    }

    // Добавление кнопок Старт и стоп. Интерфейс сервера
    private Component createButtons() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        btnStart = new JButton("Start");
        btnStop = new JButton("Stop");


        // ActionEvent — это событие в Java, которое генерируется при нажатии на кнопку.
        // При нажатии на кнопку start, то идет запуск сервера
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (is_running){
                    appendLog("Сервер уже запущен");
                } else {
                    is_running = true;
                    appendLog("Сервер запущен!");
                }
            }
        });

        // отключение от сервера, когда сервер останавливается, то он отключает всех пользователей
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!is_running){
                    appendLog("Сервер снова остановлен");
                } else {
                    is_running = false;
                    for (ClientGUI clientGUI: clientGUIList) {  // для каждого клиента происходит удаление из списка
                        try {
                            disconnectUser(clientGUI);  // вызывается метод disconnectUser
                        } catch (Exception ex) {
                            appendLog("Error: " + ex.getMessage());
//                            forceDisconnect(clientGUI);
                        }
                    }
                    clientGUIList.clear();
                    appendLog("Сервер остановлен!");
                }
            }
        });

        panel.add(btnStart);
        panel.add(btnStop);
        return panel;
    }


    public boolean connectUser(ClientGUI clientGUI){
        // метод, подключающий клиента к серверу/ на вход принимает экз. класса.
        // можно вызывать когда мы захотим подключиться ( если сервер работает)-> добавление в список,
        // если сервер не работает, то возвращается false


        if (!is_running){
            return false;                             // если клиент не нажал login- значит не подключен
        }
        // если прошла проверка пароля с логином, то идет добавление пользователя в список
        if (checkUser(clientGUI.getLogin(), clientGUI.getPassword())) {
            clientGUIList.add(clientGUI);
            return true;
        }
        return false;
    }


    // метод, проверяющий пользователя
    public boolean checkUser(String login, String password) {
        String realPassword = userWithPassword.get(login);
        return realPassword != null && Objects.equals(realPassword, password); // если прошло совпадение
    }

    public String getLog() {
        return readLog();
    }

    // метод, который отключает клиента от сервера
    public void disconnectUser(ClientGUI clientGUI){
        clientGUI.disconnectFromServer();
    }


    //
    public void message(String text){
        if (!is_running){
            return;
        }
        // если сервер запущен
        text += "";
        appendLog(text);     // запись сообщения в окошко сервера
        answerAll(text);     // проход по всем окошкам клиентов и вызвать у них answer
        saveInLog(text);     // запись
    }


    private void appendLog(String text){
        log.append(text + "\n");
    }


    private void answerAll(String text){
        for (ClientGUI clientGUI: clientGUIList){
            clientGUI.answer(text);
        }
    }

    private void saveInLog(String text){
        try (FileWriter writer = new FileWriter(LOG_PATH, true)){
            writer.write(text);
            writer.write("\n");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private String readLog(){
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader reader = new FileReader(LOG_PATH);){
            int c;
            while ((c = reader.read()) != -1){
                stringBuilder.append((char) c);
            }
//            Delete EOF
            // stringBuilder удаляет последний символ
            stringBuilder.delete(stringBuilder.length()-1, stringBuilder.length());
            return stringBuilder.toString();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private void createPanel() {
        log = new JTextArea();
        add(log);
        add(createButtons(), BorderLayout.SOUTH);
    }
}