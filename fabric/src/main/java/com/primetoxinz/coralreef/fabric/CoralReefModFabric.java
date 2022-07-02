package com.primetoxinz.coralreef.fabric;

import com.primetoxinz.coralreef.*;
import net.fabricmc.api.*;

public class CoralReefModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CoralReef.init();
        CoralReef.postInit();
    }


}
