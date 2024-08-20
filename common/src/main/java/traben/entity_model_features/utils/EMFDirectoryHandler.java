package traben.entity_model_features.utils;


import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMFManager;

import java.util.List;

import static traben.entity_model_features.utils.EMFDirectoryHandler.EMFDirectory.*;

public class EMFDirectoryHandler {

    public final String namespace;
    public final String rawFileName;
    private final boolean isSubFolder;
    private final int packIndex;
    private final EMFDirectory actualDirectory;
    private final String suffixAndFileType;
    private EMFDirectoryHandler(String namespace, String modelFileName, String suffixAndFileType, boolean printing) {
        this.namespace = namespace;
        this.rawFileName = modelFileName;
        this.suffixAndFileType = suffixAndFileType;

        //find the actual directory for this model file
        //we have to check all to ascertain the resource pack with highest priority

        var resources = Minecraft.getInstance().getResourceManager();

        var emfDirResource = getResourceOrNull(resources, EMF, printing);
        var emfSubDirResource = getResourceOrNull(resources, EMF_SUB, printing);
        var optifineDirResource = getResourceOrNull(resources, OPTIFINE, printing);
        var optifineSubDirResource = getResourceOrNull(resources, OPTIFINE_SUB, printing);


        if (emfDirResource == null && emfSubDirResource == null && optifineDirResource == null && optifineSubDirResource == null) {
            actualDirectory = null;
            packIndex = -1;
            isSubFolder = false;
            return;//quick exit as no models were found
        }

        var emfPack = getPackId(emfDirResource);
        var emfSubPack = getPackId(emfSubDirResource);
        var optifinePack = getPackId(optifineDirResource);
        var optifineSubPack = getPackId(optifineSubDirResource);

        var packOrder = EMFManager.getInstance().getResourcePackList();

        int emfDirIndex = getPackIndex(emfPack, packOrder);
        int emfSubDirIndex = getPackIndex(emfSubPack, packOrder);
        int optifineDirIndex = getPackIndex(optifinePack, packOrder);
        int optifineSubDirIndex = getPackIndex(optifineSubPack, packOrder);

        int emfHighest = Math.max(emfDirIndex, emfSubDirIndex);
        int optifineHighest = Math.max(optifineDirIndex, optifineSubDirIndex);

        if (printing)
            EMFUtils.log(" >>>> pack order indices: " + emfDirIndex + ", " + emfSubDirIndex + ", " + optifineDirIndex + ", " + optifineSubDirIndex);

        //prioritise emf if same pack priority
        if (emfHighest >= optifineHighest) {
            //prioritise the regular directory before subfolders if same pack priority
            if (emfDirIndex >= emfSubDirIndex) {
                actualDirectory = EMF;
                packIndex = emfDirIndex;
                isSubFolder = false;
            } else {
                actualDirectory = EMF_SUB;
                packIndex = emfSubDirIndex;
                isSubFolder = true;
            }
        } else {
            //prioritise the regular directory before subfolders if same pack priority
            if (optifineDirIndex >= optifineSubDirIndex) {
                actualDirectory = OPTIFINE;
                packIndex = optifineDirIndex;
                isSubFolder = false;
            } else {
                actualDirectory = OPTIFINE_SUB;
                packIndex = optifineSubDirIndex;
                isSubFolder = true;
            }
        }
        if (printing)
            EMFUtils.log(" >> Final valid directory after checking: " + actualDirectory.getAsDirectory(namespace, modelFileName) + suffixAndFileType);
    }

    public static @Nullable EMFDirectoryHandler getDirectoryManagerOrNull(boolean printing, @NotNull String namespace, @NotNull String modelFileName, @NotNull String suffixAndFileType) {
        try {
            EMFDirectoryHandler directoryManager = new EMFDirectoryHandler(namespace, modelFileName, suffixAndFileType, printing);
            if (directoryManager.foundModel()) {
                return directoryManager;
            }
        } catch (Exception e) {
            if (printing)
                EMFUtils.log(" >> Exception when searching for: " + OPTIFINE.getAsDirectory(namespace, modelFileName) + suffixAndFileType + ". " + e.getMessage());
        }
        if (printing)
            EMFUtils.log(" >> Failed to find any files for: " + OPTIFINE.getAsDirectory(namespace, modelFileName) + suffixAndFileType);

        return null;
    }

    public String getFileNameWithType() {
        return rawFileName + suffixAndFileType;
    }

    public String getRelativeDirectoryLocationNoValidation(String fileName) {
        return actualDirectory.getAsDirectory(namespace, rawFileName).replaceFirst(rawFileName + "$", fileName);
    }

    public int packIndex() {
        return packIndex;
    }

    public boolean validForThisBase(EMFDirectoryHandler propertiesOrSecond) {
        if (propertiesOrSecond == null) return false;
        return isSubFolder == propertiesOrSecond.isSubFolder && packIndex <= propertiesOrSecond.packIndex;
    }

    boolean foundModel() {
        return actualDirectory != null && packIndex != -1;
    }

    private Resource getResourceOrNull(ResourceManager resources, EMFDirectoryHandler.EMFDirectory directory, boolean printing) {
        var loc = EMFUtils.res(directory.getAsDirectory(namespace, rawFileName) + suffixAndFileType);
        var res = resources.getResource(loc);
        if (printing) EMFUtils.log(" >>> Checking directory: " + loc + ", exists = " + res.isPresent());
        return res.orElse(null);
    }

    private String getPackId(@Nullable Resource resource) {
        return resource == null ? null : resource.sourcePackId();
    }

    private int getPackIndex(@Nullable String pack, List<String> packOrder) {
        return pack == null ? -1 : packOrder.indexOf(pack);
    }

    public String getFinalFileLocation() {
        //should not be null if ever called but, expect null to be safe
        return actualDirectory.getAsDirectory(namespace, rawFileName) + suffixAndFileType;
    }

    public ResourceLocation getRelativeFilePossiblyEMFOverridden(String jpmOrVariantFileNameWithSuffixAndFileType) {
        var over = actualDirectory.override();
        var fall = actualDirectory.fallback();

        var first = over == null ? actualDirectory : over;
        var second = fall == null ? actualDirectory : fall;

        //return emf dir if file exists
        var sameDir = EMFUtils.res(first.getAsDirectory(namespace, rawFileName).replaceFirst(rawFileName + "$", jpmOrVariantFileNameWithSuffixAndFileType));
        if (Minecraft.getInstance().getResourceManager().getResource(sameDir).isPresent()) {
            return sameDir;
        }
        //else return optifine
        return EMFUtils.res(second.getAsDirectory(namespace, rawFileName).replaceFirst(rawFileName + "$", jpmOrVariantFileNameWithSuffixAndFileType));
    }

    @Override
    public String toString() {
        return "EMF model, ID = " + OPTIFINE.getAsDirectory(namespace, rawFileName) +
                ", actual = " + actualDirectory.getAsDirectory(namespace, rawFileName);
    }

    enum EMFDirectory {
        EMF {
            @Override
            public String getAsDirectory(final String namespace, final String fileName) {
                return namespace + ":emf/cem/" + fileName;
            }
        },
        EMF_SUB {
            @Override
            public String getAsDirectory(final String namespace, final String fileName) {
                return namespace + ":emf/cem/" + fileName + "/" + fileName;
            }
        },
        OPTIFINE {
            @Override
            public String getAsDirectory(final String namespace, final String fileName) {
                return namespace + ":optifine/cem/" + fileName;
            }
        },
        OPTIFINE_SUB {
            @Override
            public String getAsDirectory(final String namespace, final String fileName) {
                return namespace + ":optifine/cem/" + fileName + "/" + fileName;
            }
        };

        public abstract String getAsDirectory(String namespace, String fileName);

        public @Nullable EMFDirectory fallback() {
            return switch (this) {
                case EMF -> OPTIFINE;
                case EMF_SUB -> OPTIFINE_SUB;
                default -> null;
            };
        }

        public @Nullable EMFDirectory override() {
            return switch (this) {
                case OPTIFINE -> EMF;
                case OPTIFINE_SUB -> EMF_SUB;
                default -> null;
            };
        }
    }
}
