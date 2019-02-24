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
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateMenuTest {
    @Rule
    public ActivityTestRule <CreateMenu> activityRule = new ActivityTestRule <>(CreateMenu.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void goToCreatePalace() {
        onView(withId(R.id.new_palace)).perform(click());
        intended(hasComponent(CreatePalace.class.getName()));
    }

    @Test
    public void goToCreateNote() {
        onView(withId(R.id.new_note)).perform(click());
        intended(hasComponent(CreateNote.class.getName()));
    }
}