/*
 * Title: LogViewerActivity.java
 * Description: Activity for viewing and displaying system logs.
 * This is only accessible to the admin.
 * Author: Carlos Estrada
 * Date: November – December 2018
 */

package com.caestrada.omega.Logs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.caestrada.omega.Airline;
import com.caestrada.omega.Database.Log;
import com.caestrada.omega.R;

import java.util.List;

public class LogViewerActivity extends AppCompatActivity {
    RecyclerView rv;
    LogAdapter adapter;
    List<Log> logList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_viewer);

        logList = Airline.getSystemLogs();
        rv = findViewById(R.id.log_recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LogAdapter(this, logList);
        rv.setAdapter(adapter);
    }
}
