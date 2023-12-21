package net.jp.hellparadise.betterrespawn.asm.transformer;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.BIPUSH;

import java.util.Iterator;
import java.util.function.Function;
import net.jp.hellparadise.betterrespawn.asm.BetterRespawnPlugin;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;

public class GuiGameOverTransformer implements IClassTransformer {

    private static final String GuiScreen = "net.minecraft.client.gui.GuiScreen";
    private static final String GuiGameOver = "net.minecraft.client.gui.GuiGameOver";
    private static final String GuiButton = "net.minecraft.client.gui.GuiButton";
    private static final String Minecraft = "net.minecraft.client.Minecraft";

    private static final String[] I18n$format = {BetterRespawnPlugin.isDeobf ? "format" : "func_135052_a", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"};
    private static final String[] GuiGameOver$initGui = {BetterRespawnPlugin.isDeobf ? "initGui" : "func_73866_w_", "()V"};
    private static final String[] GuiGameOver$actionPerformed = {BetterRespawnPlugin.isDeobf ? "actionPerformed" : "func_146284_a", "(Lnet/minecraft/client/gui/GuiButton;)V"};
    private static final String[] GuiGameOver$updateScreen = {BetterRespawnPlugin.isDeobf ? "updateScreen" : "func_73876_c", "()V"};
    private static final String[] GuiGameOver$drawScreen = {BetterRespawnPlugin.isDeobf ? "drawScreen" : "func_73863_a", "(IIF)V"};
    private static final String[] GuiButton$isMouseOver = {BetterRespawnPlugin.isDeobf ? "isMouseOver" : "func_146115_a", "()Z"};
    private static final String[] Minecraft$displayGuiScreen = {BetterRespawnPlugin.isDeobf ? "displayGuiScreen" : "func_147108_a", "(Lnet/minecraft/client/gui/GuiScreen;)V"};

    private static final String[] GuiGameOver$buttonList = {BetterRespawnPlugin.isDeobf ? "buttonList" : "field_146292_n", "Ljava/util/List;"};
    private static final String[] GuiGameOver$width = {BetterRespawnPlugin.isDeobf ? "width" : "field_146294_l", "I"};
    private static final String[] GuiGameOver$height = {BetterRespawnPlugin.isDeobf ? "height" : "field_146295_m", "I"};
    private static final String[] GuiGameOver$mc = {BetterRespawnPlugin.isDeobf ? "mc" : "field_146297_k", "Lnet/minecraft/client/Minecraft;"};
    private static final String[] GuiGameOver$handleComponentHover = {BetterRespawnPlugin.isDeobf ? "handleComponentHover" : "func_175272_a", "(Lnet/minecraft/util/text/ITextComponent;II)V"};
    private static final String[] GuiButton$enabled = {BetterRespawnPlugin.isDeobf ? "enabled" : "field_146124_l", "Z"};
    private static final String[] Minecraft$player = {BetterRespawnPlugin.isDeobf ? "player" : "field_71439_g", "Lnet/minecraft/client/entity/EntityPlayerSP;"};

    private static final String BetterRespawnConfig = net.jp.hellparadise.betterrespawn.BetterRespawnConfig.class.getName();
    private static final String IBetterPlayerSP = net.jp.hellparadise.betterrespawn.interfaces.IBetterPlayerSP.class.getName();
    private static final String BetterRespawnButton = net.jp.hellparadise.betterrespawn.helper.BetterRespawnButton.class.getName();
    private static final String BetterTextComponentTranslation = net.jp.hellparadise.betterrespawn.helper.BetterTextComponentTranslation.class.getName();

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;

        if (!transformedName.contains(GuiGameOver)) {
            return basicClass;
        }

        final ClassReader cr = new ClassReader(basicClass);
        final ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        BetterRespawnPlugin.LOGGER.info("Patching class {}", transformedName);

        int patches = 0;

        for (MethodNode mn : cn.methods) {
            if (mn.name.equals(GuiGameOver$initGui[0]) && mn.desc.equals(GuiGameOver$initGui[1])
                && PatchingMethod.Patch$initGui.patchingMethod(mn)) {
                patches++;
                continue;
            }

            if (mn.name.equals(GuiGameOver$actionPerformed[0]) && mn.desc.equals(GuiGameOver$actionPerformed[1])
                && PatchingMethod.Patch$actionPerformed.patchingMethod(mn)) {
                patches++;
                continue;
            }

            if (mn.name.equals(GuiGameOver$drawScreen[0]) && mn.desc.equals(GuiGameOver$drawScreen[1])
                && PatchingMethod.Patch$drawScreen.patchingMethod(mn)) {
                patches++;
                continue;
            }

            if (mn.name.equals(GuiGameOver$updateScreen[0]) && mn.desc.equals(GuiGameOver$updateScreen[1])
                && PatchingMethod.Patch$updateScreen.patchingMethod(mn)) {
                patches++;
            }
        }

        if (patches == PatchingMethod.values().length) {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            cn.accept(classWriter);
            BetterRespawnPlugin.LOGGER.info("Successful patched class {}", transformedName);
            return classWriter.toByteArray();
        } else {
            BetterRespawnPlugin.LOGGER.info("Failed patching class {}", transformedName);
        }

        return basicClass;
    }

    private enum PatchingMethod {
        Patch$initGui(visitor -> {
            InsnList insnList = visitor.instructions;
            int IADD$count = 0, INVOKEINTERFACE$count = 0;
            int addNewButton = 0, fixButtonPos = 0;
            Iterator<AbstractInsnNode> ite = insnList.iterator();
            while(ite.hasNext()) {
                AbstractInsnNode insn = ite.next();
                // Fix button pos from exist one
                if(fixButtonPos < 2 && insn instanceof InsnNode && insn.getOpcode() == IADD
                    && insn.getPrevious() instanceof IntInsnNode intInsn && intInsn.getOpcode() == BIPUSH
                ) {
                    if (++IADD$count > 2) {
                        intInsn.operand += 24;
                        fixButtonPos++;
                    }
                }

                // Add new button
                if (addNewButton < 1 && insn instanceof MethodInsnNode && insn.getOpcode() == INVOKEINTERFACE
                    && ((MethodInsnNode) insn).owner.contains("java/util/List")
                    && ((MethodInsnNode) insn).name.contains("add")) {
                    if (++INVOKEINTERFACE$count == 4) {
                        InsnList instList = new InsnList();
                        instList.add(new LabelNode(new Label()));
                        instList.add(new VarInsnNode(ALOAD, 0));
                        instList.add(new FieldInsnNode(GETFIELD, GuiGameOver.replace(".", "/"), GuiGameOver$buttonList[0], GuiGameOver$buttonList[1]));
                        instList.add(new TypeInsnNode(NEW, BetterRespawnButton.replace(".", "/")));
                        instList.add(new InsnNode(DUP));
                        instList.add(new InsnNode(ICONST_2));
                        instList.add(new VarInsnNode(ALOAD, 0));
                        instList.add(new FieldInsnNode(GETFIELD, GuiGameOver.replace(".", "/"), GuiGameOver$width[0], GuiGameOver$width[1]));
                        instList.add(new InsnNode(ICONST_2));
                        instList.add(new InsnNode(IDIV));
                        instList.add(new IntInsnNode(BIPUSH, 100));
                        instList.add(new InsnNode(ISUB));
                        instList.add(new VarInsnNode(ALOAD, 0));
                        instList.add(new FieldInsnNode(GETFIELD, GuiGameOver.replace(".", "/"), GuiGameOver$height[0], GuiGameOver$height[1]));
                        instList.add(new InsnNode(ICONST_4));
                        instList.add(new InsnNode(IDIV));
                        instList.add(new IntInsnNode(BIPUSH, 72));
                        instList.add(new InsnNode(IADD));
                        instList.add(new LdcInsnNode("betterrespawn.died.respawn"));
                        instList.add(new InsnNode(ICONST_0));
                        instList.add(new TypeInsnNode(ANEWARRAY, "java/lang/Object"));
                        instList.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/resources/I18n", I18n$format[0], I18n$format[1], false));
                        instList.add(new MethodInsnNode(INVOKESPECIAL, BetterRespawnButton.replace(".", "/"), "<init>", "(IIILjava/lang/String;)V", false));
                        instList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true));
                        instList.add(new InsnNode(POP));
                        visitor.instructions.insert(insn.getNext(), instList);
                        addNewButton++;
                    }
                }
            }

            return fixButtonPos == 2 && addNewButton == 1;
        }),
        Patch$actionPerformed(visitor -> {
            InsnList insnList = visitor.instructions;
            Iterator<AbstractInsnNode> ite = insnList.iterator();
            while(ite.hasNext()) {
                AbstractInsnNode insn = ite.next();
                if (insn instanceof InsnNode && insn.getOpcode() == RETURN) {
                    InsnList instList = new InsnList();
                    instList.add(new LabelNode());
                    instList.add(new VarInsnNode(ALOAD, 1));
                    instList.add(new TypeInsnNode(INSTANCEOF, BetterRespawnButton.replace(".", "/")));// INSTANCEOF net/jp/hellparadise/betterrespawn/implementations/BetterRespawnButton
                    LabelNode returnLabel = new LabelNode();
                    instList.add(new JumpInsnNode(IFEQ, returnLabel));// IFNE L7
                    instList.add(new LabelNode());
                    instList.add(new VarInsnNode(ALOAD, 0));
                    instList.add(new FieldInsnNode(GETFIELD, GuiGameOver.replace(".", "/"), GuiGameOver$mc[0], GuiGameOver$mc[1]));
                    instList.add(new FieldInsnNode(GETFIELD, Minecraft.replace(".", "/"), Minecraft$player[0], Minecraft$player[1]));
                    instList.add(new TypeInsnNode(CHECKCAST, IBetterPlayerSP.replace(".", "/")));
                    instList.add(new MethodInsnNode(INVOKEINTERFACE, IBetterPlayerSP.replace(".", "/"), "betterRespawn$respawnPlayer", "()V", true));
                    instList.add(new LabelNode());
                    instList.add(new MethodInsnNode(INVOKESTATIC, BetterRespawnConfig.replace(".", "/"), "instance", "()Lnet/jp/hellparadise/betterrespawn/BetterRespawnConfig;", false));
                    instList.add(new FieldInsnNode(GETFIELD, BetterRespawnConfig.replace(".", "/"), "respawnCooldown", "I"));
                    instList.add(new FieldInsnNode(PUTSTATIC, BetterRespawnConfig.replace(".", "/"), "clientRespawnCooldown", "I"));
                    instList.add(new LabelNode());
                    instList.add(new VarInsnNode(ALOAD, 0));
                    instList.add(new FieldInsnNode(GETFIELD, GuiGameOver.replace(".", "/"), GuiGameOver$mc[0], GuiGameOver$mc[1]));
                    instList.add(new InsnNode(ACONST_NULL));
                    instList.add(new MethodInsnNode(INVOKEVIRTUAL, Minecraft.replace(".", "/"), Minecraft$displayGuiScreen[0], Minecraft$displayGuiScreen[1], false));
                    instList.add(returnLabel);
                    visitor.instructions.insertBefore(insn, instList);
                    return true;
                }
            }
            return false;
        }),
        Patch$drawScreen(visitor -> {
            InsnList insnList = visitor.instructions;
            Iterator<AbstractInsnNode> ite = insnList.iterator();
            while(ite.hasNext()) {
                AbstractInsnNode insn = ite.next();
                if (insn instanceof MethodInsnNode insertInsn && insn.getOpcode() == INVOKESPECIAL
                    && insertInsn.owner.contains(GuiScreen.replace(".", "/"))
                    && insertInsn.name.contains(GuiGameOver$drawScreen[0])
                    && insertInsn.desc.contains(GuiGameOver$drawScreen[1])
                    && insn.getPrevious().getPrevious().getPrevious().getPrevious() instanceof VarInsnNode confirmInsn
                    && confirmInsn.getOpcode() == ALOAD && confirmInsn.var == 0) {
                    InsnList instList = new InsnList();
                    instList.add(new VarInsnNode(ALOAD, 0));// ALOAD 0
                    instList.add(new FieldInsnNode(GETFIELD, GuiGameOver.replace(".", "/"), GuiGameOver$buttonList[0], GuiGameOver$buttonList[1]));// GETFIELD net/minecraft/client/gui/GuiGameOver.buttonList : Ljava/util/List;
                    instList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true));// INVOKEINTERFACE java/util/List.iterator ()Ljava/util/Iterator; (itf)
                    instList.add(new VarInsnNode(ASTORE, 5));// ASTORE 5
                    LabelNode l1 = new LabelNode();
                    instList.add(l1);// L16
                    instList.add(new FrameNode(F_APPEND, 1, new Object[]{"java/util/Iterator"}, 0, null));// FRAME APPEND [java/util/Iterator]
                    instList.add(new VarInsnNode(ALOAD, 5));// ALOAD 5
                    instList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true));// INVOKEINTERFACE java/util/Iterator.hasNext ()Z (itf)
                    LabelNode l2 = new LabelNode();
                    instList.add(new JumpInsnNode(IFEQ, l2));// IFEQ L17
                    instList.add(new VarInsnNode(ALOAD, 5));// ALOAD 5
                    instList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true));// INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object; (itf)
                    instList.add(new TypeInsnNode(CHECKCAST, GuiButton.replace(".", "/")));// CHECKCAST net/minecraft/client/gui/GuiButton
                    instList.add(new VarInsnNode(ASTORE, 6));// ASTORE 6
                    instList.add(new LabelNode());// L18
                    instList.add(new VarInsnNode(ALOAD, 6));// ALOAD 6
                    instList.add(new TypeInsnNode(INSTANCEOF, BetterRespawnButton.replace(".", "/")));// INSTANCEOF net/jp/hellparadise/betterrespawn/BetterRespawnButton
                    LabelNode l3 = new LabelNode();
                    instList.add(new JumpInsnNode(IFEQ, l3));// IFEQ L19
                    instList.add(new VarInsnNode(ALOAD, 6));// ALOAD 6
                    instList.add(new MethodInsnNode(INVOKEVIRTUAL, GuiButton.replace(".", "/"), GuiButton$isMouseOver[0], GuiButton$isMouseOver[1], false));// INVOKEVIRTUAL net/minecraft/client/gui/GuiButton.isMouseOver ()Z
                    instList.add(new JumpInsnNode(IFEQ, l3));// IFEQ L19
                    instList.add(new VarInsnNode(ALOAD, 6));// ALOAD 6
                    instList.add(new FieldInsnNode(GETFIELD, GuiButton.replace(".", "/"), GuiButton$enabled[0], GuiButton$enabled[1]));// GETFIELD net/minecraft/client/gui/GuiButton.enabled : Z
                    instList.add(new JumpInsnNode(IFNE, l3));// IFNE L19
                    instList.add(new LabelNode());// L20
                    instList.add(new VarInsnNode(ALOAD, 0));// ALOAD 0
                    instList.add(new TypeInsnNode(NEW, BetterTextComponentTranslation.replace(".", "/")));// NEW net/minecraft/util/text/TextComponentTranslation
                    instList.add(new InsnNode(DUP));// DUP
                    instList.add(new LdcInsnNode("betterrespawn.died.recently"));// LDC "You died too recently to spawn nearby."
                    instList.add(new InsnNode(ICONST_0));// ICONST_0
                    instList.add(new TypeInsnNode(ANEWARRAY, "java/lang/Object"));// ANEWARRAY java/lang/Object
                    instList.add(new MethodInsnNode(INVOKESPECIAL, BetterTextComponentTranslation.replace(".", "/"), "<init>", "(Ljava/lang/String;[Ljava/lang/Object;)V", false));// INVOKESPECIAL net/minecraft/util/text/TextComponentTranslation.<init> (Ljava/lang/String;[Ljava/lang/Object;)V
                    instList.add(new VarInsnNode(ILOAD, 1));// ILOAD 1
                    instList.add(new VarInsnNode(ILOAD, 2));// ILOAD 2
                    instList.add(new MethodInsnNode(INVOKEVIRTUAL, GuiGameOver.replace(".", "/"), GuiGameOver$handleComponentHover[0], GuiGameOver$handleComponentHover[1], false));// INVOKEVIRTUAL net/minecraft/client/gui/GuiGameOver.handleComponentHover (Lnet/minecraft/util/text/ITextComponent;II)V
                    instList.add(l3);// L19
                    instList.add(new FrameNode(F_APPEND, 1, new Object[]{GuiButton.replace(".", "/")}, 0, null));// FRAME APPEND [net/minecraft/client/gui/GuiButton]
                    instList.add(new JumpInsnNode(GOTO, l1));// GOTO L16
                    instList.add(l2);// L17
                    visitor.instructions.insert(insertInsn, instList);
                    return true;
                }
            }
            return false;
        }),
        Patch$updateScreen(visitor -> {
            InsnList insnList = visitor.instructions;
            Iterator<AbstractInsnNode> ite = insnList.iterator();
            while(ite.hasNext()) {
                AbstractInsnNode insn = ite.next();
                if (insn instanceof FieldInsnNode fieldInsn && insn.getOpcode() == PUTFIELD
                    && fieldInsn.owner.contains(GuiButton.replace(".", "/"))
                    && fieldInsn.name.contains(GuiButton$enabled[0])
                    && fieldInsn.desc.contains(GuiButton$enabled[1])
                    && insn.getPrevious().getPrevious() instanceof VarInsnNode insertInsn
                    && insn.getNext() instanceof LabelNode continueNode
                ) {
                    InsnList instList = new InsnList();
                    LabelNode enableLable = new LabelNode();
                    instList.add(new VarInsnNode(ALOAD, 2));// ALOAD 2
                    instList.add(new TypeInsnNode(INSTANCEOF, BetterRespawnButton.replace(".", "/")));// INSTANCEOF net/jp/hellparadise/betterrespawn/implementations/BetterRespawnButton
                    instList.add(new JumpInsnNode(IFEQ, enableLable));// IFNE L7
                    instList.add(new VarInsnNode(ALOAD, 2));// ALOAD 2
                    instList.add(new TypeInsnNode(INSTANCEOF, BetterRespawnButton.replace(".", "/")));// INSTANCEOF net/jp/hellparadise/betterrespawn/implementations/BetterRespawnButton
                    instList.add(new JumpInsnNode(IFEQ, continueNode));// IFNE L8
                    instList.add(new FieldInsnNode(GETSTATIC, BetterRespawnConfig.replace(".", "/"), "clientRespawnCooldown", "I"));// GETFIELD net/minecraft/client/gui/GuiButton.id : I
                    instList.add(new JumpInsnNode(IFNE, continueNode));// IFNE L8
                    instList.add(enableLable);// L7
                    visitor.instructions.insertBefore(insertInsn, instList);
                    return true;
                }
            }
            return false;
        });

        private final Function<MethodNode, Boolean> patching;

        PatchingMethod(Function<MethodNode, Boolean> patching) {
            this.patching = patching;
        }

        boolean patchingMethod(MethodNode node) {
            BetterRespawnPlugin.LOGGER.info("Visiting method {}{}", node.name, node.desc);
            boolean result = patching.apply(node);
            BetterRespawnPlugin.LOGGER.info(result ? "Successful visiting method {}{}" : "Failed visiting method {}{}", node.name, node.desc);
            return result;
        }
    }
}
