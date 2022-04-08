package com.example.spotifyapp.data.spotify_api

import com.example.spotifyapp.data.Track
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

class TrackListDeserializer : JsonDeserializer<TrackListSearchResult> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): TrackListSearchResult {
        val result = mutableListOf<Track>()

        val obj = json!!.asJsonObject
        val tracks = obj["tracks"].asJsonObject
        val arr = tracks["items"].asJsonArray
        for (i in 0 until arr.size()) {
            val trackObj = arr.get(i).asJsonObject
            val artistsArr = trackObj["artists"].asJsonArray
            val artists = mutableListOf<String>()
            for (j in 0 until artistsArr.size()) {
                artistsArr[j].asJsonObject.optString("name").also {
                    artists.add(it)
                }
            }
            Track(
                title = trackObj.optString("name"),
                artists = artists.joinToString(),
                externalUrl= trackObj.optString("category"),
            ).also {
                result.add(it)
            }
        }
        return TrackListSearchResult(result)
    }

    private fun JsonObject.optString(memberName: String): String {
        if (has(memberName)) {
            return get(memberName).asString
        }
        return ""
    }
}