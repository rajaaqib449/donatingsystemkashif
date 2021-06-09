package com.system.donate;
import  com.system.donate.model.User;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.system.donate.databinding.ActivityLoginBinding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class activity_login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView register;
    private Button loginButton;
    private EditText inputemail;
    private EditText inputpassword;
    private ProgressDialog loadingBar;
    private String parentDbname = "Users";
    private CheckBox chkBoxRememberMe;
    private String fEmail,
            fPsd,
            efName,
            pfName;

    private AppBarConfiguration appBarConfiguration;
    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean isLogRemembered= sharedPreferences.getBoolean("isLoginRemember", false);
        if(isLogRemembered==true){
            Intent intent = new Intent(activity_login.this, MainActivity.class);
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register = findViewById(R.id.textViewSignUp);
        efName = getFilesDir().toString() + "fEmail";
        pfName = getFilesDir().toString() + "fPsd";
        mAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_login.this, activity_registration.class);
                startActivity(intent);

            }

        });
        loginButton=(Button) findViewById(R.id.btnlogin);
        inputemail=(EditText) findViewById(R.id.inputEmail);

        inputpassword=(EditText) findViewById(R.id.inputPassword);
        loadingBar=new ProgressDialog(this);

        chkBoxRememberMe = (CheckBox) findViewById(R.id.Rememberme);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkBoxRememberMe.isChecked()) {
                    editor.putBoolean("isLoginRemember", true);
                }
                else {
                    editor.putBoolean("isLoginRemember", false);
                }
                editor.putString("Email",inputemail.getText().toString());
                editor.apply();
                loginUser();
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            fEmail = readFromFile(efName);
            fPsd = readFromFile(pfName);

            if (!(fEmail.isEmpty()
                    && fPsd.isEmpty())
            ) {
                signIn(fEmail, fPsd);
            }
        }

    }

    private void signIn(String fE, String fP) {
        mAuth.signInWithEmailAndPassword(fE,fP)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(),
                                    MainActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Authentication error...", Toast.LENGTH_LONG).show();

                        }

                    }
                });
    }
    //user show toast msg and cheak credentials to login
    private void loginUser()
    {


        String email = inputemail.getText().toString();
        String password = inputpassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please Enter Your E-mail", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingBar.setTitle("Login Account");
        loadingBar.setMessage("Please Wait, while we are checking the credentials.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        AllowAcessToAccount(email,password);

    }

    public static void writeToFile(String fileName, String lines) {

        try (FileWriter fw = new FileWriter(new File(fileName));
             BufferedWriter writer = new BufferedWriter(fw)) {

            writer.write(lines);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String readFromFile(String fileName) {
        Path path = Paths.get(fileName);
        String str = "";
        try (
                FileReader fr = new FileReader(fileName);
                BufferedReader reader = new BufferedReader(fr)
        )
        {
            str = reader.readLine();
            return str;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
    //allowr access to account method
    private void AllowAcessToAccount(String email, String password)
    {
        if (chkBoxRememberMe.isChecked()){
//            Paper.book().write(Prevalent.Useremailkey, email);
//            Paper.book().write(Prevalent.Userpasswordkey, password);
            writeToFile(efName,email);
            writeToFile(pfName,password);
        }
        //database

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                boolean isUserFound = false;
//             if (dataSnapshot.child(parentDbname).child(email).exists())
//             {
                Iterable<DataSnapshot> usersList = dataSnapshot.child(parentDbname).getChildren();

                for (DataSnapshot currentUser : usersList) {
                    User user = currentUser.getValue(User.class);
                    Log.e("user:: ", user.getFullname() + " " + user.getEmail());

                    if(user.getEmail().equals(email))
                    {
                        if(user.getPassword().equals(password))
                        {
                            isUserFound = true;
                        }
                    }
                }

                if (isUserFound)
                {
                    Toast.makeText(activity_login.this, "loggin successfully", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Intent intent=new Intent(activity_login.this,MainActivity.class);
                    startActivity(intent);
                }


//             }

                Toast.makeText(activity_login.this, "Account with this"+email+"email do not exit", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
//                 Toast.makeText(LoginActivity.this, "Your need to creat a Account", Toast.LENGTH_SHORT).show();
//                 loadingBar.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_activity_login);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}