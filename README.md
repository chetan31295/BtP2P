# BtP2P
## Bluetooth Peer to Peer Tic Tac Toe

To brush up on my Android skills and to explore the Android Bluetooth stack I created a small, but fun, game to wirelessly play tic tac toe over Bluetooth classic.

### Instructions
1. Clone the git project
2. Build the apk in Android Studio
3. Load the apk onto two Android devices
4. Pair Android devices over bluetooth before starting app
5. Both Android devices start app and have fun!

### Design
The game design follows a peer to peer architecture. Both phones are a client and server to the other phone. Both carry a flag on what state they are in so they won't send or receive inappropraite calls. The turn is set by the first person to make a move.
