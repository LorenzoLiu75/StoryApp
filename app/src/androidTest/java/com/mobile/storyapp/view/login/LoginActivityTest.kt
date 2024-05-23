package com.mobile.storyapp.view.login

import android.content.Intent
import android.widget.ProgressBar
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.mobile.storyapp.R
import com.mobile.storyapp.utils.EspressoIdlingResource
import com.mobile.storyapp.utils.ProgressBarIdlingResource
import com.mobile.storyapp.view.main.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)
    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }
    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLoginSuccess() {
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)
        activityScenario.onActivity {
            val progressBar = it.findViewById<ProgressBar>(R.id.progressBar)
            IdlingRegistry.getInstance().register(ProgressBarIdlingResource(progressBar))
        }

        // Input email
        onView(withId(R.id.ed_login_email)).perform(typeText("alisa999@gmail.com"), closeSoftKeyboard())
        // Input password
        onView(withId(R.id.ed_login_password)).perform(typeText("alisacantik80"), closeSoftKeyboard())
        // Click login button
        onView(withId(R.id.loginButton)).perform(click())
        // Wait for AlertDialog to appear
        onView(withText(R.string.yeah)).inRoot(isDialog()).check(matches(isDisplayed()))
        // Click the AlertDialog positive button
        onView(withText(R.string.lanjut)).perform(click())
        // Verify that MainActivity is launched
        onView(withId(R.id.main)).check(matches(isDisplayed()))
    }

    @Test
    fun testLogout() {
        // Simulate login success by launching MainActivity
        activity.scenario.onActivity {
            it.startActivity(Intent(it, MainActivity::class.java))
        }

        // Buka menu opsi
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        // Click logout button
        onView(withId(R.id.logout)).perform(click())
        // Verify that WelcomeActivity is launched
        onView(withId(R.id.welcome)).check(matches(isDisplayed()))
    }
}