package com.primetoxinz.coralreef;

import dev.architectury.registry.client.rendering.*;
import java.util.function.*;
import net.minecraft.client.renderer.*;

public class CoralReefClient {
    public static void init() {
        CoralReef.CORALS.stream().map(Supplier::get).forEach(block -> RenderTypeRegistry.register(RenderType.cutout(), block));
    }
}
