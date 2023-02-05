package com.ispgr5.locationsimulator.domain.model

import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * This Test-Class tests the en and decoding with Base64
 * There is a .m4a File to test it
 */
class SoundConverterTest {

    /**
     * The read byteArray from the .m4a File
     */
    lateinit var byteArray: ByteArray

    @Before
    fun setUp() {
        val file = File("src/test/java/com/ispgr5/locationsimulator/domain/model/klopfen.m4a")
        byteArray = file.readBytes()
    }

    @Test
    fun encodeByteArrayToBase64String() {
        val base64String: String = SoundConverter().encodeByteArrayToBase64String(byteArray)
        val byteArrayBack: ByteArray =
            SoundConverter().decodeBase64StringToByteArray(base64String = base64String)
        assertEquals(SoundConverter().encodeByteArrayToBase64String(byteArrayBack), base64String)
    }

    @Test
    fun decodeBase64StringToByteArray() {
        val base64String: String =
            SoundConverter().encodeByteArrayToBase64String(byteArray = byteArray)
        assertArrayEquals(
            SoundConverter().decodeBase64StringToByteArray(base64String = base64String),
            byteArray
        )
    }
}