## INSTRUCTIONS
To run this project, you must import it into an IDE and build it. Then you need to start the server by running Server.java. On initial startup, the server will check for the serialized file containing all the events and if it doesn't exist the server will create it and save it. The server must be running before the client tries to connect otherwise, the client will crash.
![Server Initialization](image.png)

Now the server will wait for a connection from a client. To run a client, go into Client.java and run. Upon startup enter 'y' to run the client. Enter the IP address of the server (or nothing for a local instance) then enter what you want to do according to the menu options. 

![Client Initialization](image-1.png) 

Upon fulfilling 5 requests the server will automatically save changes. Additionally, when a client decides to disconnect the server saves changes as well. 
![Client Ended Connection](image-2.png)

(As a small note, selections of menu options are meant to be single characters, but any string starting with the appropriate character will work).

## ABOUT
This project is for CSCI 455 - Networking and Parallel Computation. It demonstrates how to do some basic networking using java sockets as well as concurrency and data integrity when there are multiple clients accessing shared resources. This project uses very basic locks to achieve mutual exclusion of shared resources.
Overall the architecture and the sophistication of the threading and management of shared resources could be improved with more refinement.
