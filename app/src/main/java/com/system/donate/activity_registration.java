package com.system.donate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.system.donate.databinding.ActivityRegistrationBinding;
import com.system.donate.model.User;

import java.util.HashMap;

public class activity_registration extends AppCompatActivity {

    Button registration;
    EditText inputFullName, inputEmail, inputPassword, inputConformPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        registration = (Button) findViewById(R.id.btnRegister);
        inputFullName = (EditText) findViewById(R.id.inputUsername);
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        inputConformPassword = (EditText) findViewById(R.id.inputConformPassword);
        loadingBar = new ProgressDialog(this);

        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {

        {
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            String username = inputFullName.getText().toString();
            String retypepassword = inputConformPassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                //snake bar
                Toast.makeText(this, "Please Enter Your E-mail", Toast.LENGTH_SHORT).show();

            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(username)) {
                Toast.makeText(this, "Please Enter Your Confirm Password", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(retypepassword)) {
                Toast.makeText(this, "Please Enter Your Confirm Password", Toast.LENGTH_SHORT).show();
            } else {
                loadingBar.setTitle("Create Account");
                loadingBar.setMessage("Please Wait, while we are checking the credentials.");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                ValidateEmail(email, password, username,retypepassword);
            }
        }

    }

    private void ValidateEmail(String email, String password, String username, String retypepassword)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference ref = database.getReference("server/saving-data/fireblog");

        User newUser = new User();
        newUser.email = email;
        newUser.fullname = username;
        newUser.password = password;
        newUser.retypepassword = retypepassword;

        String uDiD = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID) + System.currentTimeMillis();
        RootRef.child("Users").child(uDiD).setValue(newUser);
//        RootRef.child(uDiD).setValue(newUser);


        RootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child(email).exists())){
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("email", email);
                    userdataMap.put("password", password);
                    userdataMap.put("repassword",retypepassword);

                    userdataMap.put("username", username);

                    RootRef.child("Users").child(uDiD).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(activity_registration.this, "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(activity_registration.this, activity_login.class);
                                        startActivity(intent);
                                    }
//                                    else
                                        {
                                        loadingBar.dismiss();
                                        Toast.makeText(activity_registration.this, "Network Error: Please try again later...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(activity_registration.this, "This " + email + " already exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(activity_registration.this, "Please try again using another email account.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(activity_registration.this, activity_login.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}