/*
 * Title: SignIn.java
 * Description: First activity in the app flow which prompts the user to sign in or create an account.
 * Author: Carlos Estrada
 * Date: November – December 2018
 */

package com.caestrada.omega;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignIn extends AppCompatActivity {
    private Button signIn, createAccount;
    private EditText editUsername, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Link everything
        signIn = findViewById(R.id.welcome_signin);
        createAccount = findViewById(R.id.welcome_signup);
        editUsername = findViewById(R.id.welcome_username);
        editPassword = findViewById(R.id.welcome_password);

        // Make the buttons do stuff
        signIn.setOnClickListener(buttonListener);
        createAccount.setOnClickListener(buttonListener);
        Airline.initialize(this);
    }

    /* Makes Sign In / Create Account buttons work */
    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.welcome_signin) {
                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();

                if (Airline.signIn(username, password)) {
                    Toast.makeText(getApplicationContext(), "Welcome back, " + username + "!", Toast.LENGTH_SHORT).show();

                    Intent intentMain = new Intent(getApplicationContext(), OmegaActivity.class);
                    getApplication().startActivity(intentMain);
                    finish();
                }
                else {
                    if (username.isEmpty())
                        Toast.makeText(getApplicationContext(), R.string.failed_sign_in_username_empty,
                                Toast.LENGTH_SHORT).show();
                    else if (!Airline.isUser(username))
                        Toast.makeText(getApplicationContext(), R.string.failed_sign_in_invalid_username,
                                Toast.LENGTH_SHORT).show();
                    else if (!Airline.isCorrectPassword(username, password))
                        Toast.makeText(getApplicationContext(), R.string.failed_sign_in_wrong_password,
                                Toast.LENGTH_SHORT).show();
                    if (password.isEmpty())
                        Toast.makeText(getApplicationContext(), R.string.failed_sign_in_password_empty,
                                Toast.LENGTH_SHORT).show();
                }
            }
            else if (v.getId() == R.id.welcome_signup) {
                Intent intentMain = new Intent(SignIn.this, SignUp.class);
                SignIn.this.startActivity(intentMain);
            }
        }
    };

    // Don't exit the app on Back Pressed
    @Override
    public void onBackPressed() {}
}
