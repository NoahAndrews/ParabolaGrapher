package me.noahandrews.grapher.app;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SeekBar.OnTouchListener, SeekBar.OnSeekBarChangeListener{
    DrawerLayout drawerLayout;
    SeekBarHolder aSeekBarHolder, bSeekBarHolder, cSeekBarHolder, zoomSeekBarHolder;
    TextView aLabel, bLabel, cLabel;
    GraphView graph;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graph = (GraphView)findViewById(R.id.graph);
        
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        aSeekBarHolder = new SeekBarHolder((SeekBar)findViewById(R.id.a_slider));
        bSeekBarHolder = new SeekBarHolder((SeekBar)findViewById(R.id.b_slider));
        cSeekBarHolder = new SeekBarHolder((SeekBar)findViewById(R.id.c_slider));
        zoomSeekBarHolder = new SeekBarHolder((SeekBar)findViewById(R.id.zoom_slider));

        aLabel = (TextView)findViewById(R.id.a_label);
        bLabel = (TextView)findViewById(R.id.b_label);
        cLabel = (TextView)findViewById(R.id.c_label);

        aSeekBarHolder.seekBar.setOnTouchListener(this);
        aSeekBarHolder.seekBar.setOnSeekBarChangeListener(this);
        aSeekBarHolder.setLimits(-10, 10);
        aSeekBarHolder.setProgress(1);

        bSeekBarHolder.seekBar.setOnTouchListener(this);
        bSeekBarHolder.seekBar.setOnSeekBarChangeListener(this);
        bSeekBarHolder.setLimits(-10, 10);
        bSeekBarHolder.setProgress(0);

        cSeekBarHolder.seekBar.setOnTouchListener(this);
        cSeekBarHolder.seekBar.setOnSeekBarChangeListener(this);
        cSeekBarHolder.setLimits(-5, 5);
        cSeekBarHolder.setProgress(0);

        zoomSeekBarHolder.seekBar.setOnTouchListener(this);
        zoomSeekBarHolder.seekBar.setOnSeekBarChangeListener(this);
        zoomSeekBarHolder.setLimits(20, 100);
        zoomSeekBarHolder.setProgress(50);
    }

    class SeekBarHolder {
        SeekBar seekBar;
        private int min, max;

        public SeekBarHolder(SeekBar seekBar) {
            this.seekBar = seekBar;
        }

        public void setLimits(int min, int max) {
            this.min = min;
            this.max = max;
            seekBar.setMax(max - min);
        }

        public void setProgress(int progress) {
            seekBar.setProgress(progress - min);
        }

        public int getAdjustedProgress() {
            return seekBar.getProgress() + min ;
        }
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
        if(id == R.id.action_settings) {
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.getParent().requestDisallowInterceptTouchEvent(true);
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(seekBar == aSeekBarHolder.seekBar){
            aLabel.setText("A: " + aSeekBarHolder.getAdjustedProgress());
            if(graph.setaCoefficient(aSeekBarHolder.getAdjustedProgress()) == false) {
                 Toast.makeText(this, "'A' coefficient cannot be zero.", Toast.LENGTH_SHORT).show();
            }
        } else if(seekBar == bSeekBarHolder.seekBar){
            bLabel.setText("B: " + bSeekBarHolder.getAdjustedProgress());
            graph.setbCoefficient(bSeekBarHolder.getAdjustedProgress());
        } else if(seekBar == cSeekBarHolder.seekBar){
            cLabel.setText("C: " + cSeekBarHolder.getAdjustedProgress());
            if(graph.setcCoefficient(cSeekBarHolder.getAdjustedProgress()) == false) {
                Toast.makeText(this, "The y-vertex must be within the vertical range.",Toast.LENGTH_SHORT).show();
            }
        } else if(seekBar == zoomSeekBarHolder.seekBar){
            if(graph.setZoomLevel(zoomSeekBarHolder.getAdjustedProgress()) == false) {
                Toast.makeText(this, "The y-vertex must be within the vertical range.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
