package com.example.pharmacieapplication.DBLocal;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.pharmacieapplication.Models.Client;
import com.example.pharmacieapplication.Models.Commune;
import com.example.pharmacieapplication.Models.Notification;
import com.example.pharmacieapplication.Models.Offre;
import com.example.pharmacieapplication.Models.Wilaya;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Amine Daikha on 13/04/2019.
 */

public class DatabaseHalper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "pharmacie.db";
    public static final String USER = "user";
    public static final String USER_Col_0 = "ID";
    public static final String USER_Col_1 = "username";
    public static final String USER_Col_2 = "email";
    public static final String USER_Col_3 = "name";
    public static final String USER_Col_4 = "wilaya";
    public static final String USER_Col_5 = "commune";
    public static final String USER_Col_6 = "approved";

    public static final String WILAYA = "algeria_cities";
    public static final String WILAYA_Col1 = "ID"; // 11
    public static final String WILAYA_Col2 = "commune_name";//12
    public static final String WILAYA_Col3 = "commune_name_ascii";
    public static final String WILAYA_Col4 = "daira_name";
    public static final String WILAYA_Col5 = "daira_name_ascii";
    public static final String WILAYA_Col6 = "wilaya_code";
    public static final String WILAYA_Col7 = "wilaya_name";
    public static final String WILAYA_Col8 = "wilaya_name_ascii";

    public static final String OFFRE = "offre";
    public static final String OFFRE_Col_0 = "ID";
    public static final String OFFRE_Col_1 = "fav";

    public static final String NOTIF = "notif";
    public static final String NOTIF_Col_0 = "ID";
    public static final String NOTIF_Col_1 = "offre";
    public static final String NOTIF_Col_2 = "msg";

    Activity activity;

    public DatabaseHalper(Context context) {
        //creat database
        super(context, DATABASE_NAME, null, 28);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //creat table
        db.execSQL("create table IF NOT EXISTS " + USER + " (ID INTEGER PRIMARY KEY, username TEXT , email TEXT," +
                " name TEXT, wilaya TEXT, commune TEXT, approved TEXT)");
        db.execSQL("create table IF NOT EXISTS " + WILAYA + " (ID INTEGER  NOT NULL PRIMARY KEY\n" +
                "  ,commune_name       VARCHAR(255) NOT NULL \n" + //COMMENT 'Name of commune in arabic'
                "  ,commune_name_ascii VARCHAR(255) NOT NULL \n" + // COMMENT 'Name of commune in ASCII characters (french)'
                "  ,daira_name         VARCHAR(255) NOT NULL \n" + //COMMENT 'Name of daira in arabic'
                "  ,daira_name_ascii   VARCHAR(255) NOT NULL \n" + //COMMENT 'Name of daira in ASCII characters (french)'
                "  ,wilaya_code        VARCHAR(4) NOT NULL\n" +
                "  ,wilaya_name        VARCHAR(255) NOT NULL \n" + // COMMENT 'Name of wilaya in arabic'
                "  ,wilaya_name_ascii  VARCHAR(255) NOT NULL )"); // COMMENT 'Name of wilaya in ASCII characters (french)'
        db.execSQL("create table IF NOT EXISTS " + OFFRE + " (ID INTEGER PRIMARY KEY, fav INTEGER)");
        db.execSQL("create table IF NOT EXISTS " + NOTIF + " (ID INTEGER PRIMARY KEY, offre INTEGER, msg INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + USER);
        db.execSQL("DROP TABLE IF EXISTS " + WILAYA);
        db.execSQL("DROP TABLE IF EXISTS " + OFFRE);
        db.execSQL("DROP TABLE IF EXISTS " + NOTIF);
        onCreate(db);
    }

    public void insertAll() {
        SQLiteDatabase db = this.getWritableDatabase();
//        try {
//            File myObj = activity.getAssets().open("file");
//            //File myObj = new File("filename.txt");
//            Scanner myReader = new Scanner(myObj);
//            String data = "";
//            while (myReader.hasNextLine()) {
//                data += myReader.nextLine();
//            }
//            db.execSQL(data);
//            myReader.close();
//        } catch (FileNotFoundException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        String data = null;
        try {
            InputStream is = getActivity().getAssets().open("file.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            data = new String(buffer, "UTF-8");
            //System.out.println(data);
            String[] datas = data.split(";");
            for (String s : datas) {
                System.out.println(s);
                db.execSQL(s);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //insert user
    public boolean insertUser(Client client) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_Col_0, client.getId());
        contentValues.put(USER_Col_1, client.getUserName());
        contentValues.put(USER_Col_2, client.getEmail());
        contentValues.put(USER_Col_3, client.getName());
        contentValues.put(USER_Col_4, client.getWilaya());
        contentValues.put(USER_Col_5, client.getCommune());
        if (client.isApproved())
            contentValues.put(USER_Col_6, "1");
        else
            contentValues.put(USER_Col_6, "0");
        long result = db.insert(USER, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    //view data
    public Cursor getAllUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select ID from " + USER, null);
        return result;
    }

    public int getAllUsers() {
        ArrayList<Client> users = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + USER, null);
        while (result.moveToNext()) {
            Client client = new Client();
            client.setId(result.getString(0));
            client.setUserName(result.getString(1));
            users.add(client);
        }
        return users.size();
    }

    public Client getOneUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + USER + " limit 1", null);
        Client client = new Client();
        while (result.moveToNext()) {
            client.setId(result.getString(0));
            client.setUserName(result.getString(1));
            client.setEmail(result.getString(2));
            client.setName(result.getString(3));
            client.setWilaya(result.getString(4));
            client.setCommune(result.getString(5));
            if (result.getString(6).equals("0"))
                client.setApproved(false);
            else
                client.setApproved(true);
        }
        return client;
    }

//    public static final String USER_Col_1 = "username";
//    public static final String USER_Col_2 = "email";
//    public static final String USER_Col_3 = "name";
//    public static final String USER_Col_4 = "wilaya";
//    public static final String USER_Col_5 = "commune";
//    public static final String USER_Col_6 = "approved";

    //update data
    public boolean updateData(String id, String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_Col_1, id);
        contentValues.put(USER, text);
        db.update(USER, contentValues, "id = ?", new String[]{id});
        return true;
    }

    // delete data
    public int deleteUser(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(USER, "id =?", new String[]{id});
    }

    public Cursor getUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + USER + " where ID = '" + email + "'", null);
        return result;
    }

    public String getStringUser(String email) {
        Cursor res = getUser(email);
        StringBuffer stringBuffer = new StringBuffer();

        while (res.moveToNext()) {
            //stringBuffer.append(res.getString(0) + " ");
            stringBuffer.append(res.getString(1));
        }
        return (stringBuffer.toString());
    }

    // table company


    public ArrayList<Wilaya> getAllWilayas() {
        ArrayList<Wilaya> wilayas = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select DISTINCT wilaya_code, wilaya_name_ascii from " + WILAYA + " ORDER BY wilaya_code", null);
        while (result.moveToNext()) {
            Wilaya company = new Wilaya();
            company.setCode(result.getString(0));
            company.setName(correct(result.getString(1)));
            wilayas.add(company);
        }
        return wilayas;
    }

    public ArrayList<Wilaya> getAllWilayas(int i) {
        ArrayList<Wilaya> wilayas = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select wilaya_code, wilaya_name_ascii from " + WILAYA + " ORDER BY wilaya_code", null);
        while (result.moveToNext()) {
            Wilaya company = new Wilaya();
            company.setCode(result.getString(0));
            company.setName(correct(result.getString(1)));
            wilayas.add(company);
        }
        return wilayas;
    }

    public ArrayList<Commune> getAllCommunes(String code) {
        ArrayList<Commune> communes = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select DISTINCT wilaya_code, commune_name_ascii from " + WILAYA + " where  wilaya_name_ascii LIKE '" + code + "'" + " ORDER BY commune_name_ascii", null);
        while (result.moveToNext()) {
            Commune company = new Commune();
            company.setCode(result.getString(0));
            company.setName(correct(result.getString(1)));
            communes.add(company);
        }
        return communes;
    }

    // offre table

    public boolean insertOffre(Offre offre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(OFFRE_Col_0, offre.getOfferId());
        if (offre.isFav())
            contentValues.put(OFFRE_Col_1, 1);
        else
            contentValues.put(OFFRE_Col_1, 0);
        long result = db.insert(OFFRE, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public ArrayList<Integer> getAllOffres() {
        ArrayList<Integer> offres = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select DISTINCT ID from " + OFFRE, null);
        while (result.moveToNext()) {
            Integer id = new Integer(result.getString(0));
            offres.add(id);
        }
        return offres;
    }

    public int deleteOffre(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(OFFRE, "ID =?", new String[]{String.valueOf(id)});
    }

    public boolean insertNotif(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIF_Col_0, notification.getId());
        contentValues.put(NOTIF_Col_1, notification.getNbOffre());
        contentValues.put(NOTIF_Col_2, notification.getNbMsg());
        long result = db.insert(NOTIF, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public ArrayList<Notification> getAllNotifs() {
        ArrayList<Notification> notifications = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + NOTIF, null);
        while (result.moveToNext()) {
            Notification notif = new Notification();
            notif.setId(result.getInt(0));
            notif.setNbOffre(result.getInt(1));
            notif.setNbMsg(result.getInt(2));
            notifications.add(notif);
        }
        return notifications;
    }

    public boolean updateNotifOffre(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIF_Col_1, notification.getNbOffre());
        db.update(NOTIF, contentValues, "ID = ?", new String[]{String.valueOf(notification.getId())});
        return true;
    }

    public boolean updateNotifMessage(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIF_Col_2, notification.getNbMsg());
        db.update(NOTIF, contentValues, "ID = ?", new String[]{String.valueOf(notification.getId())});
        return true;
    }

    String correct(String incorrect) {
        return incorrect.replace("!", "'");
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
