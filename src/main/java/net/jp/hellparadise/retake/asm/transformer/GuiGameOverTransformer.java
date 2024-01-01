package net.jp.hellparadise.retake.asm.transformer;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.jp.hellparadise.retake.Retake;
import net.jp.hellparadise.retake.asm.RetakePlugin;
import net.jp.hellparadise.retake.components.RetakeButton;
import net.jp.hellparadise.retake.packet.RetakeMessage;
import net.jp.hellparadise.retake.util.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
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
        RetakePlugin.LOGGER.debug("Generate empty method {} from interface {}", "Retake$hook$drawScreen", internal$Hook);
        patched += PatchingMethod.Inject$hook$drawScreen.patchMethod((MethodNode) classNode.visitMethod(ACC_PUBLIC, "Retake$hook$drawScreen", "(II)V", null, null));

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
            int count$IADD = 0, count$INVOKEINTERFACE = 0;
            int addNewButton = 0, fixButtonPos = 0;

            Iterator<AbstractInsnNode> insnNodeIterator = visitor.instructions.iterator();

            while(insnNodeIterator.hasNext()) {
                AbstractInsnNode insn = insnNodeIterator.next();

                // Fix button pos from exist one
                if(fixButtonPos < 2 && insn instanceof InsnNode && insn.getOpcode() == IADD
                    && insn.getPrevious() instanceof IntInsnNode intInsn && intInsn.getOpcode() == BIPUSH
                ) {
                    // implementation
                    if (++count$IADD > 2) {
                        intInsn.operand += 24;
                        fixButtonPos++;
                    }
                }

                // Add new button
                if (addNewButton < 1 && insn instanceof MethodInsnNode && insn.getOpcode() == INVOKEINTERFACE
                    && ((MethodInsnNode) insn).owner.contains("java/util/List")
                    && ((MethodInsnNode) insn).name.contains("add")
                    && ++count$INVOKEINTERFACE == 4
                ) {
                    // implementation
                    InsnList instList = new InsnList();
                    instList.add(new LabelNode());
                    instList.add(new VarInsnNode(ALOAD, 0));
                    instList.add(new FieldInsnNode(GETFIELD, internal$GuiGameOver, isDeobf ? "buttonList" : "field_146292_n", "Ljava/util/List;"));
                    instList.add(new TypeInsnNode(NEW, internal$RetakeButton));
                    instList.add(new InsnNode(DUP));
                    instList.add(new InsnNode(ICONST_2));
                    instList.add(new VarInsnNode(ALOAD, 0));
                    instList.add(new FieldInsnNode(GETFIELD, internal$GuiGameOver, isDeobf ? "width" : "field_146294_l", "I"));
                    instList.add(new InsnNode(ICONST_2));
                    instList.add(new InsnNode(IDIV));
                    instList.add(new IntInsnNode(BIPUSH, 100));
                    instList.add(new InsnNode(ISUB));
                    instList.add(new VarInsnNode(ALOAD, 0));
                    instList.add(new FieldInsnNode(GETFIELD, internal$GuiGameOver, isDeobf ? "height" : "field_146295_m", "I"));
                    instList.add(new InsnNode(ICONST_4));
                    instList.add(new InsnNode(IDIV));
                    instList.add(new IntInsnNode(BIPUSH, 72));
                    instList.add(new InsnNode(IADD));
                    instList.add(new LdcInsnNode("retake.button.respawn"));
                    instList.add(new InsnNode(ICONST_0));
                    instList.add(new TypeInsnNode(ANEWARRAY, "java/lang/Object"));
                    instList.add(new MethodInsnNode(INVOKESTATIC, isDeobf ? "net/minecraft/client/resources/I18n" : "cey", isDeobf ? "format" : "func_135052_a", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", false));
                    instList.add(new MethodInsnNode(INVOKESPECIAL, internal$RetakeButton, "<init>", "(IIILjava/lang/String;)V", false));
                    instList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true));
                    instList.add(new InsnNode(POP));
                    visitor.instructions.insert(insn.getNext(), instList);

                    addNewButton++;
                }
            }

            return fixButtonPos == 2 && addNewButton == 1;
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
                        "(" + Utils.ASM$asObjectDesc(isDeobf ? "net/minecraft/client/gui/GuiScreen" : "blk", internal$GuiButton) + ")V",
                        false));

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
                    instList.add(new VarInsnNode(ILOAD, 1));
                    instList.add(new VarInsnNode(ILOAD, 2));
                    instList.add(new MethodInsnNode(INVOKEVIRTUAL, internal$GuiGameOver, "Retake$hook$drawScreen", "(II)V", false));
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
                    visitor.instructions.insertBefore(insertInsn, instList);

                    return true;
                }
            }

            return false;
        }),

        Inject$hook$drawScreen(visitor -> {
            // implementation
            Label l0 = new Label();
            visitor.visitLabel(l0);
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitFieldInsn(GETFIELD, internal$GuiGameOver, isDeobf ? "buttonList" : "field_146292_n", "Ljava/util/List;");
            visitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
            visitor.visitVarInsn(ASTORE, 3);
            Label l1 = new Label();
            visitor.visitLabel(l1);
            visitor.visitFrame(F_APPEND, 1, new Object[]{"java/util/Iterator"}, 0, null);
            visitor.visitVarInsn(ALOAD, 3);
            visitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
            Label l2 = new Label();
            visitor.visitJumpInsn(IFEQ, l2);
            visitor.visitVarInsn(ALOAD, 3);
            visitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            visitor.visitTypeInsn(CHECKCAST, internal$GuiButton);
            visitor.visitVarInsn(ASTORE, 4);
            Label l3 = new Label();
            visitor.visitLabel(l3);
            visitor.visitVarInsn(ALOAD, 4);
            visitor.visitTypeInsn(INSTANCEOF, internal$RetakeButton);
            Label l4 = new Label();
            visitor.visitJumpInsn(IFEQ, l4);
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitFieldInsn(GETFIELD, internal$GuiGameOver, isDeobf ? "enableButtonsTimer" : "field_146347_a", "I");
            visitor.visitIntInsn(BIPUSH, 20);
            visitor.visitJumpInsn(IF_ICMPLT, l4);
            visitor.visitVarInsn(ALOAD, 4);
            visitor.visitMethodInsn(INVOKEVIRTUAL, internal$GuiButton, isDeobf ? "isMouseOver" : "func_146115_a", "()Z", false);
            visitor.visitJumpInsn(IFEQ, l4);
            visitor.visitVarInsn(ALOAD, 4);
            visitor.visitFieldInsn(GETFIELD, internal$GuiButton, isDeobf ? "enabled" : "field_146124_l", "Z");
            Label l5 = new Label();
            visitor.visitJumpInsn(IFEQ, l5);
            visitor.visitLabel(l4);
            visitor.visitFrame(F_APPEND, 1, new Object[]{internal$GuiButton}, 0, null);
            visitor.visitJumpInsn(GOTO, l1);
            visitor.visitLabel(l5);
            visitor.visitFrame(F_SAME, 0, null, 0, null);
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitTypeInsn(NEW, internal$RetakeTextComponentTranslation);
            visitor.visitInsn(DUP);
            visitor.visitLdcInsn("retake.oncooldown");
            visitor.visitInsn(ICONST_0);
            visitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            visitor.visitMethodInsn(INVOKESPECIAL, internal$RetakeTextComponentTranslation, "<init>", "(Ljava/lang/String;[Ljava/lang/Object;)V", false);
            visitor.visitVarInsn(ILOAD, 1);
            visitor.visitVarInsn(ILOAD, 2);
            visitor.visitMethodInsn(INVOKEVIRTUAL, internal$GuiGameOver, isDeobf ? "handleComponentHover" : "func_175272_a", "(L" + (isDeobf ? "net/minecraft/util/text/ITextComponent" : "hh") + ";II)V", false);
            visitor.visitLabel(l2);
            visitor.visitFrame(F_CHOP, 0, null, 0, null);
            visitor.visitInsn(RETURN);
            Label l6 = new Label();
            visitor.visitLabel(l6);
            visitor.visitLocalVariable("guiButton", Utils.ASM$asObjectDesc(internal$GuiButton), null, l3, l2, 4);
            visitor.visitLocalVariable("this", Utils.ASM$asObjectDesc(internal$GuiGameOver), null, l0, l6, 0);
            visitor.visitLocalVariable("mouseX", "I", null, l0, l6, 1);
            visitor.visitLocalVariable("mouseY", "I", null, l0, l6, 2);

            return true;
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

        default void Retake$hook$actionPerformed(GuiScreen guiScreen, GuiButton guiButton) {
            if (guiButton instanceof RetakeButton) {
                Retake.NetworkHandler.INSTANCE.sendToServer(new RetakeMessage());
                guiScreen.mc.displayGuiScreen(null);
            }
        }

        void Retake$hook$drawScreen(int mouseX, int mouseY);

        default boolean Retake$hook$updateScreen(GuiButton guiButton) {
            // We don't need any player info since this will be call from client side
            return guiButton instanceof RetakeButton && isCooldown.get();
        }

    }

}
