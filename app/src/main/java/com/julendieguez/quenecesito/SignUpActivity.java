package com.julendieguez.quenecesito;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.julendieguez.quenecesito.database.DatabaseFunctions;
import com.julendieguez.quenecesito.database.PhoneNumber;
import com.julendieguez.quenecesito.database.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseDatabase db;
    private EditText txtName, txtEmail, txtPassword, txtPasswordConfirmation, txtNumber;
    private Button btnSignUp;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setReferences();
        tryToGetPhoneNumber();
    }

    @SuppressLint("NewApi")
    public void tryToGetPhoneNumber(){
        TelephonyManager tMgr = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        try {
            String number = PhoneNumberUtils.formatNumber(tMgr.getLine1Number(),tMgr.getSimCountryIso());
            String toRemove = number.substring(0,number.indexOf(" "));
            number = number.replace(toRemove,"");
            number = number.replace(" ","");
            txtNumber.setText(number);
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),getString(R.string.signUpNumberNotAchieved),Toast.LENGTH_SHORT).show();
        }
    }

    private void setReferences(){
        txtName = (EditText) findViewById(R.id.txtName);
        txtNumber = (EditText) findViewById(R.id.txtNumber);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtPasswordConfirmation = (EditText) findViewById(R.id.txtPasswordConfirm);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareToSignUp();
            }
        });
        pDialog = new ProgressDialog(SignUpActivity.this, R.style.AppTheme_Dialog);
        pDialog.setIndeterminate(true);
        pDialog.setMessage(getString(R.string.signUpProcess));
    }

    private void prepareToSignUp(){
        if(checkIfNameIsEmpty()) {
            if (validateEmail()) {
                if (!checkIfPasswordsAreEmpty()) {
                    if (validatePasswordMatching()) {
                        if (validatePasswordLength()) {
                            pDialog.show();
                            if(!checkIfNumberExists()) {
                                signUp();
                            }else{
                                Toast.makeText(getApplicationContext(), getString(R.string.signUpNumberExistsError), Toast.LENGTH_SHORT).show();
                                pDialog.hide();
                            }
                        } else { Toast.makeText(getApplicationContext(), getString(R.string.signUpPasswordLengthError), Toast.LENGTH_SHORT).show();}
                    } else { Toast.makeText(getApplicationContext(), getString(R.string.signUpPasswordMatchError), Toast.LENGTH_SHORT).show();}
                } else { Toast.makeText(getApplicationContext(), getString(R.string.signUpPasswordEmptyError), Toast.LENGTH_SHORT).show();}
            } else { Toast.makeText(getApplicationContext(), getString(R.string.signUpEmailError), Toast.LENGTH_SHORT).show();}
        }else{ Toast.makeText(getApplicationContext(), getString(R.string.signUpNameError), Toast.LENGTH_SHORT).show();}
    }

    private void signUp(){
        FirebaseAuth  authentication = FirebaseAuth.getInstance();
        authentication.createUserWithEmailAndPassword(txtEmail.getText().toString(), txtPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    pDialog.hide();
                    Toast.makeText(SignUpActivity.this, R.string.signUpError, Toast.LENGTH_SHORT).show();
                }else{
                    createAccountData();
                }
            }
        });
    }

    private void createAccountData(){
        db = FirebaseDatabase.getInstance();
        DatabaseFunctions.updateUser(FirebaseAuth.getInstance(),db,new User(txtName.getText().toString(),txtEmail.getText().toString(),txtNumber.getText().toString()));
        updateNumberData(db);
        pDialog.hide();
        changeActivityToMainMenu();
    }

    private boolean checkIfNumberExists(){
        boolean exists = true;
        try {
            exists = new DatabaseFunctions.GetNumberMatch().execute("https://us-central1-grocery-a6978.cloudfunctions.net/checkIfNumberExists?number="+txtNumber.getText().toString()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return exists;
    }

    private void updateNumberData(FirebaseDatabase db){
        DatabaseReference ref = db.getReference("numbers").child(txtNumber.getText().toString());
        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref.setValue(userUID);
    }

    private boolean checkIfNameIsEmpty(){
        return txtName.getText().toString().length() >= 1;
    }
    private boolean validateEmail(){
        return !txtEmail.getText().toString().equals("") && Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches();
    }

    private boolean checkIfPasswordsAreEmpty(){
        return txtPassword.getText().toString().equals("") && txtPasswordConfirmation.getText().toString().equals("");
    }

    private boolean validatePasswordMatching(){
        return txtPassword.getText().toString().equals(txtPasswordConfirmation.getText().toString());
    }
    private boolean validatePasswordLength(){
        return txtPassword.getText().toString().length() >= 6;
    }
    public void changeActivityToLogIn(View v){
        Intent logInActivity = new Intent(this, MainActivity.class);
        startActivity(logInActivity);
    }

    private void changeActivityToMainMenu(){
        Intent mainMenuActivity = new Intent(this, MainMenuActivity.class);
        startActivity(mainMenuActivity);
        finish();
    }

}
