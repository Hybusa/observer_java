package pro.sky.observer_java;


import com.intellij.openapi.wm.ToolWindow;
import io.socket.client.Socket;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceManager {
   private static volatile ConnectedPanel connectedPanel;

   private static volatile InactivePanel inactivePanel;

   private static volatile String roomId;

   private static volatile String userName;

   private static volatile ToolWindow toolWindow;

   private static volatile SkyPanelToolWindowFactory skyPanelToolWindowFactory;

   private static volatile SkyPanelToolWindowFactory.SkyPanelToolWindowContent skyPanelToolWindowContent;

   private static volatile JPanel contentPanel;

   private static volatile Socket mSocket;

   private static List<Message> messageList = new ArrayList<>();

   private static volatile boolean watching = false;


    public static ConnectedPanel getConnectedPanel() {
        return connectedPanel;
    }

    public static void setConnectedPanel(ConnectedPanel connectedPanel) {
        ResourceManager.connectedPanel = connectedPanel;
    }

    public static InactivePanel getInactivePanel() {
        return inactivePanel;
    }

    public static void setInactivePanel(InactivePanel inactivePanel) {
        ResourceManager.inactivePanel = inactivePanel;
    }

    public static String getRoomId() {
        return roomId;
    }

    public static void setRoomId(String roomId) {
        ResourceManager.roomId = roomId;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        ResourceManager.userName = userName;
    }

    public static ToolWindow getToolWindow() {
        return toolWindow;
    }

    public static void setToolWindow(ToolWindow toolWindow) {
        ResourceManager.toolWindow = toolWindow;
    }

    public static SkyPanelToolWindowFactory getSkyPanelToolWindowFactory() {
        return skyPanelToolWindowFactory;
    }

    public static void setSkyPanelToolWindowFactory(SkyPanelToolWindowFactory skyPanelToolWindowFactory) {
        ResourceManager.skyPanelToolWindowFactory = skyPanelToolWindowFactory;
    }

    public static SkyPanelToolWindowFactory.SkyPanelToolWindowContent getSkyPanelToolWindowContent() {
        return skyPanelToolWindowContent;
    }

    public static void setSkyPanelToolWindowContent(SkyPanelToolWindowFactory.SkyPanelToolWindowContent skyPanelToolWindowContent) {
        ResourceManager.skyPanelToolWindowContent = skyPanelToolWindowContent;
    }

    public static JPanel getContentPanel() {
        return contentPanel;
    }

    public static void setContentPanel(JPanel contentPanel) {
        ResourceManager.contentPanel = contentPanel;
    }

    public static Socket getmSocket() {
        return mSocket;
    }

    public static void setmSocket(Socket mSocket) {
        ResourceManager.mSocket = mSocket;
    }

    public static List<Message> getMessageList() {
        return messageList;
    }

    public static void setMessageList(List<Message> messageList) {
        ResourceManager.messageList = messageList;
    }

    public static boolean isWatching() {
        return watching;
    }

    public static void setWatching(boolean watching) {
        ResourceManager.watching = watching;
    }
}