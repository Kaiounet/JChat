# JavaFX Chat Application

A simple real-time chat application built with JavaFX and Java Sockets.

## Features

- Real-time messaging
- Multi-client support
- Server-client architecture
- Thread-based client handling
- Simple and modular design

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- (Optional) tmux for running multiple instances

## Project Structure

```bash
javafx-app/
├── src/main/java/com/kaiounet/
│   ├── App.java              # JavaFX GUI
│   ├── ChatServer.java       # Server main class
│   ├── ClientHandler.java    # Handles individual clients
│   ├── ChatClient.java       # Client networking
│   └── MessageReceiver.java  # Message callback interface
├── pom.xml                   # Maven configuration
├── Makefile                  # Build automation
└── README.md
```

## Building

```bash
make compile
```

## Running

### Option 1: Manual (recommended for learning)

Open 3 separate terminals:

**Terminal 1 - Server:**

```bash
make server
```

**Terminal 2 - Client 1:**

```bash
make client
```

**Terminal 3 - Client 2:**

```bash
make client
```

### Option 2: Automated (requires tmux)

```bash
make run-all
```

## Available Make Commands

- `make help` - Show all available commands
- `make compile` - Compile the project
- `make server` - Run the chat server
- `make client` - Run a chat client
- `make clean` - Clean build artifacts
- `make run-all` - Run server and 2 clients (requires tmux)

## How It Works

1. **Server** listens on port 5555 for incoming connections
2. Each **Client** connects and sends their username
3. **ClientHandler** manages each client in a separate thread
4. Messages are broadcast to all connected clients
5. **JavaFX GUI** provides the user interface

## Architecture

The application follows the Single Responsibility Principle:

- **ChatServer**: Accepts connections and manages client list
- **ClientHandler**: Handles individual client communication
- **ChatClient**: Manages server connection and messaging
- **MessageReceiver**: Interface for receiving messages
- **App**: JavaFX GUI and user interaction

## License

MIT License
