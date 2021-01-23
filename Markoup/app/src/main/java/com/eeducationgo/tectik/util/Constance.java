package com.eeducationgo.tectik.util;

public interface Constance {

    //todo this for passenger user data
    public static String RootPassenger = "Passenger";
    public static String ChildPassengerUID = "Uid";
    public static String ChildPassengerName = "Name";
    public static String ChildPassengerImage = "Image";
    public static String ChildPassengerEmail = "Email";
    public static String ChildPassengerPhone = "Phone";
    public static String ChildPassengerType = "Type";
    public static String ChildPassengerStorageUser = "StorageUser";
    public static String ChildPassengerStation = "station";
    public static String ChildPassengerDestination = "destination";
    public static String ChildPassengerTripTime = "tripTime";
    public static String ChildPassengerTripDate = "tripDate";
    public static String ChildPassengerLate = "Late";
    public static String ChildPassengerLong = "Long";
    public static String ChildPassengerToken = "Token";

    //todo this for intent
    public  static  String userKey ="User";
    public  static  String driverKey = "Driver";

    //todo Booking Request
    public static String RootBooking = "BookingRequest";
    public static String ChildBookingSUID = "SUid";
    public static String ChildBookingRUID = "RUid";
    public static String ChildBookingRequestType = "RequestType";
    public static String ChildBookingRequestSend = "Send";
    public static String ChildBookingRequestReceiver = "Receiver";

    //todo this is booking list
    public static String RootBookingList = "BookingList";
    public static String ChildBookingListBook = "Book";
    public static String ChildBookingListSaveValue = "Save";

    //todo this for add passanger manualy
    public static String ChildBookingListUserName = "UserName";
    public static String ChildBookingListPrice = "Price";

    // TODO: 9/27/2020 this for notification keys
    String CONTENT_TYPE_HEADER = "Content-Type";
    String CONTENT_TYPE = "application/json;charset=UTF-8";
    String AUTH_HEADER = "Authorization";
    String AUTH = "key=AAAAlT1jVXw:APA91bE36-8Z9XElrMQGoEyFvLbMZEEt6NMfHq-a994hPXdBlQ_FujH20PScJu_Rlb-8W8D4Tzhmc_JpolSlccq3XaNlhmix0U-cOFP1zwORoi42_56YwMVM0QuodPSZNcdsdZVGPAoW";
    String TO_HEADER = "to";
    String TITLE_HEADER = "title";
    String BODY_HEADER = "body";
    String NOTIFICATION_OBJECT_HEADER = "notification";
    String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    String REQUEST_METHOD = "POST";
    // TODO: 9/27/2020 this for notification keys
}
