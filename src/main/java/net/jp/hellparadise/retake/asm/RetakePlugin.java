package net.jp.hellparadise.retake.asm;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.jp.hellparadise.retake.Tags;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zone.rong.mixinbooter.IEarlyMixinLoader;

@IFMLLoadingPlugin.Name("RetakePlugin")
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.TransformerExclusions({"net.jp.hellparadise.retake.asm"})
@IFMLLoadingPlugin.SortingIndex(1000)
public class RetakePlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME + "Plugin");

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{
            "net.jp.hellparadise.retake.asm.transformer.GuiGameOverTransformer"
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.retake.json");
    }

}
