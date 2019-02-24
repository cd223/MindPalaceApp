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
import static androidx.test.espresso.intent.VerificationModes.times;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignUpTest {
    @Rule
    public ActivityTestRule <SignUp> activityRule = new ActivityTestRule <>(SignUp.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void signUp_success() throws InterruptedException {
        onView(withId(R.id.signupName))
                .perform(typeText("Fabio"), closeSoftKeyboard());
        onView(withId(R.id.signupUsername))
                .perform(typeText("fnemetz"), closeSoftKeyboard());
        onView(withId(R.id.signupPassword))
                .perform(typeText("pass"), closeSoftKeyboard());
        onView(withId(R.id.signUp)).perform(click());
        Thread.sleep(2000);
        intended(hasComponent(MainMenu.class.getName()));

        onView(withId(R.id.signOut)).perform(click());

        onView(withId(R.id.loginUsername))
                .perform(typeText("fnemetz"), closeSoftKeyboard());
        onView(withId(R.id.loginPassword))
                .perform(typeText("pass"), closeSoftKeyboard());
        onView(withId(R.id.signIn)).perform(click());
        Thread.sleep(2000);
        intended(hasComponent(MainMenu.class.getName()), times(2));
    }
}