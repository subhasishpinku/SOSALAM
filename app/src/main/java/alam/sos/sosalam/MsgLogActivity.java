package alam.sos.sosalam;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import alam.sos.sosalam.SetgetActivity.AlamDataShowSetGet;

public class MsgLogActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView txt_tgl;
    String viewdate;
    Button cal;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    private DatabaseReference databaseReference;
    List<AlamDataShowSetGet> adsList;
    private RecyclerView adsRecyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_msglog);
        initToolBar();
        txt_tgl = (TextView)findViewById(R.id.txt_tgl);
        cal = (Button) findViewById(R.id.cal);
        adsRecyclerView = (RecyclerView)findViewById(R.id.adsRecyclerView);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        viewdate = df.format(c);
        txt_tgl.setText(viewdate);
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Fromdate();

            }
        };
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MsgLogActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        Fromdate();
        LinearLayoutManager recyclerLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        adsRecyclerView.setLayoutManager(recyclerLayoutManager);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(adsRecyclerView.getContext(),
                        recyclerLayoutManager.getOrientation());
        adsRecyclerView.addItemDecoration(dividerItemDecoration);
    }
    public void initToolBar() {
        toolbar =(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("SOS Msg Log");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }
    private void Fromdate() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txt_tgl.setText(sdf.format(myCalendar.getTime()));
        getClassifiedAds();
    }
    public void getClassifiedAds() {
        viewdate= ((TextView) findViewById(R.id.txt_tgl)).getText().toString();
        getClassifiedsFromDb(viewdate);
    }
    private void getClassifiedsFromDb(final String viewdate) {
        databaseReference.child("Alertdata").orderByChild("dateId")
                .equalTo(viewdate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adsList = new ArrayList<AlamDataShowSetGet>();
                for (DataSnapshot chidSnap : dataSnapshot.getChildren()) {
                    adsList.add(chidSnap.getValue(AlamDataShowSetGet.class));
                    String backupId = String.valueOf(chidSnap.child("backupId").getValue());
                    String dateId = String.valueOf(chidSnap.child("dateId").getValue());
                    String imageulr = String.valueOf(chidSnap.child("imageulr").getValue());
                    String text = String.valueOf(chidSnap.child("text").getValue());
                    String timeId = String.valueOf(chidSnap.child("timeId").getValue());
                    Log.e("Show_Data", backupId+" "+dateId+" "+imageulr+" "+text+" "+timeId);
                    AdsRecyclerView recyclerViewAdapter = new
                            AdsRecyclerView(adsList, getApplicationContext());
                    adsRecyclerView.setAdapter(recyclerViewAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error trying to get Date ads for " + date, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public class AdsRecyclerView extends RecyclerView.Adapter<AdsRecyclerView.ViewHolder> {

        private List<AlamDataShowSetGet> adsList;
        private Context context;
        public AdsRecyclerView(List<AlamDataShowSetGet> list, Context ctx) {
            adsList = list;
            context = ctx;
        }

        @Override
        public int getItemCount() {
            return adsList.size();
        }

        @Override
        public ViewHolder
        onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_alert, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final int itemPos = position;
            final AlamDataShowSetGet reportSetGet = adsList.get(position);
            holder.textTitle.setText(reportSetGet.getText());
            holder.textDate.setText(reportSetGet.getDateId());
            holder.textTime.setText(reportSetGet.getTimeId());
            Glide.with(getApplicationContext())
                    .load(reportSetGet.getImageulr())
                    .into(holder.imageId);
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textTitle;
            public TextView textDate;
            private TextView textTime;
            private ImageView imageId;
            public ViewHolder(View view) {
                super(view);
                textTitle = (TextView) view.findViewById(R.id.textTitle);
                textDate = (TextView) view.findViewById(R.id.textDate);
                textTime = (TextView) view.findViewById(R.id.textTime);
                imageId = (ImageView)view.findViewById(R.id.imageId);
            }
        }
    }
    @Override
    public void onBackPressed() {

    }

    @Override
    public void onResume(){
        super.onResume();
        System.out.println("Inside onResume");
    }

    @Override
    public void onStart(){
        super.onStart();
        System.out.println("Inside onStart");
    }

    @Override
    public void onRestart(){
        super.onRestart();
        System.out.println("Inside onReStart");
    }

    @Override
    public void onPause(){
        super.onPause();
        System.out.println("Inside onPause");
    }

    @Override
    public void onStop(){
        super.onStop();
        System.out.println("Inside onStop");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        System.out.println("Inside onDestroy");
    }
}
