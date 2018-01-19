package com.sagi.ori.dogit.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.URLUtil;

import com.sagi.ori.dogit.MyApp;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ori and sagi on 17/01/2018.
 */

public class Model {

    // instance model
    public final static Model instace = new Model();

    //  variables
    private ModelSql modelSql;
    private ModelFirebase modelFirebase;

    // ctor
    private Model(){

        modelSql = new ModelSql(MyApp.getMyContext());
        modelFirebase = new ModelFirebase();

    }

    public void addOwner(Owner p) {
        modelFirebase.addOwner(p);
    }

    public void addDogSitter(DogSitter bs) {
          modelFirebase.addDogSitter(bs);
    }

    // login callback
    public interface GetLoginCallback {
        void onSuccess();
        void onFail();
    }

    public void login(String email,String password,final GetLoginCallback callback) {

        modelFirebase.login(email, password, new ModelFirebase.GetLoginCallback()
        {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onFail() {
                callback.onFail();
            }


        });

    }
    // Get Dog Sitter Callback
    public interface GetDogSitterCallback {
        void onComplete(DogSitter bs);

        void onCancel();
    }

    public void getDogSitter(String email, final GetDogSitterCallback callback) {

        modelFirebase.getDogSitter(email, new ModelFirebase.GetDogSitterCallback() {
            @Override
            public void onComplete(DogSitter bs) {
                callback.onComplete(bs);
            }

            @Override
            public void onCancel() {
                callback.onCancel();
            }
        });

    }


//Get All DogSitters And ObserveCallback
    public interface GetAllDogSittersAndObserveCallback {
        void onComplete(List<DogSitter> list);
        void onCancel();
    }

    public void getAllDogSittersAndObserve(final GetAllDogSittersAndObserveCallback callback) {
        

        //1. get local lastUpdateDate
        SharedPreferences pref = MyApp.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        final double lastUpdateDate = pref.getFloat("DogSittersLastUpdateDate",0);
        Log.d("TAG","lastUpdateDate: " + lastUpdateDate);

        //2. get updated records from FB
        modelFirebase.getAllDogSittersAndObserve(lastUpdateDate, new ModelFirebase.GetAllDogSittersAndObserveCallback() {
            @Override
            public void onComplete(List<DogSitter> list) {

                double newLastUpdateDate = lastUpdateDate;
                Log.d("TAG", "FB detch:" + list.size());

                for (DogSitter bs: list) {
                    //3. update the local db
                    DogSitterSql.addDogSitter(modelSql.getWritableDatabase(),bs);
                    //4. update the lastUpdateDate
                    if (newLastUpdateDate < bs.lastUpdateDate){
                        newLastUpdateDate = bs.lastUpdateDate;
                    }
                }

                SharedPreferences.Editor prefEd = MyApp.getMyContext().getSharedPreferences("TAG",
                        Context.MODE_PRIVATE).edit();
                prefEd.putFloat("DogSittersLastUpdateDate", (float) newLastUpdateDate);
                prefEd.commit();
                Log.d("TAG","DogSittersLastUpdateDate: " + newLastUpdateDate);

                //5. read from local db
                List<DogSitter> data = DogSitterSql.getAllDogSitters(modelSql.getReadableDatabase());

                //6. return list of dog sitter
                callback.onComplete(data);

            }

            @Override
            public void onCancel() {

            }
        });


    }

    public interface SaveImageListener {
        void complete(String url);
        void fail();
    }

    public void saveImage(final Bitmap imageBmp, final String name, final SaveImageListener listener) {
        modelFirebase.saveImage(imageBmp, name, new SaveImageListener() {
            @Override
            public void complete(String url) {
                String fileName = URLUtil.guessFileName(url, null, null);
                ModelFiles.saveImageToFile(imageBmp,fileName);
                listener.complete(url);
            }

            @Override
            public void fail() {
                listener.fail();
            }
        });


    }


    public interface GetImageListener{
        void onSuccess(Bitmap image);
        void onFail();
    }
    public void getImage(final String url, final GetImageListener listener) {
        //check if image exsist localy
        final String fileName = URLUtil.guessFileName(url, null, null);
        ModelFiles.loadImageFromFileAsynch(fileName, new ModelFiles.LoadImageFromFileAsynch() {
            @Override
            public void onComplete(Bitmap bitmap) {
                if (bitmap != null){
                    Log.d("TAG","getImage from local success " + fileName);
                    listener.onSuccess(bitmap);
                }else {
                    modelFirebase.getImage(url, new GetImageListener() {
                        @Override
                        public void onSuccess(Bitmap image) {
                            String fileName = URLUtil.guessFileName(url, null, null);
                            Log.d("TAG","getImage from FB success " + fileName);
                            ModelFiles.saveImageToFile(image,fileName);
                            listener.onSuccess(image);
                        }

                        @Override
                        public void onFail() {
                            Log.d("TAG","getImage from FB fail ");
                            listener.onFail();
                        }
                    });

                }
            }
        });

    }



}
