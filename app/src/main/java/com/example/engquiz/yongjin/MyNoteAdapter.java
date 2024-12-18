package com.example.engquiz.yongjin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.engquiz.R;

import java.util.List;

public class MyNoteAdapter extends BaseAdapter {

    private final Context context;
    private final List<String> wordList;

    public MyNoteAdapter(Context context, List<String> wordList) {
        this.context = context;
        this.wordList = wordList;
    }

    @Override
    public int getCount() {
        return wordList.size();
    }

    @Override
    public Object getItem(int position) {
        return wordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 시험에도 나오겠지만 adapter를 구현하여 view와 data 연걸
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_mypad_item, parent, false);
        }

//        TextView textView = null;
//        textView.setText("나만의 단어장");

        // 단어, 뜻의 String array를 - 로 분리하여 각각 저장
        String[] wordAndMeaning = wordList.get(position).split("-");

        String word = wordAndMeaning[0];
        String meaning = wordAndMeaning[1];

        // listView에 나타낼 단어와 뜻
        TextView wordText = convertView.findViewById(R.id.word_text);
        TextView meaningText = convertView.findViewById(R.id.meaning_text);

        // 순번을 추가하여 텍스트 설정
        wordText.setText((position + 1) + ". 단어: " + word);
        meaningText.setText((position+1) + ".   뜻  : " + meaning);

        return convertView;
    }
}
