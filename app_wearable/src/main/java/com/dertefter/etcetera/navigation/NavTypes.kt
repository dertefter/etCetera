package com.dertefter.etcetera.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.dertefter.navigation.AttachmentNavigationModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val AttachmentListType = object : NavType<List<AttachmentNavigationModel>>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): List<AttachmentNavigationModel>? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): List<AttachmentNavigationModel> {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun put(bundle: Bundle, key: String, value: List<AttachmentNavigationModel>) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun serializeAsValue(value: List<AttachmentNavigationModel>): String {
        return Uri.encode(Json.encodeToString(value))
    }
}
