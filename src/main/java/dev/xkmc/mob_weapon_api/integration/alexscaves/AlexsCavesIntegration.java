package dev.xkmc.mob_weapon_api.integration.alexscaves;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;


/**
 * 亚历克斯洞穴（Alex's Caves）武器兼容。
 * <p>
 * 这些物品大多继承 {@code Item} 而不是原版的 {@code SwordItem} / {@code BowItem}，
 * 不会被 {@code WeaponRegistry} 的兜底谓词识别，傀儡会直接忽略它们。这里按实际使用方式逐项登记。
 * <p>
 * 分类依据：
 * <ul>
 *   <li>{@code OFFENSIVE}（近战+远程）：既能挥砍又能远程施法。用它能避免
 *       {@code AbstractWeaponManager.checkSwitch} 在敌人贴近时把武器换到副手（表现为"主副手来回切换"）；</li>
 *   <li>{@code RANGED}：纯远程；</li>
 *   <li>{@code MELEE}：纯近战，命中特效走原生 {@code Item#hurtEnemy}（MG 的近战命中回调会调用它）。</li>
 * </ul>
 * 未登记：{@code QuarrySmasherItem}（采石场专用工具，依赖 {@code QuarryBlockEntity}，不是战斗武器）。
 */
public class AlexsCavesIntegration {

	private static final String MODID = "alexscaves";

	private static ResourceLocation id(String name) {
		return new ResourceLocation(MODID, name);
	}


	public static void init() {
		// ---- 纯近战武器：命中特效走原生 Item#hurtEnemy
		melee("primitive_club", ACItemRegistry.PRIMITIVE_CLUB.get());
		melee("sharpened_candy_cane", ACItemRegistry.SHARPENED_CANDY_CANE.get());
		melee("desolate_dagger", ACItemRegistry.DESOLATE_DAGGER.get());

		// ---- 三叉戟式投掷长矛
		WeaponRegistry.HOLD.register(id("limestone_spear"),
				e -> WeaponStatus.OFFENSIVE.of(e.is(ACItemRegistry.LIMESTONE_SPEAR.get())),
				(golem, stack) -> new SpearBehavior(SpearBehavior.Kind.LIMESTONE), 10);
		WeaponRegistry.HOLD.register(id("extinction_spear"),
				e -> WeaponStatus.OFFENSIVE.of(e.is(ACItemRegistry.EXTINCTION_SPEAR.get())),
				(golem, stack) -> new SpearBehavior(SpearBehavior.Kind.EXTINCTION), 10);
		WeaponRegistry.HOLD.register(id("frostmint_spear"),
				e -> WeaponStatus.OFFENSIVE.of(e.is(ACItemRegistry.FROSTMINT_SPEAR.get())),
				(golem, stack) -> new SpearBehavior(SpearBehavior.Kind.FROSTMINT), 10);

		// ---- 持续使用型。用 OFFENSIVE 避免贴近时被 checkSwitch 换到副手
		WeaponRegistry.HOLD.register(id("ortholance"),
				e -> WeaponStatus.OFFENSIVE.of(e.is(ACItemRegistry.ORTHOLANCE.get())),
				(golem, stack) -> new OrtholanceBehavior(), 10);
		WeaponRegistry.HOLD.register(id("raygun"),
				e -> WeaponStatus.RANGED.of(e.is(ACItemRegistry.RAYGUN.get())),
				(golem, stack) -> new RaygunBehavior(), 10);
		// 方铅护手：GalenaGauntletGolemMixin 对非玩家持有者跳过了"另一只手武器 setCount(0)"，
		// 武器留在傀儡手上，环绕的磁力武器实体照常生成。
		WeaponRegistry.HOLD.register(id("galena_gauntlet"),
				// 纯远程：护手没有攻击数值，也不该拿来近战；用 RANGED 让 checkSwitch 在敌人贴身时
				// 让位给真正的近战武器，而不是像 OFFENSIVE 那样把它当成能挥砍的武器留在主手。
				e -> WeaponStatus.RANGED.of(e.is(ACItemRegistry.GALENA_GAUNTLET.get())),
				(golem, stack) -> new GalenaGauntletBehavior(), 10);

		// ---- 弓类：DreadbowItem 继承 ProjectileWeaponItem 而非 BowItem，兜底谓词覆盖不到
		WeaponRegistry.BOW.register(id("dreadbow"),
				e -> WeaponStatus.RANGED.of(e.is(ACItemRegistry.DREADBOW.get())),
				(golem, stack) -> new DreadbowBehavior(), 10);

		// ---- 瞬发施法型。同样用 OFFENSIVE 避免换手
		WeaponRegistry.INSTANT.register(id("sea_staff"),
				e -> WeaponStatus.RANGED.of(e.is(ACItemRegistry.SEA_STAFF.get())),
				(golem, stack) -> new SeaStaffBehavior(), 10);
		WeaponRegistry.INSTANT.register(id("sugar_staff"),
				e -> WeaponStatus.RANGED.of(e.is(ACItemRegistry.SUGAR_STAFF.get())),
				(golem, stack) -> new SugarStaffBehavior(), 10);
		WeaponRegistry.INSTANT.register(id("shot_gum"),
				e -> WeaponStatus.RANGED.of(e.is(ACItemRegistry.SHOT_GUM.get())),
				(golem, stack) -> new ShotGumBehavior(), 10);

		// ---- 钩锁：远程模式，勾中后才拉回
		WeaponRegistry.INSTANT.register(id("candy_cane_hook"),
				e -> WeaponStatus.RANGED.of(e.is(ACItemRegistry.CANDY_CANE_HOOK.get())),
				(golem, stack) -> new CandyCaneHookBehavior(), 10);

	}

	private static void melee(String name, Item item) {
		WeaponRegistry.INSTANT.register(id(name),
				e -> WeaponStatus.MELEE.of(e.is(item)),
				(golem, stack) -> new MeleeBehavior(), 5);
	}

}
