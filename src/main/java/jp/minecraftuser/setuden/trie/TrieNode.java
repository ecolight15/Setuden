package jp.minecraftuser.setuden.trie;

import java.util.ArrayList;
import java.util.List;

public class TrieNode {
    private TrieNode[] children;
    private boolean isEndOfWord;
    private List<String> words;
    public TrieNode() {
        children = new TrieNode[37]; // 英字小文字、数値、アンダーバーに対応
        isEndOfWord = false;
        words = new ArrayList<>();
    }

    public TrieNode[] getChildren() {
        return children;
    }

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

    public List<String> getWords() {
        return words;
    }
}