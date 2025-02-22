package com.airbnb.deeplinkdispatch.sample;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;

import com.airbnb.deeplinkdispatch.DeepLink;
import com.google.common.collect.ImmutableList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.robolectric.Shadows.shadowOf;

@Config(sdk = 21, manifest = "../sample/src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class CustomPrefixesActivityTest {
  @Test public void testAppDeepLinkIntent() {
    Intent launchedIntent = getLaunchedIntent("app://airbnb/view_users?param=foo");
    assertThat(launchedIntent.getComponent(),
        equalTo(new ComponentName(ApplicationProvider.getApplicationContext(), CustomPrefixesActivity.class)));

    assertThat(launchedIntent.getBooleanExtra(DeepLink.IS_DEEP_LINK, false), equalTo(true));
    assertThat(launchedIntent.getStringExtra(DeepLink.URI),
        equalTo("app://airbnb/view_users?param=foo"));
    assertThat(launchedIntent.getExtras().getString("param"), equalTo("foo"));
  }

  @Test public void testWebDeepLinkIntent() {
    List<String> cases = ImmutableList.of("http://airbnb.com/users", "https://airbnb.com/users");
    for (String uri : cases) {
      Intent launchedIntent = getLaunchedIntent(uri);
      assertThat(launchedIntent.getComponent(),
          equalTo(new ComponentName(ApplicationProvider.getApplicationContext(), CustomPrefixesActivity.class)));

      assertThat(launchedIntent.getBooleanExtra(DeepLink.IS_DEEP_LINK, false), equalTo(true));
      assertThat(launchedIntent.getStringExtra(DeepLink.URI), equalTo(uri));
    }
  }

  @Test
  public void testWebPlaceholderDeepLinkIntent() {
    List<String> cases = ImmutableList.of("http://airbnb.com/guests", "https://airbnb.com/guests",
      "http://de.airbnb.com/guests", "https://de.airbnb.com/guests");
    for (String uri : cases) {
      Intent launchedIntent = getLaunchedIntent(uri);
      assertThat(launchedIntent.getComponent(),
        equalTo(new ComponentName(ApplicationProvider.getApplicationContext(), CustomPrefixesActivity.class)));

      assertThat(launchedIntent.getBooleanExtra(DeepLink.IS_DEEP_LINK, false), equalTo(true));
      assertThat(launchedIntent.getStringExtra(DeepLink.URI), equalTo(uri));
    }
  }

  @Test
  public void testWebPlaceholderDeepLinkIntentPlaceholderValue() {

    String uri = "https://de.airbnb.com/guests";
    Intent launchedIntent = getLaunchedIntent(uri);
    assertThat(launchedIntent.getComponent(),
      equalTo(new ComponentName(ApplicationProvider.getApplicationContext(), CustomPrefixesActivity.class)));

    assertThat(launchedIntent.getBooleanExtra(DeepLink.IS_DEEP_LINK, false), equalTo(true));
    assertThat(launchedIntent.getStringExtra(DeepLink.URI), equalTo(uri));
    assertThat(launchedIntent.getExtras().getString("scheme"), equalTo("s"));
    assertThat(launchedIntent.getExtras().getString("host_prefix"), equalTo("de."));
  }

  @Test
  public void testWebPlaceholderDeepLinkIntentWihtId() {
    List<String> cases = ImmutableList.of("http://airbnb.com/guest/123", "https://airbnb.com/guest/123",
      "http://de.airbnb.com/guest/123", "https://de.airbnb.com/guest/123");
    for (String uri : cases) {
      Intent launchedIntent = getLaunchedIntent(uri);
      assertThat(launchedIntent.getComponent(),
        equalTo(new ComponentName(ApplicationProvider.getApplicationContext(), CustomPrefixesActivity.class)));

      assertThat(launchedIntent.getBooleanExtra(DeepLink.IS_DEEP_LINK, false), equalTo(true));
      assertThat(launchedIntent.getStringExtra(DeepLink.URI), equalTo(uri));
      assertThat(launchedIntent.getExtras().getString("id"), equalTo("123"));
    }
  }

  @Test public void testWebDeepLinkIntentWithId() {
    List<String> cases =
        ImmutableList.of("http://airbnb.com/user/123", "https://airbnb.com/user/123");
    for (String uri : cases) {
      Intent launchedIntent = getLaunchedIntent(uri);
      assertThat(launchedIntent.getComponent(),
          equalTo(new ComponentName(ApplicationProvider.getApplicationContext(), CustomPrefixesActivity.class)));

      assertThat(launchedIntent.getBooleanExtra(DeepLink.IS_DEEP_LINK, false), equalTo(true));
      assertThat(launchedIntent.getStringExtra(DeepLink.URI), equalTo(uri));
      assertThat(launchedIntent.getExtras().getString("id"), equalTo("123"));
    }
  }

  @Test public void testLibraryDeepLinkIntent() {
    String uri = "library://dld/library_deeplink";
    Intent launchedIntent = getLaunchedIntent(uri);
    assertThat(launchedIntent.getComponent(),
        equalTo(new ComponentName(ApplicationProvider.getApplicationContext(), CustomPrefixesActivity.class)));

    assertThat(launchedIntent.getBooleanExtra(DeepLink.IS_DEEP_LINK, false), equalTo(true));
    assertThat(launchedIntent.getStringExtra(DeepLink.URI), equalTo(uri));
  }

  @Test public void testLibraryDeepLinkIntentWithId() {
    String uri = "library://dld/library_deeplink/456";
    Intent launchedIntent = getLaunchedIntent(uri);
    assertThat(launchedIntent.getComponent(),
        equalTo(new ComponentName(ApplicationProvider.getApplicationContext(), CustomPrefixesActivity.class)));

    assertThat(launchedIntent.getBooleanExtra(DeepLink.IS_DEEP_LINK, false), equalTo(true));
    assertThat(launchedIntent.getStringExtra(DeepLink.URI), equalTo(uri));
    assertThat(launchedIntent.getExtras().getString("lib_id"), equalTo("456"));
  }

  private Intent getLaunchedIntent(String uri) {
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    DeepLinkActivity deepLinkActivity = Robolectric.buildActivity(DeepLinkActivity.class, intent)
        .create().get();
    ShadowActivity shadowActivity = shadowOf(deepLinkActivity);

    return shadowActivity.peekNextStartedActivityForResult().intent;
  }
}
