package dz_1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// создание клиентского окна
public class ClientGUI extends JFrame {

    // параметры клиентского окна
    public static final int WIDTH = 500;
    public static final int HEIGHT = 400;

    private ServerWindow server;
    private boolean connected;
    private String name;

    // добавляем параметры, которые были указаны в примере
    JTextArea log;
    JTextField tfIPAddress, tfPort, tfLogin, tfMessage;
    JPasswordField password;
    JButton btnLogin, btnSend; // login - подключение клиента
    JPanel headerPanel;


    // метод, заполняющий клиентское окно
    public ClientGUI(ServerWindow server){
        this.server = server;   // к каждому клиенту передали сервер для "общения", сохраняется экземпляр сервера

        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setTitle("Chat client");
        setLocation(server.getX() - 500, server.getY());

        createPanel();

        setVisible(true);
    }



    public String getLogin() {
        return tfLogin.getText();
    }

    public String getPassword() {
        return password.getText();
    }


    // метод, проверяющий подключен ли клиент
    private void connectToServer() {
        if (server.connectUser(this)){
            appendLog("Вы успешно подключились!\n");
            headerPanel.setVisible(false);
            connected = true;
            name = tfLogin.getText();
            String log = server.getLog();
            if (log != null){
                appendLog(log);
            }
        } else {
            appendLog("Подключение не удалось");
        }
    }

    // если клиент захочет отключиться от сервера
    public void disconnectFromServer() {
        if (connected) {
            headerPanel.setVisible(true);
            connected = false;
            server.disconnectUser(this);
            appendLog("Вы были отключены от сервера!");
        }
    }

    public void message(){
        if (connected){
            String text = tfMessage.getText();
            if (!text.equals("")){
                server.message(name + ": " + text);
                tfMessage.setText("");
            }
        } else {
            appendLog("Нет подключения к серверу");  // если клиент не нажал login

        }

    }

    public void answer(String text){
        appendLog(text);
    }


    // добавление логов
    private void appendLog(String text){
        log.append(text + "\n");
    }

    // создание панели
    private void createPanel() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createLog());
        add(createFooter(), BorderLayout.SOUTH);
    }

    // заполнение клиентского окна.
    private Component createHeaderPanel(){
        headerPanel = new JPanel(new GridLayout(2, 3));  // Менеджер расположения GridLayout представляет контейнер в виде таблицы с ячейками одинакового размера.
        tfIPAddress = new JTextField("127.0.0.1");
        tfPort = new JTextField("0001");
        tfLogin = new JTextField("Diana");
        password = new JPasswordField("123");  //JPasswordField — это компонент Swing, который позволяет пользователю вводить скрытный пароль
        btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {     // ActionListener — это тип класса, который получает уведомление при выполнении действия в приложении.
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });

        headerPanel.add(tfIPAddress);
        headerPanel.add(tfPort);
        headerPanel.add(new JPanel());
        headerPanel.add(tfLogin);
        headerPanel.add(password);
        headerPanel.add(btnLogin);

        return headerPanel;
    }

    private Component createLog(){
        log = new JTextArea();
        log.setEditable(false);
        return new JScrollPane(log);
    }

    // запись текста. как только пользователь нажимает "enter", то вызывается message и при соединение с сервером и текс(не пустой) отправляется на сервер
    private Component createFooter() {
        JPanel panel = new JPanel(new BorderLayout());
        tfMessage = new JTextField();
        tfMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n'){
                    message();
                }
            }
        });
        btnSend = new JButton("send");
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                message();
            }
        });
        panel.add(tfMessage);
        panel.add(btnSend, BorderLayout.EAST);
        return panel;
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING){
            disconnectFromServer();
        }
        super.processWindowEvent(e);
    }
}