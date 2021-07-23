package net.fabricmc.tinyremapper.extension.mixin.soft.annotation.injection;

import java.util.List;

import org.objectweb.asm.AnnotationVisitor;

import net.fabricmc.tinyremapper.extension.mixin.common.data.Annotation;
import net.fabricmc.tinyremapper.extension.mixin.common.data.CommonData;

public class ModifyArgsAnnotationVisitor extends CommonInjectionAnnotationVisitor {
	public ModifyArgsAnnotationVisitor(CommonData data, AnnotationVisitor delegate, boolean remap, List<String> targets) {
		super(Annotation.MODIFY_ARGS, data, delegate, remap, targets);
	}
}
