package lab.abhishek.sriniwebassignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mDatabaseReference;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    GridLayoutManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, null /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid()).child("People");
        mDatabaseReference.keepSynced(true);
        recyclerView.setHasFixedSize(true);
        lm = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(lm);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        //progressDialog.show();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PeopleForm.class));
            }
        });

        final LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);

        if (getSharedPreferences("srini_prefs",MODE_PRIVATE).getBoolean("firstTime",true)){

            ll.setVisibility(View.VISIBLE);
            fab.setEnabled(false);
            getSharedPreferences("srini_prefs",MODE_PRIVATE).edit().putBoolean("firstTime",false).apply();
            TextView tv_userName = (TextView) findViewById(R.id.tv_userName);
            tv_userName.setText(getSharedPreferences("srini_prefs",MODE_PRIVATE).getString("userName",""));

        }

        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll.setVisibility(View.INVISIBLE);
                fab.setEnabled(true);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signOut) {
            final ProgressDialog pd  = new ProgressDialog(this);
            pd.setTitle("Please Wait");
            pd.setMessage("Signing Out...");
            pd.show();
            mAuth.signOut();
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    pd.dismiss();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            });
            getSharedPreferences("srini_prefs",MODE_PRIVATE).edit().putBoolean("signedIn",false).apply();
            return true;
        } else if (id == R.id.action_exit){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<People, PeopleHolder> mFirebaseAdapter =
                new FirebaseRecyclerAdapter<People, PeopleHolder>(
                        People.class,
                        R.layout.people_card,
                        PeopleHolder.class,
                        mDatabaseReference
                ) {
                    @Override
                    protected void populateViewHolder(PeopleHolder viewHolder, People model, int position) {
                        final String key = getRef(position).getKey();
                        viewHolder.setvalues(model.getFname(), model.getLname(), model.getLocation(), model.getPrice(),model.getImage_url(), getApplicationContext());
                        //progressDialog.dismiss();

                        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MainActivity.this, PeopleForm.class);
                                intent.putExtra("key",key);
                                intent.putExtra("action","view");
                                startActivity(intent);
                            }
                        });

                        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                PopupMenu popup = new PopupMenu(v.getContext(),v);
                                MenuInflater inflater = popup.getMenuInflater();
                                inflater.inflate(R.menu.people_options, popup.getMenu());
                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {

                                        Intent intent = new Intent(MainActivity.this, PeopleForm.class);

                                        switch (item.getItemId()){

                                            case R.id.action_view :
                                                intent.putExtra("key",key);
                                                intent.putExtra("action","view");
                                                startActivity(intent);
                                                return true;

                                            case R.id.action_edit :
                                                intent.putExtra("key",key);
                                                intent.putExtra("action","edit");
                                                startActivity(intent);
                                                return true;

                                            case R.id.action_delete :
                                                mDatabaseReference.child(key).removeValue();
                                                return true;
                                        }

                                        return false;
                                    }
                                });
                                popup.show();
                                return true;

                            }
                        });

                    }
                };

        recyclerView.setAdapter(mFirebaseAdapter);

    }

}
