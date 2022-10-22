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

package appeng.datagen.providers.recipes;

import com.google.gson.JsonObject;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.ItemStack;

import appeng.datagen.providers.IAE2DataProvider;

public abstract class AE2RecipeProvider extends net.minecraft.data.recipes.RecipeProvider implements IAE2DataProvider {

    public AE2RecipeProvider(DataGenerator generator) {
        super(generator.getVanillaPackOutput());
    }

    public static JsonObject toJson(ItemStack stack) {
        var stackObj = new JsonObject();
        stackObj.addProperty("item", Registry.ITEM.getKey(stack.getItem()).toString());
        if (stack.getCount() > 1) {
            stackObj.addProperty("count", stack.getCount());
        }
        return stackObj;
    }

    @Override
    public String getName() {
        return "AE2 " + getClass().getSimpleName();
    }
}
