package me.pajic.picktool.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import me.pajic.picktool.PickToolConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.tags.TagKey;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayDeque;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow public ServerPlayer player;
	@Unique private final ArrayDeque<ItemStackWithSlot> pt$swapItems = new ArrayDeque<>();

    @ModifyExpressionValue(
            method = "handlePickItemFromBlock",
            at = @At(
                    value = "INVOKE",
					//? fabric
					target = "Lnet/minecraft/world/level/block/state/BlockState;getCloneItemStack(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Lnet/minecraft/world/item/ItemStack;"
					//? neoforge
                    //target = "Lnet/minecraft/world/level/block/state/BlockState;getCloneItemStack(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/LevelReader;ZLnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack tryToolSwap(
            ItemStack original,
			@Local(name = "level") ServerLevel level,
            @Local(name = "blockState") BlockState blockState,
            @Local(name = "includeData") LocalBooleanRef includeData
    ) {
        if (pt$shouldRun(level)) {
            ItemStack mainHandItem = player.getMainHandItem();
            if (mainHandItem.has(DataComponents.TOOL)) {
                Inventory inventory = player.getInventory();
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    ItemStack item = inventory.getItem(i);
                    if (pt$isAllowed(item) && !ItemStack.isSameItemSameComponents(item, mainHandItem) && pt$testTool(item, blockState)) {
						if (!pt$swapItems.isEmpty() && !pt$compareTools(item, pt$swapItems.peekFirst().stack(), blockState)) {
							pt$swapItems.clear();
						}
						if (pt$swapItems.stream().noneMatch(is -> ItemStack.isSameItemSameComponents(is.stack(), item))) {
							pt$swapItems.add(new ItemStackWithSlot(i, item));
						}
                    }
                }
                if (!pt$swapItems.isEmpty()) {
                    int mainHandItemSlot = inventory.findSlotMatchingItem(mainHandItem);
					ItemStackWithSlot swapItem = pt$swapItems.remove();
                    inventory.setItem(mainHandItemSlot, swapItem.stack());
                    inventory.setItem(swapItem.slot(), mainHandItem);
                    includeData.set(false);
                    return ItemStack.EMPTY;
                }
            }
        }
        return original;
    }

	@Unique private boolean pt$shouldRun(ServerLevel level) {
		MinecraftServer server = level.getServer();
		return (server.isDedicatedServer() || server.isSingleplayer()) && !player.hasInfiniteMaterials();
	}

	@Unique private boolean pt$compareTools(ItemStack itemStack1, ItemStack itemStack2, BlockState blockState) {
		return pt$testTool(itemStack1, blockState) == pt$testTool(itemStack2, blockState);
	}

	@Unique private boolean pt$testTool(ItemStack itemStack, BlockState blockState) {
		Tool tool = itemStack.get(DataComponents.TOOL);
		return tool != null && (tool.isCorrectForDrops(blockState) || tool.getMiningSpeed(blockState) > tool.defaultMiningSpeed());
	}

	@Unique private boolean pt$isAllowed(ItemStack itemStack) {
		return PickToolConfig.CONFIG.blacklist().stream().noneMatch(s -> {
			if (s.startsWith("#")) {
				Identifier id = Identifier.tryParse(s.substring(1));
				if (id != null) return itemStack.is(TagKey.create(Registries.ITEM, id));
			}
			else {
				Identifier id = Identifier.tryParse(s);
				if (id != null) return itemStack.is(ResourceKey.create(Registries.ITEM, id));
			}
			return false;
		});
	}
}
