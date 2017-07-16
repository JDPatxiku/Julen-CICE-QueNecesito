package com.julendieguez.quenecesito;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends    AppCompatActivity {
    private FirebaseAuth authentication;
    private EditText txtEmail, txtPassword;
    private Button btnLogIn;
    private ProgressDialog pDialog;
    private static boolean persistenceCalledAlready = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!persistenceCalledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            persistenceCalledAlready = true;
        }
        setContentView(R.layout.activity_main);
        authentication = FirebaseAuth.getInstance();
        if(checkIfLoggedIn(authentication.getCurrentUser())){
            startApp();
        }
        setReferences();
    }

    private boolean checkIfLoggedIn(FirebaseUser user){
        return user != null;
    }

    private void startApp(){
        changeActivityToMainMenu();
    }

    private void setReferences(){
        txtEmail = (EditText)findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {@Override public void onFocusChange(View v, boolean hasFocus) { hideKeyboard(txtEmail, hasFocus); } });
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {@Override public void onFocusChange(View v, boolean hasFocus) { hideKeyboard(txtPassword, hasFocus); } });
        btnLogIn = (Button) findViewById(R.id.btnLogIn);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareToLogIn();
            }
        });
        pDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_Dialog);
        pDialog.setIndeterminate(true);
        pDialog.setMessage(getString(R.string.authenticating));
    }

    private void prepareToLogIn(){
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        if(anyEmptyField(email,password)) {
            Toast.makeText(getApplicationContext(), getString(R.string.logInFieldMissingError), Toast.LENGTH_SHORT).show();
            return;
        }
        LogIn(email,password);
    }

    private void LogIn(String email, String password){
        pDialog.show();
        authentication.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                 if(!task.isSuccessful()){
                     Toast.makeText(getApplicationContext(), getString(R.string.logInError), Toast.LENGTH_SHORT).show();
                 }else{
                     pDialog.dismiss();
                     startApp();
                 }
                 pDialog.dismiss();
            }
        });
    }
    private boolean anyEmptyField(String email, String password){
        return email.equals("") || password.equals("");
    }

    private void hideKeyboard(EditText e, boolean b) {
        InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(getApplication().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(e.getWindowToken(), 0);
    }

    public void changeActivityToSignUp(View v){
        Intent signInActivity = new Intent(this, SignUpActivity.class);
        startActivity(signInActivity);
    }

    public void changeActivityToMainMenu(){
        Intent mainMenuActivity = new Intent(this, MainMenuActivity.class);
        startActivity(mainMenuActivity);
        finish();
    }


}
