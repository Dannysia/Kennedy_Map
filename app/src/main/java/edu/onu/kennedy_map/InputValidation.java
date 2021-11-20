package edu.onu.kennedy_map;

import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains static methods which are used for input validation around our app
 */
public class InputValidation {

    /**
     * Basic email verification with regex. Didn't make it too fancy
     * @param email The email to be tested
     * @return True if the email is valid, false otherwise
     */
    public static boolean checkIfValidEmail(String email){
        //TODO make sure this works
        Pattern emailCheck = Pattern.compile("^[^\\s@]+@(?:[^\\s@.,]+\\.)+[^\\s@.,]{2,}$");
        Matcher emailCheckMatcher = emailCheck.matcher(email);
        return emailCheckMatcher.find();
    }

    /**
     * Checks if one or more TextViews are blank
     * @param textViews Takes a variable number of TextViews and checks if they are blank
     * @return True if one or more are blank, false otherwise
     */
    public static boolean checkIfTextViewsBlank(TextView... textViews){
        if(textViews.length>0)
            for (TextView textView : textViews){
                if(textView.getText().toString().equals("")){
                    return true;
                }
            }
        return false;
    }

    /**
     * This function checks if the start reservation date and time is after the end reservation date and time, which wouldn't make sense.
     * @param startDateTime The start date and time, in the format: yyyy-MM-dd HH:mm:ss
     * @param endDateTime The end date and time, in the format: yyyy-MM-dd HH:mm:ss
     * @return True if the start date and time is greater than or equal to the end date and time, false otherwise.
     */
    public static boolean checkIfStartTimeIsGreaterThanOrEqualToEndTime(String startDateTime, String endDateTime){
        DateTimeFormatter startDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter endDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startLocalDateTime = LocalDateTime.from(startDateTimeFormatter.parse(startDateTime));
        LocalDateTime endLocalDateTime = LocalDateTime.from(endDateTimeFormatter.parse(endDateTime));
        return startLocalDateTime.isAfter(endLocalDateTime) || startLocalDateTime.isEqual(endLocalDateTime);
    }

    /**
     * Check if a reservation conflicts with any existing reservations
     * @param attemptedReservation The reservation to be attempted
     * @param currentReservations The list of reservations that will be checked
     * @return True if a reservation conflict will occur, false otherwise
     */
    public static boolean checkIfReservationConflicts(Reservation attemptedReservation, ArrayList<Reservation> currentReservations){
        DateTimeFormatter startDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter endDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime attemptedReservationStartDateTime = LocalDateTime.from(startDateTimeFormatter.parse(attemptedReservation.getStartDateTime()));
        LocalDateTime attemptedReservationEndDateTime = LocalDateTime.from(endDateTimeFormatter.parse(attemptedReservation.getEndDateTime()));
        for (Reservation reservation : currentReservations){
            LocalDateTime alreadyReservedReservationStartDateTime = LocalDateTime.from(startDateTimeFormatter.parse
                    (reservation.getStartDateTime()));
            LocalDateTime alreadyReservedReservationEndDateTime = LocalDateTime.from(endDateTimeFormatter.parse
                    (reservation.getEndDateTime()));

            // Attempted Reservation:                 |       |       OR     |        |
            //  Current Reservations:  |           |                                     |           |
            if(attemptedReservationStartDateTime.isBefore(alreadyReservedReservationStartDateTime)
                &&attemptedReservationEndDateTime.isBefore(alreadyReservedReservationEndDateTime)&&
                alreadyReservedReservationStartDateTime.isAfter(attemptedReservationStartDateTime)&&
                alreadyReservedReservationEndDateTime.isAfter(attemptedReservationEndDateTime)||
                    attemptedReservationStartDateTime.isAfter(alreadyReservedReservationStartDateTime)
                    &&attemptedReservationEndDateTime.isAfter(alreadyReservedReservationEndDateTime)&&
                    alreadyReservedReservationStartDateTime.isBefore(attemptedReservationStartDateTime)&&
                    alreadyReservedReservationEndDateTime.isBefore(attemptedReservationEndDateTime)) {

                return false;

                // Attempted Reservation:  |~~~|   |~~~|  OR  |       |       OR     |        |
                //  Current Reservations:  |           |      |           |       |           |
            }else if((attemptedReservationStartDateTime.isAfter(alreadyReservedReservationStartDateTime)||
                    attemptedReservationStartDateTime.isEqual(alreadyReservedReservationStartDateTime))&&
                    (attemptedReservationEndDateTime.isBefore(alreadyReservedReservationEndDateTime)||
                    attemptedReservationEndDateTime.isEqual(alreadyReservedReservationEndDateTime))&&
                    (alreadyReservedReservationStartDateTime.isBefore(attemptedReservationStartDateTime)||
                    alreadyReservedReservationStartDateTime.isEqual(attemptedReservationStartDateTime))&&
                    (alreadyReservedReservationEndDateTime.isAfter(attemptedReservationEndDateTime))||
                    alreadyReservedReservationEndDateTime.isEqual(alreadyReservedReservationEndDateTime)) {
                return true;



            // Attempted Reservation:  |           |  OR  |           |  OR  |           |
            //  Current Reservations:  |~~~|   |~~~|  OR  |       |      OR     |        |
            }else if((attemptedReservationStartDateTime.isBefore(alreadyReservedReservationStartDateTime)||
                attemptedReservationStartDateTime.isEqual(alreadyReservedReservationStartDateTime))&&
                (attemptedReservationEndDateTime.isAfter(alreadyReservedReservationEndDateTime)||
                attemptedReservationEndDateTime.isEqual(alreadyReservedReservationEndDateTime))&&
                (alreadyReservedReservationStartDateTime.isAfter(attemptedReservationStartDateTime)||
                alreadyReservedReservationStartDateTime.isEqual(attemptedReservationStartDateTime))&&
                (alreadyReservedReservationEndDateTime.isBefore(attemptedReservationEndDateTime))||
                alreadyReservedReservationEndDateTime.isEqual(attemptedReservationEndDateTime)){
                return true;

            // Attempted Reservation:  |       |
            //  Current Reservations:    |       |
            }else if(attemptedReservationStartDateTime.isBefore(alreadyReservedReservationStartDateTime)
                &&attemptedReservationEndDateTime.isBefore(alreadyReservedReservationEndDateTime)&&
                alreadyReservedReservationStartDateTime.isAfter(attemptedReservationStartDateTime)&&
                alreadyReservedReservationEndDateTime.isAfter(attemptedReservationEndDateTime)&&
                alreadyReservedReservationStartDateTime.isBefore(attemptedReservationEndDateTime)){
                return true;


            // Attempted Reservation:    |       |
            //  Current Reservations:  |       |
            }else if(attemptedReservationStartDateTime.isAfter(alreadyReservedReservationStartDateTime)
                    &&attemptedReservationEndDateTime.isAfter(alreadyReservedReservationEndDateTime)&&
                    alreadyReservedReservationStartDateTime.isBefore(attemptedReservationStartDateTime)&&
                    alreadyReservedReservationEndDateTime.isBefore(attemptedReservationEndDateTime)&&
                    alreadyReservedReservationEndDateTime.isAfter(attemptedReservationStartDateTime)){
                return true;
            }

        }
        return false;
    }

    /**
     * Checks if the path finding algorithm would try to calculate the path from a room to itself.
     * @param startRoom The starting room
     * @param endRoom The ending room
     * @return True if the rooms are the same, false otherwise
     */
    public static boolean checkIfStartRoomIsEndRoom(Room startRoom,Room endRoom){
        if(startRoom.getRoomID()==endRoom.getRoomID()){
            return true;
        }
        return false;
    }

    /**
     * Return a trimmed string with harmful characters removed. Special characters that are left may be in emails or names
     * @param input The string to be processed
     * @return The processed string
     */
    private static String removeHarmfulCharactersAndTrim(String input){
        String modifiedInput = input;
        modifiedInput = modifiedInput.trim();
        modifiedInput = modifiedInput.replaceAll("[^a-zA-Z0-9@.&_'-]","");
        return modifiedInput;
    }

}
