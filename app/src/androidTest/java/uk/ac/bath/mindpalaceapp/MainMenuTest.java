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
public class MainMenuTest {
    @Rule
    public ActivityTestRule <MainMenu> activityRule = new ActivityTestRule <>(MainMenu.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void signOut() {
        onView(withId(R.id.signOut)).perform(click());
        intended(hasComponent(SignIn.class.getName()));
    }

    @Test
    public void goToCreateMenu() {
        onView(withId(R.id.create_menu)).perform(click());
        intended(hasComponent(CreateMenu.class.getName()));
    }

    @Test
    public void goToTrainMenu() {
        onView(withId(R.id.train_menu)).perform(click());
        intended(hasComponent(TrainMenu.class.getName()));
    }

    @Test
    public void goToTrackMenu() {
        onView(withId(R.id.track_menu)).perform(click());
        intended(hasComponent(TrackMenu.class.getName()));
    }

    @Test
    public void goToSettings() {
        onView(withId(R.id.settings)).perform(click());
        intended(hasComponent(SettingsMenu.class.getName()));
    }
}