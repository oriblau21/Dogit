package com.sagi.ori.dogit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Ori and sagi on 17/01/2018.
 */
public class MainActivity extends Activity
        implements AddOwnerFragment.OnFragmentInteractionListener, DogSitterListFragment.OnFragmentInteractionListener,LoginFragment.OnFragmentInteractionListener,AddDogSitterFragment.OnFragmentInteractionListener ,DogSitterDetailsFragment.OnFragmentInteractionListener{
    public static  String CurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("TAG","MainActivity onCreate");
        FragmentTransaction tran =  getFragmentManager().beginTransaction();

        CurrentFragment = "login";
        LoginFragment loginFragment = LoginFragment.newInstance();
        tran.add(R.id.main_container, loginFragment);
        tran.commit();
   }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.main_logout:
                CurrentFragment = "login";
                LoginFragment loginFragment = LoginFragment.newInstance();
                FragmentTransaction tran = getFragmentManager().beginTransaction();
                tran.replace(R.id.main_container, loginFragment);
                tran.commit();
               break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    // show ds details of ds by email
    @Override
    public void onFragmentInteraction(String email) {
        this.CurrentFragment="DogSitterDetails";
        DogSitterDetailsFragment dogSitterDetailsFragment = DogSitterDetailsFragment.newInstance(email);
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.main_container, dogSitterDetailsFragment);
        tran.addToBackStack("");
        tran.commit();

    }


    // if false login list view if true show list view
    @Override
    public void onFragmentInteraction(boolean bool) {
        Log.d("TAG","onFragmentInteraction login ");
        if(!bool) {

            CurrentFragment = "login";
            LoginFragment loginFragment = LoginFragment.newInstance();
            FragmentTransaction tran = getFragmentManager().beginTransaction();
            tran.replace(R.id.main_container, loginFragment);
            tran.commit();

        }
        else
        {
            CurrentFragment="List";
            DogSitterListFragment listFragment = DogSitterListFragment.newInstance();
            FragmentTransaction tran = getFragmentManager().beginTransaction() ;
            tran.replace(R.id.main_container,listFragment);
            tran.commit();
    }
    }


    // if is ds show add ds view else show add owner view
@Override
    public void onFragmentInteractionSignUp(boolean isDs) {
    if(isDs)
    {
        AddDogSitterFragment sa = AddDogSitterFragment.newInstance();
        this.CurrentFragment="addDS";
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.main_container,sa);
        tran.addToBackStack("");
        tran.commit();

    }else
    {
        AddOwnerFragment sa = AddOwnerFragment.newInstance();
        this.CurrentFragment="addOwner";
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.main_container,sa);
        tran.addToBackStack("");
        tran.commit();

    }

    }
}
