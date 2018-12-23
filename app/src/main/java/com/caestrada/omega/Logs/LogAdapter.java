/*
 * Title: LogAdapter.java
 * Description: Custom Adapter for displaying system logs.
 * This is only accessible to the admin.
 * Author: Carlos Estrada
 * Date: November – December 2018
 */

package com.caestrada.omega.Logs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caestrada.omega.Airline;
import com.caestrada.omega.Database.Flight;
import com.caestrada.omega.Database.Log;
import com.caestrada.omega.R;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogItemHolder> {
    private Activity mActivity;
    private List<Log> sysLogs;
    private AlertDialog.Builder builder;
    private EditText input;

    /* Need the Activity and Context for the Password Dialog */
    public LogAdapter(Activity activity, List<Log> sysLogs) {
        this.sysLogs = sysLogs;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public LogItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mActivity.getApplicationContext())
                .inflate(R.layout.log_item, parent, false);
        return new LogItemHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull LogItemHolder logItemHolder, int i) {
        Log log = sysLogs.get(i);

        logItemHolder.logType.setText(log.getType());
        logItemHolder.logUser.setText(log.getUser());
        logItemHolder.logDetails.setText(log.getDetails());

        logItemHolder.logFlight.setText(log.getFlight());

        String formattedDate = log.getDateAsString();

        logItemHolder.logDate.setText(formattedDate);

        if (log.getFlight().equals("N/A"))
            logItemHolder.logFlight.setTextColor(mActivity.getResources().getColor(R.color.status_fail));

        if (log.isSuccessful()) {
            logItemHolder.logStatus.setText("Success");
            logItemHolder.logStatus.setTextColor(mActivity.getResources().getColor(R.color.status_success));
        }
        else {
            logItemHolder.logStatus.setText("Fail");
            logItemHolder.logStatus.setTextColor(mActivity.getResources().getColor(R.color.status_fail));
        }
    }

    @Override
    public int getItemCount() {
        return sysLogs.size();
    }

    public class LogItemHolder extends RecyclerView.ViewHolder {
        // Create and link all of the log's fields.
        TextView logType, logStatus, logUser, logFlight, logDetails, logDate;

        public LogItemHolder(@NonNull View view) {
            super(view);

            logType = view.findViewById(R.id.log_type);
            logStatus = view.findViewById(R.id.log_status);
            logUser = view.findViewById(R.id.log_user);
            logFlight = view.findViewById(R.id.log_flight);
            logDetails = view.findViewById(R.id.log_details);
            logDate = view.findViewById(R.id.log_date);
        }
    }
}
