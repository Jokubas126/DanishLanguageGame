package com.example.test4;

class GameObject  {
    //locations of x and y in the system, which can be found in map_structure file in raw
    private int xGrid;
    private int yGrid;

    //----------------------------------------------------------------------------------------------
    //Setters
    public void setXGrid(int xGrid) {
        this.xGrid = xGrid;
    }

    public void setYGrid(int yGrid) {
        this.yGrid = yGrid;
    }
    //----------------------------------------------------------------------------------------------
    //Getters

    public int getXGrid() { return xGrid; }

    public int getYGrid() { return yGrid; }
}
