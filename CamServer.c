#include <stdio.h>
#include <stdlib.h>
#include <capture.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <string.h>
#include <syslog.h>
#include <capture.h>

char *data;
static int intFrequency;
FILE *Image_File;

void send_int(int number, int socket);
void send_int(int number, int socket) {
	int32_t n_conv = htonl(number); //host to network long; This function converts 32-bit (4-byte) quantities from host byte order to network byte order.
	char *datab = (char *) &n_conv;
	int left = sizeof(n_conv);
	int sent_pack;
	do {
		sent_pack = write(socket, datab, left);
		if (sent_pack < 0)
			exit(1);
		else {
			datab += sent_pack;
			left -= sent_pack;
		}
	} while (left > 0);
}

//Beginning of captureSendImage function
void captureSendImage(char spec[], int socket);
void captureSendImage(char spec[], int socket) {
	size_t size;
	char read_buffer[256];
	media_frame *frame;
	media_stream *stream;

	//Establishes the connection with the stream.
	stream = capture_open_stream(IMAGE_JPEG, spec);

	int j = 1;

	while (j <= intFrequency) {
		//The actual capturing for the image
		frame = capture_get_frame(stream);
		//This is where the image will be saved.
		data = capture_frame_data(frame);
		//Gets the image's size
		size = capture_frame_size(frame);

		send_int((int) size, socket);

		bzero(read_buffer, 0);
		read(socket, read_buffer, 255);

		write(socket, (char *) data, size);

		bzero(read_buffer, 0);
		if ((read(socket, read_buffer, 255)) < 0) {
			sleep(3000);
			exit(1);
		}
		j++;
	}
	capture_frame_free(frame); //Closing the frame
	capture_close_stream(stream); //Closing the stream
}
//End of captureSendImage function

/////////////////////////////////////////////////////////////////////////////////////////////////
//Beginning of doProcessing function
void doProcessing(int socket);
void doProcessing(int socket) {
	int n;
	char read_buffer[256];
	char strImageSpec[30];

	// The bzero() function erases the data in the n bytes of the memory
	// starting at the location pointed to by s, by writing zeros (bytes
	// containing '\0') to that area.
	bzero(read_buffer, 256);

	//Read (1) how many frames per second should be captured, and the value sent from the client and assigned into intFrequency variable.
	n = read(socket, read_buffer, 255);
	if (n < 0) {
		perror("ERROR reading the intFrequency!\n");
		exit(1);
	}

	strcpy(strImageSpec, "fps="); // copies fps to strImageSpec.
	strcat(strImageSpec, (char *) read_buffer); // appends the strings
	intFrequency = atoi((char *) read_buffer); //coverts string to integer

	// Read (2) the resolution of the required image. Reads the value sent from the client

	bzero(read_buffer, 256);
	n = read(socket, read_buffer, 255);

	if (n < 0) {
		perror("ERROR reading the resolution!");
		exit(1);
	}

	strcat(strImageSpec, "&resolution=");
	strcat(strImageSpec, (char *) read_buffer);
	strcat(strImageSpec, "\nfrequency = %i");

	while (1) {
		captureSendImage(strImageSpec, socket);
	}
}

//Beginning of Main function.
int main(void) {
	int intSockFd;
	int intNewSocketFd;
	int intPortNo;
	int intClientLen;

	struct sockaddr_in serverAddress;
	struct sockaddr_in clientAddress;

	intSockFd = socket(AF_INET, SOCK_STREAM, 0);

	//Check if socket getting created or not
	if (intSockFd < 0) {
		perror("ERROR opening socket");
		exit(1);
	}
	// bzero() function erases the data in the sizeof(serverAddress) bytes of the memory
	// starting at the location pointed to by serverAddress, by writing zeros (bytes
	// containing '\0') to that area.
	bzero((char *) &serverAddress, sizeof(serverAddress));
	intPortNo = 8082;

	// Initialize socket structure
	serverAddress.sin_family = AF_INET; // address family, used for internet based application, IPv4 protocols
	serverAddress.sin_addr.s_addr = INADDR_ANY;
	serverAddress.sin_port = htons(intPortNo);

	// Bind the socket to the server address //bind function assigns a local protocol address to a socket // bind assigns the name to the socket
	if (bind(intSockFd, (struct sockaddr *) &serverAddress, sizeof(serverAddress)) < 0) {//socket descriptor returned by the socket function, contains ipadd and port, size
		perror("ERROR on binding");
		exit(1);
	}

	// Listen to the clients - process wait for the incoming connection. Here process will go in sleep mode
	listen(intSockFd, 5); //socket descriptor returned by the socket function,number of allowed connections or number of pending connection.
	intClientLen = sizeof(clientAddress);

	// Accept actual connection from the client
	while (1) {
		intNewSocketFd = accept(intSockFd, (struct sockaddr *) &clientAddress, (socklen_t *) &intClientLen); //accept function is called by a TCP server to return the next completed connection from the front of the completed connection queue.
		if (intNewSocketFd < 0) {
			perror("ERROR on accept");
			exit(1);
		}

		// Create a child process
		int intProcessId = fork(); // handle multiple clients at the same time
		if (intProcessId < 0) {
			perror("ERROR on fork");
			exit(1);
		}
		if (intProcessId == 0) { // This is the client process
			close(intSockFd);
			doProcessing(intNewSocketFd);
			exit(0);
		} else {
			close(intNewSocketFd);
		}
	}
	//return 0;
}
