package com.kaiounet;

import atlantafx.base.theme.NordLight;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application implements MessageReceiver {
    private ChatClient chatClient;
    private TextArea chatArea;
    private TextField messageField;
    private Button sendButton;
    private String username;

    @Override
    public void start(Stage primaryStage) {
        Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
        primaryStage.setTitle("Chat Application");

        // Show login dialog first
        showLoginDialog(primaryStage);
    }

    private void showLoginDialog(Stage primaryStage) {
        Dialog<String[]> dialog = new Dialog<>(); // Changed to String[] to return multiple values
        dialog.setTitle("Join Chat");
        dialog.setHeaderText("Enter your username and server info");

        ButtonType loginButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        TextField serverField = new TextField();
        serverField.setPromptText("Ex: 192.168.1.10:5555");

        VBox content = new VBox(10);
        content.getChildren().add(new Label("Username:"));
        content.getChildren().add(usernameField);
        content.getChildren().add(new Label("Server's info:"));
        content.getChildren().add(serverField);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new String[] { usernameField.getText(), serverField.getText() };
            }
            return null;
        });

        dialog.showAndWait().ifPresent(data -> {
            String name = data[0];
            String serverInfo = data[1];

            if (name != null && !name.trim().isEmpty() &&
                    serverInfo != null && !serverInfo.trim().isEmpty()) {

                username = name.trim();

                String[] parts = serverInfo.trim().split(":");

                if (parts.length == 2) {
                    try {
                        String host = parts[0];
                        int port = Integer.parseInt(parts[1]);
                        connectToServer(primaryStage, host, port);
                    } catch (NumberFormatException e) {
                        showError("Invalid Port", "Please enter a valid port number.");
                        Platform.exit();
                    }
                } else {
                    showError("Invalid Format", "Server info must be in format: host:port");
                    Platform.exit();
                }
            } else {
                Platform.exit();
            }
        });
    }

    private void connectToServer(Stage primaryStage, String host, int port) {
        chatClient = new ChatClient();
        try {
            chatClient.connect(host, port, username);
            chatClient.startListening(this);
            showChatWindow(primaryStage);
        } catch (Exception e) {
            showError("Connection Error",
                    "Could not connect to server at " + host + ":" + port + "\n" + e.getMessage());
            Platform.exit();
        }
    }

    private void showChatWindow(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Chat area
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        root.setCenter(chatArea);

        // Message input area
        HBox inputBox = new HBox(10);
        inputBox.setPadding(new Insets(10, 0, 0, 0));
        inputBox.setAlignment(Pos.CENTER);

        messageField = new TextField();
        messageField.setPromptText("Type your message...");
        messageField.setPrefWidth(400);
        messageField.setOnAction(e -> sendMessage());

        sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        inputBox.getChildren().addAll(messageField, sendButton);
        root.setBottom(inputBox);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            chatClient.disconnect();
            Platform.exit();
        });
        primaryStage.show();

        chatArea.appendText("Connected to chat as " + username + "\n\n");
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            chatClient.sendMessage(message);
            Platform.runLater(() -> {
                chatArea.appendText("You: " + message + "\n");
                messageField.clear();
            });
        }
    }

    @Override
    public void onMessageReceived(String message) {
        Platform.runLater(() -> chatArea.appendText(message + "\n"));
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
