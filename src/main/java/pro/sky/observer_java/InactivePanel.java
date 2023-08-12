package pro.sky.observer_java;

import com.google.gson.JsonObject;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pro.sky.observer_java.fileProcessor.FileStructureStringer;
import pro.sky.observer_java.resources.ResourceManager;
import pro.sky.observer_java.model.Message;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class InactivePanel {
    private JTextField urlField;
    private JTextField roomIdField;
    private JTextField nameField;
    private JButton connectButton;
    private JLabel nameLabel;
    private JLabel roomLabel;
    private JLabel hostLabel;
    private JLabel titleLabel;
    private JPanel inactivePanel;

    private final String URL_FIELD_DEFAULT_TEXT = "Enter url to connect to";
    private final String ROOM_ID_FIELD_DEFAULT_TEXT = "Enter room id";
    private final String NAME_FIELD_DEFAULT_TEXT = "Enter name to display in chat";
    private final String CONNECTED_STATUS_TEXT_FORMAT = "Connected to %s as %s";

    private final String MESSAGE_STRING_FORMAT = "%s: %s\n";
    Project openProject;


    IO.Options options = IO.Options.builder().setForceNew(true).setUpgrade(true).setTransports(new String[]{"websocket"}).build();
    private final URI SOCKET_URL = URI.create("wss://ws.postman-echo.com/socketio");

    public InactivePanel() {
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createSocketWithListenersAndConnect(/*SOCKET_URL*/URI.create(urlField.getText()));
            }
        });


        urlField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (urlField.getText().equals(URL_FIELD_DEFAULT_TEXT)) {
                    urlField.setText("");
                }
            }
        });

        urlField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (urlField.getText().isEmpty()) {
                    urlField.setText(URL_FIELD_DEFAULT_TEXT);
                }
            }
        });
        roomIdField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (roomIdField.getText().equals(ROOM_ID_FIELD_DEFAULT_TEXT)) {
                    roomIdField.setText("");
                }
            }
        });
        roomIdField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (roomIdField.getText().isEmpty()) {
                    roomIdField.setText(ROOM_ID_FIELD_DEFAULT_TEXT);
                }
            }
        });
        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (nameField.getText().equals(NAME_FIELD_DEFAULT_TEXT)) {
                    nameField.setText("");
                }
            }
        });

        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (nameField.getText().isEmpty()) {
                    nameField.setText(NAME_FIELD_DEFAULT_TEXT);
                }
            }
        });
    }

    private void createSocketWithListenersAndConnect(URI uri) {
        if (ResourceManager.getmSocket() != null) {
            ResourceManager.getmSocket().disconnect();
            ResourceManager.setMessageList(new ArrayList<>());
        }
        ResourceManager.setmSocket(IO.socket(uri, options));
        socketConnectionEventsWithBubbles();
        socketMessageEvents();
        socketProjectRequestEvents();

        ResourceManager.getmSocket().connect();
    }

    private void socketMessageEvents() {
        ResourceManager.getmSocket().on("message/to_client", args -> {
            Message message = new Message(
                    1L,
                    "HOST",
                    LocalDateTime.now(),
                    args[0].toString()
            );
            ResourceManager.getConnectedPanel().appendChat(String.format(MESSAGE_STRING_FORMAT, "SOCKET", message.getMessageText()));
            ResourceManager.getMessageList().add(message);
        });
    }


    private void socketConnectionEventsWithBubbles() {
        openProject = ResourceManager.getToolWindow().getProject();
        String id = "pro.sky.observer";

        Notification balloonNotificationConnected =
                new Notification(id, "Connected to socket!", NotificationType.IDE_UPDATE);
        balloonNotificationConnected.setTitle("Connection success");

        Notification balloonNotificationDisconnected =
                new Notification(id, "Disconnected from socket!", NotificationType.WARNING);
        balloonNotificationDisconnected.setTitle("Disconnected!");

        Notification balloonNotificationError =
                new Notification(id, "Error connecting to socket!", NotificationType.ERROR);
        balloonNotificationError.setTitle("Error connecting!");

        ResourceManager.getmSocket()
                .on(Socket.EVENT_CONNECT, this::sendEventConnect)
                .on(Socket.EVENT_DISCONNECT, args -> {
                    balloonNotificationDisconnected.notify(openProject);
                    ResourceManager.getConnectedPanel().setVisible(false);
                    ResourceManager.getInactivePanel().setVisible(true);


                }).on(Socket.EVENT_CONNECT_ERROR, args -> {
                    balloonNotificationError.notify(openProject);


                }).on("room/join", args -> {

                    JSONObject message;
                    try {
                        message = new JSONObject(args[0].toString());
                        ResourceManager.setUserId(message.getInt("user_id"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    balloonNotificationConnected.notify(openProject);
                    ResourceManager.getInactivePanel().setVisible(false);
                    ResourceManager.getConnectedPanel().setVisible(true);
                });
    }

    private void socketProjectRequestEvents() {
        ResourceManager.getmSocket().on("sharing/start", args -> {
            ResourceManager.setWatching(true);
            ResourceManager.getConnectedPanel().setMentorStatusLabelText();
            FileStructureStringer fileStructureStringer = new FileStructureStringer();
            JSONObject sendMessage = new JSONObject();
            JSONArray data;
            try {
                data = new JSONArray(fileStructureStringer.getProjectFilesList(openProject));
                // sendMessage.put("user_id", ResourceManager.getUserId());
                sendMessage.put("room_id", ResourceManager.getRoomId());
                sendMessage.put("files", data);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            ResourceManager.getmSocket().emit("sharing/code_send", sendMessage);
        });
    }

    public void setVisible(boolean toggle) {
        inactivePanel.setVisible(toggle);
    }

    public JPanel getInactiveJPanel() {
        return inactivePanel;
    }

    private void sendEventConnect(Object... args) {

        ResourceManager.setRoomId(Integer.valueOf(roomIdField.getText()));
        ResourceManager.setUserName(nameField.getText());

        ResourceManager.getConnectedPanel().setConnectionStatusLabelText(
                String.format(CONNECTED_STATUS_TEXT_FORMAT, ResourceManager.getRoomId(), ResourceManager.getUserName())
        );

        JSONObject data = new JSONObject();
        try {
            data.put("room_id", Long.parseLong(roomIdField.getText()));
            data.put("name", nameField.getText());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        ResourceManager.getmSocket().emit("room/join", data);

    }
}
