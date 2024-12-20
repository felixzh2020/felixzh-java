#include <iostream>

#include "com_felixzh_jni_HelloNative.h"

using namespace std;

JNIEXPORT void JNICALL Java_com_felixzh_jni_HelloNative_sayHello(JNIEnv *env, jclass jc)
{
  cout << "FelixZh, Hello Native C++." << endl;
}