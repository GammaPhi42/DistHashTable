Author: Michael Miller
http://github.com/GammaPhi42

This project was a submission to my CS455 Introduction to Distributed Systems class at Colorado State University. After its submission, it has since been improved.

SYNOPSIS:
At a high level, this Java project is a Distributed Hash Table (DHT) which can allow for content storage and dissemination across a distributed network (after some further implementation).

In its current implementation, the user starts N instances of a node class that communicate with a singleton Registry class. Each node registers its own unique ID, IP address, and port with the Registry.

After each Node instance is registered with the Registry, the user may issue the command to start the testing/verification process. This process includes all N Nodes each generating K random numbers, and sending each number (within a TCP packet) to a random node in the system (while maintaining at most lg(N) direct connections from each Node). After this process has completed, the Registry will collect the cumulative sums of sent and received numbers, to make sure they are equal (ensuring each packet was not lost, and that each packet was delivered to its intended recipient).

PREREQUISITES:
Java installed on each machine in the cluster (can run multiple instances of Node along with a Registry entirely from one machine), running some flavor of GNU+Linux.

HOW TO COMPILE:
Execute the command 'make all' in the project's root directory.

HOW TO EXECUTE:
Execute the command 'java cs455.overlay.node.Registry <port>' where <port> is the port on which the Registry will listen for connections.


