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
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.times;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NewNoteTest {
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
    public void createNote() throws InterruptedException {
        onView(withId(R.id.loginUsername))
                .perform(typeText("cjd47"), closeSoftKeyboard());
        onView(withId(R.id.loginPassword))
                .perform(typeText("pass"), closeSoftKeyboard());
        onView(withId(R.id.signIn)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.create_menu)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.new_note)).perform(click());

        onView(withId(R.id.noteCreationTitle))
                .perform(typeText("Test Note"), closeSoftKeyboard());
        onView(withId(R.id.noteDescription))
                .perform(typeText("Note Created By UI Test"), closeSoftKeyboard());
        onView(withId(R.id.launch_browser)).perform(click());
        onView(withId(R.id.chooseImage)).perform(click());

        onView(withId(R.id.new_note)).perform(click());
        Thread.sleep(2000);
        intended(hasComponent(MainMenu.class.getName()), times(2));
    }
}