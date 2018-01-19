package com.sagi.ori.dogit.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;



import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import com.sagi.ori.dogit.AddOwnerFragment;
import com.sagi.ori.dogit.LoginFragment;
import com.sagi.ori.dogit.MainActivity;

/**
 * Created by Ori and sagi on 17/01/2018.
 */

public class ModelFirebase {

  // add bs to fire base
    public void addDogSitter(DogSitter dogSitter) {

        final DogSitter bs = dogSitter;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(bs.email, bs.password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("dogSitter");
                            Map<String, Object> value = new HashMap<>();
                            value.put("email", bs.email);
                            value.put("name", bs.name);
                            value.put("imageUrl", bs.imageUrl);
                            value.put("password", bs.password);
                            value.put("address", bs.address);
                            value.put("age", bs.age);
                            value.put("availability", bs.availability);
                            value.put("salary", bs.salary);
                            value.put("phone", bs.phone);
                            value.put("lastUpdateDate", ServerValue.TIMESTAMP);
                            myRef.child(encodeUserEmail(bs.email)).setValue(value);
                        } else {

                        }

                    }
                });

    }





    public interface GetLoginCallback {
        void onSuccess();
        void onFail();
    }

    // login to fire base
    public void login(String email,String password,final GetLoginCallback callback) {

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            callback.onSuccess();
                        }
                    }
                });



    }

// add owner to fire base
        public void addOwner(Owner owner) {

        final Owner p = owner;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(owner.email, owner.password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Owner");
                            Map<String, Object> value = new HashMap<>();
                            value.put("email", p.email);
                            value.put("name", p.name);
                            value.put("password", p.password);
                            value.put("address", p.address);
                            myRef.child(encodeUserEmail(p.email)).setValue(value);
                        } else {

                        }

                    }
                });
    }

    // change email - from points to , - because fire base child cant fill points
    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

    interface GetDogSitterCallback {
        void onComplete(DogSitter bs);

        void onCancel();
    }

    // get bs by email from fire base
    public void getDogSitter(String email, final GetDogSitterCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("dogSitter");
        myRef.child(encodeUserEmail(email)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DogSitter bs = dataSnapshot.getValue(DogSitter.class);
                bs.email = decodeUserEmail(bs.email);
                callback.onComplete(bs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });
    }


    interface GetAllDogSittersAndObserveCallback {
        void onComplete(List<DogSitter> list);
        void onCancel();
    }

    public void getAllDogSittersAndObserve(final GetAllDogSittersAndObserveCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("dogSitter");
        ValueEventListener listener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<DogSitter> list = new LinkedList<DogSitter>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    DogSitter dogSitter = snap.getValue(DogSitter.class);
                    dogSitter.email = decodeUserEmail(dogSitter.email);
                    list.add(dogSitter);
                }
                callback.onComplete(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });
    }

    // get bs and observe on last update
    public void getAllDogSittersAndObserve(double lastUpdateDate,
                                         final GetAllDogSittersAndObserveCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("dogSitter");

        myRef.orderByChild("lastUpdateDate").startAt(lastUpdateDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<DogSitter> list = new LinkedList<DogSitter>();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            DogSitter dogSitter = snap.getValue(DogSitter.class);
                            dogSitter.email = decodeUserEmail(dogSitter.email);
                            list.add(dogSitter);
                        }
                        callback.onComplete(list);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



    public void saveImage(Bitmap imageBmp, String name, final Model.SaveImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference imagesRef = storage.getReference().child("images").child(name);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.fail();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                listener.complete(downloadUrl.toString());
            }
        });
    }


    public void getImage(String url, final Model.GetImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(3* ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                listener.onSuccess(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Log.d("TAG",exception.getMessage());
                listener.onFail();
            }
        });
    }

}
