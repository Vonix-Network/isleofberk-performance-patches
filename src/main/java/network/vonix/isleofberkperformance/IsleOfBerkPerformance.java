package network.vonix.isleofberkperformance;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import network.vonix.isleofberkperformance.config.PerformanceConfig;
import org.slf4j.Logger;

/**
 * Standalone performance-patch companion for Isle of Berk 1.2.0.
 * It does not replace IoB or bundle the original Isle of Berk implementation.
 */
@Mod(IsleOfBerkPerformance.MOD_ID)
public final class IsleOfBerkPerformance {
    public static final String MOD_ID = "isleofberkperformance";
    public static final Logger LOGGER = LogUtils.getLogger();

    public IsleOfBerkPerformance() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PerformanceConfig.SPEC, "isleofberkperformance.toml");
        LOGGER.info(
                "Isle of Berk Performance Patches loaded (companion only; keep original isleofberk-1.2.0.jar)"
        );
    }
}
