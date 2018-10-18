package com.example.bolo.os_application

import jp.co.cyberagent.android.gpuimage.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.GPUImageTwoInputFilter

/**
 * Create by bolo on 15/10/2018
 */

private const val SHARE_STRING = "varying highp vec2 textureCoordinate;\n" +
        " varying highp vec2 textureCoordinate2;\n" +
        " \n" +
        " uniform sampler2D inputImageTexture;\n" +
        " uniform sampler2D inputImageTexture2;\n" +
        " \n" +
        " uniform lowp float mixturePercent;\n" +
        " \n" +
        " void main()\n" +
        " {\n" +
        "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
        "     lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
        "\n" +
        "    gl_FragColor = vec4(textureColor.r*textureColor2.r, textureColor.g*textureColor2.g, textureColor.b*textureColor2.b, textureColor2.b);\n" +
        " }"

class ConvertImageFilter : GPUImageTwoInputFilter(SHARE_STRING) {


    override fun onInit() {
        super.onInit()
    }

}