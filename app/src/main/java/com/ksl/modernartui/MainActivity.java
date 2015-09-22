package com.ksl.modernartui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;

import java.util.Random;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "ModernArtUI";

    private View[] mViews = new View[5];
    private float[] mHues = new float[5];
    private float[] mSaturations = new float[5];
    private float[] mHsvValues = new float[3];

    private SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get handles to the colored text views
        mViews[0] = findViewById(R.id.leftTopView);
        mViews[1] = findViewById(R.id.leftBottomView);
        mViews[2] = findViewById(R.id.rightTopView);
        mViews[3] = findViewById(R.id.rightMiddleView);
        mViews[4] = findViewById(R.id.rightBottomView);

        //init hue and saturation for each tile
        int seed = 2;  //just some arbitrary integer that gave nice results
        Random rand = new Random(seed);
        for (int i=0; i<5; i++) {

            //get next random float value
            float randNum = rand.nextFloat();

            //random hue from 0 to 360
            mHues[i] = randNum * 360;

            //random saturation from 0.4 to 0.7
            mSaturations[i] = fit(randNum, 0, 1, 0.4f, 0.7f);
        }
        //force tile 4 to have saturation of 0 so that it's white/grey, as per the requirement
        mSaturations[3] = 0;

        //get a handle to the seekbar and create the listeners
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setTileColors(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //init the tile colors with 0
        setTileColors(0);
    }

    //sets colors of the tiles based on a given 0-100 offset
    private void setTileColors(int offset) {
        for (int i=0; i<5; i++) {

            //shift hue based on position
            mHsvValues[0] = (mHues[i]+fit(offset, 0, 100, 0, 80)) % 360;

            //keep initial saturation level
            mHsvValues[1] = mSaturations[i];

            //keep value at 1
            mHsvValues[2] = 1;

            //finally, set the background color
            mViews[i].setBackgroundColor(Color.HSVToColor(mHsvValues));
        }
    }

    //helper function to easily remap a value from an old min/max range to a new min/max range
    private float fit(float value, float oldMin, float oldMax, float newMin, float newMax) {
        return (value - oldMin) / (oldMax - oldMin) * (newMax - newMin) + newMin;
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
        if (id == R.id.action_moreinfo) {
            showDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //shows the more-info dialog
    private void showDialog() {

        //create a new dialog instance
        final Dialog dialog = new Dialog(this);

        //remove title area
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set the content view
        dialog.setContentView(R.layout.dialog_moreinfo);

        //create click listener for Visit MOMA button
        Button visitMomaButton = (Button) dialog.findViewById(R.id.visitMomaButton);
        visitMomaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //start activity to show the MOMA website
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.moma.org"));
                startActivity(intent);

                //also dismiss the dialog
                dialog.dismiss();
            }
        });

        //create click listener for Not Now button
        Button notNowButton = (Button) dialog.findViewById(R.id.notNowButton);
        notNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //just dismiss the dialog when Not Now is clicked
                dialog.dismiss();
            }
        });

        //finally, show the dialog
        dialog.show();
    }
}
