package com.spartan.karanbir.restorecommender;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by karanbir on 4/27/16.
 */
public class SubCategoryActivity extends AppCompatActivity {
    private static final String TAG = SubCategoryActivity.class.getSimpleName();
    private static final String SELECTED_CATEGORY = "selectedCategories";
    private ListView mSubcategoryListView;
    private GetSubcategoriesTask mGetSubcategoriesTask;
    private ArrayAdapter<String> subCategoryListAdapter;
    private HashMap<String,Integer> subcategoryMap;
    private String selectedCategories ="";
    private Bundle userInfo;
    private final String AUTHORITY = "ec2-54-186-33-120.us-west-2.compute.amazonaws.com:8080";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategory);
        mSubcategoryListView = (ListView)findViewById(R.id.subcategory_listview);
        mGetSubcategoriesTask = new GetSubcategoriesTask();
        mGetSubcategoriesTask.execute();
        subCategoryListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice);
        mSubcategoryListView.setAdapter(subCategoryListAdapter);
        mSubcategoryListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        userInfo = new Bundle();
        if(getIntent().getExtras() != null){
            userInfo = getIntent().getBundleExtra(SignUpActivity.BUNDLE_KEY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_subcategory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.signup){
            StringBuilder stringBuilder = new StringBuilder();
            SparseBooleanArray checked = mSubcategoryListView.getCheckedItemPositions();
            int size = checked.size(); // number of name-value pairs in the array
            for (int i = 0; i < size; i++) {
                int key = checked.keyAt(i);
                boolean value = checked.get(key);
                if (value){
                    String category = subCategoryListAdapter.getItem(key);
                    stringBuilder.append(subcategoryMap.get(category).toString()).append(",");
                }
            }
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
            selectedCategories = stringBuilder.toString();
            userInfo.putString("selectedCategories", selectedCategories);
            SignUpTask signUpTask = new SignUpTask();
            signUpTask.execute(userInfo);
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetSubcategoriesTask extends AsyncTask<Void,Void,HashMap<String,Integer>> {
        private final String TAG = GetSubcategoriesTask.class.getSimpleName();
        private Uri uri ;
        private static final String CMPE = "CMPE239";
        private static final String SUBCATEGORY = "GetSubCategories";
        private static final String CATEGORY_KEY = "category";
        private static final String CATEGORY_VALUE = "1";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            subcategoryMap = new HashMap<>();
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .encodedAuthority(AUTHORITY)
                    .appendPath(CMPE)
                    .appendPath(SUBCATEGORY)
                    .appendQueryParameter(CATEGORY_KEY, CATEGORY_VALUE);
            uri = builder.build();
        }

        @Override
        protected HashMap<String,Integer> doInBackground(Void... params) {
            try {
                URL url = new URL(uri.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                // wrap the urlconnection in a bufferedreader
                BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                String webPage = "",data="";

                while ((data = bufferedReader.readLine()) != null){
                    webPage += data + "\n";
                }
                bufferedReader.close();
                JSONObject jsonObject = new JSONObject(webPage);
                JSONArray array = jsonObject.getJSONArray("RESULT");
                for(int i =0 ; i<= array.length(); i++){
                    JSONObject object = array.getJSONObject(i);
                    String name =  object.getString("subcategory_name");
                    int id =  object.getInt("subcategory_id");
                    subcategoryMap.put(name,id);
                }
                Log.d(TAG, jsonObject.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return subcategoryMap;
        }

        @Override
        protected void onPostExecute(HashMap<String,Integer> result) {
            super.onPostExecute(result);
            Set keys = result.keySet();
            subCategoryListAdapter.addAll(keys);
        }
    }

    private class SignUpTask extends AsyncTask<Bundle,Void,ArrayList<String>>{
        private final String TAG = SignUpTask.class.getSimpleName();
        private ArrayList<String> result;
        private Uri uri;
        private static final String CMPE = "CMPE239";
        private static final String SIGNUP = "Signup";
        private static final String CATEGORY_KEY = "category";
        private static final String CATEGORY_VALUE = "1";
        private static final String FIRSTNAME = "firstname";
        private static final String LASTNAME = "lastname";
        private static final String USERNAME = "username";
        private static final String PASSWORD = "password";
        private static final String SUBCATEGORY = "subcategory";

        @Override
        protected ArrayList<String> doInBackground(Bundle... params) {
            Bundle info = new Bundle();
            if(params != null){
                info = params[0];
            }
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .encodedAuthority(AUTHORITY)
                    .appendPath(CMPE)
                    .appendPath(SIGNUP)
                    .appendQueryParameter(FIRSTNAME, info.getString(SignUpActivity.FIRSTNAME_KEY))
                    .appendQueryParameter(LASTNAME, info.getString(SignUpActivity.LASTNAME_KEY))
                    .appendQueryParameter(USERNAME,info.getString(SignUpActivity.EMAIL_KEY))
                    .appendQueryParameter(PASSWORD, info.getString(SignUpActivity.PASSWORD_KEY))
                    .appendQueryParameter(SUBCATEGORY,info.getString(SELECTED_CATEGORY));
            uri = builder.build();
            URL url = null;
            try {
                url = new URL(uri.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                // wrap the urlconnection in a bufferedreader
                BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                String webPage = "",data="";

                while ((data = bufferedReader.readLine()) != null){
                    webPage += data + "\n";
                }
                bufferedReader.close();
                JSONObject jsonObject = new JSONObject(webPage);
                result = new ArrayList<>();
                result.add(jsonObject.getString("STATUS"));
                result.add(jsonObject.getString("RESULT"));

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return  result;
        }

        @Override
        protected void onPostExecute(ArrayList<String> results) {
            super.onPostExecute(results);
            if(results.get(0).equals("200")){
                if(results.get(1).equals("SUCCESS")){
                    Intent i = new Intent(SubCategoryActivity.this,LoginActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(SubCategoryActivity.this,results.get(1),Toast.LENGTH_LONG).show();
                }


            }
        }
    }
}
