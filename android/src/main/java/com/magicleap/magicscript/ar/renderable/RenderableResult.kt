/*
 * Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.magicleap.magicscript.ar.renderable

import com.google.ar.sceneform.rendering.Renderable

sealed class RenderableResult<out T : Renderable> {
    data class Success<out T : Renderable>(val renderable: T) : RenderableResult<T>()
    data class Error(val error: Throwable) : RenderableResult<Nothing>()
}