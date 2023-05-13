package dev.knox.cerberus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import dev.knox.cerberus.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";
    private static final String INVALID_PASSWORD_ERROR = "Invalid password";
    private static final String AUTHENTICATION_FAILED_ERROR = "Authentication failed.";

    private EditText emailLoginEditText, passwordLoginEditText, emailRegisterEditText, passwordRegisterEditText, firstNameEditText, lastNameEditText, businessNameEditText, phoneNumberEditText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dev.knox.cerberus.databinding.ActivityAuthBinding binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // EditTexts
        emailLoginEditText = findViewById(R.id.emailLoginEditText);
        passwordLoginEditText = findViewById(R.id.passwordLoginEditText);
        emailRegisterEditText = findViewById(R.id.emailRegisterEditText);
        passwordRegisterEditText = findViewById(R.id.passwordRegisterEditText);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        businessNameEditText = findViewById(R.id.businessNameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);

        // Buttons
        Button forgotPassword = findViewById(R.id.forgotPasswordButton);
        Button login = findViewById(R.id.loginButton);
        Button register = findViewById(R.id.registerButton);

        ViewFlipper viewFlipper = findViewById(R.id.authViewFlipper);
        register.setOnClickListener(v -> viewFlipper.setDisplayedChild(1));

        forgotPassword.setEnabled(false); // Disable button by default

        // Enable button when the user starts typing in the email field
        emailLoginEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                forgotPassword.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        forgotPassword.setOnClickListener(v -> {
            String email = emailLoginEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                emailLoginEditText.setError(getString(R.string.error_empty_email));
                emailLoginEditText.requestFocus();
                return;
            }

            // Show loading indicator while sending the password reset email
            ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.loading_message), true);

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        // Dismiss the loading indicator
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            // Password reset email sent successfully
                            Toast.makeText(this, R.string.password_reset_email_sent_message, Toast.LENGTH_SHORT).show();
                        } else {
                            // Password reset email failed to send
                            Exception exception = task.getException();
                            Log.w(TAG, "sendPasswordResetEmail: failed", exception);
                            Toast.makeText(this, R.string.password_reset_email_failed_message, Toast.LENGTH_SHORT).show();
                        }
                    });
        });


        // Set TextChangedListener for password input
        passwordLoginEditText.addTextChangedListener(getPasswordWatcher());

        login.setOnClickListener(v -> {
            String email = emailLoginEditText.getText().toString().trim();
            String password = passwordLoginEditText.getText().toString();

            if (!isValidEmail(email)) {
                emailLoginEditText.setError("Please enter a valid email address");
                emailLoginEditText.requestFocus();
                return;
            }

            if (!isValidPassword(password)) {
                passwordLoginEditText.setError("Password must be at least 6 characters long");
                passwordLoginEditText.requestFocus();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // User has signed in successfully, navigate to the main activity
                            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            onSignInFailure(task.getException());
                        }
                    })
                    .addOnFailureListener(this, this::onSignInFailure);
        });


        Button next = findViewById(R.id.nextButton);
        next.setOnClickListener(v -> {
            String email = emailRegisterEditText.getText().toString().trim();
            String password = passwordRegisterEditText.getText().toString().trim();

            if (!validateEmailAndPassword(email, password)) {
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            String uid = user.getUid();
                            String userEmail = user.getEmail();

                            Calendar calendar = Calendar.getInstance();
                            String year = String.valueOf(calendar.get(Calendar.YEAR));
                            String dayOfYear = String.valueOf(calendar.get(Calendar.DAY_OF_YEAR));
                            String css = "CSS"; // or any other string
                            String lastFourDigits = uid.substring(uid.length() - 4);

                            String productKey = lastFourDigits + year + dayOfYear + css;

                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                            usersRef.child(uid).child("email").setValue(userEmail)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Log.d(TAG, "User email saved successfully");
                                        } else {
                                            Log.w(TAG, "Failed to save user email", task1.getException());
                                        }
                                    })
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Log.d(TAG, "User email saved successfully");
                                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                            // Switch to the new view after the user is created
                                            viewFlipper.setDisplayedChild(2);
                                        } else {
                                            Log.w(TAG, "Failed to save user email", task2.getException());
                                        }
                                    });
                            usersRef.child(uid).child("product_key").setValue(productKey)
                                    .addOnCompleteListener(task3 -> {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Product key saved successfully");
                                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();

                                            EditText productKeyEditTextView = findViewById(R.id.productKeyEditText);
                                            productKeyEditTextView.setText(productKey);

                                            // Switch to the new view after the user is created
                                            viewFlipper.setDisplayedChild(2);
                                        } else {
                                            Log.w(TAG, "Failed to save product key", task.getException());
                                        }
                                    });
                        } else {
                            // If sign up fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });

        });

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view -> saveData());
    }

    private TextWatcher getPasswordWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString().trim();
                passwordLoginEditText.setError(password.length() < 6 ? "Password must be at least 6 characters long" : null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // do nothing
            }
        };
    }

    private boolean validateEmailAndPassword(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailRegisterEditText.setError("Please enter your email");
            emailRegisterEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordRegisterEditText.setError("Please enter your password");
            passwordRegisterEditText.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordRegisterEditText.setError("Password must be at least 6 characters long");
            passwordRegisterEditText.requestFocus();
            return false;
        }

        return true;
    }


    private void saveData() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String businessName = businessNameEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();

        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String uid = user.getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("first_name", firstName);
        data.put("last_name", lastName);
        data.put("business_name", businessName);
        data.put("phone_number", phoneNumber);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(uid).setValue(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Information saved successfully");
                    Toast.makeText(this, "Information saved successfully", Toast.LENGTH_SHORT).show();
                    // Data saved successfully, open MainActivity
                    openMainActivity();
                })
                .addOnFailureListener(e -> {
                    // Data failed to save, show error message
                    Toast.makeText(AuthActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                });
    }

    private void onSignInFailure(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidUserException) {
            // User does not exist
            emailLoginEditText.setError(AUTHENTICATION_FAILED_ERROR);
            emailLoginEditText.requestFocus();
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            // Invalid password
            passwordLoginEditText.setError(INVALID_PASSWORD_ERROR);
            passwordLoginEditText.requestFocus();
        } else {
            // Authentication failed for some other reason
            Log.e(TAG, "signInWithEmailAndPassword: failed", exception);
            Toast.makeText(this, AUTHENTICATION_FAILED_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    @Override
    public void onBackPressed() {
        ViewFlipper viewFlipper = findViewById(R.id.authViewFlipper);
        int currentIndex = viewFlipper.getDisplayedChild();
        int newIndex = currentIndex > 0 ? currentIndex - 1 : currentIndex;
        viewFlipper.setDisplayedChild(newIndex);
        if (newIndex == currentIndex) {
            super.onBackPressed();
        }
    }
}

