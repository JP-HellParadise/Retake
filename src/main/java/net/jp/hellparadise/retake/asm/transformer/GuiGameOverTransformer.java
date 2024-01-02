package net.jp.hellparadise.retake.asm.transformer;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.jp.hellparadise.retake.Retake;
import net.jp.hellparadise.retake.asm.RetakePlugin;
import net.jp.hellparadise.retake.components.RetakeButton;
import net.jp.hellparadise.retake.components.RetakeTextComponentTranslation;
import net.jp.hellparadise.retake.packet.RetakeMessage;
import net.jp.hellparadise.retake.util.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.resources.I18n;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

@SuppressWarnings("unused")
public class GuiGameOverTransformer implements IClassTransformer {

    private static final boolean isDeobf = FMLLaunchHandler.isDeobfuscatedEnvironment();

    // GuiGameOver
    private static final String internal$GuiGameOver = isDeobf ? "net/minecraft/client/gui/GuiGameOver" : "bkv";

    // GuiButton
    private static final String internal$GuiButton = isDeobf ? "net/minecraft/client/gui/GuiButton" : "bja";

    // Retake
    private static final String internal$RetakeButton = "net/jp/hellparadise/retake/components/RetakeButton";
    private static final String internal$RetakeTextComponentTranslation = "net/jp/hellparadise/retake/components/RetakeTextComponentTranslation";
    private static final String internal$Hook = "net/jp/hellparadise/retake/asm/transformer/GuiGameOverTransformer$Hook";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;

        if (!transformedName.equals("net.minecraft.client.gui.GuiGameOver")) {
            return basicClass;
        }

        final ClassReader classReader = new ClassReader(basicClass);
        final ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        RetakePlugin.LOGGER.info("Patching class {}", transformedName);

        RetakePlugin.LOGGER.debug("Implementing interface {} to {}", internal$Hook, transformedName);
        classNode.interfaces.add(internal$Hook);

        int patched = 0;

        // method checking
        for (MethodNode mn : classNode.methods) {
            if (Utils.String$isAnyEqual(mn.name, "initGui", "func_73866_w_", "b")
                && mn.desc.equals("()V")
            ) {
                patched += PatchingMethod.Patch$initGui.patchMethod(mn);
                continue;
            }

            if (Utils.String$isAnyEqual(mn.name, "actionPerformed", "func_146284_a", "a")
                && Utils.String$isAnyEqual(mn.desc, "(Lnet/minecraft/client/gui/GuiButton;)V", "(Lbja;)V")
            ) {
                patched += PatchingMethod.Patch$actionPerformed.patchMethod(mn);
                continue;
            }

            if (Utils.String$isAnyEqual(mn.name, "drawScreen", "func_73863_a", "a")
                && mn.desc.equals("(IIF)V")
            ) {
                patched += PatchingMethod.Patch$drawScreen.patchMethod(mn);
                continue;
            }

            if (Utils.String$isAnyEqual(mn.name, "updateScreen", "func_73876_c", "e")
                && mn.desc.equals("()V")
            ) {
                patched += PatchingMethod.Patch$updateScreen.patchMethod(mn);
            }
        }

        // only apply patches if all sufficient
        if (patched == PatchingMethod.values().length) {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            RetakePlugin.LOGGER.info("Successful patched class {}", transformedName);
            return classWriter.toByteArray();
        }

        RetakePlugin.LOGGER.info("Failed patching class {}", transformedName);
        return basicClass;
    }

    private enum PatchingMethod {
        Patch$initGui(visitor -> {
            int ordinal = 4;

            Iterator<AbstractInsnNode> insnNodeIterator = visitor.instructions.iterator();
            while(insnNodeIterator.hasNext()) {
                if (insnNodeIterator.next() instanceof MethodInsnNode methodInsn && methodInsn.getOpcode() == INVOKEINTERFACE
                    && methodInsn.owner.contains("java/util/List")
                    && methodInsn.name.contains("add")
                    && --ordinal == 0
                ) {
                    // implementation
                    InsnList instList = new InsnList();
                    instList.add(new VarInsnNode(ALOAD, 0));
                    instList.add(new VarInsnNode(ALOAD, 0));
                    instList.add(new MethodInsnNode(INVOKEVIRTUAL, internal$GuiGameOver, "Retake$hook$initGui", "(" + Utils.ASM$asObjectDesc(internal$GuiGameOver) + ")V", false));

                    // insert
                    visitor.instructions.insert(methodInsn.getNext(), instList);

                    return true;
                }
            }

            return false;
        }),

        Patch$actionPerformed(visitor -> {
            Iterator<AbstractInsnNode> insnNodeIterator = visitor.instructions.iterator();
            while(insnNodeIterator.hasNext()) {
                if (insnNodeIterator.next() instanceof InsnNode returnInsn && returnInsn.getOpcode() == RETURN) {
                    // implementation
                    InsnList instList = new InsnList();
                    instList.add(new VarInsnNode(ALOAD, 0));
                    instList.add(new VarInsnNode(ALOAD, 0));
                    instList.add(new VarInsnNode(ALOAD, 1));
                    instList.add(new MethodInsnNode(INVOKEVIRTUAL,
                        internal$GuiGameOver,
                        "Retake$hook$actionPerformed",
                        "(" + Utils.ASM$asObjectDesc(internal$GuiGameOver, internal$GuiButton) + ")V",
                        false));

                    // insert
                    visitor.instructions.insertBefore(returnInsn, instList);

                    return true;
                }
            }

            return false;
        }),

        Patch$drawScreen(visitor -> {
            Iterator<AbstractInsnNode> insnNodeIterator = visitor.instructions.iterator();
            while(insnNodeIterator.hasNext()) {
                if (insnNodeIterator.next() instanceof InsnNode returnInsn && returnInsn.getOpcode() == RETURN) {
                    // implementation
                    InsnList instList = new InsnList();
                    instList.add(new VarInsnNode(ALOAD, 0));
                    instList.add(new VarInsnNode(ALOAD, 0));
                    instList.add(new VarInsnNode(ILOAD, 1));
                    instList.add(new VarInsnNode(ILOAD, 2));
                    instList.add(new MethodInsnNode(INVOKEVIRTUAL,
                        internal$GuiGameOver,
                        "Retake$hook$drawScreen",
                        "(" + Utils.ASM$asObjectDesc(internal$GuiGameOver) + "II)V",
                        false));

                    // insert
                    visitor.instructions.insertBefore(returnInsn, instList);

                    return true;
                }
            }

            return false;
        }),

        Patch$updateScreen(visitor -> {
            Iterator<AbstractInsnNode> insnNodeIterator = visitor.instructions.iterator();
            while(insnNodeIterator.hasNext()) {
                if (insnNodeIterator.next() instanceof FieldInsnNode insn && insn.getOpcode() == PUTFIELD
                    && insn.owner.equals(internal$GuiButton)
                    && (Utils.String$isAnyEqual(insn.name,"enabled", "field_146124_l", "l"))
                    && insn.desc.equals("Z")
                    && insn.getPrevious().getPrevious() instanceof VarInsnNode insertInsn
                    && insn.getNext() instanceof LabelNode continueNode
                ) {
                    //implementation
                    InsnList instList = new InsnList();
                    instList.add(new VarInsnNode(ALOAD, 0));
                    instList.add(new VarInsnNode(ALOAD, 2));
                    instList.add(new MethodInsnNode(INVOKEVIRTUAL, internal$GuiGameOver, "Retake$hook$updateScreen", "(" + Utils.ASM$asObjectDesc(internal$GuiButton) + ")Z", false));
                    instList.add(new JumpInsnNode(IFNE, continueNode));

                    // insert
                    visitor.instructions.insertBefore(insertInsn, instList);

                    return true;
                }
            }

            return false;
        });

        private final Function<MethodNode, Boolean> visitor;

        PatchingMethod(Function<MethodNode, Boolean> visitor) {
            this.visitor = visitor;
        }

        int patchMethod(MethodNode methodVisitor) {
            RetakePlugin.LOGGER.debug("Visiting method {}{}", methodVisitor.name, methodVisitor.desc);
            boolean result = visitor.apply(methodVisitor);
            RetakePlugin.LOGGER.debug(result ? "Successful visiting method {}{}" : "Failed visiting method {}{}", methodVisitor.name, methodVisitor.desc);
            return result ? 1 : 0;
        }
    }

    public interface Hook {

        AtomicBoolean isCooldown = new AtomicBoolean();

        default void Retake$hook$initGui(GuiGameOver gui) {
            gui.buttonList.forEach(button -> button.y += 24);

            gui.buttonList.add(
                new RetakeButton(2, gui.width / 2 - 100, gui.height / 4 + 72, I18n.format("retake.button.respawn"))
            );
        }

        default void Retake$hook$actionPerformed(GuiGameOver gui, GuiButton guiButton) {
            if (guiButton instanceof RetakeButton) {
                Retake.NetworkHandler.INSTANCE.sendToServer(new RetakeMessage());
                gui.mc.displayGuiScreen(null);
            }
        }

        default void Retake$hook$drawScreen(GuiGameOver gui, int mouseX, int mouseY) {
            if (gui.enableButtonsTimer >= 20) {
                gui.buttonList.stream().filter(button -> button instanceof RetakeButton && button.isMouseOver() && !button.enabled)
                    .findAny()
                    .ifPresent((button) -> gui.handleComponentHover(new RetakeTextComponentTranslation("retake.oncooldown"), mouseX, mouseY));
            }
        }

        default boolean Retake$hook$updateScreen(GuiButton guiButton) {
            // We don't need any player info since this will be call from client side
            return guiButton instanceof RetakeButton && isCooldown.get();
        }

    }

}
