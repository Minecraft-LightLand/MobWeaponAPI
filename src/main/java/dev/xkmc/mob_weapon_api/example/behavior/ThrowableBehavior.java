package dev.xkmc.mob_weapon_api.example.behavior;

import dev.xkmc.l2core.init.reg.ench.EnchHelper;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IHoldWeaponBehavior;
import dev.xkmc.mob_weapon_api.util.ShootUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.Nullable;

public abstract class ThrowableBehavior implements IHoldWeaponBehavior {

	@Override
	public double range(LivingEntity user, ItemStack stack) {
		return 25;
	}

	@Override
	public int holdTime(LivingEntity user, ItemStack stack) {
		return 20;
	}

	public float getSpeed(ItemStack stack, Projectile proj) {
		return 3f;
	}

	public float getGravity(ItemStack stack, Projectile proj) {
		return 0.05f;
	}

	@Nullable
	protected abstract Projectile getProjectile(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time);

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time) {
		if (EnchHelper.getLv(stack, Enchantments.LOYALTY) > 0) {
			var reg = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
			if (reg != null) {
				stack = stack.copy();
				var map = stack.getAllEnchantments(reg);
				var mut = new ItemEnchantments.Mutable(map);
				mut.set(reg.getOrThrow(Enchantments.LOYALTY), 0);
				stack.set(DataComponents.ENCHANTMENTS, mut.toImmutable());
			}
		}

		Projectile proj = getProjectile(user, stack, target, time);
		if (proj == null) return 20;
		ShootUtils.shootAimHelper(target, proj, getSpeed(stack, proj), getGravity(stack, proj));
		user.user().playSound(SoundEvents.TRIDENT_THROW.value(), 1.0F, 1.0F / (user.user().getRandom().nextFloat() * 0.4F + 0.8F));
		proj.getPersistentData().putInt("DespawnFactor", 20);
		user.user().level().addFreshEntity(proj);
		if (!user.bypassAllConsumption())
			stack.hurtAndBreak(1, user.user(), EquipmentSlot.MAINHAND);
		return 5;
	}

}
