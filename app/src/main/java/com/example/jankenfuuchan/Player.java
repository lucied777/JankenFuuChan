package com.example.jankenfuuchan;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Player extends RealmObject {
    @PrimaryKey
    public long id;
    public String name;
    public byte[] byteReady;
    public byte[] byteGuu;
    public byte[] byteChoki;
    public byte[] bytePaa;
    public byte[] byteWin;
    public byte[] byteLose;


}
