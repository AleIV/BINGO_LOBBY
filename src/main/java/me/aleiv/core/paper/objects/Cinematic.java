package me.aleiv.core.paper.objects;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Cinematic {
    
    List<Frame> frames;
    List<String> viewers;
    String name;

    public Cinematic(String name){
        this.frames = new ArrayList<>();
        this.viewers = new ArrayList<>();
        this.name = name;

    }
}
