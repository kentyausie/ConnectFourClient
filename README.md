# ConnectFourClient

The client was built in java with Android Studio. The program can be run on any android device or 
emulator an sdk version of 15 or higher. When the client starts, it tried to connect to the server 
first. It is assigned a player number based on the order it joined the server. Once both players 
connect the game begins. The players alternate placing pieces on the board by tapping anywhere in
the column they want to make their move. Every time a column is tapped, it adds a piece starting 
at the bottom row and moving up. If the player taps a column that is full or tries to make any other
invalid move, a message tells them that it is an invalid move and lets them make a new selection before
it is sent to the server. Once the player has made valid move, it is sent to the server. When the server
informs the client that there is a winner or a draw, the game is over. Once the game is over, the clients 
must be restarted to play again.
