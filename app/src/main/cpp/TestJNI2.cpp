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
#include <android/log.h>

#define  LOG_TAG    "NSocket"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  BUFFER_SIZE 1000

int dataLengthRest = 0;
int count = 0;

extern "C" {
	JNIEXPORT jstring JNICALL Java_com_testjni2_SelectAndShare_connectToHostJNICPP2(JNIEnv * env, jobject obj, jstring fileName);
};

JNIEXPORT jstring JNICALL Java_com_testjni2_SelectAndShare_connectToHostJNICPP2(
		JNIEnv *env, jobject obj, jstring fileName) {
	char buffer[BUFFER_SIZE] = {0};
	ssize_t bytes_read;
	FILE *fd;
	const char *name = env->GetStringUTFChars(fileName, NULL);
	if ((fd = fopen(name, "r")) == NULL) {
        env->ReleaseStringUTFChars(fileName, name);
		return NULL;
	}
    env->ReleaseStringUTFChars(fileName, name);

	// int fsize = lseek(fd, 0, SEEK_END);
	// jbyteArray firstMacArray = (*env).NewByteArray(1000);
	while (fread(buffer, 1, sizeof buffer, fd) > 0) // expecting 1 element of size BUFFER_SIZE
	{
		 // process buffer
	}
	if (feof(fd))
	{
		// hit end of file
	}
	else
	{
		// some other error interrupted read
        return NULL;
	}
	jstring jstrBuf = env->NewStringUTF(buffer);
	fclose(fd);
	return jstrBuf;
}
