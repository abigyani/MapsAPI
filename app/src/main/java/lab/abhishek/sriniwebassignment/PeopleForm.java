package lab.abhishek.sriniwebassignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PeopleForm extends AppCompatActivity {

    private EditText ed_fname, ed_lname, ed_email, ed_desc, ed_price, ed_location, ed_lat, ed_lon;
    private Spinner sp_cat, sp_avail;
    private ImageView iv_user;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private Uri image_uri;
    private String key;
    private ProgressDialog pd;
    private boolean hasValue, editAllowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peopl__form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Details");
        pd = new ProgressDialog(this);
        hasValue = false;
        editAllowed = true;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.peopl_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 201);
            }
        });

        iv_user = (ImageView) findViewById(R.id.rl_image);
        ed_fname = (EditText) findViewById(R.id.ed_fname);
        ed_lname = (EditText) findViewById(R.id.ed_lname);
        ed_email = (EditText) findViewById(R.id.ed_email);
        ed_desc = (EditText) findViewById(R.id.ed_description);
        ed_price = (EditText) findViewById(R.id.ed_price);
        ed_location = (EditText) findViewById(R.id.ed_location);
        ed_lat = (EditText) findViewById(R.id.ed_latitude);
        ed_lon = (EditText) findViewById(R.id.ed_longitude);
        sp_avail = (Spinner) findViewById(R.id.spinner_availability);
        sp_cat = (Spinner) findViewById(R.id.spinner_category);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid()).child("People");
        mDatabaseReference.keepSynced(true);
        mStorageReference = FirebaseStorage.getInstance().getReference().child(mAuth.getCurrentUser().getUid());

        key = getIntent().getStringExtra("key");
        if (key != null){
            pd.setTitle("Please Wait!");
            pd.setMessage("Connecting...");
            pd.show();
            setValues(key);
            hasValue = true;
            if (getIntent().getStringExtra("action").equals("view"))
                editAllowed = false;
        }

        if (!editAllowed){

            ed_fname.setEnabled(false);
            ed_lname.setEnabled(false);
            fab.setVisibility(View.INVISIBLE);
            ed_email.setEnabled(false);
            sp_cat.setEnabled(false);
            sp_avail.setEnabled(false);
            ed_desc.setEnabled(false);
            ed_price.setEnabled(false);
            ed_location.setEnabled(false);
            ed_lon.setEnabled(false);
            ed_lat.setEnabled(false);

        }

    }

    private void setValues(String key) {

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid()).child("People").child(key);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    Picasso.with(PeopleForm.this).load(dataSnapshot.child("image_url").getValue().toString()).placeholder(R.mipmap.userdp).into(iv_user);
                } catch (Exception e){
                    Picasso.with(PeopleForm.this).load(R.mipmap.userdp).into(iv_user);
                }
                ed_fname.setText(dataSnapshot.child("fname").getValue().toString());
                ed_lname.setText(dataSnapshot.child("lname").getValue().toString());
                ed_email.setText(dataSnapshot.child("email").getValue().toString());
                ed_desc.setText(dataSnapshot.child("description").getValue().toString());
                ed_price.setText(dataSnapshot.child("price").getValue().toString());
                ed_location.setText(dataSnapshot.child("location").getValue().toString());
                ed_lat.setText(dataSnapshot.child("lat").getValue().toString());
                ed_lon.setText(dataSnapshot.child("lon").getValue().toString());
                sp_cat.setSelection(Integer.parseInt(dataSnapshot.child("category").getValue().toString()));
                sp_avail.setSelection(Integer.parseInt(dataSnapshot.child("availability").getValue().toString()));
                pd.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 201 && resultCode == RESULT_OK){

            image_uri = data.getData();
            iv_user.setImageURI(image_uri);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_icon,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        } else if (item.getItemId() == R.id.add_veh_done){
            saveFile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveFile() {

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmss");
        String datetime = ft.format(dNow);

        final DatabaseReference newPeople;

        if (TextUtils.isEmpty(ed_fname.getText().toString()) ||
                TextUtils.isEmpty(ed_lname.getText().toString()) ||
                TextUtils.isEmpty(ed_location.getText().toString()) ||
                TextUtils.isEmpty(ed_price.getText().toString())){
            Toast.makeText(this, "Name, location and price can't be left blank!", Toast.LENGTH_LONG).show();
            return;
        }

        if (hasValue)
            newPeople = mDatabaseReference.child(key);
        else
            newPeople = mDatabaseReference.push();

        newPeople.keepSynced(true);

        newPeople.child("_id").setValue(datetime);
        newPeople.child("fname").setValue(ed_fname.getText().toString().trim());
        newPeople.child("lname").setValue(ed_lname.getText().toString().trim());
        newPeople.child("email").setValue(ed_email.getText().toString().trim());
        newPeople.child("category").setValue(""+sp_cat.getSelectedItemPosition());
        newPeople.child("availability").setValue(""+sp_avail.getSelectedItemPosition());
        newPeople.child("description").setValue(ed_desc.getText().toString().trim());
        newPeople.child("price").setValue(ed_price.getText().toString().trim());
        newPeople.child("location").setValue(ed_location.getText().toString().trim());
        newPeople.child("lat").setValue(ed_lat.getText().toString().trim());
        newPeople.child("lon").setValue(ed_lon.getText().toString().trim());

       if (image_uri != null){

           final ProgressDialog pd = new ProgressDialog(this);
           pd.setTitle("Please Wait!");
           pd.setMessage("Uploading...");
           pd.show();

           StorageReference filePath = mStorageReference.child(image_uri.getLastPathSegment());
           filePath.putFile(image_uri).addOnSuccessListener(
                   new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           //noinspection VisibleForTests
                           newPeople.child("image_url").setValue(taskSnapshot.getDownloadUrl().toString());
                           finish();
                       }
                   }
           );

       } else {
           finish();
       }

    }
}
