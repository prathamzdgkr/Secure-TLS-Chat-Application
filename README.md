# 🚀 Secure TLS Multi-Client Chat Application

A secure real-time chat application built using Java, TCP Socket Programming, SSL/TLS Encryption, Multithreading, and Java Swing GUI.

The application enables multiple clients to communicate simultaneously through a centralized server while ensuring encrypted communication using TLS. It demonstrates practical implementation of secure networking concepts, concurrent programming, file transfer mechanisms, and desktop application development.

This repository follows a client-server architecture consisting of a TLS-enabled server and multiple secure chat clients.

---

# 📌 Features

* 🔐 SSL/TLS Encrypted Communication
* 👥 Multi-Client Chat Support
* 💬 Real-Time Public Messaging
* 📩 Private Messaging Between Users
* 📁 Secure File Sharing
* 📊 Server Monitoring Dashboard
* 🌐 TCP Socket Communication
* ⚡ Thread Pool Based Concurrency
* 📡 Network Latency (RTT) Monitoring
* 👤 Active User Management
* 🎨 Modern Java Swing User Interface

---

# 🏗️ Architecture & Tech Stack

## Client (`/client`)

* Java Swing
* SSL Socket
* TCP Communication
* Base64 File Encoding
* Multithreading

## Server (`/server`)

* Java
* SSL Server Socket
* ExecutorService Thread Pool
* Concurrent Collections
* Client Session Management
* Message Routing Engine

---

# 🛡️ Security Implementation

The application uses SSL/TLS to ensure secure communication between clients and the server.

## TLS Security Features

### SSL Server Socket

The server uses SSL-enabled sockets instead of standard TCP sockets.

```java
SSLServerSocket serverSocket =
(SSLServerSocket) ssf.createServerSocket(PORT);
```

### SSL Client Socket

Clients establish encrypted TLS connections.

```java
SSLSocketFactory ssf =
(SSLSocketFactory) SSLSocketFactory.getDefault();
```

### TLS Handshake

Each connection performs a TLS handshake before communication begins.

```java
socket.startHandshake();
```

### TrustStore & KeyStore

* Server authentication using KeyStore
* Client verification using TrustStore
* Secure certificate-based communication

---

# 📁 Folder Structure

```bash
ChatApplication/
│
├── client/
│   ├── ChatClient.java
│   ├── ChatFrame.java
│   └── LoginFrame.java
│
├── server/
│   ├── ChatServer.java
│   └── ClientHandler.java
│
├── README.md
└── .gitignore
```

---

# ⚙️ Core Concepts Demonstrated

## Networking

* TCP/IP Communication
* Socket Programming
* Client-Server Architecture
* Secure Socket Layer (SSL)

## Concurrent Programming

* Thread Pool Management
* ExecutorService
* Concurrent Collections
* Multi-Client Handling

## Security

* TLS Encryption
* Certificate Management
* Secure Session Establishment
* Encrypted Data Transmission

## Desktop Development

* Java Swing GUI
* Event-Driven Programming
* Real-Time UI Updates

---

# 📡 System Architecture

```text
                    +----------------------+
                    |      TLS SERVER      |
                    |----------------------|
                    | SSL Server Socket    |
                    | Thread Pool          |
                    | User Management      |
                    | Message Routing      |
                    | File Routing         |
                    +----------+-----------+
                               |
        -------------------------------------------------
        |                       |                       |
        |                       |                       |
+---------------+     +---------------+     +---------------+
|   Client A    |     |   Client B    |     |   Client C    |
|---------------|     |---------------|     |---------------|
| Swing GUI     |     | Swing GUI     |     | Swing GUI     |
| SSL Socket    |     | SSL Socket    |     | SSL Socket    |
+---------------+     +---------------+     +---------------+

          Encrypted Communication Using TLS
```

---

# 📥 Clone Repository

```bash
git clone https://github.com/prathamzdgkr/Secure-TLS-Chat-Application.git

cd Secure-TLS-Chat-Application
```

---

# ☕ Prerequisites

Make sure the following are installed:

* Java JDK 8 or higher
* Git
* TLS KeyStore Certificate

---

# 🔑 Environment Variables

## Windows

```cmd
set KEYSTORE_PASSWORD=your_password

set TRUSTSTORE_PASSWORD=your_password
```

## Linux / macOS

```bash
export KEYSTORE_PASSWORD=your_password

export TRUSTSTORE_PASSWORD=your_password
```

---

# ▶️ Run the Server

Compile:

```bash
javac server/*.java
```

Run:

```bash
java server.ChatServer
```

Expected Output:

```text
Secure TLS Chat Server Started on Port 1222
```

---

# ▶️ Run the Client

Compile:

```bash
javac client/*.java
```

Run:

```bash
java client.LoginFrame
```

Enter a username and connect to the secure server.

---

# 💬 Messaging Features

## Public Chat

Broadcast messages to all connected users.

```text
Hello Everyone!
```

---

## Private Messaging

Send direct messages to specific users.

```text
@John Hello
```

Example:

```text
@Alex Are you available?
```

---

## File Sharing

Users can:

* Select a file
* Upload it through the client
* Send to all users
* Send privately to a specific user
* Download received files

---

## Network Diagnostics

The client supports latency monitoring.

```text
/ping
```

Displays:

```text
Latency (RTT) to secure server is 24 ms
```

---

# 📊 Server Dashboard

The server continuously displays operational metrics.

## Server Status

* Server Uptime
* Active Connections

## TCP Statistics

* Total Connections
* Total Disconnections

## Message Statistics

* Broadcast Messages
* Private Messages
* Failed Private Messages

## TLS Statistics

* TLS Version
* Cipher Suite
* Secure Sessions

## Performance Metrics

* Active Threads
* Thread Pool Utilization
* Memory Consumption

## Routing Table

Displays:

* Username
* IP Address
* Port Number
* Session Uptime

---

# 🔌 Communication Flow

```text
Client Connects
        ↓
TLS Handshake
        ↓
Authentication (Username)
        ↓
Server Registers Client
        ↓
User List Broadcast
        ↓
Message/File Routing
        ↓
Secure Communication
```

---

# 🚀 Future Enhancements

* User Authentication System
* Database Integration
* Chat History Persistence
* Group Chat Rooms
* Message Encryption at Application Layer
* User Profiles & Avatars
* Voice Communication
* Video Communication
* Cloud Deployment
* Docker Containerization
* Web-Based Client
* Mobile Application
* Message Search Functionality
* Offline Messaging Support

---

# 📚 Learning Outcomes

This project demonstrates practical understanding of:

* Computer Networks
* TCP/IP Protocol Stack
* Socket Programming
* SSL/TLS Security
* Concurrent Programming
* Java Swing Development
* Client-Server Systems
* Thread Pool Management
* Network Monitoring
* Secure Communication Systems

---

# 📄 License

This project is intended for educational, academic, and portfolio purposes.

---

# 👨‍💻 Author

Developed by **Pratham Zadgaonkar**

---

# ⭐ Support

If you found this project useful, consider giving it a ⭐ on GitHub.
