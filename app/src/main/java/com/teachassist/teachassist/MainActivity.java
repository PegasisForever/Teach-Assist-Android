package com.teachassist.teachassist;

import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.AsyncTask;

import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class MainActivity extends AppCompatActivity {
    String username = "335525168";
    String password = "4a6349kc";
    Boolean Refresh = false;
    SwipeRefreshLayout SwipeRefresh;
    private DrawerLayout drawer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto_mono);


        // Refresh
        SwipeRefresh =  findViewById(R.id.swipeRefresh);
        SwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("REFRESH", "onRefresh called from MainActivity");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        Refresh = true;
                        String Username = username;
                        String Password = password;
                        new GetTaData().execute(Username, Password);

                    }
                }
        );

        //setup toolbar for nav bar drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Nav bar Drawer
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        /*
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });
                */



        // Setup toolbar text
        //  TextView ToolbarText =  findViewById(R.id.toolbar_title);
        //ToolbarText.setText("Student Report for: "+ username);
        getSupportActionBar().setTitle("Student Report for: "+ username);


        String Username = username;
        String Password = password;
        new GetTaData().execute(Username, Password);


    }

    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }




        private class GetTaData extends AsyncTask<String, Integer, LinkedHashMap<String, List<String>>>{


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected LinkedHashMap<String, List<String>> doInBackground(String... params){
            TA ta = new TA();
            String Username = params[0];
            String Password = params[1];

            LinkedHashMap<String, List<String>> response = ta.GetTAData(Username, Password);

            return response;

        }

        protected void onProgressUpdate(Integer... progress) {}


        protected void onPostExecute(LinkedHashMap<String, List<String>> response) {
            // Set Average Text
            TA ta = new TA();
            double average = ta.GetAverage(response);
            Float Average = (float) average;
            TextView AverageInt = findViewById(R.id.AverageInt);
            AverageInt.setText(Average.toString()+"%");
            System.out.println(response);



            // Set Subject Text
            Float Mark = 0f;
            int counter = 0;
            String SubjectAbrvString = "";
            String SubjectNameString = "";
            String RoomNumber  = "";
            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                if(counter == 0) {
                    Mark = Float.parseFloat(entry.getValue().get(0));
                    SubjectAbrvString =  entry.getValue().get(1);
                    SubjectNameString =  entry.getValue().get(2);
                    RoomNumber  = entry.getValue().get(3);
                }
                counter++;
            }
            TextView SubjectInt = findViewById(R.id.SubjectInt);
            SubjectInt.setText(Mark.toString()+"%");
            TextView SubjectAbrv = findViewById(R.id.SubjectAbrv);
            SubjectAbrv.setText(SubjectAbrvString);
            TextView SubjectName = findViewById(R.id.SubjectName);
            SubjectName.setText(SubjectNameString);


            TextView roomNumber  = findViewById(R.id.RoomNumber);
            roomNumber .setText("Room " + RoomNumber );
            //Set Subject1 Text
            Float Mark1 = 0f;
            int counter1 = 0;
            String SubjectAbrvString1 = "";
            String SubjectNameString1 = "";
            String RoomNumber1 = "";
            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                if(counter1 == 1) {
                    Mark1 = Float.parseFloat(entry.getValue().get(0));
                    SubjectAbrvString1 =  entry.getValue().get(1);
                    SubjectNameString1 =  entry.getValue().get(2);
                    RoomNumber1 = entry.getValue().get(3);
                }
                counter1++;
            }
            TextView SubjectInt1 = findViewById(R.id.SubjectInt1);
            SubjectInt1.setText(Mark1.toString()+"%");
            TextView SubjectAbrv1 = findViewById(R.id.SubjectAbrv1);
            SubjectAbrv1.setText(SubjectAbrvString1);
            TextView SubjectName1 = findViewById(R.id.SubjectName1);
            SubjectName1.setText(SubjectNameString1);
            TextView roomNumber1 = findViewById(R.id.RoomNumber1);
            roomNumber1.setText("Room " + RoomNumber1);


            //Set Subject2 Text
            Float Mark2 = 0f;
            int counter2 = 0;
            String SubjectAbrvString2 = "";
            String SubjectNameString2 = "";
            String RoomNumber2 = "";
            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                if(counter2 == 2) {
                    Mark2 = Float.parseFloat(entry.getValue().get(0));
                    SubjectAbrvString2 =  entry.getValue().get(1);
                    SubjectNameString2 =  entry.getValue().get(2);
                    RoomNumber2 = entry.getValue().get(3);
                }
                counter2++;
            }
            TextView SubjectInt2 = findViewById(R.id.SubjectInt2);
            SubjectInt2.setText(Mark2.toString()+"%");
            TextView SubjectAbrv2 = findViewById(R.id.SubjectAbrv2);
            SubjectAbrv2.setText(SubjectAbrvString2);
            TextView SubjectName2 = findViewById(R.id.SubjectName2);
            SubjectName2.setText(SubjectNameString2);
            TextView roomNumber2 = findViewById(R.id.RoomNumber2);
            roomNumber2.setText("Room " + RoomNumber2);


            //Set Subject3 Text
            Float Mark3 = 0f;
            int counter3 = 0;
            String SubjectAbrvString3 = "";
            String SubjectNameString3 = "";
            String RoomNumber3 = "";
            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                if(counter3 == 3) {
                    Mark3 = Float.parseFloat(entry.getValue().get(0));
                    SubjectAbrvString3 =  entry.getValue().get(1);
                    SubjectNameString3 =  entry.getValue().get(2);
                    RoomNumber3 = entry.getValue().get(3);
                }
                counter3++;
            }
            TextView SubjectInt3 = findViewById(R.id.SubjectInt3);
            SubjectInt3.setText(Mark3.toString()+"%");
            TextView SubjectAbrv3 = findViewById(R.id.SubjectAbrv3);
            SubjectAbrv3.setText(SubjectAbrvString3);
            TextView SubjectName3 = findViewById(R.id.SubjectName3);
            SubjectName3.setText(SubjectNameString3);
            TextView roomNumber3 = findViewById(R.id.RoomNumber3);
            roomNumber3.setText("Room " + RoomNumber3);



            RunTasks(response);

        }

        private void RunTasks(LinkedHashMap<String, List<String>> response){

                new Average().execute(response);
                new MainActivity.Subject().execute(response);
                new Subject1().execute(response);
                new Subject2().execute(response);
                new Subject3().execute(response);
                if(Refresh.equals(true)) {
                    SwipeRefresh.setRefreshing(false);
                    Refresh = false;
                }

            /*
            MainActivity.Subject subject = new MainActivity.Subject();
                subject.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response);


                Subject1 subject1 = new Subject1();
                subject1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response);


                Subject2 subject2 = new Subject2();
                subject2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response);


                Subject3 subject3 = new Subject3();
                subject3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response);
                */




        }

    }
    //---------------------------------------------------------------------------------------------------------------------------------------
    private class Average extends AsyncTask<HashMap<String, List<String>>, Integer, Float>{
        @Override
        protected void onPreExecute(){


        }

        @Override
        protected Float doInBackground(HashMap<String, List<String>>... response){
            TA ta = new TA();
            double average = ta.GetAverage(response[0]);
            Float Average = (float) average;


            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.AverageBar);
                for (int i = 0; i < Math.round(Average); i+=4) {
                    publishProgress (i);
                    Thread.sleep(0, 50);


                }
                ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                    @Override
                    public void progressToComplete() {
                        // Progress reaches the maximum callback default Max value is 100
                        Toast.makeText(MainActivity.this, "100", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            return Average;

        }

        protected void onProgressUpdate(Integer... progress) {
            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.AverageBar);
            ProgressBarAverage.setProgress(progress[0]);

            }
        @Override
        protected void onPostExecute(Float Average) {


        }

    }
    //---------------------------------------------------------------------------------------------------------------------------------------
    private class Subject extends AsyncTask<HashMap<String, List<String>>, Integer, Float> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Float doInBackground(HashMap<String, List<String>>... response){
            TA ta = new TA();
            Float Mark = 0f;
            for (Map.Entry<String, List<String>> entry : response[0].entrySet()) {
                Mark =  Float.parseFloat(entry.getValue().get(0));

            }

            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar);
                for (int i = 0; i < Math.round(Mark); i+=4) {
                    publishProgress (i);
                    Thread.sleep(0, 50);


                }
                ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                    @Override
                    public void progressToComplete() {
                        // Progress reaches the maximum callback default Max value is 100
                        Toast.makeText(MainActivity.this, "100", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            return Mark;

        }

        protected void onProgressUpdate(Integer... progress) {
            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar);
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {


        }
    }
    //---------------------------------------------------------------------------------------------------------------------------------------

    private class Subject1 extends AsyncTask<HashMap<String, List<String>>, Integer, Float> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Float doInBackground(HashMap<String, List<String>>... response){
            TA ta = new TA();

            Float Mark = 0f;
            int counter = 0;
            for (Map.Entry<String, List<String>> entry : response[0].entrySet()) {
                if(counter == 1) {
                    Mark = Float.parseFloat(entry.getValue().get(0));

                }
                counter++;
            }

            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar1);
                for (int i = 0; i < Math.round(Mark); i+=4) {
                    publishProgress (i);
                    Thread.sleep(0, 50);


                }
                ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                    @Override
                    public void progressToComplete() {
                        // Progress reaches the maximum callback default Max value is 100
                        Toast.makeText(MainActivity.this, "100", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            return Mark;

        }

        protected void onProgressUpdate(Integer... progress) {
            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar1);
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {


        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------

    private class Subject2 extends AsyncTask<HashMap<String, List<String>>, Integer, Float> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Float doInBackground(HashMap<String, List<String>>... response){
            TA ta = new TA();

            Float Mark = 0f;
            int counter = 0;
            for (Map.Entry<String, List<String>> entry : response[0].entrySet()) {
                if(counter == 2) {
                    Mark = Float.parseFloat(entry.getValue().get(0));

                }
                counter++;
            }

            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar2);
                for (int i = 0; i < Math.round(Mark); i+=4) {
                    publishProgress (i);
                    Thread.sleep(0, 50);


                }
                ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                    @Override
                    public void progressToComplete() {
                        // Progress reaches the maximum callback default Max value is 100
                        Toast.makeText(MainActivity.this, "100", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            return Mark;

        }

        protected void onProgressUpdate(Integer... progress) {
            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar2);
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {


        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------

    private class Subject3 extends AsyncTask<HashMap<String, List<String>>, Integer, Float> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Float doInBackground(HashMap<String, List<String>>... response){
            TA ta = new TA();

            Float Mark = 0f;
            int counter = 0;
            for (Map.Entry<String, List<String>> entry : response[0].entrySet()) {
                if(counter == 3) {
                    Mark = Float.parseFloat(entry.getValue().get(0));
                }
                counter++;
            }

            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar3);
                for (int i = 0; i < Math.round(Mark); i+=4) {
                    publishProgress (i);
                    Thread.sleep(0, 50);


                }
                ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                    @Override
                    public void progressToComplete() {
                        // Progress reaches the maximum callback default Max value is 100
                        Toast.makeText(MainActivity.this, "100", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            return Mark;

        }

        protected void onProgressUpdate(Integer... progress) {
            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar3);
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {


        }
    }


}

