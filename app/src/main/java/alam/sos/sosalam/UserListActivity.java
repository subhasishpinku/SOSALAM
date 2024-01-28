package alam.sos.sosalam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alam.sos.sosalam.SetgetActivity.RegisterSetGet;

public class UserListActivity extends AppCompatActivity {
    Toolbar toolbar;
    ArrayList<RegisterSetGet> adsList;
    private RecyclerView adsRecyclerView;
    private DatabaseReference databaseReference;
    String backuupID,dsignation,emaino1,emaino2,flag,imgurl,phonenumber,username,whatsappno;
    String flagg ="1";
    String flaggg ="0";
    private CheckBox chk_select_all;
   // private ArrayList<RegisterSetGet> adsList = new ArrayList<>();
    AdsRecyclerView recyclerViewAdapter;
    LinearLayout saveId;
    String mess;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.userlist_activity);
        initToolBar();
        chk_select_all =(CheckBox)findViewById(R.id.chk_select_all);
        adsRecyclerView = (RecyclerView)findViewById(R.id.adsRecyclerView);
        saveId = (LinearLayout)findViewById(R.id.saveId);
        LinearLayoutManager recyclerLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        adsRecyclerView.setLayoutManager(recyclerLayoutManager);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(adsRecyclerView.getContext(),
                        recyclerLayoutManager.getOrientation());
        adsRecyclerView.addItemDecoration(dividerItemDecoration);
        Intent intent = getIntent();
        mess = intent.getStringExtra("mess");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Register_User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> name = new ArrayList<String>();
                adsList = new ArrayList<RegisterSetGet>();
                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    backuupID = String.valueOf(areaSnapshot.child("backuupID").getValue());
                    dsignation = String.valueOf(areaSnapshot.child("dsignation").getValue());
                    emaino1 = String.valueOf(areaSnapshot.child("emaino1").getValue());
                    emaino2 = String.valueOf(areaSnapshot.child("emaino2").getValue());
                    flag = String.valueOf(areaSnapshot.child("flag").getValue());
                    imgurl = String.valueOf(areaSnapshot.child("imgurl").getValue());
                    phonenumber = String.valueOf(areaSnapshot.child("phonenumber").getValue());
                    username = String.valueOf(areaSnapshot.child("username").getValue());
                    whatsappno = String.valueOf(areaSnapshot.child("whatsappno").getValue());
                    Log.e("userData",backuupID+" "+dsignation+" "+emaino1+" "+emaino2+" "+flag+" "+imgurl+" "+phonenumber+" "+username+" "+whatsappno);
                    adsList.add(areaSnapshot.getValue(RegisterSetGet.class));
                }
                recyclerViewAdapter = new
                        AdsRecyclerView(adsList, getApplicationContext());
                adsRecyclerView.setAdapter(recyclerViewAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        chk_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (chk_select_all.isChecked()) {

                    for (RegisterSetGet model : adsList) {
                        model.setSelected(true);
                        Log.e("Check","True");
                    }
                } else {

                    for (RegisterSetGet model : adsList) {
                        model.setSelected(false);
                        Log.e("Check","False");
                    }
                }

                recyclerViewAdapter.notifyDataSetChanged();
            }
        });
        saveId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShowAlamActivity.class);
                startActivity(intent);
            }
        });
    }
    public void initToolBar() {
        toolbar =(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("SOS User Show");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        finish();
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    public class AdsRecyclerView extends RecyclerView.Adapter<AdsRecyclerView.ViewHolder> {
       // private List<RegisterSetGet> adsList;
        public ArrayList<RegisterSetGet> adsList;
        private Context context;
        String userdb;
        String userId,userName,disg,userPhone,userWhatsapp,userFlag,Imei1,Imei2;
        public AdsRecyclerView(ArrayList<RegisterSetGet> list, Context ctx) {
            adsList = list;
            context = ctx;
        }
        @Override
        public int getItemCount() {
            return adsList.size();
        }
        @Override
        public AdsRecyclerView.ViewHolder
        onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adminview_all_user, parent, false);
            AdsRecyclerView.ViewHolder viewHolder =
                    new AdsRecyclerView.ViewHolder(view);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(final AdsRecyclerView.ViewHolder holder, int position) {
            final int pos = position;
            final RegisterSetGet Adminallusersetget = adsList.get(position);
            holder.usernameId.setText(Adminallusersetget.getUsername());
            holder.desigId.setText(Adminallusersetget.getDsignation());
            holder.userphoneID.setText(Adminallusersetget.getPhonenumber());
            holder.whatsappId.setText(Adminallusersetget.getWhatsappno());
            holder.flagId.setText(Adminallusersetget.getFlag());
            holder.useId.setText(Adminallusersetget.getBackuupID());
            holder.imei1.setText(Adminallusersetget.getEmaino1());
            holder.imei2.setText(Adminallusersetget.getEmaino2());
            Glide.with(getApplicationContext())
                    .load(Adminallusersetget.getImgurl())
                    .into(holder.imageView);
            if (holder.flagId.getText().toString().equals("1")){
                holder.useractiveunaviveId.setChecked(true);
                holder.checkBoxID.setChecked(true);
            }
            else {
                holder.useractiveunaviveId.setChecked(false);
                holder.checkBoxID.setChecked(false);
            }
            holder.useractiveunaviveId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        userId  =  holder.useId.getText().toString();
                        userName =  holder.usernameId.getText().toString();
                        disg =  holder.desigId.getText().toString();
                        userPhone =  holder.userphoneID.getText().toString();
                        userWhatsapp =  holder.whatsappId.getText().toString();
                        userFlag =  holder.flagId.getText().toString();
                        Imei1 =  holder.imei1.getText().toString();
                        Imei2 =  holder.imei2.getText().toString();
                        String imag = Adminallusersetget.getImgurl();
                        Log.e("ALERT", "T1" +" "+userId+ " "+userName+" "+" "+disg+" "+userPhone+" "+userWhatsapp+" "+userFlag+" "+Imei1+" "+Imei2);
                        updateEvent(userId,userName,disg,userPhone,userWhatsapp,flagg,Imei1,Imei2,imag);
                        RegisterSetGet user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                        String  userName = String.valueOf(user.getUsername());
                        Log.e("TGG",userName);
                        sedmessagee(mess,userName,Imei1);
                    } else {
                        userId  =  holder.useId.getText().toString();
                        userName =  holder.usernameId.getText().toString();
                        disg =  holder.desigId.getText().toString();
                        userPhone =  holder.userphoneID.getText().toString();
                        userWhatsapp =  holder.whatsappId.getText().toString();
                        userFlag =  holder.flagId.getText().toString();
                        Imei1 =  holder.imei1.getText().toString();
                        Imei2 =  holder.imei2.getText().toString();
                        String imag = Adminallusersetget.getImgurl();
                          Log.e("ALERT", "T0" +" "+userId+ " "+userName+" "+" "+disg+" "+userPhone+" "+userWhatsapp+" "+userFlag+" "+Imei1+" "+Imei2);
                        updateEvent(userId,userName,disg,userPhone,userWhatsapp,flaggg,Imei1,Imei2,imag);

                    }
                }
            });


           if (holder.flagId.getText().toString().equals("1")){
                holder.checkBoxID.setChecked(true);
            }
            else {
                holder.checkBoxID.setChecked(false);
            }
           holder.checkBoxID.setChecked(adsList.get(position).isSelected());
           holder.checkBoxID.setTag(adsList.get(position));
            holder.checkBoxID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                    CheckBox cb = (CheckBox) v;
                    RegisterSetGet model = (RegisterSetGet) cb.getTag();
                    model.setSelected(cb.isChecked());
                    adsList.get(pos).setSelected(cb.isChecked());
                    if (isChecked){
                        //holder.checkBoxID.setChecked(true);
                        userId  =  holder.useId.getText().toString();
                        userName =  holder.usernameId.getText().toString();
                        disg =  holder.desigId.getText().toString();
                        userPhone =  holder.userphoneID.getText().toString();
                        userWhatsapp =  holder.whatsappId.getText().toString();
                        userFlag =  holder.flagId.getText().toString();
                        Imei1 =  holder.imei1.getText().toString();
                        Imei2 =  holder.imei2.getText().toString();
                        String imag = Adminallusersetget.getImgurl();
                        Log.e("ALERT", "T1" +" "+userId+ " "+userName+" "+" "+disg+" "+userPhone+" "+userWhatsapp+" "+userFlag+" "+Imei1+" "+Imei2);
                        // populateUpdateAd(userId);
                        updateEvent(userId,userName,disg,userPhone,userWhatsapp,flagg,Imei1,Imei2,imag);
                        RegisterSetGet user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                        String  userName = String.valueOf(user.getUsername());
                        sedmessage(mess,userName);
                    }
                    else {
                       // holder.checkBoxID.setChecked(false);
                        userId  =  holder.useId.getText().toString();
                        userName =  holder.usernameId.getText().toString();
                        disg =  holder.desigId.getText().toString();
                        userPhone =  holder.userphoneID.getText().toString();
                        userWhatsapp =  holder.whatsappId.getText().toString();
                        userFlag =  holder.flagId.getText().toString();
                        Imei1 =  holder.imei1.getText().toString();
                        Imei2 =  holder.imei2.getText().toString();
                        String imag = Adminallusersetget.getImgurl();
                        Log.e("ALERT", "T0" +" "+userId+ " "+userName+" "+" "+disg+" "+userPhone+" "+userWhatsapp+" "+userFlag+" "+Imei1+" "+Imei2);
                        updateEvent(userId,userName,disg,userPhone,userWhatsapp,flaggg,Imei1,Imei2,imag);

                    }
                }
            });
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView usernameId;
            Switch useractiveunaviveId;
            public TextView desigId;
            public TextView userphoneID;
            public TextView whatsappId;
            public TextView flagId;
            public TextView useId;
            public TextView  imei1;
            public TextView  imei2;
            public CheckBox checkBoxID;
            public ViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.imageView);
                usernameId = (TextView) view.findViewById(R.id.usernameId);
                useractiveunaviveId = (Switch) view.findViewById(R.id.useractiveunaviveId);
                desigId = (TextView) view.findViewById(R.id.desigId);
                userphoneID = (TextView) view.findViewById(R.id.userphoneID);
                whatsappId = (TextView) view.findViewById(R.id.whatsappId);
                flagId = (TextView) view.findViewById(R.id.flagId);
                useId = (TextView) view.findViewById(R.id.useId);
                imei1 = (TextView) view.findViewById(R.id.imei1);
                imei2 = (TextView)view.findViewById(R.id.imei2);
                checkBoxID = (CheckBox) view.findViewById(R.id.checkBoxID);
            }}
    }

    public void updateEvent(final String userId,final String userName,final String disg,final String userPhone,final String userWhatsapp,final String userFlag,final String Imei1,final String Imei2,final String imag) {
        RegisterSetGet classifiedAd = createClassifiedAdObj(userId,userName,disg,userPhone,userWhatsapp,userFlag,Imei1,Imei2,imag);
        updateClassifiedToDB(classifiedAd,userId);
    }
    private RegisterSetGet createClassifiedAdObj(final String userId, final String userName, final String disg, final String userPhone, final String userWhatsapp, final String userFlag, final String Imei1, final String Imei2, final String imag) {
        final RegisterSetGet ad = new RegisterSetGet();
        Log.e("ALERT", "T3" +" "+userId+ " "+userName+" "+" "+disg+" "+userPhone+" "+userWhatsapp+" "+userFlag+" "+Imei1+" "+Imei2);
        ad.setBackuupID(userId);
        ad.setUsername(userName);
        ad.setDsignation(disg);
        ad.setPhonenumber(userPhone);
        ad.setWhatsappno(userWhatsapp);
        ad.setFlag(userFlag);
        ad.setEmaino1(Imei1);
        ad.setEmaino2(Imei2);
        ad.setImgurl(imag);
        return ad;
    }
    private void updateClassifiedToDB(RegisterSetGet classifiedAd, String userId) {
        addClassified(classifiedAd, userId);
        Log.e("Register_User",userId);
    }
    private void addClassified(RegisterSetGet classifiedAd, String userId) {
        classifiedAd.setBackuupID(userId);
        databaseReference.child("Register_User").child(userId)
                .setValue(classifiedAd)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        } else {

                        }
                    }
                });
    }
    public void sedmessage(final String mess,final String userName){
        final String tit = "Alert Show";
        //final String mess = message.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SEND_MULTIPLE_PUSH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response","response");

                        try {
                            JSONObject obj = new JSONObject(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", userName);
                params.put("message", mess);
                return params;
            }
        };
        FcmVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void sedmessagee(final String mess,final String userName,final String Imei1){
        final String tit = "Alert Show";
        //final String mess = message.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SEND_SINGLE_PUSH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response","response");

                        try {
                            JSONObject obj = new JSONObject(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", userName);
                params.put("message", mess);
                params.put("email",Imei1);
                return params;
            }
        };
        FcmVolley.getInstance(this).addToRequestQueue(stringRequest);
    }



}
