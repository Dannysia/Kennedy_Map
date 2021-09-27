package edu.onu.kennedy_map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class TitleScreen extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.title_screen);

		// Remove top bar
		ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.hide();

		// Set view correctly if not done in xml already:
		LinearLayout signinSignupLayout = (LinearLayout) findViewById(R.id.signinSignupLayout);
		LinearLayout signinLayout = (LinearLayout) findViewById(R.id.signinLayout);
		LinearLayout signupLayout = (LinearLayout) findViewById(R.id.signupLayout);
		signinSignupLayout.setVisibility(View.VISIBLE);
		signinLayout.setVisibility(View.GONE);
		signupLayout.setVisibility(View.GONE);
	}
	// This is what user's see when they first enter the app.
	// ----------------------------------------------- Title Portion -----------------------------------------------
	public void proceedToSigninButton(View view){
		//TODO add animation to pressing the sign in button
		LinearLayout signinSignupLayout = (LinearLayout) findViewById(R.id.signinSignupLayout);
		LinearLayout signinLayout = (LinearLayout) findViewById(R.id.signinLayout);
		signinSignupLayout.setVisibility(View.GONE);
		signinLayout.setVisibility(View.VISIBLE);
	}
	public void proceedToSignupButton(View view){
		//TODO add animation to pressing the sign up button
		LinearLayout signinSignupLayout = (LinearLayout) findViewById(R.id.signinSignupLayout);
		LinearLayout signupLayout = (LinearLayout) findViewById(R.id.signupLayout);
		signinSignupLayout.setVisibility(View.GONE);
		signupLayout.setVisibility(View.VISIBLE);
	}
	public void guestButton(View view){
		//TODO change this to the right main screen class
		Intent intent = new Intent(TitleScreen.this,MainScreen.class);
		startActivity(intent);
	}
	// ------------------------------------------- END Title Portion -----------------------------------------------

	// This is what the user sees when they press Sign-In
	// ----------------------------------------------- Sign-In Portion -----------------------------------------------
	public void signinButton(View view){
		EditText emailLoginEditText		= (EditText)findViewById(R.id.emailLoginEditText);
		EditText passwordLoginEditText	= (EditText)findViewById(R.id.passwordLoginEditText);

		// Make sure neither are blank
		if(emailLoginEditText.getText().toString().equals("")||passwordLoginEditText.getText().toString().equals("")){
			Toast.makeText(TitleScreen.this, "Email and Password are required.", Toast.LENGTH_LONG).show();
			return;
		}

		// TODO: More input sanitation here before we send it to our database (or webserver)

		// TODO: Replace here the code we use for account management
		// Else we can ensure the credentials are authentic, use our special database method here
		//if(){

		//}else{
			//Toast.makeText(TitleScreen.this, "Account information incorrect or no account.", Toast.LENGTH_LONG).show();
		//}
	}
	public void returnToTitleButton(View view){
		//TODO add animation to pressing the sign in button
		LinearLayout signinSignupLayout = (LinearLayout) findViewById(R.id.signinSignupLayout);
		LinearLayout signinLayout = (LinearLayout) findViewById(R.id.signinLayout);
		signinLayout.setVisibility(View.GONE);
		signinSignupLayout.setVisibility(View.VISIBLE);
	}
	// ------------------------------------------- END Sign-In Portion -----------------------------------------------

	// This is what the user sees when they press Sign-Up
	// ----------------------------------------------- Sign-Up Portion -----------------------------------------------
	public void signupButton(View view){
		EditText emailSignupEditText          	= (EditText)findViewById(R.id.emailSignupEditText);
		EditText passwordSignupEditText         = (EditText)findViewById(R.id.passwordSignupEditText);
		EditText confirmPasswordSignupEditText  = (EditText)findViewById(R.id.confirmPasswordSignupEditText);
		RadioGroup studentTeacherRadioGroup 		= (RadioGroup) findViewById(R.id.studentTeacherRadioGroup);

		// Make sure none are blank
		if(emailSignupEditText.getText().toString().equals("")||passwordSignupEditText.getText().toString().equals("")
				||confirmPasswordSignupEditText.getText().toString().equals("")||studentTeacherRadioGroup.getCheckedRadioButtonId()==View.NO_ID){
			Toast.makeText(TitleScreen.this, "All fields are required.", Toast.LENGTH_LONG).show();
		}
		// Make sure passwords match
		if(!passwordSignupEditText.getText().toString().equals(confirmPasswordSignupEditText.getText().toString())){
			Toast.makeText(TitleScreen.this, "Passwords don't match. Retype them.", Toast.LENGTH_LONG).show();
		}

		// TODO: More input sanitation here before we send it to our database (or webserver)

		// Now check if the username matches another in the database (if the user doesn't exist... make the account)
		// Use our special database method here

		// Else if the username does already exist
		//if(){

		//}else{
			//Toast.makeText(TitleScreen.this, "Username taken, enter a different username", Toast.LENGTH_LONG).show();
		//}
	}
	public void returnToTitleButton2(View view){
		//TODO add animation to pressing the sign in button
		LinearLayout signinSignupLayout = (LinearLayout) findViewById(R.id.signinSignupLayout);
		LinearLayout signupLayout = (LinearLayout) findViewById(R.id.signinLayout);
		signupLayout.setVisibility(View.GONE);
		signinSignupLayout.setVisibility(View.VISIBLE);
	}
	// ----------------------------------------------- END Sign-Up Portion -----------------------------------------------
}