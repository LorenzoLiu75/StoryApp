package com.mobile.storyapp.view.login

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.mobile.storyapp.R
import com.mobile.storyapp.utils.EspressoIdlingResource
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
    fun testLoginAndLogout() {

        onView(withId(R.id.ed_login_email)).perform(replaceText("test@example.com"))
        onView(withId(R.id.ed_login_password)).perform(replaceText("password"))
        onView(withId(R.id.loginButton)).perform(click())

        Espresso.onIdle()

        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))

        Espresso.onIdle()

        onView(withText(R.string.yeah)).check(matches(isDisplayed()))
        onView(withText(R.string.lanjut)).perform(click())

        onView(withId(R.id.main)).check(matches(isDisplayed()))

        onView(withContentDescription(R.string.logout)).perform(click())
        onView(withText(R.string.logout)).perform(click())

        onView(withId(R.id.welcome)).check(matches(isDisplayed()))
    }
}