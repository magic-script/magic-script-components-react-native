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
package com.magicleap.magicscript.utils

import com.magicleap.magicscript.utils.VerySimpleDateFormat
import org.amshove.kluent.shouldEqual
import org.junit.Test
import java.util.*

class VerySimpleDateFormatTest {

    val locale = Locale.getDefault()

    @Test
    fun `should downcase days`() {
        val tested =
            VerySimpleDateFormat("DD", locale)

        tested.toPattern() shouldEqual "dd"
    }

    @Test
    fun `should properly format date from given data`() {
        val tested = VerySimpleDateFormat(
            "DD/MM/YYYY",
            locale
        )

        val format = tested.format(2020, 10, 20)

        format shouldEqual "20/10/2020"
    }
}