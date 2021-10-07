/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2021, TeamAppliedEnergistics, All rights reserved.
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

package appeng.parts.crafting;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.util.AECableType;
import appeng.core.AppEng;
import appeng.core.definitions.AEParts;
import appeng.helpers.iface.DualityPatternProvider;
import appeng.helpers.iface.IPatternProviderHost;
import appeng.items.parts.PartModels;
import appeng.menu.MenuLocator;
import appeng.menu.MenuOpener;
import appeng.parts.BasicStatePart;
import appeng.parts.PartModel;

public class PatternProviderPart extends BasicStatePart implements IPatternProviderHost {

    public static final ResourceLocation MODEL_BASE = new ResourceLocation(AppEng.MOD_ID,
            "part/pattern_provider_base");

    // TODO: unify the following between the 3 interface parts?
    @PartModels
    public static final PartModel MODELS_OFF = new PartModel(MODEL_BASE,
            new ResourceLocation(AppEng.MOD_ID, "part/item_interface_off"));

    @PartModels
    public static final PartModel MODELS_ON = new PartModel(MODEL_BASE,
            new ResourceLocation(AppEng.MOD_ID, "part/item_interface_on"));

    @PartModels
    public static final PartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE,
            new ResourceLocation(AppEng.MOD_ID, "part/item_interface_has_channel"));

    private final DualityPatternProvider duality;

    public PatternProviderPart(ItemStack is) {
        super(is);
        this.duality = new DualityPatternProvider(this.getMainNode(), this);
    }

    @Override
    public void saveChanges() {
        getHost().markForSave();
    }

    @Override
    public void getBoxes(final IPartCollisionHelper bch) {
        bch.addBox(2, 2, 14, 14, 14, 16);
        bch.addBox(5, 5, 12, 11, 11, 14);
    }

    @Override
    public void readFromNBT(final CompoundTag data) {
        super.readFromNBT(data);
        this.duality.readFromNBT(data);
    }

    @Override
    public void writeToNBT(final CompoundTag data) {
        super.writeToNBT(data);
        this.duality.writeToNBT(data);
    }

    @Override
    public void addToWorld() {
        super.addToWorld();
        this.duality.updatePatterns();
    }

    @Override
    public void getDrops(final List<ItemStack> drops, final boolean wrenched) {
        this.duality.addDrops(drops);
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 4;
    }

    @Override
    public boolean onPartActivate(final Player p, final InteractionHand hand, final Vec3 pos) {
        if (!p.getCommandSenderWorld().isClientSide()) {
            MenuOpener.open(getMenuType(), p, MenuLocator.forPart(this));
        }
        return true;
    }

    @Override
    public ItemStack getItemStackRepresentation() {
        return AEParts.PATTERN_PROVIDER.stack();
    }

    @Override
    public DualityPatternProvider getDuality() {
        return duality;
    }

    @Override
    public EnumSet<Direction> getTargets() {
        return EnumSet.of(this.getSide());
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capabilityClass) {
        return this.duality.getCapability(capabilityClass);
    }
}