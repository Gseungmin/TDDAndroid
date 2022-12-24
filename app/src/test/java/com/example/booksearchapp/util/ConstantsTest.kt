package com.example.booksearchapp.util

import androidx.test.filters.SmallTest
import com.example.booksearchapp.util.Constants.API_KEY
import com.google.common.truth.Truth
import org.junit.Test

@SmallTest
class ConstantsTest {


    @Test
    fun `key test`() {
        // Given
        val key = API_KEY
        val value = "937eecb78ab4f0780521a9f149517547"

        Truth.assertThat(key).isEqualTo(value)
    }
}