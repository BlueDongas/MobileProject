package com.example.engquiz.yongjin;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.engquiz.MainActivity;
import com.example.engquiz.R;

import java.util.ArrayList;
import java.util.List;

public class MyPadActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPadPrefs";
    private static final String WORD_LIST_KEY = "WordList";

    private MyPadAdapter adapter;
    private List<String> wordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypad);

        // 단어 리스트 초기화
        // 이거 때문에 계속 저장이 안되는 건가
        wordList = new ArrayList<>();

        GridView wordGridView = findViewById(R.id.mypad_grid_view);

        ImageButton addWordButton = findViewById(R.id.mypad_word_add);
        ImageButton goMainButton = findViewById(R.id.go_main_button);

        adapter = new MyPadAdapter(this, wordList);
        wordGridView.setAdapter(adapter);

        addWordButton.setOnClickListener(v -> addWordDialog());

        goMainButton.setOnClickListener(v ->  {
            Intent i = new Intent(MyPadActivity.this, MainActivity.class);
            startActivity(i);
        });

        wordGridView.setOnItemLongClickListener((parent, view, position, id) -> {
            // 수정, 삭제 기능 사용을 위한 dialog button
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("수정 / 삭제");
            alertDialog.setItems(new String[]{"수정", "삭제"}, (dialog, which) -> {
                if (which == 0) {
                    updateWordDialog(position);
                } else if (which == 1) {
                    deleteWordDialog(position);
                }
            }).create().show();

            return true;
        });


        // 저장된 단어들 불러오기
        loadWords();
    }

    @Override
    protected void onDestroy() {
        // Activity 생명주기의 onDestroy에서 단어들 저장
        // aws s3 연결시 필요없을 듯(어차피 저장된거 load만 하니까)
        super.onDestroy();
        saveWords();
    }

    // wordListView longClicked -> 수정 Button -> updateWordDialog
    private void updateWordDialog(int position) {
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
                wordList.set(position, updateWord + "-" + updateMeaning);
                adapter.notifyDataSetChanged();

                Toast.makeText(this, "단어가 수정되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "단어와 뜻을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // wordListView longClicked -> 삭제 button -> deleteWordDialog
    private void deleteWordDialog(int position) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("단어 삭제")
                .setMessage("정말 이 단어를 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> {
                    // 선택한 단어 삭제
                    wordList.remove(position);
                    adapter.notifyDataSetChanged();

                    Toast.makeText(this, "단어가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                }).setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // 단어 추가 dialog
    private void addWordDialog() {
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
                // 중복된 단어일 때(이미 등록)
                if (!isDuplicate(word)) {
                    wordList.add(word + "-" + meaning);
                    adapter.notifyDataSetChanged();

                    Toast.makeText(this, "단어가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "이미 존재하는 단어입니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "단어와 뜻을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // duplicate validation
    private boolean isDuplicate(String word) {
        for (String item : wordList) {
            if (item.startsWith(word)) {
                return true;
            }
        }
        return false;
    }

    // aws s3 사용시 필요 없을 듯?
    private void saveWords() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringBuilder wordListString = new StringBuilder();
        for (String word : wordList) {
            wordListString.append(word).append(";");
        }
        editor.putString(WORD_LIST_KEY, wordListString.toString());
        editor.apply();
    }

    // aws s3 사용시 단어 불러오는 logic 수정
    private void loadWords() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String wordListString = sharedPreferences.getString(WORD_LIST_KEY, "");

        if (!wordListString.isEmpty()) {
            String[] words = wordListString.split(";");
            for (String word : words) {
                wordList.add(word);
            }
        }
    }

}
