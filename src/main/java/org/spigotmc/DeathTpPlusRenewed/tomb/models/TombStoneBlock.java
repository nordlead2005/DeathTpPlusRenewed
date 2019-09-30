package org.spigotmc.DeathTpPlusRenewed.tomb.models;

/**
 * PluginName: DeathTpPlusRenewed
 * Class: TombStoneBlock
 * User: DonRedhorse
 * Date: 19.10.11
 * Time: 22:21
 */

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class TombStoneBlock {
    private Block block;
    private Block lBlock;
    private Block sign;
    private Sign BlockLockerSign;
    private long time;
    private String owner;
    private boolean lwcEnabled = false;
    private int droppedExperience;

    public TombStoneBlock(Block block, Block lBlock, Block sign, String owner,
                             long time, int droppedExperience) {
        this.block = block;
        this.lBlock = lBlock;
        this.sign = sign;
        this.owner = owner;
        this.time = time;
        this.droppedExperience = droppedExperience;
    }

    public TombStoneBlock(Block block, Block lBlock, Block sign, String owner, long time,
                             boolean lwc, int droppedExperience) {
        this.block = block;
        this.lBlock = lBlock;
        this.sign = sign;
        this.owner = owner;
        this.time = time;
        this.droppedExperience = droppedExperience;
        this.lwcEnabled = lwc;
    }

    public int getDroppedExperience() {
        return droppedExperience;
    }

    public long getTime() {
        return time;
    }

    public Block getBlock() {
        return block;
    }

    public Block getLBlock() {
        return lBlock;
    }

    public Block getSign() {
        return sign;
    }

    public Sign getBlockLockerSign() {
        return BlockLockerSign;
    }

    public String getOwner() {
        return owner;
    }

    public boolean getLwcEnabled() {
        return lwcEnabled;
    }


    public void setLwcEnabled(boolean val) {
        lwcEnabled = val;
    }

    public void setBlockLockerSign(Sign sign) {
        this.BlockLockerSign = sign;
    }

    public void removeBlockLockerSign() {
        this.BlockLockerSign = null;
    }

    public void clearExperience() {
        droppedExperience = 0;
    }

}

