package net.jp.hellparadise.betterrespawn.asm.transformer;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.BIPUSH;

import java.util.Iterator;
import net.jp.hellparadise.betterrespawn.asm.BetterRespawnPlugin;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;

public class GuiGameOverTransformer implements IClassTransformer {

    private static final String GuiGameOver = "net.minecraft.client.gui.GuiGameOver";
    private static final String GuiButton = "net.minecraft.client.gui.GuiButton";
    private static final String Minecraft = "net.minecraft.client.Minecraft";

    private static final String[] I18n$format = {BetterRespawnPlugin.isDeobf ? "format" : "func_135052_a", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"};
    private static final String[] GuiGameOver$initGui = {BetterRespawnPlugin.isDeobf ? "initGui" : "func_73866_w_", "()V"};
    private static final String[] GuiGameOver$actionPerformed = {BetterRespawnPlugin.isDeobf ? "actionPerformed" : "func_146284_a", "(Lnet/minecraft/client/gui/GuiButton;)V"};
    private static final String[] GuiGameOver$updateScreen = {BetterRespawnPlugin.isDeobf ? "updateScreen" : "func_73876_c", "()V"};
    private static final String[] Minecraft$displayGuiScreen = {BetterRespawnPlugin.isDeobf ? "displayGuiScreen" : "func_147108_a", "(Lnet/minecraft/client/gui/GuiScreen;)V"};

    private static final String[] GuiGameOver$buttonList = {BetterRespawnPlugin.isDeobf ? "buttonList" : "field_146292_n", "Ljava/util/List;"};
    private static final String[] GuiGameOver$width = {BetterRespawnPlugin.isDeobf ? "width" : "field_146294_l", "I"};
    private static final String[] GuiGameOver$height = {BetterRespawnPlugin.isDeobf ? "height" : "field_146295_m", "I"};
    private static final String[] GuiGameOver$mc = {BetterRespawnPlugin.isDeobf ? "mc" : "field_146297_k", "Lnet/minecraft/client/Minecraft;"};
    private static final String[] GuiButton$id = {BetterRespawnPlugin.isDeobf ? "id" : "field_146127_k", "I"};
    private static final String[] GuiButton$enabled = {BetterRespawnPlugin.isDeobf ? "enabled" : "field_146124_l", "Z"};
    private static final String[] Minecraft$player = {BetterRespawnPlugin.isDeobf ? "player" : "field_71439_g", "Lnet/minecraft/client/entity/EntityPlayerSP;"};

    private static final String BetterRespawnConfig = net.jp.hellparadise.betterrespawn.BetterRespawnConfig.class.getName();
    private static final String IBetterPlayerSP = net.jp.hellparadise.betterrespawn.interfaces.IBetterPlayerSP.class.getName();

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
            if (mn.name.equals(GuiGameOver$initGui[0]) && mn.desc.equals(GuiGameOver$initGui[1])) {
                BetterRespawnPlugin.LOGGER.info("Visiting method {}{}", mn.name, mn.desc);
                InsnList insnList = mn.instructions;
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
                            instList.add(new TypeInsnNode(NEW, GuiButton.replace(".", "/")));
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
                            instList.add(new LdcInsnNode("Test"));
                            instList.add(new InsnNode(ICONST_0));
                            instList.add(new TypeInsnNode(ANEWARRAY, "java/lang/Object"));
                            instList.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/resources/I18n", I18n$format[0], I18n$format[1], false));
                            instList.add(new MethodInsnNode(INVOKESPECIAL, GuiButton.replace(".", "/"), "<init>", "(IIILjava/lang/String;)V", false));
                            instList.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true));
                            instList.add(new InsnNode(POP));
                            mn.instructions.insert(insn.getNext(), instList);
                            addNewButton++;
                        }
                    }
                }

                if (fixButtonPos == 2 && addNewButton == 1) {
                    BetterRespawnPlugin.LOGGER.info("Successful visiting method {}{}", mn.name, mn.desc);
                    patches++;
                } else {
                    BetterRespawnPlugin.LOGGER.info("Failed visiting method {}{}", mn.name, mn.desc);
                    break;
                }
            }

            if (mn.name.equals(GuiGameOver$actionPerformed[0]) && mn.desc.equals(GuiGameOver$actionPerformed[1])) {
                BetterRespawnPlugin.LOGGER.info("Visiting method {}{}", mn.name, mn.desc);
                InsnList insnList = mn.instructions;
                boolean patched = false;
                Iterator<AbstractInsnNode> ite = insnList.iterator();
                while(ite.hasNext()) {
                    AbstractInsnNode insn = ite.next();
                    if (insn instanceof InsnNode && insn.getOpcode() == RETURN) {
                        InsnList instList = new InsnList();
                        instList.add(new LabelNode());
                        instList.add(new VarInsnNode(ALOAD, 1));
                        instList.add(new FieldInsnNode(GETFIELD, GuiButton.replace(".", "/"), GuiButton$id[0], GuiButton$id[1]));
                        instList.add(new InsnNode(ICONST_2));
                        LabelNode returnLabel = new LabelNode();
                        instList.add(new JumpInsnNode(IF_ICMPNE, returnLabel));
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
                        mn.instructions.insertBefore(insn, instList);
                        patched = true;
                    }
                }

                if (patched) {
                    BetterRespawnPlugin.LOGGER.info("Successful visiting method {}{}", mn.name, mn.desc);
                    patches++;
                } else {
                    BetterRespawnPlugin.LOGGER.info("Failed visiting method {}{}", mn.name, mn.desc);
                    break;
                }
            }

            if (mn.name.equals(GuiGameOver$updateScreen[0]) && mn.desc.equals(GuiGameOver$updateScreen[1])) {
                BetterRespawnPlugin.LOGGER.info("Visiting method {}{}", mn.name, mn.desc);
                InsnList insnList = mn.instructions;
                boolean patched = false;
                Iterator<AbstractInsnNode> ite = insnList.iterator();
                while(ite.hasNext()) {
                    AbstractInsnNode insn = ite.next();
                    if (insn instanceof FieldInsnNode fieldInsn && insn.getOpcode() == PUTFIELD
                        && fieldInsn.owner.contains(GuiButton.replace(".", "/"))
                        && fieldInsn.name.contains(GuiButton$enabled[0])
                        && fieldInsn.desc.contains(GuiButton$enabled[1])
                        && insn.getPrevious().getPrevious() instanceof VarInsnNode insnNode
                        && insn.getNext() instanceof LabelNode continueNode
                    ) {
                        InsnList instList = new InsnList();
                        LabelNode enableLable = new LabelNode();
                        instList.add(new VarInsnNode(ALOAD, 2));// ALOAD 2
                        instList.add(new FieldInsnNode(GETFIELD, GuiButton.replace(".", "/"), GuiButton$id[0], GuiButton$id[1]));// GETFIELD net/minecraft/client/gui/GuiButton.id : I
                        instList.add(new InsnNode(ICONST_2));// ICONST_2
                        instList.add(new JumpInsnNode(IF_ICMPNE, enableLable));// IF_ICMPNE L7
                        instList.add(new VarInsnNode(ALOAD, 2));// ALOAD 2
                        instList.add(new FieldInsnNode(GETFIELD, GuiButton.replace(".", "/"), GuiButton$id[0], GuiButton$id[1]));// GETFIELD net/minecraft/client/gui/GuiButton.id : I
                        instList.add(new InsnNode(ICONST_2));// ICONST_2
                        instList.add(new JumpInsnNode(IF_ICMPNE, continueNode));// IF_ICMPNE L8
                        instList.add(new FieldInsnNode(GETSTATIC, BetterRespawnConfig.replace(".", "/"), "clientRespawnCooldown", "I"));// GETFIELD net/minecraft/client/gui/GuiButton.id : I
                        instList.add(new JumpInsnNode(IFNE, continueNode));// IFNE L8
                        instList.add(enableLable);// L7
                        mn.instructions.insertBefore(insnNode, instList);
                        patched = true;
                    }
                }

                if (patched) {
                    BetterRespawnPlugin.LOGGER.info("Successful visiting method {}{}", mn.name, mn.desc);
                    patches++;
                } else {
                    BetterRespawnPlugin.LOGGER.info("Failed visiting method {}{}", mn.name, mn.desc);
                    break;
                }
            }
        }

        if (patches == 3) { // specific patching amount, will rewrite all of this to something better instead
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            cn.accept(classWriter);
            BetterRespawnPlugin.LOGGER.info("Successful patched class {}", transformedName);
            return classWriter.toByteArray();
        } else {
            BetterRespawnPlugin.LOGGER.info("Failed patching class {}", transformedName);
        }

        return basicClass;
    }
}
