package com.farminghelper.speaax.Patch;

public class ItemRequirement
{
    private int itemId;

    private int quantity;

    public ItemRequirement(int itemId, int quantity)
    {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public int getItemId()
    {
        return itemId;
    }

    public int getQuantity()
    {
        return quantity;
    }
}
