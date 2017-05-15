package io.github.klawkreations.caferp.rp.roles;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.klawkreations.caferp.rp.ERole;

public class Guard {
	public static String cuff(RolePlayer instigator, RolePlayer target) {
        if (target.getPlayer() == null) {
            return "Unable to cuff " + target.getName();
        }

        if (instigator.getLocation().distance(target.getLocation()) < 5.00 &&
                instigator.getRole() == ERole.OFFICER){
            target.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10000, 100));
            return "Successfully cuffed " + target.getName() + "!";
        }
        return "Unable to cuff " + target.getName() + "!";
    }
	
	public static String unCuff(RolePlayer instigator, RolePlayer target) {
        if (target == null) {
            return "Unable to uncuff targeted player as they may not exist";
        }

        if (instigator.getLocation().distance(target.getLocation()) < 5.00 &&
                instigator.getRole() == ERole.OFFICER){
            target.getPlayer().removePotionEffect(PotionEffectType.SLOW);
            return "Successfully uncuffed " + target.getName() + "!";
        }
        return "Unable to uncuff " + target.getName() + "!";
    }
}
