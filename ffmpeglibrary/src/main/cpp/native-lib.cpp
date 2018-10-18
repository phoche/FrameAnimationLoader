#include <jni.h>
#include <string>
extern "C" {

#include "ffmpeg.h"
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>
JNIEXPORT jstring JNICALL
Java_com_bolo_ffmpeglibrary_FFmpegUtil_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

JNIEXPORT jstring JNICALL
Java_com_bolo_ffmpeglibrary_FFmpegUtil_avformatinfo(
        JNIEnv *env,
        jobject /* this */) {
    char info[40000] = {0};

    av_register_all();

    AVInputFormat *if_temp = av_iformat_next(NULL);
    AVOutputFormat *of_temp = av_oformat_next(NULL);

    while (if_temp != NULL) {
        sprintf(info, "%sInput: %s\n", info, if_temp->name);
        if_temp = if_temp->next;
    }

    while (of_temp != NULL) {
        sprintf(info, "%sOutput: %s\n", info, of_temp->name);
        of_temp = of_temp->next;
    }

    return env->NewStringUTF(info);
}

JNIEXPORT jstring JNICALL
Java_com_bolo_ffmpeglibrary_FFmpegUtil_urlprotocolinfo(
        JNIEnv *env,
        jobject /* this */) {

    char info[40000] = {0};
    av_register_all();

    struct URLProtocol *pup = NULL;

    struct URLProtocol **p_temp = &pup;
    avio_enum_protocols((void **) p_temp, 0);

    while ((*p_temp) != NULL) {
        sprintf(info, "%sInput: %s\n", info, avio_enum_protocols((void **) p_temp, 0));
    }
    pup = NULL;
    avio_enum_protocols((void **) p_temp, 1);
    while ((*p_temp) != NULL) {
        sprintf(info, "%sInput: %s\n", info, avio_enum_protocols((void **) p_temp, 1));
    }
    return env->NewStringUTF(info);
}

JNIEXPORT jstring JNICALL
Java_com_bolo_ffmpeglibrary_FFmpegUtil_avcodecinfo(
        JNIEnv *env,
        jobject /* this */) {

    char info[40000] = {0};

    av_register_all();

    AVCodec *c_temp = av_codec_next(NULL);

    while (c_temp != NULL) {
        if (c_temp->decode != NULL) {
            sprintf(info, "%sdecode:", info);
        } else {
            sprintf(info, "%sencode:", info);
        }
        switch (c_temp->type) {
            case AVMEDIA_TYPE_VIDEO:
                sprintf(info, "%s(video):", info);
                break;
            case AVMEDIA_TYPE_AUDIO:
                sprintf(info, "%s(audio):", info);
                break;
            default:
                sprintf(info, "%s(other):", info);
                break;
        }
        sprintf(info, "%s[%10s]\n", info, c_temp->name);
        c_temp = c_temp->next;
    }

    return env->NewStringUTF(info);
}

JNIEXPORT jstring JNICALL
Java_com_bolo_ffmpeglibrary_FFmpegUtil_avfilterinfo(JNIEnv *env, jobject /* this */) {

    char info[40000] = {0};
    avfilter_register_all();

    AVFilter *f_temp = (AVFilter *) avfilter_next(NULL);
    while (f_temp != NULL) {
        sprintf(info, "%s%s\n", info, f_temp->name);
        f_temp = f_temp->next;
    }
    return env->NewStringUTF(info);
}

JNIEXPORT jint JNICALL
Java_com_bolo_ffmpeglibrary_FFmpegUtil_run(JNIEnv *env, jclass type, jint cmdLen,
                                                       jobjectArray cmd) {

    char *argCmd[cmdLen];
    jstring buf[cmdLen];

    for (int i = 0; i < cmdLen; ++i) {
        buf[i] = static_cast<jstring>(env->GetObjectArrayElement(cmd, i));
        char *string = const_cast<char *>(env->GetStringUTFChars(buf[i], JNI_FALSE));
        argCmd[i] = string;
    }

    ffmpeg_exec(cmdLen, argCmd);

    return 0;

}
}