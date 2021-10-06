package edu.onu.kennedy_map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginScreen extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);

		// Remove top bar
		ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.hide();

		// Set view correctly if not done in xml already:
		LinearLayout signinSignupLayout = (LinearLayout) findViewById(R.id.signinSignupLayout);
		LinearLayout signinLayout = (LinearLayout) findViewById(R.id.signinLayout);
		LinearLayout signupLayout = (LinearLayout) findViewById(R.id.signupLayout);
		LinearLayout forgotPasswordLayout = (LinearLayout) findViewById(R.id.forgotPasswordLayout);
		signinSignupLayout.setVisibility(View.VISIBLE);
		signinLayout.setVisibility(View.GONE);
		signupLayout.setVisibility(View.GONE);
		forgotPasswordLayout.setVisibility(View.GONE);
	}

	// This is the functionality when they first enter the app.
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
		//TODO make sure to pass a GuestUser as the extra along with this startActivity later
		Intent intent = new Intent(LoginScreen.this, MenuScreen.class);
		GuestUser guestUser = new GuestUser();
		intent.putExtra("user",guestUser);
		startActivity(intent);
	}
	// ------------------------------------------- END Title Portion -----------------------------------------------

	// This is the functionality when they press Sign-In
	// ----------------------------------------------- Sign-In Portion -----------------------------------------------
	public void signinButton(View view){
		EditText emailLoginEditText		= (EditText)findViewById(R.id.emailLoginEditText);
		EditText passwordLoginEditText	= (EditText)findViewById(R.id.passwordLoginEditText);

		// Make sure neither are blank
		if(emailLoginEditText.getText().toString().equals("")||passwordLoginEditText.getText().toString().equals("")){
			Toast.makeText(LoginScreen.this, "Email and Password are required.", Toast.LENGTH_LONG).show();
			return;
		}

		// TODO: More input sanitation here before we send it to our webserver

		//TODO put this all in LoginHelper
		String authenticationEndpoint = APIRequestQueue.getInstance(this).getENDPOINT()+"/login"; // 1. Endpoint
		JSONObject requestBody=null;
		try {
			requestBody = new JSONObject();
			requestBody.put("email", emailLoginEditText.getText().toString());
			requestBody.put("password", passwordLoginEditText.getText().toString());
			System.out.println("Sent Content: "+requestBody.toString());
		}catch (JSONException ignored){ }
		AtomicInteger returnedUserID = new AtomicInteger(0);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
				(Request.Method.POST, authenticationEndpoint, requestBody,
						response -> {
							try{
								int userID = response.getInt("userID");
								returnedUserID.set(userID);
								System.out.println("Recieved Content: "+returnedUserID.get());
							}catch(Exception e){
								returnedUserID.set(0);
							}
							if(returnedUserID.get()!=0){
								Intent intent = new Intent(LoginScreen.this, MenuScreen.class);
								// RegisteredUser is created with userID received and userInfo(not used)
								RegisteredUser registeredUser = new RegisteredUser(returnedUserID.get(),null);
								System.out.println(returnedUserID.get());
								intent.putExtra("user",registeredUser);
								startActivity(intent);
							}
							else if(returnedUserID.get()==0) {
								Toast.makeText(LoginScreen.this, "Account information incorrect or no account.", Toast.LENGTH_LONG).show();
							}
						}, error -> { System.out.println("Error "+error.toString());
				});
		//TODO pop up waiting symbol, until the response is received.
		// The code to show will be right here, but the the code to remove it will be in the listeners

		// Add the request to the queue, which will complete eventually
		APIRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);
	}

	public void forgotPasswordButton(View view){
		//TODO add animation to pressing the sign in button
		LinearLayout forgotPasswordLayout = (LinearLayout) findViewById(R.id.forgotPasswordLayout);
		LinearLayout signinLayout = (LinearLayout) findViewById(R.id.signinLayout);
		signinLayout.setVisibility(View.GONE);
		forgotPasswordLayout.setVisibility(View.VISIBLE);
	}
	public void returnToTitleButton(View view){
		//TODO add animation to pressing the sign in button
		LinearLayout signinSignupLayout = (LinearLayout) findViewById(R.id.signinSignupLayout);
		LinearLayout signinLayout = (LinearLayout) findViewById(R.id.signinLayout);
		signinLayout.setVisibility(View.GONE);
		signinSignupLayout.setVisibility(View.VISIBLE);
	}
	// ------------------------------------------- END Sign-In Portion -----------------------------------------------

	// This is the functionality when they press Reset Password from the sign-in screen
	// ----------------------------------------------- Password Reset Portion ----------------------------------------
	public void resetPasswordButton(View view){
		//Make sure email is not blank
		EditText emailPasswordResetEditText = (EditText) findViewById(R.id.emailPasswordResetEditText);
		if(emailPasswordResetEditText.getText().toString().equals("")){
			Toast.makeText(LoginScreen.this, "Email is required.", Toast.LENGTH_LONG).show();
		}
		//TODO: More input sanitation here before we send it to our database (or webserver)
		//TODO: Reset the user's password somehow.
	}
	public void returnToSigninButton(View view){
		//TODO add animation to pressing the sign in button
		LinearLayout forgotPasswordLayout = (LinearLayout) findViewById(R.id.forgotPasswordLayout);
		LinearLayout signinLayout = (LinearLayout) findViewById(R.id.signinLayout);
		signinLayout.setVisibility(View.VISIBLE);
		forgotPasswordLayout.setVisibility(View.GONE);
	}

	// ------------------------------------------- END Password Reset Portion ----------------------------------------

	// This is the functionality when they press Sign-Up
	// ----------------------------------------------- Sign-Up Portion -----------------------------------------------
	public void signupButton(View view){

		EditText firstNameSignUpEditText        = (EditText)findViewById(R.id.firstNameSignUpEditText);
		EditText lastNameSignUpEditText         = (EditText)findViewById(R.id.lastNameSignUpEditText);
		EditText emailSignupEditText          	= (EditText)findViewById(R.id.emailSignupEditText);
		EditText passwordSignupEditText         = (EditText)findViewById(R.id.passwordSignupEditText);
		EditText confirmPasswordSignupEditText  = (EditText)findViewById(R.id.confirmPasswordSignupEditText);


		// Make sure none are blank
		if(emailSignupEditText.getText().toString().equals("")||passwordSignupEditText.getText().toString().equals("")
				||confirmPasswordSignupEditText.getText().toString().equals("")||firstNameSignUpEditText.getText().toString().equals("")
				||lastNameSignUpEditText.getText().toString().equals("")){
			Toast.makeText(LoginScreen.this, "All fields are required.", Toast.LENGTH_LONG).show();
		}
		// Make sure passwords match
		if(!passwordSignupEditText.getText().toString().equals(confirmPasswordSignupEditText.getText().toString())){
			Toast.makeText(LoginScreen.this, "Passwords don't match. Retype them.", Toast.LENGTH_LONG).show();
		}

		// TODO: More input sanitation here before we send it to our database (or webserver)

		//TODO put this all in LoginHelper
		String authenticationEndpoint = APIRequestQueue.getInstance(this).getENDPOINT()+"/register"; // 1. Endpoint
		JSONObject requestBody=null;
		try {
			requestBody = new JSONObject();
			requestBody.put("email", emailSignupEditText.getText().toString());
			requestBody.put("password", passwordSignupEditText.getText().toString());
			requestBody.put("first_name", firstNameSignUpEditText.getText().toString());
			requestBody.put("last_name", lastNameSignUpEditText.getText().toString());
			System.out.println("Sent Content: "+requestBody.toString());
		}catch (JSONException ignored){ }
		AtomicBoolean successBoolean = new AtomicBoolean(false);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
				(Request.Method.POST, authenticationEndpoint, requestBody,
						response -> {
							try{
								//TODO Change name of JSON pair to whatever danny makes it
								boolean successToAddAccount = response.getBoolean("creationSuccessful");
								successBoolean.set(successToAddAccount);
								System.out.println("Recieved Content: "+successBoolean.get());
							}catch(Exception e){
								successBoolean.set(false);
							}
							if(successBoolean.get()){
								LinearLayout signinSignupLayout = (LinearLayout) findViewById(R.id.signinSignupLayout);
								LinearLayout signupLayout = (LinearLayout) findViewById(R.id.signinLayout);
								signupLayout.setVisibility(View.GONE);
								signinSignupLayout.setVisibility(View.VISIBLE);
								Toast.makeText(LoginScreen.this, "Account Creation Successful. Login Now.", Toast.LENGTH_LONG).show();
							}
							else if(!successBoolean.get()) {
								Toast.makeText(LoginScreen.this, "Email already used. Pick another one.", Toast.LENGTH_LONG).show();
							}
						}, error -> { System.out.println("Error "+error.toString());
				});
		//TODO pop up waiting symbol, until the response is received.
		// The code to show will be right here, but the the code to remove it will be in the listeners

		// Add the request to the queue, which will complete eventually
		APIRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);
	}
	public void returnToTitleButton2(View view){
		//TODO add animation to pressing the sign in button
		LinearLayout signinSignupLayout = (LinearLayout) findViewById(R.id.signinSignupLayout);
		LinearLayout signupLayout = (LinearLayout) findViewById(R.id.signinLayout);
		signupLayout.setVisibility(View.GONE);
		signinSignupLayout.setVisibility(View.VISIBLE);
	}
	// ----------------------------------------------- END Sign-Up Portion -----------------------------------------------

	/**
	 * This login function is used to login a 'Guest' user. This function will call LoginHelper, which can then tell that nothing was passed
	 * and it will create and return the relevant GuestUser Object that you will pass around the app using the getExtra function.
	 * @return A filled GuestUser object.
	 */
	public GuestUser login(){
		return null;
	}

	/**
	 * This login function is used to login a registered user. This function will call LoginHelper, which will then check the database using
	 * DatabaseManager and eventually return a filled RegisteredUser object that you will pass around.
	 * @param username The username of the user, which will most likely be pulled from the EditText and sanitized
	 * @param password The password of the user, which will most likely be pulled from the EditText and sanitized
	 * @return A filled RegisterUser Object
	 */
	public RegisteredUser login(String username, String password){
		return null;
	}

}