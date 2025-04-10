import com.endtinkers.Endtinkers;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

public class endtinkersMaterials {

    public static final MaterialId dot_item = createMaterial("dot_item");

    public static MaterialId createMaterial(String name) {
        return new MaterialId(new ResourceLocation(Endtinkers.MODID, name)); //
    }
}
