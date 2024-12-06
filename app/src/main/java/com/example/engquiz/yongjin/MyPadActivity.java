package com.example.engquiz.yongjin;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.engquiz.ApiService;
import com.example.engquiz.MainActivity;
import com.example.engquiz.R;
import com.example.engquiz.RetrofitClient;
import com.example.engquiz.WordItem;
import com.example.engquiz.WordUpdateRequest;
import com.example.engquiz.config.LoginActivity;
import com.example.engquiz.sheunghoon.SubjectiveActivity;
import com.example.engquiz.sheunghoon.SubjectiveQuizActivity;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPadActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPadPrefs";
    private static final String WORD_LIST_KEY = "WordList";

    private MyPadAdapter adapter;
    private List<String> wordList;
    private Button goQuizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypad);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = prefs.getString("jwtToken", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MyPadActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        else{
            Log.d("QuizActivity","JWT Token : "+token);
        }
        ApiService apiService = RetrofitClient.getClient(token,this).create(ApiService.class);

        // 단어 리스트 초기화
        // 이거 때문에 계속 저장이 안되는 건가
        wordList = new ArrayList<>();

        GridView wordGridView = findViewById(R.id.mypad_grid_view);

        ImageButton addWordButton = findViewById(R.id.mypad_word_add);
        ImageButton goMainButton = findViewById(R.id.go_main_button);
        addWordButton = findViewById(R.id.mypad_word_add);
        goMainButton = findViewById(R.id.go_main_button);
        goQuizButton = findViewById(R.id.go_shuenghoon_quiz);

        adapter = new MyPadAdapter(this, wordList);
        wordGridView.setAdapter(adapter);

        addWordButton.setOnClickListener(v -> addWordDialog(apiService));

        goMainButton.setOnClickListener(v ->  {
            Intent i = new Intent(MyPadActivity.this, MainActivity.class);
            startActivity(i);
        });
        goQuizButton.setOnClickListener(v->{
            Intent i = new Intent(MyPadActivity.this, SubjectiveQuizActivity.class);
            i.putExtra("Quizflag","myquiz");
            startActivity(i);
        });

        wordGridView.setOnItemLongClickListener((parent, view, position, id) -> {
            // 수정, 삭제 기능 사용을 위한 dialog button
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("수정 / 삭제");
            alertDialog.setItems(new String[]{"수정", "삭제"}, (dialog, which) -> {
                if (which == 0) {
                    updateWordDialog(position,apiService);
                } else if (which == 1) {
                    deleteWordDialog(position,apiService);
                }
            }).create().show();

            return true;
        });


        // 저장된 단어들 불러오기
        loadWordsFromServer(apiService);
    }


    // wordListView longClicked -> 수정 Button -> updateWordDialog
    private void updateWordDialog(int position,ApiService apiService) {
        // dialog 생성
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("단어 수정");

        // 정의한 dialog xml 넣기
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_mypad_word_add_dialog, null);
        alertDialog.setView(dialogView);

        // 수정할 단어, 뜻 입력
        EditText wordInput = dialogView.findViewById(R.id.input_word);
        EditText meaningInput = dialogView.findViewById(R.id.input_meaning);

        String[] wordAndMeaning = wordList.get(position).split("-");

        wordInput.setText(wordAndMeaning[0]);
        meaningInput.setText(wordAndMeaning[1]);

        alertDialog.setPositiveButton("수정", (dialog, which) -> {
            // 입력한 단어, 뜻 가져오기
            String updateWord = wordInput.getText().toString().trim();
            String updateMeaning = meaningInput.getText().toString().trim();

            // 수정한 단어, 뜻을 모두 작성했다면
            if (!updateWord.isEmpty() && !updateMeaning.isEmpty()) {
                updateWordToServer(wordAndMeaning[0],wordAndMeaning[1],updateWord,updateMeaning,apiService);
            } else {
                Toast.makeText(this, "단어와 뜻을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void updateWordToServer(String oldWord,String oldMean,String newWord,String newMean,ApiService apiService){
        WordUpdateRequest request = new WordUpdateRequest();
        request.setOldWord(oldWord);
        request.setNewWord(newWord);
        request.setOldMean(oldMean);
        request.setNewMean(newMean);

        apiService.updateMyWord(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(MyPadActivity.this, "단어가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    loadWordsFromServer(apiService);
                }
                else {
                    Toast.makeText(MyPadActivity.this, "수정 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MyPadActivity.this, "API 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // wordListView longClicked -> 삭제 button -> deleteWordDialog
    private void deleteWordDialog(int position, ApiService apiService) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("단어 삭제")
                .setMessage("정말 이 단어를 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> {
                    String[] wordAndMean = wordList.get(position).split("-");
                    deleteWordFromServer(wordAndMean[0],wordAndMean[1],apiService);
                }).setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void deleteWordFromServer(String word,String meaning,ApiService apiService){
        apiService.deleteMyWord(word,meaning).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MyPadActivity.this, "단어가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    loadWordsFromServer(apiService);
                }else{
                    Toast.makeText(MyPadActivity.this, "삭제 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MyPadActivity.this, "API 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 단어 추가 dialog
    private void addWordDialog(ApiService apiService) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("단어 추가");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_mypad_word_add_dialog, null);
        alertDialog.setView(dialogView);

        // dialog에서 editText를 사용
        EditText wordInput = dialogView.findViewById(R.id.input_word);
        EditText meaningInput = dialogView.findViewById(R.id.input_meaning);

        alertDialog.setPositiveButton("추가", (dialog, which) -> {
            String word = wordInput.getText().toString().trim();
            String meaning = meaningInput.getText().toString().trim();

            if (!word.isEmpty() && !meaning.isEmpty()) {
                saveWordToServer(word,meaning,apiService);
            } else {
                Toast.makeText(this, "단어와 뜻을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void saveWordToServer(String word, String meaning, ApiService apiService){
        WordItem wordItem = new WordItem();
        wordItem.setWord(word);
        wordItem.setMean(meaning);

        apiService.saveMyWord(wordItem).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Toast.makeText(MyPadActivity.this,"단어가 추가 되었습니다.",Toast.LENGTH_SHORT).show();
                    loadWordsFromServer(apiService);
                }else{
                    Toast.makeText(MyPadActivity.this,"단어 추가 실패",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MyPadActivity.this,"API 오류 : "+t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWordsFromServer(ApiService apiService) {
        apiService.getMyWords().enqueue(new Callback<List<WordItem>>() {
            @Override
            public void onResponse(Call<List<WordItem>> call, Response<List<WordItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    wordList.clear();

                    // 단어가 없는 경우 처리
                    if (response.body().isEmpty()) {
                        Toast.makeText(MyPadActivity.this, "저장된 단어가 없습니다. 단어를 추가해 보세요!", Toast.LENGTH_SHORT).show();
                    } else {
                        // 단어가 있으면 리스트에 추가
                        for (WordItem word : response.body()) {
                            wordList.add(word.getWord() + "-" + word.getMean());
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MyPadActivity.this, "단어를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<WordItem>> call, Throwable t) {
                Toast.makeText(MyPadActivity.this, "API 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
