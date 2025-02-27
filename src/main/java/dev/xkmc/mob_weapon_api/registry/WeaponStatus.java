package dev.xkmc.mob_weapon_api.registry;

import java.util.Optional;

public record WeaponStatus(
		boolean isMelee,
		boolean isRanged,
		boolean isShield,
		int priority
) {

	public static final WeaponStatus MELEE = new WeaponStatus(true, false, false, 0);
	public static final WeaponStatus RANGED = new WeaponStatus(false, true, false, 0);
	public static final WeaponStatus SHIELD = new WeaponStatus(false, false, true, 0);
	public static final WeaponStatus OFFENSIVE = new WeaponStatus(true, true, false, 0);
	public static final WeaponStatus BATTLE_SHIELD = new WeaponStatus(true, false, true, 0);

	public Optional<WeaponStatus> of(boolean enabled) {
		return enabled ? Optional.of(this) : Optional.empty();
	}

	public WeaponStatus withPriority(int p) {
		return new WeaponStatus(isMelee, isRanged, isShield, p);
	}

}
