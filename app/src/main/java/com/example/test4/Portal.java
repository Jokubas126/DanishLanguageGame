package com.example.test4;

import android.animation.ObjectAnimator;
import android.widget.ImageView;

class Portal extends GameObject {

    private ImageView map;
    private int offsetWorldX;
    private int offsetWorldY;

    // locations of x and y in the grid system (can be found in the map_structure file in raw)
    // it defines a place, where the player needs to be sent
    private int destGridX;
    private int destGridY;

    Portal (ImageView map, int portalGridX, int portalGridY, int offsetWorldX, int offsetWorldY, int gridX, int gridY){
        this.map = map;
        this.setXGrid(portalGridX);
        this.setYGrid(portalGridY);
        this.offsetWorldX = offsetWorldX;
        this.offsetWorldY = offsetWorldY;
        destGridX = gridX;
        destGridY = gridY;
    }
    public void teleport(PlayerObject player)
    {
        movePlayer(player);
        moveMap();
    }
    private void movePlayer(PlayerObject player)
    {
        player.setXGrid(destGridX);
        player.setYGrid(destGridY);
    }
    private void moveMap (){
        float moveY = map.getY() - offsetWorldY;
        ObjectAnimator animation = ObjectAnimator.ofFloat(map, "y", map.getY(), moveY);
        animation.setDuration(0);
        animation.start();

        float moveX = map.getX() - offsetWorldX;
        ObjectAnimator animation2 = ObjectAnimator.ofFloat(map, "x", map.getX(), moveX);
        animation2.setDuration(0);
        animation2.start();
    }
}
