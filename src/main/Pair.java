package main;

public class Pair <U, V> {
    public U first;
    public V second;
    public boolean special = false;
    public int winX1 = -1;
    public int winY1 = -1;
    public int winX2 = -1;
    public int winY2 = -1;

    public Pair(U first, V second) {
        this.first = first;
        this.second = second;
    }
}
