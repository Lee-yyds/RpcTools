#include <jni.h>
#include <string>
#include "JVM/JVM.h"
#include "Util/FindClass.h"
#include "Util/CLog.h"


extern "C" JNIEXPORT jstring JNICALL
Java_com_lychow_rpctools_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    LOGI("Native stringFromJNI called");
    bool ok = ArtInternals::Init();
    if (!ok){
        LOGE("Unsupported System! Please set your offsets in Findclass.cpp");
        return env->NewStringUTF("Unsupported System! Please set your offsets in Findclass.cpp");
    }
    Class_Method_Finder::iterate_class_info(env);
    for (const auto& c :  UnorderedStore<CLASSNAMETYPE>::Instance().GetAll()){
        LOGI("get class -----> %s",c.c_str());
    }
    jclass myjclass = Class_Method_Finder::FindClassViaLoadClass(env,"com.lychow.rpctools.tools");
    LOGI("myjclass %p",myjclass);
    return env->NewStringUTF(hello.c_str());
}