/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2017, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.util.inv;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.compat.FixedInventoryVanillaWrapper;
import appeng.util.Platform;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class AdaptorItemHandlerPlayerInv extends AdaptorFixedInv {

    public AdaptorItemHandlerPlayerInv(final PlayerEntity playerInv) {
        super(new FixedInventoryVanillaWrapper(playerInv.inventory));
    }

    /**
     * Tries to fill existing stacks first
     */
    @Override
    protected ItemStack addItems(final ItemStack itemsToAdd, final boolean simulate) {
        if (itemsToAdd.isEmpty()) {
            return ItemStack.EMPTY;
        }

        Simulation sim = simulate ? Simulation.SIMULATE : Simulation.ACTION;

        ItemStack left = itemsToAdd.copy();

        // First try filling slots
        for (int slot = 0; slot < this.itemHandler.getSlotCount(); slot++) {
            ItemStack is = this.itemHandler.getInvStack(slot);

            if (Platform.itemComparisons().isSameItem(is, left)) {
                left = itemHandler.getSlot(slot).attemptInsertion(left, sim);
            }
            if (left.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        return transferable.attemptInsertion(left, sim);
    }

}