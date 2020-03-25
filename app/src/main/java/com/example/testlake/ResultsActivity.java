package com.example.testlake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testlake.Scanner.ScanResult;
import com.scandit.datacapture.barcode.data.SymbologyDescription;

import java.util.ArrayList;
import java.util.HashSet;

public class ResultsActivity extends AppCompatActivity {

    public static final int RESULT_CODE_CLEAN = 1;
    private static final String ARG_SCAN_RESULTS = "scan-results";

    public static Intent getIntent(Context context, HashSet<ScanResult> scanResults) {
        return new Intent(context, ResultsActivity.class)
                .putExtra(ARG_SCAN_RESULTS, scanResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        // Setup recycler view.
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL));

        // Receive results from previous screen and set recycler view items.
        final ArrayList<ScanResult> scanResults = new ArrayList<>(
                (HashSet<ScanResult>) getIntent().getSerializableExtra(ARG_SCAN_RESULTS));
        recyclerView.setAdapter(new ScanResultsAdapter(this, scanResults));

        Button doneButton = findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }





    private class ScanResultsAdapter extends RecyclerView.Adapter<ViewHolder> {

        private Context context;
        private ArrayList<ScanResult> items;


        ScanResultsAdapter(Context context, ArrayList<ScanResult> items) {
            this.context = context;
            this.items = items;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.scan_result_item, parent, false));
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.update(items.get(position));
            holder.scan_result_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "Btn is clicked111.", Toast.LENGTH_SHORT).show();
                }
            });
            holder.buttonDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.buttonShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Штрихкод следующий:\n" + items.get(holder.getAdapterPosition()).data + "\n" + "Тип:\n" + items.get(holder.getAdapterPosition()).symbology) ;
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout scan_result_item;

        public TextView getDataTextView() {
            return dataTextView;
        }

        public TextView getTypeTextView() {
            return typeTextView;
        }

        private TextView dataTextView;
        private TextView typeTextView;
        private Button  buttonDownload;
        private Button  buttonShare;

        ViewHolder(View itemView) {
            super(itemView);
            dataTextView = itemView.findViewById(R.id.data_text);
            typeTextView = itemView.findViewById(R.id.type_text);
            scan_result_item = itemView.findViewById(R.id.scan_result_item);
            buttonDownload = itemView.findViewById(R.id.btndownload);
            buttonShare = itemView.findViewById(R.id.btnshare);
        }

       void update(ScanResult scanResult) {
           dataTextView.setText(scanResult.data);
           typeTextView.setText(
                   SymbologyDescription.create(scanResult.symbology).getReadableName()
           );
       }
    }
}
