package uk.ac.bath.mindpalaceapp;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.espresso.intent.Intents;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewPalaceTest {
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
    public void viewProgress() throws InterruptedException {
        onView(withId(R.id.loginUsername))
                .perform(typeText("cjd47"), closeSoftKeyboard());
        onView(withId(R.id.loginPassword))
                .perform(typeText("pass"), closeSoftKeyboard());
        onView(withId(R.id.signIn)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.track_menu)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.track)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.palaceTitle))
                .check(matches(withText("Fruit")));
        onView(withId(R.id.palaceDescription))
                .check(matches(withText("Fruit Palace")));
        onData(anything()).inAdapterView(withId(R.id.unrememberedNotes)).atPosition(0).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.noteDetailsTitle))
                .check(matches(withText("Apple")));
        onView(withId(R.id.noteDetailsDescription))
                .check(matches(withText("Apple Description")));

        onView(withId(R.id.noteImage)).perform(click());
        Thread.sleep(2000);

        intended(hasComponent(ViewPalace.class.getName()));
    }
}