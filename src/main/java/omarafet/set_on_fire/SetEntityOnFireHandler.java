package omarafet.set_on_fire;

import java.util.concurrent.ThreadLocalRandom;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SetEntityOnFireHandler {

    public static void register() {
        UseEntityCallback.EVENT.register(SetEntityOnFireHandler::handleEntityUse);
    }

    private static ActionResult handleEntityUse(PlayerEntity player, World world, Hand hand, Entity entity,
            EntityHitResult hitResult) {
        try {
            // Ensure server-side execution
            if (world.isClient()) {
                return ActionResult.PASS;
            }

            // Ensure the entity is a LivingEntity
            if (!(entity instanceof LivingEntity livingEntity)) {
                return ActionResult.PASS;
            }

            // Check if the player is holding Flint and Steel
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.getItem() != Items.FLINT_AND_STEEL) {
                return ActionResult.PASS;
            }

            // Check if the mob is already on fire
            if (livingEntity.isOnFire()) {
                return ActionResult.FAIL;
            }

            // Set fire duration closer to Minecraft standards (between 5 to 15 seconds)
            int fireDuration = ThreadLocalRandom.current().nextInt(5, 15 + 1);
            livingEntity.setOnFireFor(fireDuration);

            // Damage the Flint and Steel item
            EquipmentSlot slot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            itemStack.damage(1, player, slot);

            // Play the Flint and Steel use sound
            BlockPos entityPos = entity.getBlockPos();
            world.playSound(null, entityPos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);

            // Swing the player's hand
            player.swingHand(hand, true);

            return ActionResult.SUCCESS;

        } catch (Exception e) {
            return ActionResult.FAIL;
        }
    }
}