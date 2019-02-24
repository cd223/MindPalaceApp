package uk.ac.bath.mindpalaceapp;

import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.intent.Intents;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.VerificationModes.times;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TrainMenuTest {
    @Rule
    public ActivityTestRule <SignIn> activityRule = new ActivityTestRule <>(SignIn.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void goToViewNote() throws InterruptedException {
        onView(withId(R.id.loginUsername))
                .perform(typeText("cjd47"), closeSoftKeyboard());
        onView(withId(R.id.loginPassword))
                .perform(typeText("pass"), closeSoftKeyboard());
        onView(withId(R.id.signIn)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.train_menu)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.train)).perform(click());
        intended(hasComponent(ViewNote.class.getName()));
    }
}