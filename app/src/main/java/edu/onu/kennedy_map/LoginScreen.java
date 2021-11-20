package edu.onu.kennedy_map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * The LoginScreen class is the first screen the user sees, and handles the range of logging in activities for the app.
 */
public class LoginScreen extends AppCompatActivity {

	private ArrayList<Room> allRooms = new ArrayList<>();

	// When the app is first opened, the proper screen is displayed and various initializations are done
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);

		// Remove top bar
		ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.hide();

		// Set view correctly if not done in xml already:
		LinearLayout signinSignupLayout = findViewById(R.id.signinSignupLayout);
		LinearLayout signinLayout = findViewById(R.id.signinLayout);
		LinearLayout signupLayout = findViewById(R.id.signupLayout);
		LinearLayout forgotPasswordLayout = findViewById(R.id.forgotPasswordLayout);
		signinSignupLayout.setVisibility(View.VISIBLE);
		signinLayout.setVisibility(View.GONE);
		signupLayout.setVisibility(View.GONE);
		forgotPasswordLayout.setVisibility(View.GONE);

		// Get room info, present loading screen until room info successfully arrives
		DatabaseManager.getInstance().getRooms(this,allRooms);
	}

	// This is the functionality when they first enter the app.
	// ----------------------------------------------- Title Portion -----------------------------------------------

	// When the initial Sign-In button is pressed, they are shown the sign-in display
	public void proceedToSigninButton(View view){
		LinearLayout signinSignupLayout = (LinearLayout) findViewById(R.id.signinSignupLayout);
		LinearLayout signinLayout = (LinearLayout) findViewById(R.id.signinLayout);
		signinSignupLayout.setVisibility(View.GONE);
		signinLayout.setVisibility(View.VISIBLE);
	}

	// When the initial Sign-Up button is pressed, they are shown the sign-up display
	public void proceedToSignupButton(View view){
		LinearLayout signinSignupLayout = (LinearLayout) findViewById(R.id.signinSignupLayout);
		LinearLayout signupLayout = (LinearLayout) findViewById(R.id.signupLayout);
		signinSignupLayout.setVisibility(View.GONE);
		signupLayout.setVisibility(View.VISIBLE);
	}

	// Pressing the guest button takes them to the main menu screen, necessary info is passed
	public void guestButton(View view){
		Intent intent = new Intent(this, MenuScreen.class);
		GuestUser guestUser = (GuestUser) new ConcreteGuestUserCreator().createUser("GUEST",-1);
		intent.putExtra("user",guestUser);
		intent.putExtra("rooms",allRooms);
		this.startActivity(intent);
	}
	// ------------------------------------------- END Title Portion -----------------------------------------------

	// ----------------------------------------------- Sign-In Portion -----------------------------------------------
	// When the Sign-In button is pressed on the sign-in display, their input is validated before being checked by the API
	// If the API gives the OK, then they are transported to the main menu screen.
	public void signinButton(View view){
		EditText emailLoginEditText		= (EditText)findViewById(R.id.emailLoginEditText);
		EditText passwordLoginEditText	= (EditText)findViewById(R.id.passwordLoginEditText);

		// Make sure neither are blank
		if(InputValidation.checkIfTextViewsBlank(emailLoginEditText, passwordLoginEditText)) {
			Toast.makeText(LoginScreen.this, "Email and Password are required.", Toast.LENGTH_LONG).show();
			return;
		}else{
			if(!InputValidation.checkIfValidEmail(emailLoginEditText.getText().toString())) {
				Toast.makeText(LoginScreen.this, "Enter a valid email address.", Toast.LENGTH_LONG).show();
				return;
			}
		}
		DatabaseManager.getInstance().loginPageLogin(this,emailLoginEditText,passwordLoginEditText,allRooms);
	}

	// Presents the forgot password display to the user
	public void forgotPasswordButton(View view){
		LinearLayout forgotPasswordLayout = (LinearLayout) findViewById(R.id.forgotPasswordLayout);
		LinearLayout signinLayout = (LinearLayout) findViewById(R.id.signinLayout);
		signinLayout.setVisibility(View.GONE);
		forgotPasswordLayout.setVisibility(View.VISIBLE);
	}

	// On any of the displays, the user is then presented with initial display
	public void returnToTitleButton(View view){
		LinearLayout signinSignupLayout = (LinearLayout) findViewById(R.id.signinSignupLayout);
		LinearLayout signinLayout = (LinearLayout) findViewById(R.id.signinLayout);
		signinLayout.setVisibility(View.GONE);
		signinSignupLayout.setVisibility(View.VISIBLE);
	}
	// ------------------------------------------- END Sign-In Portion -----------------------------------------------

	// ----------------------------------------------- Password Reset Portion ----------------------------------------
	// When the user enters the necessary information for password resets, their input is validated, then sent to the API
	// The API then sends them an email if their account actually exists, no hinting if the account exists is presented app-side
	public void resetPasswordButton(View view){
		//Make sure email is not blank
		EditText emailPasswordResetEditText = (EditText) findViewById(R.id.emailPasswordResetEditText);
		if(InputValidation.checkIfTextViewsBlank(emailPasswordResetEditText)){
			Toast.makeText(LoginScreen.this, "Email is required.", Toast.LENGTH_LONG).show();
			return;
		}
		if(!InputValidation.checkIfValidEmail(emailPasswordResetEditText.getText().toString())){
			Toast.makeText(LoginScreen.this, "Enter a valid email address", Toast.LENGTH_LONG).show();
			return;
		}
		DatabaseManager.getInstance().loginPageResetPassword(this,emailPasswordResetEditText);
	}

	// Returns the user to the sign up display
	public void returnToSigninButton(View view){
		LinearLayout forgotPasswordLayout = (LinearLayout) findViewById(R.id.forgotPasswordLayout);
		LinearLayout signinLayout = (LinearLayout) findViewById(R.id.signinLayout);
		signinLayout.setVisibility(View.VISIBLE);
		forgotPasswordLayout.setVisibility(View.GONE);
	}

	// ------------------------------------------- END Password Reset Portion ----------------------------------------

	// ----------------------------------------------- Sign-Up Portion -----------------------------------------------
	// When the user is on the sign-up dispaly and they press sign-up, their input is validated before being sent to the API
	// When the API returns the OK, they are returned to the title screen to login with their new information
	public void signupButton(View view){

		EditText firstNameSignUpEditText        = (EditText)findViewById(R.id.firstNameSignUpEditText);
		EditText lastNameSignUpEditText         = (EditText)findViewById(R.id.lastNameSignUpEditText);
		EditText emailSignupEditText          	= (EditText)findViewById(R.id.emailSignupEditText);
		EditText passwordSignupEditText         = (EditText)findViewById(R.id.passwordSignupEditText);
		EditText confirmPasswordSignupEditText  = (EditText)findViewById(R.id.confirmPasswordSignupEditText);

		// Make sure none are blank
		if(InputValidation.checkIfTextViewsBlank(firstNameSignUpEditText,lastNameSignUpEditText,emailSignupEditText,
				passwordSignupEditText,confirmPasswordSignupEditText)){
			Toast.makeText(LoginScreen.this, "All fields are required.", Toast.LENGTH_LONG).show();
			return;
		}
		// Make sure passwords match
		if(!passwordSignupEditText.getText().toString().equals(confirmPasswordSignupEditText.getText().toString())){
			Toast.makeText(LoginScreen.this, "Passwords don't match. Retype them.", Toast.LENGTH_LONG).show();
			return;
		}
		// Make sure email is valid
		if(!InputValidation.checkIfValidEmail(emailSignupEditText.getText().toString())){
			Toast.makeText(LoginScreen.this, "Enter a valid email address.", Toast.LENGTH_LONG).show();
			return;
		}

		DatabaseManager.getInstance().loginPageRegister(this,emailSignupEditText,passwordSignupEditText,firstNameSignUpEditText,lastNameSignUpEditText);
	}

	// Also returns the user to the title display
	public void returnToTitleButton2(View view){
		LinearLayout signinSignupLayout = (LinearLayout) findViewById(R.id.signinSignupLayout);
		LinearLayout signupLayout = (LinearLayout) findViewById(R.id.signinLayout);
		signupLayout.setVisibility(View.GONE);
		signinSignupLayout.setVisibility(View.VISIBLE);
	}
	// ----------------------------------------------- END Sign-Up Portion -----------------------------------------------

}