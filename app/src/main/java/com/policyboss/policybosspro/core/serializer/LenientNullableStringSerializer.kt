package com.policyboss.policybosspro.core.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

/**
 * Lenient serializer for nullable strings in API responses.
 * Gracefully parses numbers (e.g. 9773113793 -> "9773113793"), unquotes strings,
 * and handles "NA", "null", JsonNull, and empty values by returning null.
 */

/*
What is LenientNullableStringSerializer?In Android and Kotlin applications,
 when your app talks to a backend server, the server sometimes sends data
 in unexpected formats. LenientNullableStringSerializer is a custom safety guard
 (serializer) built for Kotlin Serialization that handles messy text fields
  from the server so your app doesn't crash.   What Purpose Does It Serve?Normally,
  if your app expects a strict String from the server
   (like a user's name or mobile number), but the server accidentally
   sends back something weird, Kotlin throws a crash. This custom serializer
   acts like a translator and safety filter that fixes 4 major issues
   automatically:Handles Server Text Quirks: If the server sends literal
    text garbage like "null", "NA", or leaves the field completely blank,
    the serializer catches it and safely converts it into a proper Kotlin
     null instead of crashing.   Cleans Up Extra Quotes: Sometimes servers wrap strings inside
     extra quotes or escape characters. It automatically strips them out so you get a clean text value.   Parses Numbers Sent as Text: If the server sends a phone number or ID as a raw number or formatted text, it smoothly reads it as a valid string (9773113793 becomes "9773113793").   Prevents JSON Parsing Crashes: Without this, a single malformed data type from an API response could break your entire login or profile screen. This ensures the app fails gracefully or reads it as empty.
 */
@OptIn(ExperimentalSerializationApi::class)
object LenientNullableStringSerializer : KSerializer<String?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LenientNullableString", PrimitiveKind.STRING)



    override fun deserialize(decoder: Decoder): String? {
        val jsonDecoder = decoder as? JsonDecoder ?: return if (decoder.decodeNotNullMark()) decoder.decodeString() else null
        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> {
                if (element is JsonNull) {
                    null
                } else {
                    val str = element.content.trim().removeSurrounding("\"")
                    if (str.equals("NA", ignoreCase = true) || str.equals("null", ignoreCase = true) || str.isBlank()) {
                        null
                    } else {
                        str
                    }
                }
            }
            // Removed the unreachable 'is JsonNull -> null' branch here
            else -> null
        }
    }

    override fun serialize(encoder: Encoder, value: String?) {
        if (value == null) encoder.encodeNull() else encoder.encodeString(value)
    }
}

