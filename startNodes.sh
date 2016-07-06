#!/bin/bash

#Starting Registry
gnome-terminal -x java cs455.overlay.node.Registry 13337

for i in {1..8}
do
	gnome-terminal -x java cs455.overlay.node.MessagingNode fedorabox 13337
done
