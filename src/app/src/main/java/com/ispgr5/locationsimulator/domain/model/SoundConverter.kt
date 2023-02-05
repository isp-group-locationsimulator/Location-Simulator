package com.ispgr5.locationsimulator.domain.model

import com.google.common.io.BaseEncoding

/**
 * This Class can Convert soundFiles in form of their read ByteArrays to String by using Base64 Encoding
 * And decode the String back to a ByteArray
 */
class SoundConverter {

    /**
     * Converts the Sound-ByteArray to A String using Base64 encoding
     * @return the Base64 String
     */
    fun encodeByteArrayToBase64String(byteArray: ByteArray): String {
        return BaseEncoding.base64().encode(byteArray)
    }

    /**
     * Converts the Base64 String back to a ByteArray by using Base64 decoding
     * @return the decoded ByteArray
     */
    fun decodeBase64StringToByteArray(base64String: String): ByteArray {
        return BaseEncoding.base64().decode(base64String)
    }
}