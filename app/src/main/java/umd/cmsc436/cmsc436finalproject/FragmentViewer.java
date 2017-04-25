package umd.cmsc436.cmsc436finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import java.util.HashMap;

/**
 * Created by Clayton on 4/25/2017.
 */

public class FragmentViewer extends AppCompatActivity implements AHBottomNavigation.OnTabSelectedListener {

    AHBottomNavigation bottomNavigation;
    private String prev_class;
    HashMap<String, HashMap> taskScenarioData;
    HashMap<String, HashMap> meta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        //extract hashtable from bundle passed in
//        taskScenarioData = (HashMap<String, HashMap>) getIntent().getSerializableExtra("scenarioData");


        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnTabSelectedListener(this);

//        prev_class = getIntent().getStringExtra("intent");
        this.createNavItems();




    }

    private void createNavItems() {

        //CREATE ITEMS

        //AHBottomNavigationItem homeItem = new AHBottomNavigationItem(R.string.home, R.drawable.home_icon, R.color.colorAccent);
        //AHBottomNavigationItem generalItem=new AHBottomNavigationItem(R.string.general, R.drawable.graph_icon, R.color.colorAccent);
        //AHBottomNavigationItem fitnessItem=new AHBottomNavigationItem(R.string.fitness,R.drawable.run_icon, R.color.colorAccent);
        //AHBottomNavigationItem dietItem=new AHBottomNavigationItem(R.string.diet,R.drawable.diet_icon, R.color.colorAccent);

        //ADD THEM to bar

        //bottomNavigation.addItem(homeItem);
        //bottomNavigation.addItem(generalItem);
        //bottomNavigation.addItem(fitnessItem);
        //bottomNavigation.addItem(dietItem);

        //set properties
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        //set current item
//        if (prev_class != null) {
//            switch (prev_class) {
//                case "diet":
//                    bottomNavigation.setCurrentItem(3);
//                    break;
//                case "general":
//                    bottomNavigation.setCurrentItem(1);
//                    break;
//                case "fitness":
//                    bottomNavigation.setCurrentItem(2);
//                    break;
//            }
//            prev_class = "";
//        }
    }


    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {


        //show fragment
        switch(position) {
            /* navigate to home */
            case 0:
                System.out.println("case 0");

                //Intent intent = new Intent(FragmentViewer.this, MainActivity.class);
                //intent.putExtra("currentScenario", meta.get("scenarioName") );
                //startActivity(intent);
                break;

            /* navigate to general */
            case 1:
                System.out.println("case 1");

                //GeneralFragment generalFragment = new GeneralFragment();
                //Bundle generalData = new Bundle();
                //taskScenarioData = (HashMap<String, HashMap>) getIntent().getSerializableExtra("scenarioData");
                //generalData.putSerializable("taskScenarioData", taskScenarioData);
                //generalFragment.setArguments(generalData);

                //getSupportFragmentManager()
                //        .beginTransaction()
                //        .replace(R.id.content_id, generalFragment)
                //        .commit();
                break;

            /* navigate to fitness */
            case 2:
                System.out.println("case 2");

                //FitnessFragment fitnessFragment = new FitnessFragment();
                //Bundle fitnessData = new Bundle();
                //taskScenarioData = (HashMap<String, HashMap>) getIntent().getSerializableExtra("scenarioData");
                //fitnessData.putSerializable("taskScenarioData", taskScenarioData);
                //fitnessFragment.setArguments(fitnessData);

                //getSupportFragmentManager()
                //        .beginTransaction()
                //        .replace(R.id.content_id, fitnessFragment)
                //        .commit();
                break;

            /* navigate to diet */
            case 3:
                System.out.println("case 3");

                //DietFragment dietFragment = new DietFragment();
                //Bundle dietData = new Bundle();
                //taskScenarioData = (HashMap<String, HashMap>) getIntent().getSerializableExtra("scenarioData");
                //dietData.putSerializable("taskScenarioData", taskScenarioData);
                //dietFragment.setArguments(dietData);

                //getSupportFragmentManager()
                //        .beginTransaction()
                //       .replace(R.id.content_id,dietFragment)
                //        .commit();
                break;
        }
        return true;
    }

}
