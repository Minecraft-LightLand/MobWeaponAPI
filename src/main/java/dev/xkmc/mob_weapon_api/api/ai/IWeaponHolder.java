package dev.xkmc.mob_weapon_api.api.ai;

import dev.xkmc.mob_weapon_api.example.vanilla.SimpleUser;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;

public interface IWeaponHolder {

	static <E extends Mob> IWeaponHolder simple(E mob) {
		return new SimpleUser(mob);
	}

	InteractionHand getWeaponHand();

	void setInRangeAttack(boolean b);

	ISmartUser toUser();

}
