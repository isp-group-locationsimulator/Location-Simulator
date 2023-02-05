package com.ispgr5.locationsimulator.domain.model

import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.Assert.*
import org.junit.Test

/**
 * Test class for the Configuration Component Converter
 */
class ConfigurationComponentConverterTest {

    private val testCompList: List<ConfigComponent> = listOf(
        Vibration(
            minStrength = 2,
            maxStrength = 5,
            minPause = 3,
            maxPause = 8,
            minDuration = 3,
            maxDuration = 4
        ),
        Sound(
            source = "soundSource",
            minVolume = 3,
            maxVolume = 7,
            minPause = 1,
            maxPause = 9,
            isRandom = true
        )
    )

    private val testCompListStr: String = "[" +
            "{\"comp_type\":\"Vibration\",\"minStrength\":2,\"maxStrength\":5,\"minPause\":3,\"maxPause\":8,\"minDuration\":3,\"maxDuration\":4}," +
            "{\"comp_type\":\"Sound\",\"source\":\"soundSource\",\"minVolume\":3,\"maxVolume\":7,\"minPause\":1,\"maxPause\":9,\"isRandom\":true}" +
            "]"

    /**
     * test that the json string is the right String from the component List
     */
    @ExperimentalSerializationApi
    @Test
    fun componentListToString() {
        assertEquals(
            testCompListStr,
            ConfigurationComponentConverter().componentListToString(testCompList)
        )
    }

    /**
     * test that the component list is the right when parsing the json String
     */
    @ExperimentalSerializationApi
    @Test
    fun componentStrToComponentList() {
        assertEquals(
            testCompList,
            ConfigurationComponentConverter().componentStrToComponentList(testCompListStr)
        )
    }
}