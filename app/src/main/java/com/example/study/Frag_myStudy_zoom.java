package com.example.study;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;


public class Frag_myStudy_zoom extends Fragment {
    private View view;

    private ZoomSDKAuthenticationListener authListener = new ZoomSDKAuthenticationListener() {
        /**
         * This callback is invoked when a result from the SDK's request to the auth server is
         * received.
         */
        @Override
        public void onZoomSDKLoginResult(long result) {
            if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                // Once we verify that the request was successful, we may start the meeting
                startMeeting(getActivity());
            }
        }

        @Override
        public void onZoomSDKLogoutResult(long l) {}@Override
        public void onZoomIdentityExpired() {}@Override
        public void onZoomAuthIdentityExpired() {}
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.frag8_mystudy_zoom,container,false);

        initializeSdk(getContext());
        initViews();

        return view;
    }

    public void initializeSdk(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        // TODO: For the purpose of this demo app, we are storing the credentials in the client app itself. However, you should not use hard-coded values for your key/secret in your app in production.
        ZoomSDKInitParams params = new ZoomSDKInitParams();
        params.appKey = "O__MPFRNQyaLsCoWCCYfSw"; // TODO: Retrieve your SDK key and enter it here
        params.appSecret = "CQ6zRChx2zGonfMiNAwOZkq0eZTYyfazxf2O"; // TODO: Retrieve your SDK secret and enter it here
        params.domain = "zoom.us";
        params.enableLog = true;
        // TODO: Add functionality to this listener (e.g. logs for debugging)
        ZoomSDKInitializeListener listener = new ZoomSDKInitializeListener() {
            /**
             * @param errorCode {@link us.zoom.sdk.ZoomError#ZOOM_ERROR_SUCCESS} if the SDK has been initialized successfully.
             */
            @Override
            public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
            }

            @Override
            public void onZoomAuthIdentityExpired() { }
        };
        sdk.initialize(context, listener, params);
    }
    //1. Write the joinMeeting function.
    private void joinMeeting(Context context, String meetingNumber, String password) {
        MeetingService meetingService = ZoomSDK.getInstance().getMeetingService();
        JoinMeetingOptions options = new JoinMeetingOptions();
        JoinMeetingParams params = new JoinMeetingParams();
        params.displayName = "mobile"; // TODO: Enter your name
        params.meetingNo = meetingNumber;
        params.password = password;
        meetingService.joinMeetingWithParams(context, params, options);
    }

    // 1. Write the login function

    private void login(String username, String password) {
        int result = ZoomSDK.getInstance().loginWithZoom(username, password);
        if (result == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {

            // 2. After request is executed, listen for the authentication result prior to starting a meeting
            ZoomSDK.getInstance().addAuthenticationListener(authListener);
        }
        else {
            Toast.makeText(getActivity(), "아이디 또는 비밀번호 오류", Toast.LENGTH_SHORT).show();
        }
    }
    // 3. Write the startMeeting function
    private void startMeeting(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        if (sdk.isLoggedIn()) {
            MeetingService meetingService = sdk.getMeetingService();
            StartMeetingOptions options = new StartMeetingOptions();
            meetingService.startInstantMeeting(context, options);
        }
    }
    // 1. Create a dialog where a participant can enter the meeting information to join a meeting.
    private void createJoinMeetingDialog() {
        new AlertDialog.Builder(getContext()).setView(R.layout.dialog_zoom).setPositiveButton("Join", new DialogInterface.OnClickListener() {@Override
        public void onClick(DialogInterface dialogInterface, int i) {
            AlertDialog dialog = (AlertDialog) dialogInterface;
            TextInputEditText numberInput = dialog.findViewById(R.id.meeting_no_input);
            TextInputEditText passwordInput = dialog.findViewById(R.id.password_input);
            if (numberInput != null && numberInput.getText() != null && passwordInput != null && passwordInput.getText() != null) {
                String meetingNumber = numberInput.getText().toString();
                String password = passwordInput.getText().toString();
                if (meetingNumber.trim().length() > 0 && password.trim().length() > 0) {
                    joinMeeting(getContext(), meetingNumber, password);
                }
            }
        }
        }).show();
    }

    // 2. Create a dialog where a host can enter Zoom email and password to login and start an instant meeting.
    private void createLoginDialog() {
        new AlertDialog.Builder(getContext()).setView(R.layout.dialog_zoom2).setPositiveButton("Log in", new DialogInterface.OnClickListener() {@Override
        public void onClick(DialogInterface dialogInterface, int i) {
            AlertDialog dialog = (AlertDialog) dialogInterface;
            TextInputEditText emailInput = dialog.findViewById(R.id.email_input);
            TextInputEditText passwordInput = dialog.findViewById(R.id.pw_input);
            if (emailInput != null && emailInput.getText() != null && passwordInput != null && passwordInput.getText() != null) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    login(email, password);
                }
            }
        }
        }).show();
    }

    //1. Write initViews method to handle onClick events for the Join Meeting and Login & Start Meeting buttons.
    private void initViews() {
        view.findViewById(R.id.join_button).setOnClickListener(new View.OnClickListener() {@Override
        public void onClick(View view) {
            createJoinMeetingDialog();
        }
        });

        view.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {@Override
        public void onClick(View view) {
            // 2.  If a user is logged in, start the instant meeting, else prompt the user to login.
            if (ZoomSDK.getInstance().isLoggedIn()) {
                startMeeting(getActivity());
            } else {
                createLoginDialog();
            }
        }
        });
    }
}