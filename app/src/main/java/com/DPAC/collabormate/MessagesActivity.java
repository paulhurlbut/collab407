package com.DPAC.collabormate;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.DPAC.collabormate.main.Consts;
import com.DPAC.collabormate.main.SharedPreference;
import com.DPAC.collabormate.main.helper.PlayServicesHelper;
import com.DPAC.collabormate.main.utils.DialogUtils;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBMessages;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;

import java.util.List;

//import com.quickblox.simplesample.messages.R;

public class MessagesActivity extends Activity {

    private static final String TAG = MessagesActivity.class.getSimpleName();

    private EditText messageOutEditText;
    private EditText messageInEditText;
    private ProgressBar progressBar;
    private Button sendMessageButton;
    private Button deleteMessageButton;

    private SharedPreference sharedPreference;
    Activity context = this;

    public static String savedMessage = "";   // store sent messages

    private PlayServicesHelper playServicesHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_layout);

        // create sharedPreference
        sharedPreference = new SharedPreference();
        // Retrieve a value from SharedPreference
        savedMessage = sharedPreference.getValue(context);

        playServicesHelper = new PlayServicesHelper(this);

        initUI();

        messageInEditText.setText(savedMessage);

        // Register to receive push notifications events
        LocalBroadcastManager.getInstance(this).registerReceiver(mPushReceiver,
                new IntentFilter(Consts.NEW_PUSH_EVENT));
    }

    @Override
    protected void onDestroy() {

        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPushReceiver);

        // Save the text in SharedPreference
        sharedPreference.save(context, savedMessage);
        Toast.makeText(context,
                getResources().getString(R.string.saved),
                Toast.LENGTH_LONG).show();

        super.onDestroy();
    }

    private void initUI() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        messageOutEditText = (EditText) findViewById(R.id.message_out_edittext);
        messageInEditText = (EditText) findViewById(R.id.messages_in_edittext);
        sendMessageButton = (Button) findViewById(R.id.send_message_button);
        deleteMessageButton = (Button) findViewById(R.id.delete_message_button);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMessage();
                //sendMessageOnClick(view);

                // empty input after displaying message
                messageOutEditText.getText().clear();
                savedMessage = messageInEditText.getText().toString();
            }
        });

        deleteMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Delete saved messages
                savedMessage = "";
                // Clear board
                messageInEditText.getText().clear();

            }
        });
    }

    private void addMessageToList() {
        //String message = getIntent().getStringExtra(Consts.EXTRA_MESSAGE);
        String message = messageOutEditText.getText().toString();
        //savedMessage +
        if (message != null) {
            retrieveMessage(message);
        }
    }

    public void retrieveMessage(final String message) {
        String text = savedMessage + message + "\n\n";
                //+ messageInEditText.getText().toString();
        messageInEditText.setText(text);
        progressBar.setVisibility(View.INVISIBLE);
    }

    // adding new method to display messages
    public void displayMessage() {
        // addMessageToList();
        String message = savedMessage + messageOutEditText.getText().toString()
                + "\n\n";
        messageInEditText.setText(message);
        progressBar.setVisibility(View.INVISIBLE);

        // hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageOutEditText.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        playServicesHelper.checkPlayServices();
    }


    public void sendMessageOnClick(View view) {
        // Send Push: create QuickBlox Push Notification Event
        QBEvent qbEvent = new QBEvent();
        qbEvent.setNotificationType(QBNotificationType.PUSH);
        qbEvent.setEnvironment(QBEnvironment.DEVELOPMENT);

        // generic push - will be delivered to all platforms (Android, iOS, WP, Blackberry..)
        qbEvent.setMessage(messageOutEditText.getText().toString());

        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        userIds.add(1243440);
        qbEvent.setUserIds(userIds);


        QBMessages.createEvent(qbEvent, new QBEntityCallbackImpl<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle bundle) {
                progressBar.setVisibility(View.INVISIBLE);

                // hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(messageOutEditText.getWindowToken(), 0);
            }

            @Override
            public void onError(List<String> strings) {
                // errors
                DialogUtils.showLong(MessagesActivity.this, strings.toString());

                progressBar.setVisibility(View.INVISIBLE);

                // hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(messageOutEditText.getWindowToken(), 0);
            }
        });

        progressBar.setVisibility(View.VISIBLE);
    }

    // Our handler for received Intents.
    //
    private BroadcastReceiver mPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Get extra data included in the Intent
            String message = intent.getStringExtra(Consts.EXTRA_MESSAGE);

            Log.i(TAG, "Receiving event " + Consts.NEW_PUSH_EVENT + " with data: " + message);

            retrieveMessage(message);
        }
    };
}