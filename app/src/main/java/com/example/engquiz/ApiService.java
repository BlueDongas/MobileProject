package com.example.engquiz;

import com.example.engquiz.config.LoginRequest;
import com.example.engquiz.config.LoginResponse;
import com.example.engquiz.config.SignupRequest;
import com.example.engquiz.yongjin.Question;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

import java.util.List;

public interface ApiService {
    @POST("/signup")
    Call<Void> signup(@Body SignupRequest request);

    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("/get_words") // Flask API 엔드포인트
    Call<List<Question>> getWords(@Query("level") int level); // level 파라미터 전달

    @GET("/get_my_words")
    Call<List<WordItem>> getMyWords();

    @POST("/save_my_word")
    Call<Void> saveMyWord(@Body WordItem word);

    @DELETE("/my_word")
    Call<Void> deleteMyWord(@Query("word") String word, @Query("mean") String mean);

    @PUT("/my_word")
    Call<Void> updateMyWord(@Body WordUpdateRequest request);

    @GET("/load_my_word")
    Call<List<Question>> loadMyWord();


}
