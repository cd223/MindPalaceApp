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
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewNoteTest {
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
    public void checkNoteExists() throws InterruptedException {
        onView(withId(R.id.loginUsername))
                .perform(typeText("cjd47"), closeSoftKeyboard());
        onView(withId(R.id.loginPassword))
                .perform(typeText("pass"), closeSoftKeyboard());
        onView(withId(R.id.signIn)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.train_menu)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.train)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.check_note)).perform(scrollTo(), click());
        Thread.sleep(2000);
        onView(withId(R.id.noteViewTitle))
                .check(matches(withText("Test Note")));
        onView(withId(R.id.noteDescription))
                .check(matches(withText("Note Created By UI Test")));
    }

    @Test
    public void checkNoteExists_markRemembered() throws InterruptedException {
        onView(withId(R.id.loginUsername))
                .perform(typeText("cjd47"), closeSoftKeyboard());
        onView(withId(R.id.loginPassword))
                .perform(typeText("pass"), closeSoftKeyboard());
        onView(withId(R.id.signIn)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.train_menu)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.train)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.check_note)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.noteViewTitle))
                .check(matches(withText("Test Note")));
        onView(withId(R.id.noteDescription))
                .check(matches(withText("Note Created By UI Test")));
        onView(withId(R.id.rememberedButton)).perform(scrollTo(), click());
    }

    @Test
    public void checkNoteExists_markUnremembered() throws InterruptedException {
        onView(withId(R.id.loginUsername))
                .perform(typeText("cjd47"), closeSoftKeyboard());
        onView(withId(R.id.loginPassword))
                .perform(typeText("pass"), closeSoftKeyboard());
        onView(withId(R.id.signIn)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.train_menu)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.train)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.check_note)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.noteViewTitle))
                .check(matches(withText("Test Note")));
        onView(withId(R.id.noteDescription))
                .check(matches(withText("Note Created By UI Test")));
        onView(withId(R.id.unrememberedButton)).perform(scrollTo(), click());
    }
}