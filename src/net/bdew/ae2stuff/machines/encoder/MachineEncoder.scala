/*
 * Copyright (c) bdew, 2014
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.encoder

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.ae2stuff.network.{MsgSetRecipe, NetHandler}
import net.bdew.lib.Misc
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.Machine
import net.minecraft.entity.player.EntityPlayer

object MachineEncoder extends Machine("Encoder", BlockEncoder) with GuiProvider {
  override def guiId = 1
  override type TEClass = TileEncoder

  lazy val idlePowerDraw = tuning.getDouble("IdlePower")

  @SideOnly(Side.CLIENT)
  override def getGui(te: TEClass, player: EntityPlayer) = new GuiEncoder(new ContainerEncoder(te, player))
  override def getContainer(te: TEClass, player: EntityPlayer) = new ContainerEncoder(te, player)

  NetHandler.regServerHandler {
    case (MsgSetRecipe(recipe), player) =>
      Misc.asInstanceOpt(player.openContainer, classOf[ContainerEncoder]).map { cont =>
        for ((slotNum, recIdx) <- cont.te.slots.recipe.zipWithIndex) {
          cont.te.setInventorySlotContents(slotNum, recipe.get(recIdx).map(_.stack).orNull)
        }
        cont.updateRecipe()
      }
  }
}
