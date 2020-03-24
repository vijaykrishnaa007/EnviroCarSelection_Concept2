package org.andresoviedo.app.model3D.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alespero.expandablecardview.ExpandableCardView;

import org.andresoviedo.app.model3D.api;
import org.andresoviedo.app.model3D.demo.ExampleSceneLoader;
import org.andresoviedo.app.model3D.demo.SceneLoader;
import org.andresoviedo.app.model3D.manuadapter1;
import org.andresoviedo.app.model3D.manufacturer;
import org.andresoviedo.app.model3D.vehicleapi;
import org.andresoviedo.app.model3D.vehicledetails;
import org.andresoviedo.app.model3D.vehicledetailsapi;
import org.andresoviedo.app.model3D.vehicles;
import org.andresoviedo.dddmodel2.R;
import org.andresoviedo.util.android.ContentUtils;
import org.andresoviedo.util.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This activity represents the container for our 3D viewer.
 *
 * @author andresoviedo
 */
public class ModelActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOAD_TEXTURE = 1000;
    private static final int FULLSCREEN_DELAY = 10000;
    ExpandableCardView vehicle;
    private List<manufacturer> mExampleList;
    private List<vehicles> mExampleList1;
    AutoCompleteTextView searchmanu;
    private List<vehicledetails> details=new LinkedList<>();
    private RecyclerView mRecyclerView1;
    private manuadapter1 mAdapter1;
    /**
     * Type of model if file name has no extension (provided though content provider)
     */
    private int paramType;
    /**
     * The file to load. Passed as input parameter
     */
    private Uri paramUri;
    /**
     * Enter into Android Immersive mode so the renderer is full screen or not
     */
    private boolean immersiveMode = true;
    /**
     * Background GL clear color. Default is light gray
     */
    private float[] backgroundColor = new float[]{0f, 0f, 0f, 1.0f};

    private ModelSurfaceView gLView;

    private SceneLoader scene;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rot);
        // Try to get input parameters
        mRecyclerView1 = findViewById(R.id.manurecycle1);
        vehicle=findViewById(R.id.vehicle);
        searchmanu = findViewById(R.id.searchmanu);
        progressBar=findViewById(R.id.progress);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.getString("uri") != null) {
                this.paramUri = Uri.parse(b.getString("uri"));
            }
            this.paramType = b.getString("type") != null ? Integer.parseInt(b.getString("type")) : -1;
            this.immersiveMode = "true".equalsIgnoreCase(b.getString("immersiveMode"));
            try {
                String[] backgroundColors = b.getString("backgroundColor").split(" ");
                backgroundColor[0] = Float.parseFloat(backgroundColors[0]);
                backgroundColor[1] = Float.parseFloat(backgroundColors[1]);
                backgroundColor[2] = Float.parseFloat(backgroundColors[2]);
                backgroundColor[3] = Float.parseFloat(backgroundColors[3]);
            } catch (Exception ex) {
                // Assuming default background color
            }
        }
        Log.i("Renderer", "Params: uri '" + paramUri + "'");
        getUserList();
        handler = new Handler(getMainLooper());

        // Create our 3D sceneario
        if (paramUri == null) {
            scene = new ExampleSceneLoader(this);
        } else {
            scene = new SceneLoader(this);
        }
        scene.init();
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        try {
            gLView = new ModelSurfaceView(this);
            RelativeLayout relativeLayout=findViewById(R.id.relative);
            relativeLayout.addView(gLView);

        } catch (Exception e) {
            Toast.makeText(this, "Error loading OpenGL view:\n" +e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Show the Up button in the action bar.
        setupActionBar();

        // TODO: Alert user when there is no multitouch support (2 fingers). He won't be able to rotate or zoom
        ContentUtils.printTouchCapabilities(getPackageManager());

        setupOnSystemVisibilityChangeListener();
    }


    private void getUserList() {

        try {
            String url = "https://processing.envirocar.org/vehicles/";


            Retrofit retrofit = null;

            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

            }


            api service = retrofit.create(api.class);


            Call<List<manufacturer>> call = service.getUserData();

            call.enqueue(new Callback<List<manufacturer>>() {
                @Override
                public void onResponse(Call<List<manufacturer>> call, Response<List<manufacturer>> response) {

                    mExampleList = response.body();
                    String[] names = new String[mExampleList.size()];int i=0;
                    for(manufacturer temp:mExampleList)
                    {
                        names[i++]=temp.getName();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (ModelActivity.this,android.R.layout.select_dialog_item,names);
                    searchmanu.setAdapter(adapter);
                    searchmanu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            getVehicleList(mExampleList.get(i).getHsn());
                            vehicle.expand();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                        }
                    });
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<List<manufacturer>> call, Throwable t) {
                    int hy=0;
                }
            });
        }
        catch (Exception r)
        {

        }
    }




    private void filter1(String text) {
        ArrayList<vehicledetails> filteredList = new ArrayList<>();

        for (vehicledetails item : details) {
            if (item.getCommercialName().toLowerCase().contains(text.toLowerCase())) {

                filteredList.add(item);
            }
        }

        mAdapter1.filterList(filteredList);
    }


    private void getVehicleList(final String hsn) {

        try {
            String url = "https://processing.envirocar.org/vehicles/";


            Retrofit retrofit = null;

            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

            }


            vehicleapi service = retrofit.create(vehicleapi.class);


            Call<List<vehicles>> call = service.getUserData(hsn);
            details.clear();
            buildRecyclerView1();
            mAdapter1.notifyDataSetChanged();
            final Retrofit finalRetrofit = retrofit;
            call.enqueue(new Callback<List<vehicles>>() {
                @Override
                public void onResponse(Call<List<vehicles>> call, Response<List<vehicles>> response) {

                    mExampleList1 = response.body();
                    vehicledetailsapi service1 = finalRetrofit.create(vehicledetailsapi.class);
                    int h=10;
                    for(vehicles temp:mExampleList1) {
                        h--;
                        if(h==0)
                            break;
                        Call<vehicledetails> call1 = service1.getUserData(hsn,temp.tsn);
                        call1.enqueue(new Callback<vehicledetails>() {
                            @Override
                            public void onResponse(Call<vehicledetails> call, Response<vehicledetails> response) {
                                details.add(response.body());
                                mAdapter1.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(Call<vehicledetails> call, Throwable t) {
                                int hy=0;
                            }
                        });
                    }

                }

                @Override
                public void onFailure(Call<List<vehicles>> call, Throwable t) {
                    int hy=0;
                }
            });
        }
        catch (Exception r)
        {
            int h=0;
        }
    }
    ProgressBar progressBar;













    private void buildRecyclerView1() {

        mAdapter1 = new manuadapter1(details);
        mRecyclerView1.setLayoutManager(new GridLayoutManager(mRecyclerView1.getContext(),2));
        mRecyclerView1.setAdapter(mAdapter1);

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        // }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.model, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setupOnSystemVisibilityChangeListener() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return;
        }
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                // The system bars are visible. Make any desired
                hideSystemUIDelayed();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUIDelayed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.model_toggle_wireframe:
                scene.toggleWireframe();
                break;
            case R.id.model_toggle_boundingbox:
                scene.toggleBoundingBox();
                break;
            case R.id.model_toggle_textures:
                scene.toggleTextures();
                break;
            case R.id.model_toggle_animation:
                scene.toggleAnimation();
                break;
            case R.id.model_toggle_collision:
                scene.toggleCollision();
                break;
            case R.id.model_toggle_lights:
                scene.toggleLighting();
                break;
            case R.id.model_toggle_stereoscopic:
                scene.toggleStereoscopic();
                break;
            case R.id.model_toggle_blending:
                scene.toggleBlending();
                break;
            case R.id.model_toggle_immersive:
                toggleImmersive();
                break;
            case R.id.model_load_texture:
                Intent target = ContentUtils.createGetContentIntent("image/*");
                Intent intent = Intent.createChooser(target, "Select a file");
                try {
                    startActivityForResult(intent, REQUEST_CODE_LOAD_TEXTURE);
                } catch (ActivityNotFoundException e) {
                    // The reason for the existence of aFileChooser
                }
                break;
        }

        hideSystemUIDelayed();
        return super.onOptionsItemSelected(item);
    }

    private void toggleImmersive() {
        this.immersiveMode = !this.immersiveMode;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return;
        }
        if (this.immersiveMode) {
            hideSystemUI();
        } else {
            showSystemUI();
        }
        Toast.makeText(this, "Fullscreen " +this.immersiveMode, Toast.LENGTH_SHORT).show();
    }

    private void hideSystemUIDelayed() {
        if (!this.immersiveMode) {
            return;
        }
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(this::hideSystemUI, FULLSCREEN_DELAY);

    }

    private void hideSystemUI() {
        if (!this.immersiveMode) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideSystemUIKitKat();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            hideSystemUIJellyBean();
        }
    }

    // This snippet hides the system bars.
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void hideSystemUIKitKat() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void hideSystemUIJellyBean() {
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
    // except for the ones that make the content appear under the system bars.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showSystemUI() {
        handler.removeCallbacksAndMessages(null);
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    public Uri getParamUri() {
        return paramUri;
    }

    public int getParamType() {
        return paramType;
    }

    public float[] getBackgroundColor() {
        return backgroundColor;
    }

    public SceneLoader getScene() {
        return scene;
    }

    public ModelSurfaceView getGLView() {
        return gLView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_LOAD_TEXTURE:
                // The URI of the selected file
                final Uri uri = data.getData();
                if (uri != null) {
                    Log.i("ModelActivity", "Loading texture '" + uri + "'");
                    try {
                        ContentUtils.setThreadActivity(this);
                        scene.getObjects().get(0).setTextureData(IOUtils.read(ContentUtils.getInputStream(uri)));
                    } catch (IOException ex) {
                        Log.e("ModelActivity", "Error loading texture: " + ex.getMessage(), ex);
                        Toast.makeText(this, "Error loading texture '" + uri + "'. " + ex
                                .getMessage(), Toast.LENGTH_LONG).show();
                    } finally {
                        ContentUtils.setThreadActivity(null);
                    }
                }
        }
    }
}
