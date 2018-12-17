package com.teachassist.teachassist;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.components.LimitLine;

import org.decimal4j.util.DoubleRounder;

import java.io.IOException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.teachassist.teachassist.LaunchActivity.CREDENTIALS;
import static com.teachassist.teachassist.LaunchActivity.PASSWORD;
import static com.teachassist.teachassist.LaunchActivity.USERNAME;


public class TA{
    String student_id;
    String session_token;
    ArrayList<String> subjects = new ArrayList<>();
    LinkedHashMap<String, List<String>> Marks;
    String[] marksResponse;








    /*
    public String Get_session_token_and_student_ID(String Username, String Password) {
        try {
        //get sesison token and studentID
        String url = "https://ta.yrdsb.ca/live/index.php?";
        String path = "/live/index.php?";
        HashMap<String, String> headers = new HashMap<>();
        HashMap<String, String> parameters = new HashMap<>();
        HashMap<String, String> cookies = new HashMap<>();
        parameters.put("username", Username);
        parameters.put("password", Password);
        parameters.put("subject_id", "0");

        //get response
        SendRequest sr = new SendRequest();
        String[] response = sr.send(url, headers, parameters, cookies, path);


        String session_token = response[1].split("=")[1].split(";")[0];
        String student_id = response[2].split("=")[1].split(";")[0];
        return  Login(session_token, student_id, Username, Password);




        }
        catch(IOException e) {
            e.printStackTrace();
            //String[] returnString = {"ERROR! Check in SendRequest"};
            return "ERROR! Check in SendRequest";
        }



        }

    public String Login(String session_token, String student_id, String Username, String Password){

        //add headers parameters and cookies
        String url = "https://ta.yrdsb.ca/live/index.php?";
        String path = "/live/index.php?";
        HashMap<String, String> headers = new HashMap<>();
        HashMap<String, String> parameters = new HashMap<>();
        HashMap<String, String> cookies = new HashMap<>();
        parameters.put("username", Username);
        parameters.put("password", Password);
        parameters.put("subject_id", "0");
        cookies.put("session_token", session_token);
        cookies.put("student_id", student_id);
        parameters.put("student_id ", student_id);


        SendRequest sr = new SendRequest();
        try {
            String[] response = sr.send(url, headers, parameters, cookies, path);
            return response.toString();
        }
        catch(IOException e) {
            e.printStackTrace();

            return "ERROR! Check in SendRequest";
        }

    }
    */
    public LinkedHashMap<String, List<String>> GetTAData(String Username, String Password){
        try {
            Crashlytics.log(Log.DEBUG, "username", Username);
            Crashlytics.log(Log.DEBUG, "password", Password);
            //get sesison token and studentID
            String url = "https://ta.yrdsb.ca/live/index.php?";
            String path = "/live/index.php?";
            HashMap<String, String> headers = new HashMap<>();
            HashMap<String, String> parameters = new HashMap<>();
            HashMap<String, String> cookies = new HashMap<>();
            parameters.put("username", Username);
            parameters.put("password", Password);
            parameters.put("subject_id", "0");

            //get response
            SendRequest sr = new SendRequest();
            String[] response = sr.send(url, headers, parameters, cookies, path);

            try {

            session_token = response[1].split("=")[1].split(";")[0];
            student_id = response[2].split("=")[1].split(";")[0];
            cookies.put("session_token", session_token);
            cookies.put("student_id", student_id);
            parameters.put("student_id ", student_id);

            String[] resp = sr.send(url, headers, parameters, cookies, path);

            Marks = new LinkedHashMap();

            int courseNum = 0;
            int numberOfEmptyCourses = 0;
            for(String i :resp[0].split("<td>")){
                if(i.contains("current mark =  ")){
                    String Subject_id = i.split("subject_id=")[1].split("&")[0].trim();
                    String Current_mark = i.split("current mark =  ")[1].split("%")[0].trim();
                    String Course_Name = i.split(":")[0].trim();
                    String Course_code = i.split(":")[1].split("<br>")[0].trim();
                    String Room_Number = i.split("rm. ")[1].split("</td>")[0].trim();
                    List Stats = new ArrayList<>();
                    Stats.add(Current_mark);
                    Stats.add(Course_Name);
                    Stats.add(Course_code);
                    Stats.add(Room_Number);
                    courseNum++;

                    subjects.add(Subject_id);
                    Marks.put(Subject_id, Stats);
                }
                else if(i.contains("Please see teacher for current status regarding achievement in the course")){
                    System.out.println("Please see teacher for current status regarding achievement in the course");
                    ArrayList<String> Stats = new ArrayList<>();
                    String Course_Name = i.split(":")[0].trim();
                    String Course_code = i.split(":")[1].split("<br>")[0].trim();
                    String Room_Number = i.split("rm. ")[1].split("</td>")[0].trim();
                    Stats.add(Course_Name);
                    Stats.add(Course_code);
                    Stats.add(Room_Number);

                    courseNum++;
                    numberOfEmptyCourses++;
                    subjects.add("0");
                    Marks.put("NA"+numberOfEmptyCourses, Stats);
                }

                else if(i.contains("Click Here")){
                    String Subject_id = i.split("subject_id=")[1].split("&")[0].trim();
                    subjects.add(Subject_id);
                    LinkedHashMap<String,List<Map<String,List<String>>>> marks = GetMarks(courseNum);


                    String Current_mark = ParseAverageFromMarksView(marks).toString();
                    String Course_Name = i.split(":")[0].trim();
                    String Course_code = i.split(":")[1].split("<br>")[0].trim();
                    String Room_Number = i.split("rm. ")[1].split("</td>")[0].trim();
                    List Stats = new ArrayList<>();
                    Stats.add(Current_mark);
                    Stats.add(Course_Name);
                    Stats.add(Course_code);
                    Stats.add(Room_Number);
                    courseNum++;

                    Marks.put(Subject_id, Stats);
                }

            }
            return Marks;

            }
            catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
                LinkedHashMap<String, List<String>> returnMap = new LinkedHashMap<>();
                System.out.println("HERE");
                return returnMap;
            }




        }
        catch(IOException e) {
            e.printStackTrace();
            //String[] returnString = {"ERROR! Check in SendRequest"};
            LinkedHashMap<String, List<String>> returnMap = new LinkedHashMap<>();
            return returnMap;
        }

    }


    public Double GetAverage(HashMap<String, List<String>> Marks){

        //Get average
        double Average = 0;
        int x = 0;
        for (Map.Entry<String, List<String>> entry : Marks.entrySet()) {
            if (!entry.getKey().contains("NA")) {
                x++;
            }
        }
        double[] grades = new double[x];
        int i = 0;
        for (Map.Entry<String, List<String>> entry : Marks.entrySet()) {
            if (!entry.getKey().contains("NA")) {
                grades[i] = Double.parseDouble(entry.getValue().get(0));
                i++;
            }


        }
        for (double value:grades)
            Average += value;
        Average = DoubleRounder.round(Average/grades.length, 1);

        return Average;

    }

    public String GetCourse(int subject_number){
        String subject_id = subjects.get(subject_number);
        String course = Marks.get(subject_id).get(1);
        return course;
    }

    public LinkedHashMap<String,List<Map<String,List<String>>>> GetMarks(int subject_number){
        try {
            if(subject_number >=0) {
                String url = "https://ta.yrdsb.ca/live/students/viewReport.php?";
                String path = "/live/students/viewReport.php?";
                LinkedHashMap<String, String> headers = new LinkedHashMap<>();
                LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
                LinkedHashMap<String, String> cookies = new LinkedHashMap<>();
                parameters.put("subject_id", subjects.get(subject_number));
                parameters.put("student_id", student_id);
                cookies.put("session_token", session_token);
                cookies.put("student_id", student_id);

                Map<String, String> colors = new HashMap<>();
                colors.put("knowledge", "ffffaa");
                colors.put("thinking", "c0fea4");
                colors.put("communication", "afafff");
                colors.put("application", "ffd490");
                colors.put("other", "#dedede");

                //get response
                SendRequest sr = new SendRequest();
                marksResponse = sr.send(url, headers, parameters, cookies, path);

                LinkedHashMap<String, List<Map<String, List<String>>>> marks = new LinkedHashMap<>();

                for (String i : marksResponse[0].split("rowspan")) {
                    ArrayList<Map<String, List<String>>> stats = new ArrayList<>();
                    if (i.charAt(0) == '=') {
                        String assignment = i.split(">")[1].split("<")[0].trim().replaceAll("&eacute;", "é").replaceAll("&#039;", "'");
                        ArrayList knowledge = new ArrayList<>();
                        ArrayList thinking = new ArrayList<>();
                        ArrayList communication = new ArrayList<>();
                        ArrayList application = new ArrayList<>();
                        ArrayList other = new ArrayList<>();

                        try {
                            String weight;
                            String field;
                            Map<String, List<String>> mark = new HashMap<>();

                            if (i.split("colspan=")[0].split("bgcolor=\"" + colors.get("knowledge"))[1].split("</td>")[0].contains("border")) {
                                if (i.contains("<font color=\"red\">")) {
                                    field = i.split("bgcolor=\"" + colors.get("knowledge"))[2].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    weight = i.split("bgcolor=\"" + colors.get("knowledge"))[2].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                } else {
                                    field = i.split("bgcolor=\"" + colors.get("knowledge"))[2].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    weight = i.split("bgcolor=\"" + colors.get("knowledge"))[2].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                }
                            } else {
                                field = "";
                                weight = "";
                            }

                            knowledge.add(field);
                            knowledge.add(weight);

                            mark.put("knowledge", knowledge);
                            stats.add(mark);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            String weight;
                            String field;

                            try {
                                Map<String, List<String>> mark = new HashMap<>();

                                if (i.split("colspan=")[0].split("bgcolor=\"" + colors.get("knowledge"))[1].split("</td>")[0].contains("border")) {
                                    if (i.contains("<font color=\"red\">")) {
                                        field = i.split("bgcolor=\"" + colors.get("knowledge"))[1].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                        weight = i.split("bgcolor=\"" + colors.get("knowledge"))[1].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    } else {
                                        field = i.split("bgcolor=\"" + colors.get("knowledge"))[1].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                        weight = i.split("bgcolor=\"" + colors.get("knowledge"))[1].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    }
                                } else {
                                    field = "";
                                    weight = "";
                                }


                                knowledge.add(field);
                                knowledge.add(weight);

                                mark.put("knowledge", knowledge);
                                stats.add(mark);

                            } catch (ArrayIndexOutOfBoundsException e1) {

                            }
                        }
                        try {
                            String weight;
                            String field;
                            Map<String, List<String>> mark = new HashMap<>();

                            if (i.split("colspan=")[0].split("bgcolor=\"" + colors.get("thinking"))[1].split("</td>")[0].contains("border")) {
                                if (i.contains("<font color=\"red\">")) {
                                    field = i.split("bgcolor=\"" + colors.get("thinking"))[2].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    weight = i.split("bgcolor=\"" + colors.get("thinking"))[2].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                } else {
                                    field = i.split("bgcolor=\"" + colors.get("thinking"))[2].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    weight = i.split("bgcolor=\"" + colors.get("thinking"))[2].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                }
                            } else {
                                field = "";
                                weight = "";
                            }

                            thinking.add(field);
                            thinking.add(weight);

                            mark.put("thinking", thinking);
                            stats.add(mark);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            String weight;
                            String field;
                            try {
                                Map<String, List<String>> mark = new HashMap<>();
                                if (i.split("colspan=")[0].split("bgcolor=\"" + colors.get("thinking"))[1].split("</td>")[0].contains("border")) {
                                    if (i.contains("<font color=\"red\">")) {
                                        field = i.split("bgcolor=\"" + colors.get("thinking"))[1].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                        weight = i.split("bgcolor=\"" + colors.get("thinking"))[1].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    } else {
                                        field = i.split("bgcolor=\"" + colors.get("thinking"))[1].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                        weight = i.split("bgcolor=\"" + colors.get("thinking"))[1].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    }
                                } else {
                                    field = "";
                                    weight = "";
                                }

                                thinking.add(field);
                                thinking.add(weight);

                                mark.put("thinking", thinking);
                                stats.add(mark);

                            } catch (ArrayIndexOutOfBoundsException e1) {

                            }

                        }
                        try {
                            String weight;
                            String field;
                            Map<String, List<String>> mark = new HashMap<>();
                            if (i.split("colspan=")[0].split("bgcolor=\"" + colors.get("communication"))[1].split("</td>")[0].contains("border")) {
                                if (i.contains("<font color=\"red\">")) {
                                    field = i.split("bgcolor=\"" + colors.get("communication"))[2].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    weight = i.split("bgcolor=\"" + colors.get("communication"))[2].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                } else {
                                    field = i.split("bgcolor=\"" + colors.get("communication"))[2].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    weight = i.split("bgcolor=\"" + colors.get("communication"))[2].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                }
                            } else {
                                field = "";
                                weight = "";
                            }

                            communication.add(field);
                            communication.add(weight);

                            mark.put("communication", communication);
                            stats.add(mark);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            String weight;
                            String field;
                            try {
                                Map<String, List<String>> mark = new HashMap<>();
                                if (i.split("colspan=")[0].split("bgcolor=\"" + colors.get("communication"))[1].split("</td>")[0].contains("border")) {
                                    if (i.contains("<font color=\"red\">")) {
                                        field = i.split("bgcolor=\"" + colors.get("communication"))[1].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                        weight = i.split("bgcolor=\"" + colors.get("communication"))[1].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    } else {
                                        field = i.split("bgcolor=\"" + colors.get("communication"))[1].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                        weight = i.split("bgcolor=\"" + colors.get("communication"))[1].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    }
                                } else {
                                    field = "";
                                    weight = "";
                                }

                                communication.add(field);
                                communication.add(weight);

                                mark.put("communication", communication);
                                stats.add(mark);
                            } catch (ArrayIndexOutOfBoundsException e1) {

                            }
                        }


                        try {
                            //List Crash = new ArrayList();
                            //Crash.get(1);
                            String weight;
                            String field;
                            Map<String, List<String>> mark = new HashMap<>();
                            if (i.split("colspan=")[0].split("bgcolor=\"" + colors.get("application"))[1].split("</td>")[0].contains("border")) {
                                if (i.contains("<font color=\"red\">")) {
                                    field = i.split("bgcolor=\"" + colors.get("application"))[2].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    weight = i.split("bgcolor=\"" + colors.get("application"))[2].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                } else {
                                    field = i.split("bgcolor=\"" + colors.get("application"))[2].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    weight = i.split("bgcolor=\"" + colors.get("application"))[2].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                }
                            } else {
                                field = "";
                                weight = "";
                            }

                            application.add(field);
                            application.add(weight);

                            mark.put("application", application);
                            stats.add(mark);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            String weight;
                            String field;
                            try {
                                Map<String, List<String>> mark = new HashMap<>();
                                if (i.split("colspan=")[0].split("bgcolor=\"" + colors.get("application"))[1].split("</td>")[0].contains("border")) {
                                    if (i.contains("<font color=\"red\">")) {
                                        field = i.split("bgcolor=\"" + colors.get("application"))[1].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                        weight = i.split("bgcolor=\"" + colors.get("application"))[1].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    } else {
                                        field = i.split("bgcolor=\"" + colors.get("application"))[1].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                        weight = i.split("bgcolor=\"" + colors.get("application"))[1].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    }
                                } else {
                                    field = "";
                                    weight = "";
                                }

                                application.add(field);
                                application.add(weight);

                                mark.put("application", application);
                                stats.add(mark);

                            } catch (ArrayIndexOutOfBoundsException e1) {

                            }
                        }
                        try {
                            String weight;
                            String field;
                            Map<String, List<String>> mark = new HashMap<>();
                            if (i.split("colspan=")[0].split("bgcolor=\"" + colors.get("other"))[1].split("</td>")[0].contains("border")) {
                                if (i.contains("<font color=\"red\">")) {
                                    field = i.split("bgcolor=\"" + colors.get("other"))[2].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    weight = i.split("bgcolor=\"" + colors.get("other"))[2].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                } else {
                                    field = i.split("bgcolor=\"" + colors.get("other"))[2].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    weight = i.split("bgcolor=\"" + colors.get("other"))[2].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                }
                            } else {
                                field = "";
                                weight = "";
                            }

                            other.add(field);
                            other.add(weight);

                            mark.put("other", other);
                            stats.add(mark);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            String weight;
                            String field;
                            try {
                                Map<String, List<String>> mark = new HashMap<>();
                                if (i.split("bgcolor=\"" + colors.get("other"))[1].split("</td>")[0].contains("border")) {
                                    if (i.contains("<font color=\"red\">")) {
                                        field = i.split("colspan=")[0].split("bgcolor=\"" + colors.get("other"))[1].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                        weight = i.split("bgcolor=\"" + colors.get("other"))[1].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    } else {
                                        field = i.split("colspan=")[0].split("bgcolor=\"" + colors.get("other"))[1].split("id=")[1].replaceAll("<font color=\"red\">", "").split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                        weight = i.split("bgcolor=\"" + colors.get("other"))[1].split("font size=")[1].split(">")[1].split("<")[0].replaceAll("\\s+", "");
                                    }
                                } else {
                                    field = "";
                                    weight = "";
                                }

                                other.add(field);
                                other.add(weight);

                                mark.put("other", other);
                                stats.add(mark);

                            } catch (ArrayIndexOutOfBoundsException e1) {

                            }
                        }

                        marks.put(assignment, stats);
                    }

                }


                return marks;
           }else{
               LinkedHashMap<String,List<Map<String,List<String>>>> returnMap = new LinkedHashMap<>();
               return returnMap;
           }
        }
        catch(IOException e) {
            e.printStackTrace();
            //String[] returnString = {"ERROR! Check in SendRequest"};
            LinkedHashMap<String,List<Map<String,List<String>>>> returnMap = new LinkedHashMap<>();
            return returnMap;
        }

    }

    public LinkedHashMap<String, Double> GetCourseWeights(){ //doesnt need course parameter because its being called with the same class instance as GetMarks
        LinkedHashMap<String, Double> weights = new LinkedHashMap();
        String response = marksResponse[0];
        Double Knowledge;
        Double Thinking;
        Double Communication;
        Double Application;
        try {
            response = response.split("<th>Course Weighting</th>")[1].split("</table>")[0];
            String knowledge = response.split("Knowledge/Understanding")[1].split("\"right\">")[1].split("%</td>")[0];
            String thinking = response.split("Thinking")[1].split("\"right\">")[1].split("%</td>")[0];
            String communication = response.split("Communication")[1].split("\"right\">")[1].split("%</td>")[0];
            String application = response.split("Application")[1].split("\"right\">")[1].split("%</td>")[0];

            Knowledge = Double.parseDouble(knowledge);
            Thinking = Double.parseDouble(thinking);
            Communication = Double.parseDouble(communication);
            Application = Double.parseDouble(application);
        }catch (ArrayIndexOutOfBoundsException e){
            Knowledge = 1.0;
            Thinking = 1.0;
            Communication = 1.0;
            Application = 1.0;
        }



        weights.put("Knowledge", Knowledge);
        weights.put("Thinking", Thinking);
        weights.put("Communication", Communication);
        weights.put("Application", Application);
        return weights;
    }

    public Double ParseAverageFromMarksView(LinkedHashMap<String,List<Map<String,List<String>>>> marks){
        LinkedHashMap<String, Double> weights = GetCourseWeights();
        Double Knowledge = weights.get("Knowledge");
        Double Thinking = weights.get("Thinking");
        Double Communication = weights.get("Communication");
        Double Application = weights.get("Application");




        Double knowledge = 0.0;
        Double thinking = 0.0;
        Double communication = 0.0;
        Double application = 0.0;

        Double totalWeightKnowledge = 0.0;
        Double totalWeightThinking = 0.0;
        Double totalWeightCommunication = 0.0;
        Double totalWeightApplication = 0.0;

        for(List<Map<String,List<String>>> Assignment: marks.values()){
            for(Map<String,List<String>> CategoryList:Assignment){
                for(Map.Entry<String,List<String>> Category:CategoryList.entrySet()){
                    if(Category.getKey().equals("knowledge") && !Category.getValue().get(0).split("/")[0].isEmpty()){
                        if(Category.getValue().get(0).split("=").length > 1) {
                            Double mark = Double.parseDouble(Category.getValue().get(0).split("=")[1].split("%")[0]);
                            Double weight = Double.parseDouble(Category.getValue().get(1).split("=")[1]);
                            totalWeightKnowledge += weight;
                            knowledge += mark * weight;
                        }else{
                            Double weight = Double.parseDouble(Category.getValue().get(1).split("=")[1]);
                            totalWeightKnowledge += weight;
                            //got a zero
                        }
                    }
                    else if(Category.getKey().equals("thinking") && !Category.getValue().get(0).split("/")[0].isEmpty()){
                        if(Category.getValue().get(0).split("=").length > 1) {
                            Double mark = Double.parseDouble(Category.getValue().get(0).split("=")[1].split("%")[0]);
                            Double weight = Double.parseDouble(Category.getValue().get(1).split("=")[1]);
                            totalWeightThinking += weight;
                            thinking += mark * weight;
                        }else{
                            Double weight = Double.parseDouble(Category.getValue().get(1).split("=")[1]);
                            totalWeightThinking += weight;
                            //got a zero
                        }
                    }
                    else if(Category.getKey().equals("communication") && !Category.getValue().get(0).split("/")[0].isEmpty()){
                        if(Category.getValue().get(0).split("=").length > 1) {
                            Double mark = Double.parseDouble(Category.getValue().get(0).split("=")[1].split("%")[0]);
                            Double weight = Double.parseDouble(Category.getValue().get(1).split("=")[1]);
                            totalWeightCommunication += weight;
                            communication += mark * weight;
                        }else{
                            Double weight = Double.parseDouble(Category.getValue().get(1).split("=")[1]);
                            totalWeightCommunication += weight;
                            //got a zero
                        }
                    }
                    else if(Category.getKey().equals("application") && !Category.getValue().get(0).split("/")[0].isEmpty()){
                        if(Category.getValue().get(0).split("=").length > 1) {
                            Double mark = Double.parseDouble(Category.getValue().get(0).split("=")[1].split("%")[0]);
                            Double weight = Double.parseDouble(Category.getValue().get(1).split("=")[1]);
                            totalWeightApplication += weight;
                            application += mark * weight;
                        }else{
                            Double weight = Double.parseDouble(Category.getValue().get(1).split("=")[1]);
                            totalWeightApplication += weight;
                            //got a zero
                        }
                    }

                }
            }
        }
        Double finalKnowledge;
        Double finalThinking;
        Double finalCommunication;
        Double finalApplication;

        //omit category if there is no assignment in it
        if(totalWeightKnowledge != 0.0) {
            finalKnowledge = knowledge / totalWeightKnowledge;
        }else{
            finalKnowledge = 0.0;
            Knowledge = 0.0;
        }
        if(totalWeightThinking != 0.0) {
            finalThinking = thinking/totalWeightThinking;
        }else{
            finalThinking = 0.0;
            Thinking = 0.0;
        }
        if(totalWeightCommunication != 0.0) {
            finalCommunication = communication/totalWeightCommunication;
        }else{
            finalCommunication = 0.0;
            Communication = 0.0;
        }
        if(totalWeightApplication != 0.0) {
            finalApplication = application/totalWeightApplication;
        }else{
            finalApplication = 0.0;
            Application = 0.0;
        }
        finalKnowledge = finalKnowledge*Knowledge;
        finalThinking = finalThinking*Thinking;
        finalCommunication = finalCommunication*Communication;
        finalApplication = finalApplication*Application;

        Double Average = (finalApplication + finalKnowledge + finalThinking +finalCommunication) / (Knowledge+Thinking+Communication+Application);


        return DoubleRounder.round(Average, 1);


    }

}
/*<td>Application</td>
    								<td align="right">21.4%</td>
I/System.out: 								<td align="right">15%</td>
    								<td align="right">82.2%</td>*/
