#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/un.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <netinet/in.h>
#include <netdb.h>
#include <android/log.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <errno.h>

#define  LOG_TAG    "NSocket"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

int dataLengthRest = 0;
int count = 0;
//, jstring port, jstring addr, jstring data, int maxDataLength
 extern "C" {
     JNIEXPORT jstring JNICALL Java_com_helloworld_testjni2_SelectAndShare_connectToHostJNICPP(JNIEnv * env, jobject obj, jstring fileName);
 };

 JNIEXPORT jstring JNICALL Java_com_helloworld_testjni2_SelectAndShare_connectToHostJNICPP(
 	JNIEnv *env, jobject obj, jstring fileName) {

	 	char buf[28] = {0};
	 	char bufFinal[1000] = {0};
	 	ssize_t bytes_read;
	 	int fd;
	 	int sizeOfBuf = 1000;
	 	int size = 0;
	 	const char *name = (*env).GetStringUTFChars(fileName, NULL);
	 	if ((fd = open(name, O_RDONLY)) == -1)
	 	{
	 		//__android_log_print(ANDROID_LOG_DEBUG, "LOG_TAG", "Log catch %s", "File is not readable");
			size++;
	 	}
	 	jbyteArray firstMacArray = (*env).NewByteArray(1000);
	 	while((bytes_read = read(fd, buf, 28)) > 0){

	 		strncat(bufFinal, buf, 28);

	 		//__android_log_print(ANDROID_LOG_DEBUG, "LOG_TAG", "Log catch %i", bytes_read);
	 		//__android_log_print(ANDROID_LOG_DEBUG, "LOG_TAG", "Log catch %s", buf);
 	 	}

	 	jstring jstrBuf = (*env).NewStringUTF(bufFinal);
	 	close(fd);
	    return jstrBuf;
	 	//return env->NewStringUTF("Native code rules!");
 	 }
