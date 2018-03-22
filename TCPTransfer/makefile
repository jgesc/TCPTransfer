# Simple makefile to build the project

make:
	javac -d . src/*.java
	jar cfm TCPTransfer.jar src/Manifest.txt tcptransfer/*.class

.PHONY: clean
clean:
	rm -r tcptransfer
	rm TCPTransfer.jar
