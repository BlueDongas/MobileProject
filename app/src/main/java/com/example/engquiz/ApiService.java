package com.example.engquiz;

import com.example.engquiz.yongjin.Question;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface QuizApi {
    @GET("/get_words") // Flask API 엔드포인트
    Call<List<Question>> getWords(@Query("level") int level); // level 파라미터 전달
}
