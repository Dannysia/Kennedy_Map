package edu.onu.kennedy_map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseManager {
    private static DatabaseManager databaseManager = null;
    private DatabaseManager(){}

    private final String ENDPOINT = "http://eccs3421.siatkosky.net:3421";

    public static DatabaseManager getInstance(){
        if(databaseManager==null){
            databaseManager = new DatabaseManager();
        }
        return databaseManager;
    }

    /**
     * Gets all rooms to store them in the arraylist at the beginning
     * @param loginScreen The loginscreen activity class
     * @param allRooms The array to fill
     */
        public void getRooms(Activity loginScreen,ArrayList<Room> allRooms){
        String roomIDEndpoint =ENDPOINT+"/roomsWithInformation"; // 1. Endpoint
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, roomIDEndpoint, null,
                        response -> {
                            try{
                                JSONArray roomObjects = response.getJSONArray("roomObjects");
                                //System.out.println("HERE");
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
                                        e.printStackTrace();
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
                                    // Remove loading screen
                                    LinearLayout loadingLinearLayout = (LinearLayout)loginScreen.findViewById(R.id.loginLoadingLinearLayout);
                                    loadingLinearLayout.setVisibility(View.GONE);
                                    allRooms.add(returnedRoom);
                                }
                                System.out.println(allRooms.size());
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }, error ->
                { System.out.println("Error "+error.toString());
                    error.printStackTrace();
                    getRooms(loginScreen,allRooms);
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Pop-up loading screen
        LinearLayout loginLoadingLinearLayout = (LinearLayout)loginScreen.findViewById(R.id.loginLoadingLinearLayout);
        loginLoadingLinearLayout.setVisibility(View.VISIBLE);
        loginLoadingLinearLayout.setClickable(true);
        loginLoadingLinearLayout.setOnClickListener(listener ->{
            // Purposefully blank
        });
        // Add the request to the queue, which will complete eventually
        APIRequestQueue.getInstance(loginScreen).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * This function sends a POST request to our API in order to log the user in. Do input validation before this is called.
     * @param currentScreen The current screen the user is on. The LoginScreen class will be passed.
     * @param emailInput The EditText containing the email.
     * @param passwordInput The EditText containing the password.
     */
    public void loginPageLogin(Activity currentScreen, EditText emailInput, EditText passwordInput, ArrayList<Room> allRooms){
        String authenticationEndpoint =ENDPOINT+"/login"; // 1. Endpoint
        JSONObject requestBody=null;
        try {
            requestBody = new JSONObject();
            requestBody.put("email", emailInput.getText().toString());
            requestBody.put("password", passwordInput.getText().toString());
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
                                // Remove loading screen
                                LinearLayout loadingLinearLayout = (LinearLayout)currentScreen.findViewById(R.id.loginLoadingLinearLayout);
                                loadingLinearLayout.setVisibility(View.GONE);
                                // Button Debounce off
                                Button signInButton = currentScreen.findViewById(R.id.signinButton);
                                signInButton.setEnabled(true);
                            }catch(Exception e){
                                returnedUserID.set(0);
                            }
                            if(returnedUserID.get()!=0){
                                Intent intent = new Intent(currentScreen, MenuScreen.class);
                                // RegisteredUser is created with userID received and userInfo(not used)
                                RegisteredUser registeredUser = (RegisteredUser) new ConcreteRegisteredUserCreator().createUser(null,returnedUserID.get());
                                System.out.println(returnedUserID.get());
                                intent.putExtra("user",registeredUser);
                                intent.putExtra("rooms",allRooms);
                                currentScreen.startActivity(intent);
                            }
                            else if(returnedUserID.get()==0) {
                                Toast.makeText(currentScreen, "Account information incorrect or no account.", Toast.LENGTH_LONG).show();
                            }
                        }, error -> {
                    System.out.println("Error "+error.toString());
                    // Recursive fix
                    loginPageLogin( currentScreen,  emailInput,  passwordInput,  allRooms);
                });
        //Pop-up loading screen
        LinearLayout loginLoadingLinearLayout = (LinearLayout)currentScreen.findViewById(R.id.loginLoadingLinearLayout);
        loginLoadingLinearLayout.setVisibility(View.VISIBLE);
        loginLoadingLinearLayout.setClickable(true);
        loginLoadingLinearLayout.setOnClickListener(listener ->{
            // Purposefully blank
        });
        // Button Debounce on
        Button signInButton = currentScreen.findViewById(R.id.signinButton);
        signInButton.setEnabled(false);
        // Add the request to the queue, which will complete eventually
        APIRequestQueue.getInstance(currentScreen).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * This function sends a POST request to our API in order to let the user reset their password.
     * @param currentScreen The current screen the user is on. The LoginScreen will be password.
     * @param emailPasswordResetEditText The edit text that contains the user's password
     */
    public void loginPageResetPassword(Activity currentScreen, TextView emailPasswordResetEditText){
        String authenticationEndpoint =ENDPOINT+"/account/password/forgot"; // 1. Endpoint
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
                                LinearLayout loginLoadingLinearLayout = currentScreen.findViewById(R.id.loginLoadingLinearLayout);
                                loginLoadingLinearLayout.setVisibility(View.GONE);
                                // Button Debounce off
                                Button resetPasswordButton = currentScreen.findViewById(R.id.resetPasswordButton);
                                resetPasswordButton.setEnabled(true);
                            }catch(Exception e){
                                emailSentBoolean.set(false);
                            }
                            if(!emailSentBoolean.get()) {
                                Toast.makeText(currentScreen, "Account information incorrect or no account.", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(currentScreen, "Email Sent if account exists. Check your inbox/spam folder! ", Toast.LENGTH_LONG).show();
                            }
                        }, error -> { System.out.println("Error "+error.toString());
                    // Recursive fix
                    loginPageResetPassword( currentScreen,  emailPasswordResetEditText);
                });
        //Pop-up loading screen
        LinearLayout loginLoadingLinearLayout = currentScreen.findViewById(R.id.loginLoadingLinearLayout);
        loginLoadingLinearLayout.setVisibility(View.VISIBLE);
        loginLoadingLinearLayout.setClickable(true);
        loginLoadingLinearLayout.setOnClickListener(listener ->{
            // Purposefully blank
        });
        // Button Debounce on
        Button resetPasswordButton = currentScreen.findViewById(R.id.resetPasswordButton);
        resetPasswordButton.setEnabled(false);
        // Add the request to the queue, which will complete eventually
        APIRequestQueue.getInstance(currentScreen).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * This function sends a POST request to our API in order to register the user. Do input validation before this is called.
     * @param currentScreen The current screen the user is on. The LoginScreen class will be passed.
     * @param emailInput The EditText containing the email.
     * @param passwordInput The EditText containing the password.
     * @param firstNameInput The EditText containing the first name.
     * @param lastNameInput The EditText containing the last name.
     */
    public void loginPageRegister(Activity currentScreen, EditText emailInput, EditText passwordInput,
                                  EditText firstNameInput, EditText lastNameInput){
        String authenticationEndpoint = ENDPOINT+"/register"; // 1. Endpoint
        JSONObject requestBody=null;
        try {
            requestBody = new JSONObject();
            requestBody.put("email", emailInput.getText().toString());
            requestBody.put("password", passwordInput.getText().toString());
            requestBody.put("first_name", firstNameInput.getText().toString());
            requestBody.put("last_name", lastNameInput.getText().toString());
            System.out.println("Sent Content: "+requestBody.toString());
        }catch (JSONException ignored){ }
        AtomicBoolean successBoolean = new AtomicBoolean(false);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, authenticationEndpoint, requestBody,
                        response -> {
                            try{
                                boolean successToAddAccount = response.getBoolean("creationSuccessful");
                                successBoolean.set(successToAddAccount);
                                System.out.println("Recieved Content: "+successBoolean.get());
                                // Remove loading screen
                                LinearLayout loadingLinearLayout = (LinearLayout)currentScreen.findViewById(R.id.loginLoadingLinearLayout);
                                loadingLinearLayout.setVisibility(View.GONE);
                                // Button Debounce off
                                Button signUpButton = currentScreen.findViewById(R.id.signupButton);
                                signUpButton.setEnabled(true);
                            }catch(Exception e){
                                successBoolean.set(false);
                            }
                            if(successBoolean.get()){
                                LinearLayout signinSignupLayout = (LinearLayout) currentScreen.findViewById(R.id.signinSignupLayout);
                                LinearLayout signupLayout = (LinearLayout) currentScreen.findViewById(R.id.signinLayout);
                                signupLayout.setVisibility(View.GONE);
                                signinSignupLayout.setVisibility(View.VISIBLE);
                                Toast.makeText(currentScreen, "Account Creation Successful. Sign-in Now.", Toast.LENGTH_LONG).show();
                            }
                            else if(!successBoolean.get()) {
                                Toast.makeText(currentScreen, "Email already used. Pick another one.", Toast.LENGTH_LONG).show();
                            }
                        }, error -> {
                    // Recursive fix
                    loginPageRegister( currentScreen,  emailInput,  passwordInput,
                             firstNameInput,  lastNameInput);
                    //LinearLayout loadingLinearLayout = (LinearLayout)currentScreen.findViewById(R.id.loginLoadingLinearLayout);
                    //loadingLinearLayout.setVisibility(View.GONE);
                    System.out.println("Error "+error.toString());
                });
        //Pop-up loading screen
        LinearLayout loginLoadingLinearLayout = (LinearLayout)currentScreen.findViewById(R.id.loginLoadingLinearLayout);
        loginLoadingLinearLayout.setVisibility(View.VISIBLE);
        loginLoadingLinearLayout.setClickable(true);
        loginLoadingLinearLayout.setOnClickListener(listener ->{
            // Purposefully blank
        });
        // Button Debounce on
        Button signUpButton = currentScreen.findViewById(R.id.signupButton);
        signUpButton.setEnabled(false);
        // Add the request to the queue, which will complete eventually
        APIRequestQueue.getInstance(currentScreen).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * This sends a GET request to our API in order to return a list of current reservations for the selected room.
     * @param currentScreen The current screen the user is on. The ReservationScreen will be passed.
     * @param valueResult The selected Room in the dropdown menu.
     * @param pressedButton The button that is pressed to activate the dropdown menu.
     * @param allRooms The arraylist of all the rooms
     */
    public void reservationPageSelectedRoom(Activity currentScreen, String valueResult, Button pressedButton,
                                            ArrayList<Room> allRooms, ArrayList<Reservation> selectedRoomReservations){

        if(valueResult==null){
            valueResult=pressedButton.getText().toString();
        }

        // Need to convert the valueResult from shortName back into roomID
        String roomIDToQuery="";
        for (Room room : allRooms){
            if(valueResult.equals(room.getShortName())){
                roomIDToQuery = ""+room.getRoomID();
            }
        }
        if(roomIDToQuery.equals("")&&!valueResult.equals("")){
            try {
                throw new Exception();
            }catch(Exception e){
                System.out.println("Error, no shortname matches a room in the list");
            }
        }

        String reservationsInRoomEndpoint = ENDPOINT+"/reservations/room/"+roomIDToQuery; // 1. Endpoint
        //System.out.println(roomIDToQuery);
        String finalValueResult = valueResult;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, reservationsInRoomEndpoint, null,
                        response -> {
                            try{
                                //Take the JSONArray, for each JSONObject of that array, form a string that will be printed
                                ArrayList<String> finishedRoomStrings = new ArrayList<>();
                                JSONArray currentRoomReservationsJSONArray=null;
                                try {
                                    currentRoomReservationsJSONArray = response.getJSONArray("currentRoomReservations");
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                ArrayList<JSONObject> currentReservationsJSONObjects = new ArrayList<>();
                                for(int i=0; i<currentRoomReservationsJSONArray.length(); i++){

                                    JSONObject currentRoomReservationJSONData = currentRoomReservationsJSONArray.getJSONObject(i);
                                    finishedRoomStrings.add(currentRoomReservationJSONData.get("startTime")+" - "+currentRoomReservationJSONData.get("endTime")+"\n");
                                    selectedRoomReservations.add(new Reservation((String)currentRoomReservationJSONData.get("startTime"),(String)currentRoomReservationJSONData.get("endTime")));
                                }
                                //Toast.makeText(currentScreen, ""+selectedRoomReservations.size(), Toast.LENGTH_LONG).show();
                                if(currentRoomReservationsJSONArray.length()==0){
                                    TextView currentReservationsTextView = (TextView) currentScreen.findViewById(R.id.currentReservationsTextView);
                                    currentReservationsTextView.setText("No Current Reservations");
                                }else {
                                    TextView currentReservationsTextView = (TextView) currentScreen.findViewById(R.id.currentReservationsTextView);
                                    StringBuilder finishedRoomStringDisplay = new StringBuilder();
                                    for (String roomString : finishedRoomStrings) {
                                        finishedRoomStringDisplay.append(roomString);
                                    }
                                    currentReservationsTextView.setText(finishedRoomStringDisplay.toString());
                                }
                                // Remove loading screen
                                LinearLayout loadingLinearLayout = (LinearLayout)currentScreen.findViewById(R.id.reservationLoadingLinearLayout);
                                loadingLinearLayout.setVisibility(View.GONE);
                            }catch(Exception e){
                                Toast.makeText(currentScreen, "Some error occurred", Toast.LENGTH_LONG).show();
                            }
                        }, error -> {
                    // Recursive fix
                    reservationPageSelectedRoom( currentScreen, finalValueResult,  pressedButton,
                            allRooms,  selectedRoomReservations);

                    System.out.println("Error "+error.toString());
                });
        //Pop-up loading screen
        LinearLayout loadingLinearLayout = (LinearLayout)currentScreen.findViewById(R.id.reservationLoadingLinearLayout);
        loadingLinearLayout.setVisibility(View.VISIBLE);
        loadingLinearLayout.setClickable(true);
        loadingLinearLayout.setOnClickListener(listener ->{
            // Purposefully blank
        });
        // Leave text alone if valueResult is blank
        pressedButton.setText(valueResult);
        ImageView reservationScreenRoomImageView = currentScreen.findViewById(R.id.reservationScreenRoomImageView);
        // Regex here in case one of the roomID return with the a letter appended to the end
        String searchRoom;
        switch(valueResult){
            case "Crown Innovation Center":
                searchRoom="crown_innovation_center";
                break;
            case "Terrace":
                searchRoom="terrace";
                break;
            case "Huddle":
                searchRoom="huddle";
                break;
            case "Engineering Activities Room":
                searchRoom="engineering_activities_room";
                break;
            default:
                Pattern pattern = Pattern.compile("(\\w{3}\\d{3}).*");
                Matcher matcher = pattern.matcher(valueResult.trim());;
                matcher.find();
                searchRoom = matcher.group(1);
                //System.out.println(valueResult);
        }
        // If null is thrown here something is up with either my regex or the picker
        // If searchRoom equals "" it means that the image should not be updated
        reservationScreenRoomImageView.setImageResource(currentScreen.getResources().getIdentifier(searchRoom.toLowerCase(),
                    "drawable", currentScreen.getPackageName()));

        APIRequestQueue.getInstance(currentScreen).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * This sends a POST request to our API in order to reserve a room for the user.
     * @param currentScreen The current screen the user is on. The ReservationScreen will be passed.
     * @param currentUser The currently logged in user, passed from screen to screen using putExtra.
     */
    public void reservationPageReserveRoom(Activity currentScreen, RegisteredUser currentUser,
                                           ArrayList<Room> allRooms, ArrayList<Reservation> currentReservations){
        String makeAReservationEndpoint = ENDPOINT+"/reservations/create"; // 1. Endpoint
        JSONObject requestBody=null;
        try {
            requestBody = new JSONObject();

            Button selectRoomButton = currentScreen.findViewById(R.id.selectRoomButton);
            Button startTimeButton = currentScreen.findViewById(R.id.startTimeButton);
            Button startDateButton = currentScreen.findViewById(R.id.startDateButton);
            Button endTimeButton = currentScreen.findViewById(R.id.endTimeButton);
            Button endDateButton = currentScreen.findViewById(R.id.endDateButton);

            if(selectRoomButton.getText().toString().equals("")||
                    selectRoomButton.getText().toString().equalsIgnoreCase("SELECT A ROOM")){
                Toast.makeText(currentScreen, "Please select a room.", Toast.LENGTH_LONG).show();
                return;
            }
            if(startTimeButton.getText().toString().equalsIgnoreCase("Set Start Time")||
                startDateButton.getText().toString().equalsIgnoreCase("Set Start Date")||
                endTimeButton.getText().toString().equalsIgnoreCase("Set End Time")||
                endDateButton.getText().toString().equalsIgnoreCase("Set End Date")){
                Toast.makeText(currentScreen, "Enter Start Time and Date, and End Time and Date", Toast.LENGTH_LONG).show();
                return;
            }

            String valueResult = selectRoomButton.getText().toString();
            String roomIDToQuery="";
            for (Room room : allRooms){
                if(valueResult.equals(room.getShortName())){
                    roomIDToQuery = ""+room.getRoomID();
                }
            }
            if(roomIDToQuery.equals("")){
                try {
                    throw new Exception();
                }catch(Exception e){
                    System.out.println("Error, no shortname matches a room in the list");
                    return;
                }
            }
            requestBody.put("roomID", roomIDToQuery);
            requestBody.put("ownerID", currentUser.getUserID());

            DateTimeFormatter inputFormattedStart = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            DateTimeFormatter outputFormatterStart = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            TextView startYear = currentScreen.findViewById(R.id.startYear);
            TextView startMonth= currentScreen.findViewById(R.id.startMonth);
            TextView startDay  = currentScreen.findViewById(R.id.startDay);
            TextView startHour  = currentScreen.findViewById(R.id.startHour);
            TextView startMinute  = currentScreen.findViewById(R.id.startMinute);
            String startSecond  = "00";
            LocalDateTime convertToRealTimeStart = LocalDateTime.from(inputFormattedStart.parse(startYear.getText().toString()+
                    startMonth.getText().toString()+startDay.getText().toString()+startHour.getText().toString()+
                    startMinute.getText().toString()+startSecond));
            String dateTimeFormatStart = outputFormatterStart.format(convertToRealTimeStart);
            requestBody.put("start_time",dateTimeFormatStart);

            DateTimeFormatter inputFormattedEnd = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            DateTimeFormatter outputFormatterEnd = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            TextView endYear = currentScreen.findViewById(R.id.endYear);
            TextView endMonth= currentScreen.findViewById(R.id.endMonth);
            TextView endDay  = currentScreen.findViewById(R.id.endDay);
            TextView endHour  = currentScreen.findViewById(R.id.endHour);
            TextView endMinute  = currentScreen.findViewById(R.id.endMinute);
            String endSecond  = "00";
            LocalDateTime convertToRealTimeEnd = LocalDateTime.from(inputFormattedEnd.parse(endYear.getText().toString()+
                    endMonth.getText().toString()+endDay.getText().toString()+endHour.getText().toString()+
                    endMinute.getText().toString()+endSecond));
            String dateTimeFormatEnd = outputFormatterEnd.format(convertToRealTimeEnd);
            requestBody.put("end_time", dateTimeFormatEnd);

            // Make sure reservation isn't for the past
            if(InputValidation.checkIfStartTimeIsGreaterThanOrEqualToEndTime(dateTimeFormatStart,dateTimeFormatEnd)){
                Toast.makeText(currentScreen, "Please make start date before end date.", Toast.LENGTH_LONG).show();
                return;
            }

            // Make sure reservation doesn't conflict with any of the others for that room
            if(InputValidation.checkIfReservationConflicts(new Reservation(dateTimeFormatStart,dateTimeFormatEnd),currentReservations)){
                Toast.makeText(currentScreen, "Reservation conflict with another. Choose a different time.", Toast.LENGTH_LONG).show();
                return;
            }

            System.out.println("Sent Content: "+requestBody.toString());
        }catch (JSONException ignored){ }
        AtomicBoolean successBoolean = new AtomicBoolean(false);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, makeAReservationEndpoint, requestBody,
                        response -> {
                            try{
                                boolean successToReserve = response.getBoolean("reservationSuccessful");
                                successBoolean.set(successToReserve);
                                System.out.println("Received Content: "+response.get("reservationSuccessful"));
                            }catch(Exception e){
                                successBoolean.set(false);
                            }
                            //if(successBoolean.get()){
                                Toast.makeText(currentScreen, "Reservation Successful.", Toast.LENGTH_LONG).show();
                            //}
                            //else if(!successBoolean.get()) {
                            //    Toast.makeText(currentScreen, "Reservation Unsuccessful, try another time.", Toast.LENGTH_LONG).show();
                            //}
                            // Pop-up loading screen
                            LinearLayout loadingLinearLayout = currentScreen.findViewById(R.id.reservationLoadingLinearLayout);
                            loadingLinearLayout.setVisibility(View.GONE);
                            // Button Debounce off
                            Button reserveConfirmButton = currentScreen.findViewById(R.id.reserveConfirmButton);
                            reserveConfirmButton.setEnabled(true);

                            if(!successBoolean.get()) {
                                // Clear time and date reservation buttons
                                Button startTimeButton = currentScreen.findViewById(R.id.startTimeButton);
                                TextView startHour = currentScreen.findViewById(R.id.startHour);
                                TextView startMinute = currentScreen.findViewById(R.id.startMinute);
                                startTimeButton.setText("SET START TIME");
                                startHour.setText("");
                                startMinute.setText("");
                                Button endTimeButton = currentScreen.findViewById(R.id.endTimeButton);
                                TextView endHour = currentScreen.findViewById(R.id.endHour);
                                TextView endMinute = currentScreen.findViewById(R.id.endMinute);
                                endTimeButton.setText("SET END TIME");
                                endHour.setText("");
                                endMinute.setText("");
                                Button startDateButton = currentScreen.findViewById(R.id.startDateButton);
                                TextView startDay = currentScreen.findViewById(R.id.startDay);
                                TextView startMonth = currentScreen.findViewById(R.id.startMonth);
                                TextView startYear = currentScreen.findViewById(R.id.startYear);
                                startDateButton.setText("SET START DATE");
                                startDay.setText("");
                                startMonth.setText("");
                                startYear.setText("");
                                Button endDateButton = currentScreen.findViewById(R.id.endDateButton);
                                TextView endDay = currentScreen.findViewById(R.id.endDay);
                                TextView endMonth = currentScreen.findViewById(R.id.endMonth);
                                TextView endYear = currentScreen.findViewById(R.id.endYear);
                                endDateButton.setText("SET END DATE");
                                endDay.setText("");
                                endMonth.setText("");
                                endYear.setText("");
                            }

                        }, error -> {
                    //recursive fix
                    reservationPageReserveRoom( currentScreen,  currentUser,
                            allRooms,  currentReservations);
                    System.out.println("Line 347 Error "+error.toString());
                });
        //Pop-up loading screen
        LinearLayout loadingLinearLayout = currentScreen.findViewById(R.id.reservationLoadingLinearLayout);
        loadingLinearLayout.setVisibility(View.VISIBLE);
        loadingLinearLayout.setClickable(true);
        loadingLinearLayout.setOnClickListener(listener ->{
            // Purposefully blank
        });
        // Button Debounce on
        Button reserveConfirmButton = currentScreen.findViewById(R.id.reserveConfirmButton);
        reserveConfirmButton.setEnabled(false);
        // Add the request to the queue, which will complete eventually
        APIRequestQueue.getInstance(currentScreen).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * This sends a GET request to our API in order to retrieve current reservations for a user.
     * @param currentScreen The current screen the user is on. The ViewReservationsScreen will be passed.
     * @param currentUser The currently logged in user, passed from screen to screen using putExtra.
     * @param recyclerViewAdapter The recyclerViewAdapter to fill with information.
     * @param allRooms List of all the rooms
     */
    public void viewReservationPagePopulateRecyclerView(Activity currentScreen, RegisteredUser currentUser,
                                                        RecyclerViewAdapter recyclerViewAdapter, ArrayList<Room> allRooms){
        String authenticationEndpoint = ENDPOINT+"/reservations/user/"+
                currentUser.getUserID(); // 1. Endpoint
        ArrayList<ReservationHolder> reservationHolders = new ArrayList<>();
        @SuppressLint("NotifyDataSetChanged") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, authenticationEndpoint, null,
                        response -> {
                            try{
                                JSONArray userReservations = response.getJSONArray("currentUserReservations");
                                for (int i = 0; i<userReservations.length(); i++){
                                    JSONObject reservation = userReservations.getJSONObject(i);
                                    ReservationHolder reservationHolder = new ReservationHolder();

                                    // Convert roomID to shortName
                                    String roomShortName="";
                                    String roomID = reservation.getString("roomID");
                                    for (Room room : allRooms){
                                        if(roomID.equals(Integer.toString(room.getRoomID()))){
                                            roomShortName = room.getShortName();
                                        }
                                    }
                                    if(roomShortName.equals("")){
                                        try {
                                            throw new Exception();
                                        }catch(Exception e){
                                            System.out.println("Error, no roomID matches a room in the list");
                                        }
                                    }
                                    reservationHolder.setReservationIDRoomIDUserIDStartTimeEndTime(reservation.getInt("reservationID"),
                                            roomShortName,reservation.getInt("userID"),
                                            reservation.getString("startTime"),reservation.getString("endTime"));
                                    reservationHolders.add(reservationHolder);
                                }
                                ReservationHolder[] reservationHolders1 = new ReservationHolder[reservationHolders.size()];
                                for(int i =0; i<reservationHolders1.length; i++){
                                    reservationHolders1[i] = reservationHolders.get(i);
                                }
                                System.out.println("Recieved Content: "+userReservations);
                                recyclerViewAdapter.setAllUserReservations(reservationHolders1);
                                recyclerViewAdapter.notifyDataSetChanged();

                                //Remove loading screen
                                LinearLayout loadingLinearLayout = currentScreen.findViewById(R.id.viewReservationLoadingLinearLayout);
                                loadingLinearLayout.setVisibility(View.GONE);

                            }catch(Exception e){ e.printStackTrace();}
                        }, error -> {
                    // Recursive fix
                    viewReservationPagePopulateRecyclerView( currentScreen,  currentUser, recyclerViewAdapter,  allRooms);

                    System.out.println("Error "+error.toString());
                });
        //Pop-up loading screen
        LinearLayout loadingLinearLayout = currentScreen.findViewById(R.id.viewReservationLoadingLinearLayout);
        loadingLinearLayout.setVisibility(View.VISIBLE);
        loadingLinearLayout.setClickable(true);
        loadingLinearLayout.setOnClickListener(listener ->{
            // Purposefully blank
        });

        // Add the request to the queue, which will complete eventually
        APIRequestQueue.getInstance(currentScreen).addToRequestQueue(jsonObjectRequest);
    }

    public void sendFeedback(Activity currentScreen, String currentUser, EditText feedback){
        String sendFeedbackEndpoint = ENDPOINT+"/sendFeedback"; // 1. Endpoint
        JSONObject requestBody=null;
        try {
            requestBody = new JSONObject();
            requestBody.put("user", currentUser);
            requestBody.put("feedback", feedback.getText().toString());
            System.out.println("Sent Content: "+requestBody.toString());
        }catch (JSONException ignored){ }
        AtomicBoolean successBoolean = new AtomicBoolean(false);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, sendFeedbackEndpoint, requestBody,
                        response -> {
                            try{
                                boolean successToAddAccount = response.getBoolean("creationSuccessful");
                                successBoolean.set(successToAddAccount);
                                System.out.println("Recieved Content: "+successBoolean.get());

                            }catch(Exception e){
                                successBoolean.set(false);
                            }
                            if(successBoolean.get()){
                                Toast.makeText(currentScreen, "Feedback successfully sent. Thanks!", Toast.LENGTH_LONG).show();
                            }
                            else if(!successBoolean.get()) {
                                Toast.makeText(currentScreen, "Feedback unsuccessfully sent.", Toast.LENGTH_LONG).show();
                            }
                        }, error -> {
                    Toast.makeText(currentScreen, "Feedback successfully sent. Thanks!", Toast.LENGTH_LONG).show();
                    System.out.println("Error "+error.toString());
                });

        // Add the request to the queue, which will complete eventually
        APIRequestQueue.getInstance(currentScreen).addToRequestQueue(jsonObjectRequest);
    }


}
