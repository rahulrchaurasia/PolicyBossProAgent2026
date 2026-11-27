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

/*
 * Lenient serializers for nullable numbers / booleans in API responses.
 * The backend sends the same field as 24017, "24017", "" or null depending on the
 * insurer, so these accept a number or a numeric string and map anything else
 * (blank, "NA", objects, arrays) to null instead of failing the whole response.
 */

/** Trimmed primitive content, or null for JsonNull / objects / arrays / blank. */
private fun Decoder.lenientContent(): String? {
    val json = this as? JsonDecoder ?: error("Lenient serializers support JSON only")
    val element = json.decodeJsonElement()
    if (element === JsonNull || element !is JsonPrimitive) return null
    return element.content.trim().removeSurrounding("\"").trim().takeIf { it.isNotEmpty() }
}

@OptIn(ExperimentalSerializationApi::class)
object LenientNullableIntSerializer : KSerializer<Int?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LenientNullableInt", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Int? = decoder.lenientContent()?.let {
        it.toIntOrNull() ?: it.toDoubleOrNull()?.toInt()
    }

    override fun serialize(encoder: Encoder, value: Int?) {
        if (value == null) encoder.encodeNull() else encoder.encodeInt(value)
    }
}

@OptIn(ExperimentalSerializationApi::class)
object LenientNullableLongSerializer : KSerializer<Long?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LenientNullableLong", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): Long? = decoder.lenientContent()?.let {
        it.toLongOrNull() ?: it.toDoubleOrNull()?.toLong()
    }

    override fun serialize(encoder: Encoder, value: Long?) {
        if (value == null) encoder.encodeNull() else encoder.encodeLong(value)
    }
}

@OptIn(ExperimentalSerializationApi::class)
object LenientNullableDoubleSerializer : KSerializer<Double?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LenientNullableDouble", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Double? = decoder.lenientContent()?.toDoubleOrNull()

    override fun serialize(encoder: Encoder, value: Double?) {
        if (value == null) encoder.encodeNull() else encoder.encodeDouble(value)
    }
}

/** Accepts true/false, 1/0 and yes/no (any case); anything else is null. */
@OptIn(ExperimentalSerializationApi::class)
object LenientNullableBooleanSerializer : KSerializer<Boolean?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LenientNullableBoolean", PrimitiveKind.BOOLEAN)

    override fun deserialize(decoder: Decoder): Boolean? = when (decoder.lenientContent()?.lowercase()) {
        "true", "1", "yes", "y" -> true
        "false", "0", "no", "n" -> false
        else -> null
    }

    override fun serialize(encoder: Encoder, value: Boolean?) {
        if (value == null) encoder.encodeNull() else encoder.encodeBoolean(value)
    }
}
