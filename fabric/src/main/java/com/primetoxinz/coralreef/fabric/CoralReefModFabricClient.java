
package com.primetoxinz.coralreef.fabric;

import com.primetoxinz.coralreef.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.*;

public class CoralReefModFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CoralReefClient.init();
    }
}
