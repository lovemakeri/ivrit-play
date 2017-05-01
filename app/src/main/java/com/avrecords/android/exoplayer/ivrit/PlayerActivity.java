/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.avrecords.android.exoplayer.ivrit;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.gson.Gson;

public class PlayerActivity extends Activity {


//  Context mContext;
  // For use within com.google.android.exoplayer.com.avrecords.android.exoplayer.ivrit app code.
  public static final String CONTENT_ID_EXTRA = "content_id";
  public static final String CONTENT_TYPE_EXTRA = "content_type";
  public static final String PROVIDER_EXTRA = "provider";

  public static final String PARTID = "dictid";
    public int responsecode;
    public String ResponseMessage;
// specify here a path to your audio files
    public static final String path="http://mediartweb.ru/music/part";


  // For use when launching the com.google.android.exoplayer.com.avrecords.android.exoplayer.ivrit app using adb.
  private static final String CONTENT_EXT_EXTRA = "type";

  private static final String TAG = "PlayerActivity";


  private ListView ListView;

  private ListView ListView2;

  private ListView ListView3;

    private WebView tvData;

    private Button CloseButton;

    ProgressBar pb;
 //   Dialog dialog;

    int downloadedSize = 0;
    int totalSize = 0;

  public int newsongindex = 1, connection;
  public int part=1;
//  private Handler durationHandler = new Handler();

  private SharedPreferences preferences;

  private SharedPreferences preferences1;
  private SharedPreferences preferences2;
  private SharedPreferences preferences3;


  IabHelper mHelper;
// specify here your item SCUs
  static final String ITEM_SKU1 = "com.avstudio.android.exoplayer.ivrit.item1.purchased";
  static final String ITEM_SKU2 = "com.avstudio.android.exoplayer.ivrit.item2.purchased";
  static final String ITEM_SKU3 = "com.avstudio.android.exoplayer.ivrit.item3.purchased";


  private boolean isPurchase1;
  private boolean isPurchase2;
  private boolean isPurchase3;

  private static int REQUEST_CODE = 10001;

  private String songurl, answer;

  static final String base64EncodedPublicKey = "1234567890";



  private SeekBar seekBar;

  private ImageButton previewPlayBtn;


  private MusicService musicService;
  private Intent playIntent;
  private boolean musicBound = false;


    private Button butpart1;
    private Button butpart2;
    private Button butpart3;

  public static final String ifPurchased1 = "isPurchase1";
  public static final String ifPurchased2 = "isPurchase2";
  public static final String ifPurchased3 = "isPurchase3";


  private TextView currentPosition;
  private TextView totalDuration;
  private TextView SongName;

  int[] anArray1;
  int[] anArray2;
  int[] anArray3;

  public static final String MY_LIST1 = "my_list1";
  public static final String MY_LIST2 = "my_list2";
  public static final String MY_LIST3 = "my_list3";

    static final Integer WRITE_EXST = 0x3;



    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    TelephonyManager mTelephonyMgr;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.player_activity);

    seekBar = (SeekBar) findViewById(R.id.seekBar);
    currentPosition = (TextView) findViewById(R.id.currentPosition);
    totalDuration = (TextView) findViewById(R.id.totalDuration);

      mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

      mTelephonyMgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

    currentPosition = (TextView) findViewById(R.id.currentPosition);
    totalDuration = (TextView) findViewById(R.id.totalDuration);

    previewPlayBtn = (ImageButton) findViewById(R.id.previewPlayBtn);
    previewPlayBtn.setOnClickListener(togglePlayBtn);



    preferences1 = getSharedPreferences(MY_LIST1, MODE_PRIVATE);
    preferences2 = getSharedPreferences(MY_LIST2, MODE_PRIVATE);
    preferences3 = getSharedPreferences(MY_LIST3, MODE_PRIVATE);

    preferences = getSharedPreferences("inapppurchase", MODE_PRIVATE);

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " +
                            result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");

                  if (mHelper != null) {

                    try {
                      mHelper.queryInventoryAsync(mGotInventoryListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                      e.printStackTrace();
                    }
                  }

                }
                // Hooray, IAB is fully set up!
            }
        });




    part = 1;
    SongName = (TextView) findViewById(R.id.songName);

    anArray1 = new int[30];
    anArray2 = new int[30];
    anArray3 = new int[30];
    Arrays.fill(anArray1, 0);
    Arrays.fill(anArray2, 0);
    Arrays.fill(anArray3, 0);
//
    ListView = (ListView) findViewById(R.id.listView);

    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            this, R.array.names, R.layout.list_item);



    ListView.setAdapter(adapter);

    ListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


    ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> adapterView, View view,
                              int position, long id) {


        playSong(position, part);
      }

    });
  //

    ListView2 = (ListView) findViewById(R.id.listView2);


    ListView2.setAdapter(adapter);

    ListView2.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    ListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> adapterView, View view,
                              int position, long id) {


        playSong(position, part);
      }

    });
    //
    ListView3 = (ListView) findViewById(R.id.listView3);


    ListView3.setAdapter(adapter);

    ListView3.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    ListView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> adapterView, View view,
                              int position, long id) {

        ListView3.setSelection(position);
        playSong(position, part);
      }

    });


      butpart1 = (Button) findViewById(R.id.part1);
      butpart2 = (Button) findViewById(R.id.part2);
      butpart3 = (Button) findViewById(R.id.part3);


 Gson gson = new Gson();
    int counter = 0;
    if (preferences1.contains(MY_LIST1)) {
      String jsonFavorites1 = preferences1.getString(MY_LIST1, null);

      anArray1 = gson.fromJson(jsonFavorites1,
              int[].class);


      while (counter < 30) {
        if (anArray1[counter] == 1) {
          ListView.setItemChecked(counter, true);

        }
        counter++;
      }
    }
      //
    if (preferences2.contains(MY_LIST2)) {
      String jsonFavorites2 = preferences2.getString(MY_LIST2, null);

      anArray2 = gson.fromJson(jsonFavorites2,
              int[].class);

      counter = 0;
      while (counter < 30) {
        if (anArray2[counter] == 1) {
          ListView2.setItemChecked(counter, true);

        }
        counter++;
      }
    }

    if (preferences3.contains(MY_LIST3)) {
      String jsonFavorites3 = preferences3.getString(MY_LIST3, null);

      anArray3 = gson.fromJson(jsonFavorites3,
              int[].class);

       counter = 0;
      while (counter < 30) {
        if (anArray3[counter] == 1) {
          ListView3.setItemChecked(counter, true);

        }
        counter++;
      }
    }




  }

  IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

      if (mHelper != null) {

        try {
          mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
          e.printStackTrace();
        }
      }

      if (result.isFailure()) {
        if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {

          Toast.makeText(getApplicationContext(), "Успешно приобретено. Кликните на уроке еще раз для запуска.", Toast.LENGTH_LONG).show();



        }
        // Handle error

        Toast.makeText(getApplicationContext(), "Во время покупки произошла ошибка\n" + result.getMessage(), Toast.LENGTH_LONG).show();

        return;
      } else {

        Toast.makeText(getApplicationContext(), "Успешно приобретено: \n" + result, Toast.LENGTH_LONG).show();



      }


      if (purchase.getSku().equals(ITEM_SKU1)) {
        // item purchased
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(ifPurchased1, true);
        editor.apply();

        Toast.makeText(getApplicationContext(), "Успешно приобретено. Кликните на уроке еще раз для запуска.", Toast.LENGTH_LONG).show();
        //    alert("Successfully Purchased");
      } else if (purchase.getSku().equals(ITEM_SKU2)) {
        // item purchased
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(ifPurchased2, true);
        editor.apply();
    //    songName.setText("Успешно приобретено. Кликните на уроке еще раз для запуска.");
        Toast.makeText(getApplicationContext(), "Успешно приобретено. Кликните на уроке еще раз для запуска.", Toast.LENGTH_LONG).show();
        //    alert("Successfully Purchased");
      } else if (purchase.getSku().equals(ITEM_SKU3)) {
        // item purchased
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(ifPurchased3, true);
        editor.apply();

        Toast.makeText(getApplicationContext(), "Успешно приобретено. Кликните на уроке еще раз для запуска.", Toast.LENGTH_LONG).show();

      }

    }
  };


  IabHelper.QueryInventoryFinishedListener mGotInventoryListener
          = new IabHelper.QueryInventoryFinishedListener() {
    public void onQueryInventoryFinished(IabResult result,
                                         Inventory inventory) {

      if (result.isFailure()) {
        // handle error here
      }
      else {

        if (inventory.hasPurchase(ITEM_SKU1)) {
          SharedPreferences.Editor editor = preferences.edit();

          editor.putBoolean(ifPurchased1, true);
          editor.apply();

        }
        if (inventory.hasPurchase(ITEM_SKU2)) {
          SharedPreferences.Editor editor = preferences.edit();

          editor.putBoolean(ifPurchased2, true);
          editor.apply();

        }
        if (inventory.hasPurchase(ITEM_SKU3)) {
          SharedPreferences.Editor editor = preferences.edit();

          editor.putBoolean(ifPurchased3, true);
          editor.apply();

        }
      }
    }
  };


  public void playSong(final int position, final int part) {

    newsongindex = position+1;

    ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    if (null != activeNetwork) {
      if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
        answer="You are connected to a WiFi Network";
      if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
        answer = "You are connected to a Mobile Network";

      connection = 1;
    }
    else {
      answer = "Пожалуйста, проверьте интернет-соединение.";
      connection = 0;
        Toast.makeText(getApplicationContext(), answer, Toast.LENGTH_LONG).show();
    }



    if (((newsongindex>3) && (part==1)) || (part!=1)) {

      if (mHelper != null) {

        try {
          mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
          e.printStackTrace();
        }
      }

      if (part==1) {
        isPurchase1 = preferences.getBoolean("isPurchase1", false);
        if (isPurchase1==true) {

          playMusic(newsongindex, part);
        } else {
               if (mHelper != null)
            mHelper.flagEndAsync();


     /*     try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
          } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
          }
*/
            try {
            mHelper.launchPurchaseFlow(PlayerActivity.this, ITEM_SKU1,
                    REQUEST_CODE, mPurchaseFinishedListener,
                    "mypurchasetoken");
          } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
          }
        }
      } else if (part==2) {
        isPurchase2 = preferences.getBoolean("isPurchase2", false);
        if (isPurchase2==true) {

          playMusic(newsongindex, part);
        } else {
          if (mHelper != null)
            mHelper.flagEndAsync();

        /*  try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
          } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
          }
*/

          try {
            mHelper.launchPurchaseFlow(PlayerActivity.this, ITEM_SKU2,
                    REQUEST_CODE, mPurchaseFinishedListener,
                    "mypurchasetoken");
          } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
          }
        }
      } else if (part==3) {
        isPurchase3 = preferences.getBoolean("isPurchase3", false);
        if (isPurchase3==true) {

          playMusic(newsongindex, part);
        } else {
          if (mHelper != null)
            mHelper.flagEndAsync();
/*
          try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
          } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
          }
*/

          try {
            mHelper.launchPurchaseFlow(PlayerActivity.this, ITEM_SKU3,
                    REQUEST_CODE, mPurchaseFinishedListener,
                    "mypurchasetoken");
          } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
          }
        }
      }
    } else {
      playMusic(newsongindex, part);

    }

  }


  public void playMusic(final int position, final int part) {


      musicService.setSong(part, position);


    musicService.togglePlay();



  }

    public void part1click(View view) {

      if (mHelper != null) {

        try {
          mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
          e.printStackTrace();
        }
      }

        part = 1;


        butpart1 = (Button) findViewById(R.id.part1);
        butpart2 = (Button) findViewById(R.id.part2);
        butpart3 = (Button) findViewById(R.id.part3);
        butpart1.setBackgroundColor(Color.parseColor("#3300CC"));
        butpart2.setBackgroundColor(Color.parseColor("#333333"));
        butpart3.setBackgroundColor(Color.parseColor("#333333"));

     ListView.setVisibility(View.VISIBLE);
      ListView2.setVisibility(View.GONE);
      ListView3.setVisibility(View.GONE);

    }

    public void part2click(View view) {

      if (mHelper != null) {

        try {
          mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
          e.printStackTrace();
        }
      }

        part = 2;


        butpart1 = (Button) findViewById(R.id.part1);
        butpart2 = (Button) findViewById(R.id.part2);
        butpart3 = (Button) findViewById(R.id.part3);
        butpart1.setBackgroundColor(Color.parseColor("#333333"));
        butpart2.setBackgroundColor(Color.parseColor("#3300CC"));
        butpart3.setBackgroundColor(Color.parseColor("#333333"));

      ListView.setVisibility(View.GONE);
      ListView2.setVisibility(View.VISIBLE);
      ListView3.setVisibility(View.GONE);
    }

    public void part3click(View view) {

      if (mHelper != null) {

        try {
          mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
          e.printStackTrace();
        }
      }

        part = 3;


        butpart1 = (Button) findViewById(R.id.part1);
        butpart2 = (Button) findViewById(R.id.part2);
        butpart3 = (Button) findViewById(R.id.part3);
        butpart1.setBackgroundColor(Color.parseColor("#333333"));
        butpart2.setBackgroundColor(Color.parseColor("#333333"));
        butpart3.setBackgroundColor(Color.parseColor("#3300CC"));

      ListView.setVisibility(View.GONE);
      ListView2.setVisibility(View.GONE);
      ListView3.setVisibility(View.VISIBLE);



    }

  public void dictionaryclick(View view) {

    if (mHelper != null) {

      try {
        mHelper.queryInventoryAsync(mGotInventoryListener);
      } catch (IabHelper.IabAsyncInProgressException e) {
        e.printStackTrace();
      }
    }

    if (part==1) {
      isPurchase1 = preferences.getBoolean("isPurchase1", false);
      if ((isPurchase1==true)&&(checkConnection())) {

          Checkpermission();


      } else {
        if (mHelper != null)
          mHelper.flagEndAsync();


        try {
          mHelper.launchPurchaseFlow(PlayerActivity.this, ITEM_SKU1,
                  REQUEST_CODE, mPurchaseFinishedListener,
                  "mypurchasetoken");
        } catch (IabHelper.IabAsyncInProgressException e) {
          e.printStackTrace();
        }
      }
    } else if (part==2) {
      isPurchase2 = preferences.getBoolean("isPurchase2", false);
      if ((isPurchase2==true)&&(checkConnection())) {


          Checkpermission();


      } else {
        if (mHelper != null)
          mHelper.flagEndAsync();


        try {
          mHelper.launchPurchaseFlow(PlayerActivity.this, ITEM_SKU2,
                  REQUEST_CODE, mPurchaseFinishedListener,
                  "mypurchasetoken");
        } catch (IabHelper.IabAsyncInProgressException e) {
          e.printStackTrace();
        }
      }
    } else if (part==3) {
      isPurchase3 = preferences.getBoolean("isPurchase3", false);
      if ((isPurchase3==true)&&(checkConnection())) {


          Checkpermission();


      } else {
        if (mHelper != null)
          mHelper.flagEndAsync();


        try {
          mHelper.launchPurchaseFlow(PlayerActivity.this, ITEM_SKU3,
                  REQUEST_CODE, mPurchaseFinishedListener,
                  "mypurchasetoken");
        } catch (IabHelper.IabAsyncInProgressException e) {
          e.printStackTrace();
        }
      }
    }


  }

    public boolean checkConnection()

    {
        boolean connected;

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                answer="You are connected to a WiFi Network";
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                answer = "You are connected to a Mobile Network";

            connected = true;
        }
        else {
            answer = "Пожалуйста, проверьте интернет-соединение.";
            connected = false;
            Toast.makeText(getApplicationContext(), answer, Toast.LENGTH_LONG).show();
        }
        return connected;

    }

  //

public void Checkpermission() {

    if (ContextCompat.checkSelfPermission(PlayerActivity.this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(PlayerActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Дайте, пожалуйста доступ для сохранения словаря.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(PlayerActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXST);
          //  ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.RECORD_AUDIO}, 110);
        } else {
            Toast.makeText(this, "Дайте, пожалуйста доступ для сохранения словаря.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXST);

        }


    } else {

        new AsyncRequest().execute();

    }
}

  /* Checks if external storage is available for read and write */
  public boolean isExternalStorageWritable() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      return true;
    }
    return false;
  }

  //
class AsyncRequest extends AsyncTask<String, Integer, String> {

  @Override
  protected String doInBackground(String... arg) {
      String wherefile="";
    try {
      URL url = new URL("http://www.av-records.studio/part" + part + ".pdf");
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

      urlConnection.setRequestMethod("GET");
      urlConnection.setDoOutput(true);

      //connect
      urlConnection.connect();

      responsecode = urlConnection.getResponseCode();

      ResponseMessage = urlConnection.getResponseMessage();

        File directory;
        File file;

if (isExternalStorageWritable()) {
  //set the path where we want to save the file
  directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
  //create a new file, to save the downloaded file
  file = new File(directory, "part" + part + ".pdf");
    wherefile ="Sd-карта/Download/part" + part + ".pdf";

} else {

    //create new file directory object
   /* directory = new File(Environment.getDataDirectory()
            + "/RobotiumTestLog/");  */
    directory = new File(Environment.getDataDirectory()
            + "/Ivrit-Pimsler/");
            /*
             * this checks to see if there are any previous test photo files
             * if there are any photos, they are deleted for the sake of
             * memory
             */

    // if no directory exists, create new directory
    if (!directory.exists()) {
        directory.mkdir();
    }


    file = new File(directory, "part" + part + ".pdf");

    wherefile ="Data/Ivrit-Pimsler/part" + part + ".pdf";

}

      FileOutputStream fileOutput = new FileOutputStream(file);

      //Stream used for reading the data from the internet
      InputStream inputStream = urlConnection.getInputStream();

      //this is the total size of the file which we are downloading
      totalSize = urlConnection.getContentLength();

      //    showProgress();

      //    setmaxpb(totalSize);

        /*    Handler mHandler=new Handler();

            mHandler.post(new Runnable()
            {

          //  runOnUiThread(new Runnable() {
        //   runOnUiThread(new Runnable() {
          //  mHandler = new Handler(Looper.getMainLooper()) {

        //        public void run() {
           //         pb.setMax(totalSize);
                 //   pb.setMax(2827453);
                }
            });
*/
      //create a buffer...
      byte[] buffer = new byte[1024];
      int bufferLength = 0;

      while ((bufferLength = inputStream.read(buffer)) > 0) {
        fileOutput.write(buffer, 0, bufferLength);
        downloadedSize += bufferLength;



      }

      //close the output stream when complete //
      fileOutput.close();

    } catch (final MalformedURLException e) {
      showError("Error : MalformedURLException " + e);
      e.printStackTrace();
    } catch (final IOException e) {
      showError("Error : IOException " + e + ". Ответ сервера: " + responsecode + ". Сообщение сервера: " + ResponseMessage);
      e.printStackTrace();
    } catch (final Exception e) {
      showError("Error : Please check your internet connection " + e);
    }

    showError("Вы можете открыть словарь здесь: " + wherefile);
    return null;
  }


  void setmaxpb(final int totalSize) {

    runOnUiThread(new Runnable() {


      public void run() {
        pb.setMax(totalSize);
        //   pb.setMax(2827453);
      }
    });
  }

  void setprogress(final int downloadedSize, final int totalSize) {

    runOnUiThread(new Runnable() {
      public void run() {
        pb.setProgress(downloadedSize);
        float per = ((float) downloadedSize / totalSize) * 100;
     //   songName.setText("Скачано " + downloadedSize + "KB / " + totalSize + "KB (" + (int) per + "%)");
      }
    });

  }


  }

  public void openDictionaryPDF(int part) {

  //  songName = (TextView) findViewById(R.id.songName);

    try {
      URL url = new URL("http://av-records.studio/4btkmsecuj7r_dictionary/part" + part + ".pdf");
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

      urlConnection.setRequestMethod("GET");
      urlConnection.setDoOutput(true);

      //connect
      urlConnection.connect();

      //set the path where we want to save the file
      File SDCardRoot = Environment.getExternalStorageDirectory();
      //create a new file, to save the downloaded file
      File file = new File(SDCardRoot, "part" + part + ".pdf");

      FileOutputStream fileOutput = new FileOutputStream(file);

      //Stream used for reading the data from the internet
      InputStream inputStream = urlConnection.getInputStream();

      //this is the total size of the file which we are downloading
      totalSize = urlConnection.getContentLength();

      runOnUiThread(new Runnable() {
        public void run() {
          pb.setMax(totalSize);
        }
      });

      //create a buffer...
      byte[] buffer = new byte[1024];
      int bufferLength = 0;

      while ((bufferLength = inputStream.read(buffer)) > 0) {
        fileOutput.write(buffer, 0, bufferLength);
        downloadedSize += bufferLength;
        // update the progressbar //
        runOnUiThread(new Runnable() {
          public void run() {
            pb.setProgress(downloadedSize);
            float per = ((float) downloadedSize / totalSize) * 100;
       //     songName.setText("Скачано " + downloadedSize + "KB / " + totalSize + "KB (" + (int) per + "%)");
          }
        });
      }
      //close the output stream when complete //
      fileOutput.close();
      runOnUiThread(new Runnable() {
        public void run() {
          // pb.dismiss(); // if you want close it..
        }
      });

    } catch (final MalformedURLException e) {
      showError("Error : MalformedURLException " + e);
      e.printStackTrace();
    } catch (final IOException e) {
      showError("Error : IOException " + e);
      e.printStackTrace();
    } catch (final Exception e) {
      showError("Error : Please check your internet connection " + e);
    }

  }

  void showError(final String err) {
    runOnUiThread(new Runnable() {
      public void run() {
        Toast.makeText(PlayerActivity.this, err, Toast.LENGTH_LONG).show();


      }
    });
  }
// This function is used to open html file related to already opened audio file
  public void openDictionary(int part) {


    CloseButton = (Button) findViewById(R.id.closedictionary);
    CloseButton.setEnabled(true);
    CloseButton.setVisibility(View.VISIBLE);


    tvData = (WebView) findViewById(R.id.webView);
    tvData.setEnabled(true);
    tvData.getSettings().setJavaScriptEnabled(true);
    tvData.getSettings().setBuiltInZoomControls(false);
    tvData.getSettings().setAllowFileAccess(true);

    //   tvData.loadData(c.getString(3), "text/html", "UTF-8");

    //   Specify here the location of your html file
    tvData.loadUrl("http://mediartweb.ru/dictionary/part" + part + ".html#urok" + newsongindex);

    tvData.setVisibility(View.VISIBLE);
  }

  public void Closeclick(View view) {

    tvData = (WebView) findViewById(R.id.webView);
    tvData.setEnabled(false);
    tvData.setVisibility(View.INVISIBLE);

    CloseButton = (Button) findViewById(R.id.closedictionary);
    CloseButton.setEnabled(false);
    CloseButton.setVisibility(View.INVISIBLE);
  }



// Кінець даних


  // Connect to the service
  private ServiceConnection musicConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
      // Get service
      musicService = binder.getService();
      // Pass song list
   //   musicService.setSongs(songList);
      musicService.setUIControls(seekBar, currentPosition, totalDuration);
      musicBound = true;


      // Initialize interfaces
      musicService.setOnSongChangedListener(new MusicService.OnSongChangedListener() {

        ImageButton previewPlayBtn = (ImageButton) findViewById(R.id.previewPlayBtn);

        @Override
        public void onSongChanged(int song) {


          SongName.setText("Вы слушаете урок "+song+", часть "+part);


          song--;
          switch (part) {
            case 1:
              anArray1[song] = 1;
              SharedPreferences.Editor editor1 = preferences1.edit();
              editor1.remove(MY_LIST1);
              editor1.commit();
              editor1.putString(MY_LIST1, new Gson().toJson(anArray1));
              editor1.commit();
              ListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
              ListView.setItemChecked(song, true);

              break;
            case 2:
              anArray2[song] = 1;
              SharedPreferences.Editor editor2 = preferences2.edit();
              editor2.remove(MY_LIST2);
              editor2.commit();
              editor2.putString(MY_LIST2, new Gson().toJson(anArray2));
              editor2.commit();
              ListView2.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
              ListView2.setItemChecked(song, true);

              break;
            case 3:
              anArray3[song] = 1;
              SharedPreferences.Editor editor3 = preferences3.edit();
              editor3.remove(MY_LIST3);
              editor3.commit();
              editor3.putString(MY_LIST3, new Gson().toJson(anArray3));
              editor3.commit();
              ListView3.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
              ListView3.setItemChecked(song, true);

              break;
          }


        }

        @Override
        public void onPlayerStatusChanged(int status) {
          switch(status) {
            case MusicService.PLAYING:
              previewPlayBtn.setImageResource(android.R.drawable.ic_media_pause);

              break;
            case MusicService.PAUSED:
              previewPlayBtn.setImageResource(android.R.drawable.ic_media_play);

              break;
          }
        }
      });

      musicService.setSong(part, newsongindex);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      musicBound = false;
    }
  };



  // Play/pause the song on click
  private View.OnClickListener togglePlayBtn = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      musicService.togglePlay();
    }
  };


  @Override
  protected void onStart() {
    super.onStart();

    // Start service when we start the activity
    if (playIntent == null) {
      playIntent = new Intent(this, MusicService.class);
      bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
      startService(playIntent);
    }
  }


  @Override
  protected void onDestroy() {
    stopService(playIntent);
    if (musicBound) {
      unbindService(musicConnection);
    }
      mTelephonyMgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    super.onDestroy();
  }

  public MusicService getMusicService() {
    return musicService;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return super.onCreateOptionsMenu(menu);
  }

  public void forward(View view) {
    musicService.forward();


  }
  public void backforward(View view) {
    musicService.backforward();
  }



    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            // Test for incoming call, dialing call, active or on hold
            if (state==TelephonyManager.CALL_STATE_RINGING || state==TelephonyManager.CALL_STATE_OFFHOOK)
            {
                //   stop();  // Put here the code to stop your music
                musicService.pause();
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    private boolean shouldAskPermission(){

        return(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    new AsyncRequest().execute();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}




