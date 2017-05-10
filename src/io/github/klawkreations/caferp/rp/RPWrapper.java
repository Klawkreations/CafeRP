package io.github.klawkreations.caferp.rp;

import org.bukkit.entity.Player;

public class RPWrapper {
    private Player player;
    private ERole role;

    public RPWrapper(Player player, ERole role) {
        this.player = player;
        this.role = role;
    }

    public void assignRole(ERole role){
        this.role = role;
    }

    public ERole getRole() {
        return role;
    }

    public Player getPlayer() {
        return player;
    }
}
