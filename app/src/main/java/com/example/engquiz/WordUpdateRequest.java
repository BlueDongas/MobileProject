package com.example.engquiz;

public class WordUpdateRequest {
    private String oldWord;
    private String oldMean;
    private String newWord;
    private String newMean;

    // Getter and Setter
    public String getOldWord() { return oldWord; }
    public void setOldWord(String oldWord) { this.oldWord = oldWord; }

    public String getOldMean() { return oldMean; }
    public void setOldMean(String oldMean) { this.oldMean = oldMean; }

    public String getNewWord() { return newWord; }
    public void setNewWord(String newWord) { this.newWord = newWord; }

    public String getNewMean() { return newMean; }
    public void setNewMean(String newMean) { this.newMean = newMean; }
}
