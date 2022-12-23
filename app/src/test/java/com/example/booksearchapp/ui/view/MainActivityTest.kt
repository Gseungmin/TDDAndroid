package com.example.booksearchapp.ui.view

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.SmallTest
import androidx.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class MainActivityTest {

    @get:Rule
    var activityScenarioRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun test_Activity_State() {
        val activityState = activityScenarioRule.scenario.state.name
        assertThat(activityState).isEqualTo("RESUMED")
    }
}