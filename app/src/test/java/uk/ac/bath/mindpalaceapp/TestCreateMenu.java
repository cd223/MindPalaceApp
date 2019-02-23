package uk.ac.bath.mindpalaceapp;

import android.arch.lifecycle.ReportFragment;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ReportFragment.class })
public class TestCreateMenu {
    @Test
    public void createMenu_onCreate() {
        mockStatic(ReportFragment.class);
        ActionBar mockActionBar = mock(ActionBar.class);
        Intent mockIntent = mock(Intent.class);
        CreateMenu activity = spy(new CreateMenu());
        String testString = "TEST";

        doReturn(mockActionBar).when(activity).getSupportActionBar();
        doNothing().when(mockActionBar).setDisplayShowHomeEnabled(true);
        doNothing().when(mockActionBar).setLogo(R.drawable.logo);
        doNothing().when(mockActionBar).setDisplayUseLogoEnabled(true);
        doNothing().when(activity).setContentView(R.layout.activity_create_menu);
        doReturn(mock(AppCompatDelegate.class)).when(activity).getDelegate();
        doReturn(mockIntent).when(activity).getIntent();
        doReturn(testString).when(mockIntent).getStringExtra(anyString());

        activity.onCreate(null);
        verify(activity, times(1)).setContentView(R.layout.activity_create_menu);
        assertEquals(activity.username, testString);
        assertEquals(activity.name, testString);
    }


}