package be.ephys.whitherdimfilter.mixins;

import com.google.common.collect.Lists;
import net.minecraft.block.WitherSkeletonSkullBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(WitherSkeletonSkullBlock.class)
public class WitherSkeletonSkullBlockMixin {
  @Redirect(
    method = "checkSpawn",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/block/pattern/BlockPattern;find(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/pattern/BlockPattern$PatternHelper;")
  )
  private static BlockPattern.PatternHelper checkSpawn$preventForDim(BlockPattern blockPattern, IWorldReader worldReader, BlockPos pos) {
    World world = (World) worldReader;

    BlockPattern.PatternHelper structure = blockPattern.find(world, pos);
    if (structure == null) {
      return null;
    }

    // the_nether
    // the_end
    // overworld
    if (!world.dimension().location().toString().equals("minecraft:the_nether")) {
      List<PlayerEntity> players = getNearbyPlayers(world, new AxisAlignedBB(pos).inflate(16));

      for (PlayerEntity player : players) {
        player.displayClientMessage(new TranslationTextComponent("mods.whitherdimfilter.cannot-summon-here"), true);
      }

      return null;
    }

    return structure;
  }

  private static List<PlayerEntity> getNearbyPlayers(World world, AxisAlignedBB bb) {
    List<PlayerEntity> list = Lists.newArrayList();

    for (PlayerEntity playerentity : world.players()) {
      if (bb.contains(playerentity.getX(), playerentity.getY(), playerentity.getZ())) {
        list.add(playerentity);
      }
    }

    return list;
  }
}
