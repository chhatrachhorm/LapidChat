package chhatrachhorm.androidapp.onenterpise.lapidchat;

import android.os.SystemClock;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

/**
 * Created by chhormchhatra on 8/25/17.
 */

@RunWith(AndroidJUnit4.class)
public class WelcomeActivityTest {
    @Rule
    public final ActivityRule<MainActivity> main = new ActivityRule<>(MainActivity.class);

    @Test
    public void things() {
        SystemClock.sleep(TimeUnit.SECONDS.toMillis(2));
    }
}
