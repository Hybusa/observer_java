package pro.sky.observer_java;

import com.intellij.openapi.project.Project;
import io.socket.engineio.client.Socket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pro.sky.observer_java.fileProcessor.FileStructureStringer;
import pro.sky.observer_java.resources.ResourceManager;
import pro.sky.observer_java.model.Message;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;

public class ConnectedPanel {
    private JTextField messageField;
    private JButton sendButton;
    private JButton stopSharingButton;
    private JLabel logoLabel;
    private JLabel mentorStatusLabel;
    private JTextArea chatArea;
    private JPanel connectedPanel;
    private JLabel connectionStatusLabel;
    private JScrollPane scroll;
    private JSeparator separator;

    private final String MESSAGE_STRING_FORMAT = "%s: %s\n";

    public ConnectedPanel() {
        sendButton.addActionListener(e -> sendMessage());

        stopSharingButton.addActionListener(e -> ResourceManager.getmSocket().disconnect());

        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    sendMessage();
                }
            }
        });

    }

    private void sendMessage(){
        if(messageField.getText().isEmpty()){
            return;
        }
        if(chatArea.getText().equals("No messages")){
            chatArea.setText("");
        }
        String senderName = ResourceManager.getUserName();
        String messageText = messageField.getText();

        chatArea.append(String.format(MESSAGE_STRING_FORMAT,senderName,messageText));

        JSONObject sendMessage = new JSONObject();
        try {
            // sendMessage.put("user_id", ResourceManager.getUserId());
            sendMessage.put("room_id", ResourceManager.getRoomId());
            sendMessage.put("content", messageText);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        ResourceManager.getmSocket().emit("message/to_mentor", sendMessage);

        Message message = new Message(1L, senderName, LocalDateTime.now(), messageText);
        ResourceManager.getMessageList().add(message);
        messageField.setText("");

    }
    public JPanel getConnectedJPanel() {
       return connectedPanel;
    }

    public void setVisible(boolean toggle){
        connectedPanel.setVisible(toggle);
    }

    public void setConnectionStatusLabelText(String text) {
        this.connectionStatusLabel.setText(text);
    }

    public void setMentorStatusLabelText() {
        if(ResourceManager.isWatching()){
            mentorStatusLabel.setText("Mentor is watching");
            return;
        }
        mentorStatusLabel.setText("Mentor in not watching");
    }

    public void appendChat(String string){
        chatArea.append(string);
    }
}
