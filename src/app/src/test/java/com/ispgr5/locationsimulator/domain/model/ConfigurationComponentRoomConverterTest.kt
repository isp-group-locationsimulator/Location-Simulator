package com.ispgr5.locationsimulator.domain.model

import kotlinx.serialization.ExperimentalSerializationApi
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import org.junit.Assert.*
import org.junit.Test

/**
 * Test class for the Configuration Component Converter
 */
class ConfigurationComponentRoomConverterTest {

    private val testCompList: List<ConfigComponent> = listOf(
        ConfigComponent.Vibration(
            id = 2,
            name = "testVib",
            minStrength = 2,
            maxStrength = 5,
            minPause = 3,
            maxPause = 8,
            minDuration = 3,
            maxDuration = 4
        ),
        ConfigComponent.Sound(
            id = 3,
            source = "soundSource",
            minVolume = 3f,
            maxVolume = 7f,
            minPause = 1,
            maxPause = 9,
            name = "name1"
        )
    )

    val component1 = mapOf(
        "comp_type" to "Vibration",
        "id" to 2,
        "name" to "testVib",
        "minStrength" to 2,
        "maxStrength" to 5,
        "minPause" to 3,
        "maxPause" to 8,
        "minDuration" to 3,
        "maxDuration" to 4
    )

    val component2 = mapOf(
        "comp_type" to "Sound",
        "source" to "soundSource",
        "minVolume" to 3,
        "maxVolume" to 7,
        "minPause" to 1,
        "maxPause" to 9,
        "name" to "name1",
        "id" to 3
    )

    private val serializedComponents = listOf(component1, component2)

    /**
     * test that the json string is the right String from the component List
     */
    @ExperimentalSerializationApi
    @Test
    fun componentListToString() {
        val serialized = ConfigurationComponentRoomConverter().componentListToString(testCompList)
        val jsonArray = JSONTokener(serialized).nextValue() as JSONArray
        @Suppress("UNCHECKED_CAST") val zipped = (jsonArray as Iterable<JSONObject>).zip(serializedComponents)
        zipped.forEach { (json, expected) ->
            expected.entries.forEach { (key, value) ->
                assert(json.has(key)) {
                    "key '$key' not serialized"
                }
                when (value) {
                    is String -> assertEquals(json.getString(key), value)
                    is Int -> assertEquals(json.getInt(key), value)
                    is Boolean -> assertEquals(json.getBoolean(key), value)
                }
            }
            json.keys().forEach { key ->
                assert(expected.containsKey(key)) {
                    "Key '$key' serialized but not expected"
                }
            }
        }
    }

    /**
     * test that the component list is the right when parsing the json String
     */
    @ExperimentalSerializationApi
    @Test
    fun componentStrToComponentList() {
        val objects = serializedComponents.map {
            JSONObject(it)
        }
        val array = JSONArray(objects)
        val strung = array.toString()
        try {
            val parsed = ConfigurationComponentRoomConverter().componentStrToComponentList(strung)
            assertEquals(serializedComponents.size, parsed.size)
        } catch (e: Exception) {
            fail("Error parsing '$strung' due to: ${e.message}")
        }
    }
}