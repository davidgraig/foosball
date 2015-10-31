package codes.davidrussell.android.foosball;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.textview_email)
    EditText mEmailEditText;
    @Bind(R.id.textview_password)
    EditText mPasswordEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.link_login)
    public void loginLinkClick(View v) {
        finish();
    }

    @OnCheckedChanged(R.id.show_password)
    protected void showPassword(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            mPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            mPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    @OnClick(R.id.link_login)
    protected void launchLoginActivity(View view) {
        finish();
    }

    @OnClick(R.id.button_register)
    public void register(View v) {
        final Button registerButton = (Button) v;

        if (!validate()) {
            onRegisterFailed(registerButton, "Invalid data entered");
            return;
        }

        registerButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registering...");
        progressDialog.show();

        ParseUser user = new ParseUser();
        user.setUsername(mEmailEditText.getText().toString());
        user.setPassword(mPasswordEditText.getText().toString());
        user.setEmail(mEmailEditText.getText().toString());
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    onRegisterSuccess(registerButton);
                } else {
                    onRegisterFailed(registerButton, e.getMessage());
                }
            }
        });
    }


    private void onRegisterSuccess(Button registerButton) {
        registerButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    private void onRegisterFailed(Button registerButton, String reason) {
        Snackbar.make(coordinatorLayout, "Login Failed: " + reason, Snackbar.LENGTH_LONG).show();
        registerButton.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;

        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailEditText.setError("enter a valid email address");
            valid = false;
        } else {
            mEmailEditText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            mPasswordEditText.setError("greater than 4 characters");
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }

        return valid;
    }
}
