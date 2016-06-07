#!/bin/bash

for i in [1,20]:
do
	java cs455.overlay.node.MessagingNode 10.0.0.12 1666 &
done
