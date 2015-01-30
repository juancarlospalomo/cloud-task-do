package com.productivity.cloudtaskdo.tests;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.productivity.cloudtaskdo.R;
import com.productivity.cloudtaskdo.SignInActivity;

/**
 * Created by JuanCarlos on 29/01/2015.
 */
public class testSignInActivity extends ActivityInstrumentationTestCase2<SignInActivity> {

    private Activity mActivity;
    private SignInButton mSignInButton;

    public testSignInActivity() {
        super(SignInActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(false);

        mActivity = getActivity();
        mSignInButton = (SignInButton) mActivity.findViewById(R.id.plus_sign_in_button);
    }


    public void testGooglePlayServices() throws Exception {
        boolean googlePlayServicesAvailable = (GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity) == ConnectionResult.SUCCESS);
        assertEquals(googlePlayServicesAvailable, mSignInButton.getVisibility() == View.VISIBLE);
    }
}
