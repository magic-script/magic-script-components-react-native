/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.reactlibrary.utils

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import com.google.gson.JsonArray
import com.google.gson.JsonObject

fun ReadableMap.convertMapToJson(): JsonObject {
    val jsonObject = JsonObject()
    val iterator = keySetIterator()
    while (iterator.hasNextKey()) {
        val key = iterator.nextKey()
        when (getType(key)) {
            ReadableType.Null -> jsonObject.add(key, null)
            ReadableType.Boolean -> jsonObject.addProperty(key, getBoolean(key))
            ReadableType.Number -> jsonObject.addProperty(key, getDouble(key))
            ReadableType.String -> jsonObject.addProperty(key, getString(key))
            ReadableType.Map -> jsonObject.add(key, getMap(key)?.convertMapToJson())
            ReadableType.Array -> jsonObject.add(key, getArray(key)?.convertArrayToJson())
        }
    }
    return jsonObject
}

fun ReadableArray.convertArrayToJson(): JsonArray {
    val array = JsonArray()
    for (i in 0 until size()) {
        when (getType(i)) {
            ReadableType.Null -> { }
            ReadableType.Boolean -> array.add(getBoolean(i))
            ReadableType.Number -> array.add(getDouble(i))
            ReadableType.String -> array.add(getString(i))
            ReadableType.Map -> array.add(getMap(i)?.convertMapToJson())
            ReadableType.Array -> array.add(getArray(i)?.convertArrayToJson())
        }
    }
    return array
}