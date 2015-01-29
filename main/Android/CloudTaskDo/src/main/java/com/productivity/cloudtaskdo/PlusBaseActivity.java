package com.productivity.cloudtaskdo;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;

/**
 * A base class to wrap communication with the Google Play Services PlusClient.
 */
public abstract class PlusBaseActivity extends Activity {

    private static final String TAG = PlusBaseActivity.class.getSimpleName();

    // A magic number we will use to know that our sign-in error resolution activity has completed
    private static final int OUR_REQUEST_CODE = 49404;

    // A flag to track when a connection is already in progress
    public boolean mPlusClientIsConnecting = false;

    // This is the helper object that connects to Google Play Services.
    private GoogleApiClient mGoogleApiClient;

    // The saved result from {@link #onConnectionFailed(ConnectionResult)}.  If a connection
    // attempt has been made, this is non-null.
    // If this IS null, then the connect method is still running.
    private ConnectionResult mConnectionResult;

    /**
     * Called when the {@link PlusClient} revokes access to this app.
     */
    protected abstract void onPlusClientRevokeAccess();

    /**
     * Called when the PlusClient is successfully connected.
     */
    protected abstract void onPlusClientSignIn();

    /**
     * Called when the {@link PlusClient} is disconnected.
     */
    protected abstract void onPlusClientSignOut();

    /**
     * Called when the {@link PlusClient} is blocking the UI.  If you have a progress bar widget,
     * this tells you when to show or hide it.
     */
    protected abstract void onPlusClientBlockingUI(boolean show);

    /**
     * Called when there is a change in connection state.  If you have "Sign in"/ "Connect",
     * "Sign out"/ "Disconnect", or "Revoke access" buttons, this lets you know when their states
     * need to be updated.
     */
    protected abstract void updateConnectButtonState();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the PlusClient connection.
        // Scopes indicate the information about the user your application will be able to access.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiConnectionCallBack())
                .addOnConnectionFailedListener(new GoogleApiConnectionFailedListener())
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    /**
     * Try to sign in the user.
     */
    public void signIn() {
        if (!mGoogleApiClient.isConnected()) {
            // Show the dialog as we are now signing in.
            setProgressBarVisible(true);
            // We should always have a connection result ready to resolve,
            // so we can start that process.
            if (mConnectionResult != null) {
                startResolution();
            } else {
                // If we don't have one though, we can start connect in
                // order to retrieve one.
                initiatePlusClientConnect();
            }
        }

        updateConnectButtonState();
    }

    /**
     * Connect the {@link PlusClient} only if a connection isn't already in progress.  This will
     * call back to {@link # onConnected(android.os.Bundle)} or
     * {@link # onConnectionFailed(com.google.android.gms.common.ConnectionResult)}.
     */
    private void initiatePlusClientConnect() {
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Disconnect the {@link PlusClient} only if it is connected (otherwise, it can throw an error.)
     * This will call back to {@link # onDisconnected()}.
     */
    private void initiatePlusClientDisconnect() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Sign out the user (so they can switch to another account).
     */
    public void signOut() {
        // We only want to sign out if we're connected.
        if (mGoogleApiClient.isConnected()) {
            // Clear the default account in order to allow the user to potentially choose a
            // different account from the account chooser.
            mGoogleApiClient.clearDefaultAccountAndReconnect();
            // Disconnect from Google Play Services, then reconnect in order to restart the
            // process from scratch.
            initiatePlusClientDisconnect();
            Log.v(TAG, "Sign out successful!");
        }

        updateConnectButtonState();
    }

    /**
     * Revoke Google+ authorization completely.
     */
    public void revokeAccess() {

        if (mGoogleApiClient.isConnected()) {
            // Clear the default account as in the Sign Out.
            mGoogleApiClient.clearDefaultAccountAndReconnect();

            // Revoke access to this entire application. This will call back to
            // onAccessRevoked when it is complete, as it needs to reach the Google
            // authentication servers to revoke all tokens.
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        initiatePlusClientConnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        initiatePlusClientDisconnect();
    }

    public boolean isPlusClientConnecting() {
        return mPlusClientIsConnecting;
    }

    private void setProgressBarVisible(boolean flag) {
        mPlusClientIsConnecting = flag;
        onPlusClientBlockingUI(flag);
    }

    /**
     * A helper method to flip the mResolveOnFail flag and start the resolution
     * of the ConnectionResult from the failed connect() call.
     */
    private void startResolution() {
        try {
            // If we can resolve the error, then call start resolution and pass it an integer tag
            // we can use to track.
            // This means that when we get the onActivityResult callback we'll know it's from
            // being started here.
            mConnectionResult.startResolutionForResult(this, OUR_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            // Any problems, just try to connect() again so we get a new ConnectionResult.
            mConnectionResult = null;
            initiatePlusClientConnect();
        }
    }

    /**
     * An earlier connection failed, and we're now receiving the result of the resolution attempt
     * by GoogleApiClient.
     *
     * @see # onConnectionFailed(ConnectionResult)
     */
    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        //updateConnectButtonState();
        if (requestCode == OUR_REQUEST_CODE && responseCode == RESULT_OK) {
            // If we have a successful result, let's call connect() again. If there are any more
            // errors to resolve we'll get our onConnectionFailed, but if not,
            // we'll get onConnected.
            initiatePlusClientConnect();
        } else if (requestCode == OUR_REQUEST_CODE && responseCode != RESULT_OK) {
            // If we've got an error we can't resolve, we're no longer in the midst of signing
            // in, so we can stop the progress spinner.
            setProgressBarVisible(false);
        }
    }

    public GoogleApiClient getPlusClient() {
        return mGoogleApiClient;
    }

    public class GoogleApiConnectionCallBack implements ConnectionCallbacks {

        /**
         * Successfully connected (called by GoogleApiClient)
         */
        @Override
        public void onConnected(Bundle bundle) {
            //updateConnectButtonState();
            setProgressBarVisible(false);
            onPlusClientSignIn();
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

    }

    private class GoogleApiConnectionFailedListener implements OnConnectionFailedListener {

        /**
         * Connection failed for some reason (called by GoogleApiClient)
         * Try and resolve the result.  Failure here is usually not an indication of a serious error,
         * just that the user's input is needed.
         *
         * @see #onActivityResult(int, int, Intent)
         */
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            //updateConnectButtonState();
            // Most of the time, the connection will fail with a user resolvable result. We can store
            // that in our mConnectionResult property ready to be used when the user clicks the
            // sign-in button.
            if (!mPlusClientIsConnecting && connectionResult.hasResolution()) {
                try {
                    mPlusClientIsConnecting = true;
                    startIntentSenderForResult(connectionResult.getResolution().getIntentSender(), OUR_REQUEST_CODE,
                            null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mPlusClientIsConnecting = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

}
