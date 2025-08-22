package com.author.example_mod.mixin;

import com.author.example_mod.Main;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "init", at = @At("HEAD"))
    public void initMixinExample(CallbackInfo ci) {
        String baseString = "Hello from %LOADER% on Minecraft %VERSION%";

        /// https://stonecutter.kikugie.dev/stonecutter/guide/comments
        // See build.gradle for the registered swaps
        String loader =  /*$ loader_string {*/"fabric"/*$}*/;
        String version = /*$ minecraft_version_string {*/"1.21.8"/*$}*/;

        baseString = baseString.replace("%LOADER%",loader);
        baseString = baseString.replace("%VERSION%",version);

        Main.LOGGER.info(baseString);
    }
}