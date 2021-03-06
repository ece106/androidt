package com.thinqtv.thinqtv_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.stripe.android.CustomerSession;
import com.thinqtv.thinqtv_android.data.*;
import com.thinqtv.thinqtv_android.stripe.StripeHostActivity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

import com.thinqtv.thinqtv_android.data.UserRepository;

public class profile_fragment extends Fragment {

    private GoogleSignInClient googleSignInClient;
    public profile_fragment(GoogleSignInClient googleSignInClient) {
        this.googleSignInClient = googleSignInClient;
    }

    public static profile_fragment newInstance(GoogleSignInClient googleSignInClient) {
        profile_fragment fragment = new profile_fragment(googleSignInClient);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState)
    {
        ImageView img = (ImageView) getView().findViewById(R.id.imageView);
        img.setImageResource(R.drawable.defaultprofil);

        // Connect the Edit Profile button
        final Button browseScheduleBtn = getView().findViewById(R.id.edit_profileBtn);
        browseScheduleBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ProfileSettingsActivity.class);
                startActivity(i);
            }
        });

        // Connect the Edit Account Settings button
        Button controlPanelBtn = view.findViewById(R.id.edit_accountBtn);
        controlPanelBtn.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), AccountSettingsActivity.class);
            startActivity(i);
        });

        // Connect the Schedule a Conversation button
        Button scheduleConversationButton = view.findViewById(R.id.scheduleConversation);
        scheduleConversationButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddEventActivity.class);
            startActivity(intent);
        });

        // Connect the sign out button
        Button signOutBtn = view.findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(v -> {
            logout();
        });

        Button stripeSettingsButton = view.findViewById(R.id.merchandise_button);
        stripeSettingsButton.setOnClickListener((v -> {
            Intent intent = new Intent(getActivity(), EditMerchandiseListActivity.class);
            startActivity(intent);
        }));

        // Update username text
        TextView usernameTV = view.findViewById(R.id.username);
        usernameTV.setText(UserRepository.getInstance().getLoggedInUser().getUserInfo().get("name"));

        // Update profile picture
        class UploadTask extends AsyncTask<String, String, String> {

            @Override
            protected String doInBackground(String... strings) {
                try {
                    ImageView userPic = view.findViewById(R.id.imageView);
                    URL picURL = new URL(UserRepository.getInstance().getLoggedInUser().getUserInfo().get("profilepic"));
                    Bitmap icon_val = BitmapFactory.decodeStream(picURL.openConnection().getInputStream());

                    Thread thread = new Thread()
                    {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    userPic.setImageBitmap(icon_val);
                                }
                            });
                        }
                    };
                    thread.start();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }
        UploadTask updatePic = new UploadTask();
        updatePic.execute();
    }

    public void logout() {
        UserRepository.getInstance().logout(getContext());
        LoginManager.getInstance().logOut();
        googleSignInClient.signOut()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(getString(R.string.google_sign_in_tag), "Signed out of Google account");
                    }
                });
        ((MainActivity)getActivity()).openFragment(welcome_fragment.newInstance(), getString(R.string.welcome_fragment));
    }


}
