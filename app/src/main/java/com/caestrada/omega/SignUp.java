/*
 * Title: SignUp.java
 * Description: Allows user to create an account and signs them in upon success.
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

public class SignUp extends AppCompatActivity {
    private EditText editUsername, editPassword, editConfirmPassword;
    private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editUsername = findViewById(R.id.create_username);
        editPassword = findViewById(R.id.create_password);
        editConfirmPassword = findViewById(R.id.create_confirm_password);
        signUp = findViewById(R.id.create_signup);

        signUp.setOnClickListener(buttonListener);
        Airline.initialize(this);
    }

    // Validates all entered information on Sign Up click
    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == signUp.getId()) {
                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();
                String confirm = editConfirmPassword.getText().toString();

                if (Airline.signUp(username, password, confirm)) {
                    Toast.makeText(SignUp.this, String.format("Account created!\nWelcome, %s!",
                            username), Toast.LENGTH_LONG).show();

                    Intent intentMain = new Intent(SignUp.this, OmegaActivity.class);
                    SignUp.this.startActivity(intentMain);
                    finish();
                }
                else {
                    // A bunch of error handling and toast
                    if (username.isEmpty())
                        Toast.makeText(getApplicationContext(), R.string.failed_sign_up_username_empty,
                                Toast.LENGTH_SHORT).show();
                    else if (Airline.isUser(username))
                        Toast.makeText(getApplicationContext(), R.string.failed_sign_up_username_taken,
                                Toast.LENGTH_SHORT).show();
                    else if (!Airline.isValidUserOrPassword(username))
                        Toast.makeText(getApplicationContext(), R.string.failed_sign_up_username_invalid,
                                Toast.LENGTH_SHORT).show();
                    if (!Airline.isValidUserOrPassword(password))
                        Toast.makeText(getApplicationContext(), R.string.failed_sign_up_password_invalid,
                                Toast.LENGTH_SHORT).show();
                    if (!password.equals(confirm))
                        Toast.makeText(getApplicationContext(), R.string.failed_sign_up_password_mismatch,
                                Toast.LENGTH_LONG).show();
                }
            }
        }
    };
}
