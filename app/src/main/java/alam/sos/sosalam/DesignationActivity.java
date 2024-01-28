package alam.sos.sosalam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import alam.sos.sosalam.SetgetActivity.DesigSetGet;
import alam.sos.sosalam.SetgetActivity.ViewDesig;

public class DesignationActivity extends AppCompatActivity implements View.OnClickListener{
    Toolbar toolbar;
    EditText desId,abbreId;
    LinearLayout saveId;
    String des,abbre;
    Firebase firebase;
    DatabaseReference databaseReference;
    private DatabaseReference dbRef;
    public static final String Firebase_Server_URL = "https://insertdata-android-examples.firebaseio.com/";
    public static final String Database_Path = "Desig_Details";
    private RecyclerView adsRecyclerView;
    List<ViewDesig> viewDesigs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_designation_master);
        initToolBar();
        desId = (EditText)findViewById(R.id.desId);
        abbreId = (EditText)findViewById(R.id.abbreId);
        saveId = (LinearLayout) findViewById(R.id.saveId);
        saveId.setOnClickListener(this);
        Firebase.setAndroidContext(DesignationActivity.this);
        firebase = new Firebase(Firebase_Server_URL);
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        dbRef = FirebaseDatabase.getInstance().getReference();

        adsRecyclerView = (RecyclerView)findViewById(R.id.adsRecyclerView);
        LinearLayoutManager recyclerLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        adsRecyclerView.setLayoutManager(recyclerLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(adsRecyclerView.getContext(),
                recyclerLayoutManager.getOrientation());
        adsRecyclerView.addItemDecoration(dividerItemDecoration);
        dbRef.child("Desig_Details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                viewDesigs = new ArrayList<ViewDesig>();
                for (DataSnapshot adSnapshot : dataSnapshot.getChildren()) {
                   abbre = String.valueOf(adSnapshot.child("abbre").getValue());
                   des = String.valueOf(adSnapshot.child("desgId").getValue());
                    Log.e("desig"," "+abbre+" "+des);
                    viewDesigs.add(adSnapshot.getValue(ViewDesig.class));
                }
                AdsRecyclerView recyclerViewAdapter = new
                        AdsRecyclerView(viewDesigs, getApplicationContext());
                adsRecyclerView.setAdapter(recyclerViewAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void initToolBar() {
        toolbar =(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("SOS Designation");
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
    @Override
    public void onClick(View view) {
      switch (view.getId()){
          case R.id.saveId:
              des = desId.getText().toString().trim();
              abbre = abbreId.getText().toString().trim();
              if (TextUtils.isEmpty(des)){
                  desId.setError("Please enter Designation");
                  desId.requestFocus();
                  return;
              }
              if (TextUtils.isEmpty(abbre)) {
                  abbreId.setError("Please enter abbreviation");
                  abbreId.requestFocus();
                  return;
              }
              SaveData();
              break;
              default:
      }
    }
    public void SaveData(){
        DesigSetGet desg = new DesigSetGet();
        GetDataFromEditText();
        desg.setDesgId(des);
        desg.setAbbre(abbre);
        String StudentRecordIDFromServer = databaseReference.push().getKey();
        databaseReference.child(StudentRecordIDFromServer).setValue(desg);
        Toast.makeText(DesignationActivity.this,"Data Inserted Successfully", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }
    public void GetDataFromEditText(){
        des = desId.getText().toString().trim();
        abbre = abbreId.getText().toString().trim();
    }
    public class AdsRecyclerView extends RecyclerView.Adapter<AdsRecyclerView.ViewHolder> {
        private List<ViewDesig> viewDesigs;
        private Context context;
        public AdsRecyclerView(List<ViewDesig> list, Context ctx) {
            viewDesigs = list;
            context = ctx;
        }
        @Override
        public int getItemCount() {
            return viewDesigs.size();
        }
        @Override
        public ViewHolder
        onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_disg, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final int itemPos = position;
            final ViewDesig viewprojet = viewDesigs.get(position);
            holder.disgId.setText(viewprojet.getDesgId());
            holder.abbreId.setText(viewprojet.getAbbre());

        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView disgId;
            public TextView abbreId;
            public ViewHolder(View view) {
                super(view);
                disgId = (TextView) view.findViewById(R.id.disgId);
                abbreId = (TextView) view.findViewById(R.id.abbreId);
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
