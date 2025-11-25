//
// Created by feibo on 11/24/25.
//

#ifndef RPCTOOLS_CLOG_H
#define RPCTOOLS_CLOG_H

#include <android/log.h>
#include <string>
#include <sstream>
#include <iomanip>

#define DEBUG
#ifdef DEBUG
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  "LychowHook", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "LychowHook", __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "LychowHook", __VA_ARGS__)
#else
#define LOGI(...) ((void)0)
#define LOGE(...) ((void)0)
#define LOGD(...) ((void)0)
#endif
namespace Logger {
    void hex_dump_log(const void *addr, size_t size, const char *tag = "DUMP");
}

#endif //RPCTOOLS_CLOG_H
