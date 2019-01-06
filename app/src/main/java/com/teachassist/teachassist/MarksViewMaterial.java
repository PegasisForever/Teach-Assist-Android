package com.teachassist.teachassist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.charts.LineChart;

import org.decimal4j.util.DoubleRounder;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;


public class MarksViewMaterial extends AppCompatActivity {
    private Context mContext;
    LinearLayout linearLayout;
    JSONObject Marks;
    String username;
    String password;
    int subject_number;
    String CourseName;
    String Mark;
    ProgressDialog dialog;
    Context context = this;
    ArrayList<View> rlList = new ArrayList<>();
    int numberOfAssignments;
    int numberOfRemovedAssignments;
    ArrayList<Integer> removedAssignmentIndexList = new ArrayList<>();
    LinkedHashMap<String, Integer> assignmentIndex = new LinkedHashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Request window feature action bar
        //requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marks_card_view);

        //progress dialog
        dialog = ProgressDialog.show(MarksViewMaterial.this, "",
                "Loading...", true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);//back button


        // Get the application context
        mContext = getApplicationContext();

        //get intents
        Intent intent = getIntent();
        username = intent.getStringExtra("username").replaceAll("\\s+", "");
        password = intent.getStringExtra("password").replaceAll("\\s+", "");
        subject_number = intent.getIntExtra("subject", 0);
        Mark = intent.getStringExtra("subject Mark");
        Crashlytics.setUserIdentifier(username);
        Crashlytics.setString("username", username);
        Crashlytics.setString("password", password);
        Crashlytics.log(Log.DEBUG, "username", username);
        Crashlytics.log(Log.DEBUG, "password", password);

        linearLayout = findViewById(R.id.LinearLayoutMarksView);


        new GetMarks().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private class GetMarks extends AsyncTask<String, Integer, JSONObject> {
        View rl;

        @Override
        protected void onPreExecute() {
            if(Mark == null){
                new AlertDialog.Builder(context)
                        .setTitle("Connection Error")
                        .setMessage("Something went Wrong while trying to reach TeachAssist. Please check your internet connection and try again.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("MainActivity", "No internet connection");
                            }
                        })
                        .show();
                dialog.dismiss();
                return;
            }
            TextView AverageInt = findViewById(R.id.AverageInt);
            AverageInt.setText(Mark+"%");
            int Average = Math.round(Float.parseFloat(Mark.replaceAll("%", "")));
            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.AverageBar);
            ProgressBarAverage.setProgress(Average-2);
        }

        @Override
        protected JSONObject doInBackground(String... temp) {
            TA ta = new TA();
            ta.GetTADataNotifications(username, password);
            List<JSONObject> returnValue = ta.newGetMarks(subject_number);
            if(returnValue == null){
                return null;
            }
            Marks = returnValue.get(0);
            try {
                CourseName = returnValue.get(1).getString("course");
            }catch (JSONException e){
                e.printStackTrace();
            }
            return Marks;


        }

        protected void onProgressUpdate(Integer... temp) {
            super.onProgressUpdate();
        }
        protected void onPostExecute(JSONObject marks){
            if(marks == null){
                return;
            }
            numberOfAssignments= marks.length()-1;
            String title;
            String feedback;
            Double Kweight = 0.0;
            Double Kmark = 0.0;
            Double Tweight;
            Double Tmark = 0.0;
            Double Cweight;
            Double Cmark = 0.0;
            Double Aweight;
            Double Amark = 0.0;
            Double Oweight = 0.0;
            Double Omark = 0.0;
            DecimalFormat round = new DecimalFormat(".#");

            for(int i = 0; i <numberOfAssignments; i++){
                Kweight = 0.0;
                Kmark = 0.000000001;
                Tweight = 0.0;
                Tmark = 0.000000001;
                Cweight = 0.0;
                Cmark = 0.000000001;
                Aweight = 0.0;
                Amark = 0.000000001;
               Oweight = 0.0;
               Omark = 0.0;
                rl = LayoutInflater.from(mContext).inflate(R.layout.marks_view_assignment, null);
                linearLayout.addView(rl);
                rlList.add(rl);
                try {
                    final JSONObject assignment = marks.getJSONObject(String.valueOf(i));
                    title = assignment.getString("title");
                    feedback = assignment.getString("feedback");

                    if(assignment.has("K")) {
                        if (assignment.getJSONObject("K").getString("weight").isEmpty()) {
                            Kweight = 0.0;
                        } else {
                            Kweight = Double.parseDouble(assignment.getJSONObject("K").getString("weight"));
                        }
                        if (assignment.getJSONObject("K").getString("outOf").equals("0") || assignment.getJSONObject("K").getString("outOf").equals("0.0")) {
                            Kweight = 0.0;
                            Kmark = 0.0;
                        }else {
                            if(!assignment.getJSONObject("K").isNull("mark") && !assignment.getJSONObject("K").isNull("outOf")) {
                                Kmark = Double.parseDouble(assignment.getJSONObject("K").getString("mark")) /
                                        Double.parseDouble(assignment.getJSONObject("K").getString("outOf"));
                                Kmark = Double.parseDouble(round.format(Kmark * 100));
                            }
                        }
                    }
                    if(assignment.has("T")) {
                        if (assignment.getJSONObject("T").getString("weight").isEmpty()) {
                            Tweight = 0.0;
                        } else {
                            Tweight = Double.parseDouble(assignment.getJSONObject("T").getString("weight"));
                        }
                        if (assignment.getJSONObject("T").getString("outOf").equals("0") || assignment.getJSONObject("T").getString("outOf").equals("0.0")) {
                            Tweight = 0.0;
                            Tmark = 0.0;
                        }else {
                            if(!assignment.getJSONObject("T").isNull("mark") && !assignment.getJSONObject("T").isNull("outOf")) {
                                Tmark = Double.parseDouble(assignment.getJSONObject("T").getString("mark")) /
                                        Double.parseDouble(assignment.getJSONObject("T").getString("outOf"));
                                Tmark = Double.parseDouble(round.format(Tmark * 100));
                            }
                        }
                    }
                    if(assignment.has("C")) {
                        if (assignment.getJSONObject("C").getString("weight").isEmpty()) {
                            Cweight = 0.0;
                        } else {
                            Cweight = Double.parseDouble(assignment.getJSONObject("C").getString("weight"));
                        }
                        if (assignment.getJSONObject("C").getString("outOf").equals("0") || assignment.getJSONObject("C").getString("outOf").equals("0.0")) {
                            Cweight = 0.0;
                            Cmark = 0.0;
                        }else {
                            if(!assignment.getJSONObject("C").isNull("mark") && !assignment.getJSONObject("C").isNull("outOf")) {
                                Cmark = Double.parseDouble(assignment.getJSONObject("C").getString("mark")) /
                                        Double.parseDouble(assignment.getJSONObject("C").getString("outOf"));
                                Cmark = Double.parseDouble(round.format(Cmark * 100));
                            }
                        }
                    }
                    if(assignment.has("A")) {
                        if (assignment.getJSONObject("A").getString("weight").isEmpty()) {
                            Aweight = 0.0;
                        } else {
                            Aweight = Double.parseDouble(assignment.getJSONObject("A").getString("weight"));
                        }
                        if (assignment.getJSONObject("A").getString("outOf").equals("0") || assignment.getJSONObject("A").getString("outOf").equals("0.0")) {
                            Aweight = 0.0;
                            Amark = 0.0;
                        }else {
                            if(!assignment.getJSONObject("A").isNull("mark") && !assignment.getJSONObject("A").isNull("outOf")) {
                                Amark = Double.parseDouble(assignment.getJSONObject("A").getString("mark")) /
                                        Double.parseDouble(assignment.getJSONObject("A").getString("outOf"));
                                Amark = Double.parseDouble(round.format(Amark * 100));
                            }
                        }
                    }

                    assignmentIndex.put(title, i);


                    final String titleOnClick = title;

                    final int index = i;
                    ImageButton trashButton = (ImageButton) rl.findViewById(R.id.trash_can);
                    trashButton.setOnClickListener(/*new onAssignmentClick(index));*/

                            new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("CLICK "+index);
                            int toSubtract = 0;
                            int len = removedAssignmentIndexList.size();
                            for(Integer i: removedAssignmentIndexList){
                                if(i < assignmentIndex.get(titleOnClick)){
                                    toSubtract++;
                                }
                            }

                            linearLayout.removeViewAt(index + 2 - toSubtract);
                            Marks.remove(String.valueOf(index));
                            removedAssignmentIndexList.add(index);
                            numberOfRemovedAssignments++;

                            String returnval = CalculateTotalAverage(Marks);
                            TextView AverageInt = findViewById(R.id.AverageInt);
                            AverageInt.setText(returnval+"%");
                            int Average = Math.round(Float.parseFloat(returnval));
                            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.AverageBar);
                            ProgressBarAverage.setProgress(Average);

                        }
                    });


                    // Setup toolbar text
                    getSupportActionBar().setTitle(CourseName);

                    //set title
                    TextView Title = rl.findViewById(R.id.title);
                    Title.setText(title);

                    //set mark
                    System.out.println(i);
                    TextView Average = rl.findViewById(R.id.AveragePercent);
                    String returnval = CalculateAverage(marks, String.valueOf(i));
                    Average.setText(returnval+"%");

                    //set bars
                    View bar1 = rl.findViewById(R.id.BarGraph1);
                    View bar2 = rl.findViewById(R.id.BarGraph2);
                    View bar3 = rl.findViewById(R.id.BarGraph3);
                    View bar4 = rl.findViewById(R.id.BarGraph4);
                    bar1.getLayoutParams().height = (int)Math.round(1.5*(Kmark))+45;
                    bar2.getLayoutParams().height = (int)Math.round(1.5*(Tmark))+45;
                    bar3.getLayoutParams().height = (int)Math.round(1.5*(Cmark))+45;
                    bar4.getLayoutParams().height = (int)Math.round(1.5*(Amark))+45;

                    //set percentage texts
                    TextView Kpercent = rl.findViewById(R.id.Kpercent);
                    TextView Tpercent = rl.findViewById(R.id.Tpercent);
                    TextView Cpercent = rl.findViewById(R.id.Cpercent);
                    TextView Apercent = rl.findViewById(R.id.Apercent);
                    TextView K = rl.findViewById(R.id.K);
                    TextView T = rl.findViewById(R.id.T);
                    TextView C = rl.findViewById(R.id.C);
                    TextView A = rl.findViewById(R.id.A);
                    if(Kmark == 100.0){
                        Kpercent.setText(String.valueOf(Math.round(Kmark)));
                    }else if(Kmark == 0.0){
                        Kpercent.setText("0.0");
                    }else if(Kmark == 0.000000001){
                        bar1.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
                        Kpercent.setTextColor(Color.WHITE);
                        Kpercent.setText("NA");
                    }else {
                        Kpercent.setText(String.valueOf(Kmark));
                    }

                    if(Tmark == 100.0){
                        Tpercent.setText(String.valueOf(Math.round(Tmark)));
                    }else if(Tmark == 0.0){
                        Tpercent.setText("0.0");
                    }else if(Tmark == 0.000000001){
                        bar2.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
                        Tpercent.setTextColor(Color.WHITE);
                        Tpercent.setText("NA");
                    }else {
                        Tpercent.setText(String.valueOf(Tmark));
                    }

                    if(Cmark == 100.0){
                        Cpercent.setText(String.valueOf(Math.round(Cmark)));
                    }else if(Cmark == 0.0){
                        Cpercent.setText("0.0");
                    }else if(Cmark == 0.000000001){
                        bar3.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
                        Cpercent.setTextColor(Color.WHITE);
                        Cpercent.setText("NA");
                    }else {
                        Cpercent.setText(String.valueOf(Cmark));
                    }

                    if(Amark == 100.0){
                        Apercent.setText(String.valueOf(Math.round(Amark)));
                    }else if(Amark == 0.0){
                        Apercent.setText("0.0");
                    }else if(Amark == 0.000000001){
                        bar4.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
                        Apercent.setTextColor(Color.WHITE);
                        Apercent.setText("NA");
                    }else {
                        Apercent.setText(String.valueOf(Amark));
                    }
                    dialog.dismiss();


                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private String CalculateAverage(JSONObject marks, String assingmentNumber){
    try {
        JSONObject weights = marks.getJSONObject("categories");
        Double weightK = weights.getDouble("K")*10;
        Double weightT = weights.getDouble("T")*10;
        Double weightC = weights.getDouble("C")*10;
        Double weightA = weights.getDouble("A")*10;
        Double Kmark = 0.0;
        Double Tmark = 0.0;
        Double Cmark = 0.0;
        Double Amark = 0.0;
        Double Omark = 0.0;
        DecimalFormat round = new DecimalFormat(".#");
        JSONObject assignment = marks.getJSONObject(assingmentNumber);

        if(assignment.has("")){
            if (!assignment.getJSONObject("").getString("outOf").equals("0") || !assignment.getJSONObject("").getString("outOf").equals("0.0")) {
                if (!assignment.getJSONObject("").isNull("mark")) {
                    Omark = Double.parseDouble(assignment.getJSONObject("").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("").getString("outOf"));
                    return round.format(Omark * 100);
                }
            }
        }

        if(assignment.has("K")) {
            if (!assignment.getJSONObject("K").getString("outOf").equals("0") || !assignment.getJSONObject("K").getString("outOf").equals("0.0")) {
                if (!assignment.getJSONObject("K").isNull("mark")) {
                    Kmark = Double.parseDouble(assignment.getJSONObject("K").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("K").getString("outOf"));
                }else{
                    weightK = 0.0;
                }
            }
        }else{
                weightK = 0.0;
            }
        if(assignment.has("T")) {
            if (!assignment.getJSONObject("T").getString("outOf").equals("0") || !assignment.getJSONObject("T").getString("outOf").equals("0.0")) {
                if (!assignment.getJSONObject("T").isNull("mark")) {
                    Tmark = Double.parseDouble(assignment.getJSONObject("T").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("T").getString("outOf"));
                }else{
                    weightT = 0.0;
                }
            }
        }else{
                weightT = 0.0;
            }
        if(assignment.has("C")) {
            if (!assignment.getJSONObject("C").getString("outOf").equals("0") || !assignment.getJSONObject("C").getString("outOf").equals("0.0")) {
                if (!assignment.getJSONObject("C").isNull("mark")) {
                    Cmark = Double.parseDouble(assignment.getJSONObject("C").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("C").getString("outOf"));
                }else{
                    weightC = 0.0;
                }
            }
        }else{
                weightC = 0.0;
            }
        if(assignment.has("A")) {
            if (!assignment.getJSONObject("A").getString("outOf").equals("0") || !assignment.getJSONObject("A").getString("outOf").equals("0.0")) {
                if (!assignment.getJSONObject("A").isNull("mark")) {
                    Amark = Double.parseDouble(assignment.getJSONObject("A").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("A").getString("outOf"));
                }else{
                    weightA = 0.0;
                }
            }
        }else{
                weightA = 0.0;
            }

        Kmark*=weightK;
        Tmark*=weightT;
        Cmark*=weightC;
        Amark*=weightA;
        String Average = round.format((Kmark+Tmark+Cmark+Amark)/(weightK+weightT+weightC+weightA)*100);
        if(Average.equals(".0")){
            Average = "0";
        }
        return Average;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
    public String CalculateTotalAverage(JSONObject marks) {
        DecimalFormat round = new DecimalFormat(".#");
        try {
            JSONObject weights = marks.getJSONObject("categories");
            Double weightK = weights.getDouble("K") * 10;
            Double weightT = weights.getDouble("T") * 10;
            Double weightC = weights.getDouble("C") * 10;
            Double weightA = weights.getDouble("A") * 10;
            Double Kmark = 0.0;
            Double Tmark = 0.0;
            Double Cmark = 0.0;
            Double Amark = 0.0;
            Double KweightAssignment = 0.0;
            Double TweightAssignment = 0.0;
            Double CweightAssignment = 0.0;
            Double AweightAssignment = 0.0;
            Double KweightAssignmentTemp;
            Double TweightAssignmentTemp;
            Double CweightAssignmentTemp;
            Double AweightAssignmentTemp;
            for (int i = 0; i < marks.length()-1+numberOfRemovedAssignments; i++) {
                JSONObject assignment;
                try {
                    assignment = marks.getJSONObject(String.valueOf(i));
                }catch (JSONException e){
                        e.printStackTrace();
                        continue;}

                try {
                    Double assignmentK = Double.parseDouble(assignment.getJSONObject("K").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("K").getString("outOf"));
                    KweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("K").getString("weight"));
                    Kmark += assignmentK * KweightAssignmentTemp;
                    KweightAssignment += KweightAssignmentTemp;
                }catch (JSONException e){}

                try {
                    Double assignmentT = Double.parseDouble(assignment.getJSONObject("T").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("T").getString("outOf"));
                    TweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("T").getString("weight"));
                    Tmark += assignmentT * TweightAssignmentTemp;
                    TweightAssignment += TweightAssignmentTemp;
                }catch (JSONException e){}

                try {
                    Double assignmentC = Double.parseDouble(assignment.getJSONObject("C").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("C").getString("outOf"));
                    CweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("C").getString("weight"));
                    Cmark += assignmentC * CweightAssignmentTemp;
                    CweightAssignment += CweightAssignmentTemp;
                }catch (JSONException e){}

                try {
                    Double assignmentA = Double.parseDouble(assignment.getJSONObject("A").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("A").getString("outOf"));
                    AweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("A").getString("weight"));
                    Amark += assignmentA * AweightAssignmentTemp;
                    AweightAssignment += AweightAssignmentTemp;
                }catch (JSONException e){}

            }
            if(KweightAssignment == 0.0){
                Kmark = 0.0;
                weightK = 0.0;
            }else {
                Kmark /= KweightAssignment;
            }

            if(TweightAssignment == 0.0){
                Tmark = 0.0;
                weightT = 0.0;
            }else {
                Tmark /= TweightAssignment;
            }

            if(CweightAssignment == 0.0){
                Cmark = 0.0;
                weightC = 0.0;
            }else {
                Cmark /= CweightAssignment;
            }

            if(AweightAssignment == 0.0){
                Amark = 0.0;
                weightA = 0.0;
            }else {
                Amark /= AweightAssignment;
            }

            Kmark *= weightK;
            Tmark *= weightT;
            Cmark *= weightC;
            Amark *= weightA;
            String Average = String.valueOf(round.format((Kmark + Tmark + Cmark + Amark) / (weightK + weightT + weightC + weightA) * 100));
            return Average;

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}
