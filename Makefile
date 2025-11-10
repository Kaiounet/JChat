.PHONY: help clean compile server client run-all

# Default target
help:
	@echo "Available targets:"
	@echo "  make compile    - Compile the project"
	@echo "  make server     - Run the chat server"
	@echo "  make client     - Run a chat client"
	@echo "  make run-all    - Run server and 2 clients in separate terminals"
	@echo "  make clean      - Clean build artifacts"

# Compile the project
compile:
	mvn clean compile

# Run the server
server:
	mvn compile exec:java -Dexec.mainClass="com.kaiounet.ChatServer"

# Run a client
client:
	mvn javafx:run

# Run server and multiple clients (requires tmux or multiple terminal windows)
run-all:
	@echo "Starting server and three clients in new window..."
	@echo "Make sure you have tmux installed for this to work"
	tmux new-window -n chat 'make server' \; \
		split-window -h 'sleep 2 && make client' \; \
		split-window -v 'sleep 3 && make client' \; \
		split-window -v 'sleep 4 && make client' \; \
		select-layout tiled \; \
		select-window -t :chat

# Clean build artifacts
clean:
	mvn clean
