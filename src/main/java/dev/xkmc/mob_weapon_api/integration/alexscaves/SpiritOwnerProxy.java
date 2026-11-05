package dev.xkmc.mob_weapon_api.integration.alexscaves;

import com.github.alexmodguy.alexscaves.server.entity.item.DinosaurSpiritEntity;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 给 {@code DinosaurSpiritEntity} 用的 owner 代理。
 * <p>
 * 该实体的 {@code tick()} 通过 {@code getUsingPlayer()} 取 owner，而原版实现只查玩家列表
 * （客户端 {@code getPlayerByUUID}、服务端 {@code getPlayerList().getPlayer}）。傀儡不是玩家，
 * 于是返回 null → {@code setFading(true)} → 之灵只闪一下就消失；下游的
 * {@code tickGrottoceratops} / {@code tickTremorsaurus} 又都要求 {@code Player} 类型，
 * 无法用非玩家驱动。
 * <p>
 * 这里为傀儡 owner 造一个<b>不加入世界</b>的 {@code Player} 代理：位置、tickCount、手持物品
 * 每 tick 从真实 owner 同步。代理只被之灵的 tick 读取，不会被任何世界系统 tick。
 * <p>
 * 客户端拿不到 owner 实体（ClientLevel 没有按 UUID 查实体的接口），所以客户端代理只保证
 * 非 null —— 实体位置由服务端的 {@code lerpTo} 控制，代理位置不影响最终渲染。
 */
public final class SpiritOwnerProxy {

	private static final Map<UUID, Player> CACHE = new HashMap<>();
	private static final Map<UUID, Entity> CLIENT_OWNER = new HashMap<>();
	private static final String PROXY_NAME = "golem_spirit";

	private SpiritOwnerProxy() {
	}

	@Nullable
	public static Player resolve(DinosaurSpiritEntity self) {
		UUID uuid = self.getPlayerUUID();
		if (uuid == null) return null;
		Level level = self.level();
		Player real = level.getPlayerByUUID(uuid);
		if (real != null) return real;
		if (level.isClientSide) return resolveClient(self, uuid);
		LivingEntity owner = findServerOwner(level, uuid);
		if (owner == null) {
			CACHE.remove(uuid);
			return null;
		}
		Player proxy = getOrCreate(level, uuid, owner.blockPosition());
		snapTo(proxy, owner.getX(), owner.getY(), owner.getZ());
		proxy.tickCount = owner.tickCount;
		ItemStack held = owner.getMainHandItem();
		if (!ItemStack.matches(proxy.getMainHandItem(), held)) {
			proxy.setItemInHand(InteractionHand.MAIN_HAND, held);
		}
		// tickGrottoceratops 靠 player.getUseItem() 判断"还在蓄力吗"，必须同步使用状态
		if (owner.isUsingItem()) {
			if (!proxy.isUsingItem()) proxy.startUsingItem(InteractionHand.MAIN_HAND);
		} else if (proxy.isUsingItem()) {
			proxy.stopUsingItem();
		}
		return proxy;
	}

	/**
	 * 把代理还原成真实 owner。代理只是为了让 AC 的 {@code Player} 签名能编译通过，
	 * 绝不能让它出现在伤害源里——别的 mod（如 l2hostility）会把 {@code getEntity()} 强转成
	 * {@code ServerPlayer}，代理不是，直接 {@code ClassCastException} 崩服。
	 */
	public static Entity unwrap(Entity entity) {
		if (!(entity instanceof Player player)) return entity;
		for (Map.Entry<UUID, Player> entry : CACHE.entrySet()) {
			if (entry.getValue() != player) continue;
			if (player.level() instanceof ServerLevel serverLevel) {
				Entity owner = serverLevel.getEntity(entry.getKey());
				if (owner != null) return owner;
			}
			return entity;
		}
		return entity;
	}

	/**
	 * 代理从不参与 {@code Entity#tick}，{@code xOld/yOld/zOld} 不会自己更新。
	 * <p>
	 * 这里手动复刻 {@code tick()} 开头的 {@code xo = getX()}：old 存<b>上一帧</b>的位置，
	 * 再把当前位置换成新值。{@code DinosaurSpiritRenderer} 用 {@code player.getPosition(partialTicks)}
	 * 算朝向，保留上一帧才能让傀儡冲锋/大跳那种瞬移在渲染侧平滑过渡。
	 * <p>
	 * 不能把 old 直接设成新位置——那样插值结果恒等于新位置，等于关掉插值，
	 * 傀儡一瞬移朝向就一帧内跳几十度，表现为龙灵抽搐。
	 */
	private static void snapTo(Player proxy, double x, double y, double z) {
		proxy.setPos(x, y, z);
		// old 直接对齐到新位置：代理每帧被重新定位，保留"上一帧"没有意义——
		// 创建时的初始位置会一直卡在 old 里，渲染器每帧在"初始位置"和"当前位置"
		// 之间插值，代理坐标来回摆，龙灵朝向跟着抖。
		proxy.xOld = x;
		proxy.yOld = y;
		proxy.zOld = z;
	}

	@Nullable
	private static LivingEntity findServerOwner(Level level, UUID uuid) {
		if (level instanceof ServerLevel sl) {
			Entity e = sl.getEntity(uuid);
			return e instanceof LivingEntity living ? living : null;
		}
		return null;
	}

	private static Player resolveClient(DinosaurSpiritEntity self, UUID uuid) {
		Level level = self.level();
		Player proxy = getOrCreate(level, uuid, self.blockPosition());
		// 客户端拿不到 owner 时不能把代理摆在龙灵自己的位置上：
		// DinosaurSpiritRenderer 会用 (playerPos - dinoPos) 算朝向来实现"潮汐锁定"，
		// 两者重合会让 atan2 恒为 0，龙灵就只会平移不会转。这里按 UUID 反查真实 owner。
		// 缓存找到的 owner：entitiesForRendering() 不保证每次都返回同一个集合，
		// 一旦某帧找不到就回退到龙灵自身坐标，朝向会瞬间跳一次，表现为抽搐。
		Entity owner = CLIENT_OWNER.get(uuid);
		if (owner == null || owner.isRemoved() || owner.level() != level) {
			owner = null;
			if (level instanceof net.minecraft.client.multiplayer.ClientLevel clientLevel) {
				for (Entity entity : clientLevel.entitiesForRendering()) {
					if (entity.getUUID().equals(uuid)) {
						owner = entity;
						break;
					}
				}
			}
			if (owner != null) {
				CLIENT_OWNER.put(uuid, owner);
			}

		}
		if (owner != null) {
			proxy.tickCount = owner.tickCount;
			snapTo(proxy, owner.getX(), owner.getY(), owner.getZ());

		}
		// 找不到 owner 时保持代理上一次的位置，但必须把 old 对齐到当前位置：
		// 否则 xOld 和 getX() 停在两个不同的值上，渲染器每帧在两者之间插值，
		// 代理坐标会来回摆，龙灵朝向跟着抖。
		snapTo(proxy, proxy.getX(), proxy.getY(), proxy.getZ());
		if (!proxy.getMainHandItem().is(ACItemRegistry.EXTINCTION_SPEAR.get())) {
			proxy.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ACItemRegistry.EXTINCTION_SPEAR.get()));
		}
		if (!proxy.isUsingItem()) proxy.startUsingItem(InteractionHand.MAIN_HAND);
		return proxy;
	}

	private static Player getOrCreate(Level level, UUID uuid, BlockPos pos) {
		Player proxy = CACHE.get(uuid);
		if (proxy == null || proxy.level() != level) {
			// Player 是抽象类（isSpectator / isCreative），必须匿名实现；代理永不加入世界。
			proxy = new Player(level, pos, 0.0F, new GameProfile(uuid, PROXY_NAME)) {
				@Override
				public boolean isSpectator() {
					return false;
				}

				@Override
				public boolean isCreative() {
					return false;
				}
			};
			CACHE.put(uuid, proxy);
		}
		return proxy;
	}

}
