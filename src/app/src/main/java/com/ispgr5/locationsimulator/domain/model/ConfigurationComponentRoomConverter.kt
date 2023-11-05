package com.ispgr5.locationsimulator.domain.model

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass


/**
 * Type Converter Class for a Vibration Object.
 * This class helps the Room Database to handle a List of ConfigComponent by
 * Converting the List to a String and back.
 * Room will automatically call the @TypeConverter functions to Convert the data
 */
@ProvidedTypeConverter
class ConfigurationComponentRoomConverter {
	/**
	 * The json en- and decoder with the Information of hierarchy and Class type names
	 */
	private val json = Json {
		//The class hierarchy
		serializersModule = SerializersModule {
			polymorphic(ConfigComponent::class) {
				subclass(ConfigComponent.Vibration::class)
				subclass(ConfigComponent.Sound::class)
			}
		}
		//under this name the Class Type is stored in json for example{"comp_type":"Vibration"}
		classDiscriminator = "comp_type"
	}

	/**
	 * This function converts a List of Vibration to a String by using Json
	 */
	@ExperimentalSerializationApi
	@TypeConverter
	fun componentListToString(componentList: List<ConfigComponent>): String {
		return json.encodeToString(componentList)
	}

	/**
	 * This function converts a String to a List of Vibrations by using Json
	 */
	@ExperimentalSerializationApi
	@TypeConverter
	fun componentStrToComponentList(componentStr: String): List<ConfigComponent> {
		return json.decodeFromString(componentStr)
	}
}