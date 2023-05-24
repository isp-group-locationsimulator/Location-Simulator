package com.ispgr5.locationsimulator.domain.model

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.io.IOException
import java.lang.reflect.Type


private const val COMP_TYPE = "comp_type"


class ConfigurationComponentGsonConverter : JsonDeserializer<ConfigComponent>, JsonSerializer<ConfigComponent> {
	override fun deserialize(
		json: JsonElement?,
		typeOfT: Type,
		context: JsonDeserializationContext
	): ConfigComponent {
		if (json == null) throw NullPointerException("null value received for config component deserialization")
		val kind = json.asJsonObject.get(COMP_TYPE).asString
		return when (ConfigComponent.ComponentKind.valueOf(kind)) {
			ConfigComponent.ComponentKind.Sound -> context.deserialize(json, Sound::class.java)
			ConfigComponent.ComponentKind.Vibration -> context.deserialize(json, Vibration::class.java)

		}
	}

	override fun serialize(
		src: ConfigComponent,
		typeOfSrc: Type,
		context: JsonSerializationContext
	): JsonElement {
		val serialized = context.serialize(src).asJsonObject
		val kind = when (src) {
			is Sound -> ConfigComponent.ComponentKind.Sound
			is Vibration -> ConfigComponent.ComponentKind.Vibration
			else -> throw UnsupportedOperationException("can't serialize class ${src::class.qualifiedName}")
		}
		serialized.addProperty(COMP_TYPE, kind.name)
		return serialized
	}
}

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
				subclass(Vibration::class)
				subclass(Sound::class)
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