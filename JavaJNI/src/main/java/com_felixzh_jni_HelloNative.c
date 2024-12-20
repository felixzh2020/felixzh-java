#include "com_felixzh_jni_HelloNative.h"

#include <stdio.h>

JNIEXPORT void JNICALL Java_com_felixzh_jni_HelloNative_sayHello(JNIEnv *env, jclass jc)
{
  printf("FelixZh，Hello JNI.");
}