package edu.onu.kennedy_map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginScreen extends AppCompatActivity {

	// TODO add button debouncing

	ArrayList<Room> allRooms = new ArrayList<>();
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

		//TODO move to databaseManager
		String roomIDEndpoint ="http://eccs3421.siatkosky.net:3421"+"/roomsWithInformation"; // 1. Endpoint
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
				(Request.Method.GET, roomIDEndpoint, null,
						response -> {
							try{
								JSONArray roomObjects = response.getJSONArray("roomObjects");
								System.out.println("HERE");
								JSONObject eachRoom;
								for (int i = 0; i<roomObjects.length(); i++){
									eachRoom = roomObjects.getJSONObject(i);
									Room returnedRoom = new Room();
									// List of items to set in the room object
									// 1. roomID
									try {
										returnedRoom.setRoomID(eachRoom.getInt("roomID"));
									}catch(JSONException e){
										System.out.println("RoomID error"+eachRoom);
									}
									// 2. shortName
									try {
									returnedRoom.setShortName(eachRoom.getString("shortName"));
									}catch(JSONException e){
										System.out.println("Shortname error"+eachRoom);
									}
									// 3. roomName
									returnedRoom.setRoomName(eachRoom.getString("roomName"));
									// 4. description
									returnedRoom.setDescription(eachRoom.getString("description"));
									// 5. floor
									returnedRoom.setFloor(eachRoom.getInt("floor"));
									// 6. boundaryCoordinates Arraylist
									JSONArray roomBoundaryCoordinates = eachRoom.getJSONArray("boundaryCoordinates");
									ArrayList<XYZCoordinate> roomBoundaryCoordinatesArrayList = new ArrayList<>();
									JSONObject eachXYZCoordinate;
									for (int j = 0; j<roomBoundaryCoordinates.length(); j++){
										try {
											eachXYZCoordinate = roomBoundaryCoordinates.getJSONObject(j);
											roomBoundaryCoordinatesArrayList.add(new XYZCoordinate(eachXYZCoordinate.getInt("LocationX"),
													eachXYZCoordinate.getInt("LocationY"), eachXYZCoordinate.getInt("LocationZ")));
										}catch (JSONException jsonException){
											jsonException.printStackTrace();
											System.out.println("boundaryCoordinatesError "+eachRoom);
										}
									}
									returnedRoom.setBoundaryCoordinates(roomBoundaryCoordinatesArrayList);
									returnedRoom.setCenter(returnedRoom.getBoundaryCoordinates());
									// 7. reservable
									try {
										if("True".equals(eachRoom.getString("reservable"))){
											returnedRoom.setReservable(true);
										}else{
											returnedRoom.setReservable(false);
										}
										//returnedRoom.setReservable(response.getBoolean("reservable"));
									}catch(JSONException e){
										e.printStackTrace();
										System.out.println("reservable boolean error "+eachRoom);
									}
									allRooms.add(returnedRoom);
								}
								System.out.println(allRooms.size());
							}catch(Exception e){
								e.printStackTrace();
							}
						}, error -> { System.out.println("Error "+error.toString());
				});
		// Add the request to the queue, which will complete eventually
		APIRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);
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
		Intent intent = new Intent(this, MenuScreen.class);
		GuestUser guestUser = (GuestUser) new ConcreteGuestUserCreator().createUser("GUEST",-1);
		intent.putExtra("user",guestUser);
		intent.putExtra("rooms",allRooms);
		this.startActivity(intent);
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
		DatabaseManager.getInstance().loginPageLogin(this,emailLoginEditText,passwordLoginEditText,allRooms);
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
		// Add regex test to make sure it is an email

		//TODO: Move this to database helper
		String authenticationEndpoint ="http://eccs3421.siatkosky.net:3421"+"/account/password/forgot"; // 1. Endpoint
		JSONObject requestBody=null;
		try {
			requestBody = new JSONObject();
			requestBody.put("email", emailPasswordResetEditText.getText().toString());
			System.out.println("Sent Content: "+requestBody.toString());
		}catch (JSONException ignored){ }
		AtomicBoolean emailSentBoolean = new AtomicBoolean();
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
				(Request.Method.POST, authenticationEndpoint, requestBody,
						response -> {
							try{
								boolean emailSent = response.getBoolean("resetRequestSuccessful");
								emailSentBoolean.set(emailSent);
								System.out.println("Recieved Content: "+emailSentBoolean.get());
								LinearLayout loginLoadingLinearLayout = findViewById(R.id.loginLoadingLinearLayout);
								loginLoadingLinearLayout.setVisibility(View.GONE);
							}catch(Exception e){
								emailSentBoolean.set(false);
							}
							if(!emailSentBoolean.get()) {
								Toast.makeText(this, "Account information incorrect or no account.", Toast.LENGTH_LONG).show();
							}else{
								Toast.makeText(this, "Email Sent if account exists. Check your inbox/spam folder! ", Toast.LENGTH_LONG).show();
							}
						}, error -> { System.out.println("Error "+error.toString());
				});
		//Pop-up loading screen
		LinearLayout loginLoadingLinearLayout = findViewById(R.id.loginLoadingLinearLayout);
		loginLoadingLinearLayout.setVisibility(View.VISIBLE);
		loginLoadingLinearLayout.setClickable(true);
		loginLoadingLinearLayout.setOnClickListener(listener ->{
			// Purposefully blank
		});
		// Add the request to the queue, which will complete eventually
		APIRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);


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

		DatabaseManager.getInstance().loginPageRegister(this,emailSignupEditText,passwordSignupEditText,firstNameSignUpEditText,lastNameSignUpEditText);
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