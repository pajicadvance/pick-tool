package me.pajic.picktool.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import me.pajic.picktool.PickToolConfig;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
@Mixin(Main.class)
public class ClientMainMixin {

	@Inject(
			method = "<clinit>",
			at = @At("HEAD")
	)
	private static void onInit(CallbackInfo ci) {
		PickToolConfig.loadConfig();
	}
}
