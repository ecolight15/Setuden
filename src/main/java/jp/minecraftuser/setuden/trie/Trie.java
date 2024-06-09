package jp.minecraftuser.setuden.trie;

import java.util.ArrayList;
import java.util.List;

public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            c = Character.toLowerCase(c); // 小文字に変換
            int index = getCharIndex(c);
            if (index == -1) {
                continue; // 英字およびアンダーバー、数値以外を無視
            }
            if (node.getChildren()[index] == null) {
                node.getChildren()[index] = new TrieNode();
            }
            node = node.getChildren()[index];
            node.getWords().add(word); // 単語を追加
        }
        node.setEndOfWord(true);
    }

    public List<String> startsWith(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            c = Character.toLowerCase(c); // 小文字に変換
            int index = getCharIndex(c);
            if (index == -1 || node.getChildren()[index] == null) {
                return new ArrayList<>(); // 該当なし
            }
            node = node.getChildren()[index];
        }
        return node.getWords();
    }

    private int getCharIndex(char c) {
        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        } else if (c >= '0' && c <= '9') {
            return 26 + (c - '0'); // 数字のインデックス（26から35）
        } else if (c == '_') {
            return 36; // アンダーバーのインデックス
        } else {
            return -1; // 英字、数値、およびアンダーバー以外は無視
        }
    }
}