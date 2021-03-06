package com.example.android.customerapp.viewmodels;

import android.util.Log;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.customerapp.models.Member;
import com.example.android.customerapp.requests.BackendAPIClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel {

    public MediatorLiveData<Member> mMember;

    public RegisterViewModel() {
        mMember = new MediatorLiveData<>();
    }

    public void memberRegister(Member member) throws NoSuchAlgorithmException {
        member.setPassword(toSHA(member.getPassword()));
        BackendAPIClient.getInstance().memberRegister(member).enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                if (response.code() == 200) {
                    Log.e("Register", "Success " + response.body().id);
                    mMember.setValue(response.body());
                } else {
                    Log.e("Register", "Failed " + response.code());
                }

            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                Log.e("API", "FAIL");
            }
        });
    }

    private String toSHA(String pwd) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(pwd.getBytes());

        byte byteData[] = md.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
