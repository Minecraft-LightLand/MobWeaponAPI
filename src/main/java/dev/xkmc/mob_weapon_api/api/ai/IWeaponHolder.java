package dev.xkmc.mob_weapon_api.api.ai;

import net.minecraft.world.InteractionHand;

public interface IWeaponHolder {

	InteractionHand getWeaponHand();

	void setInRangeAttack(boolean b);

	ISmartUser toUser();

}
