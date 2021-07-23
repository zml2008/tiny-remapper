package net.fabricmc.tinyremapper.extension.mixin.hard.annotation;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.objectweb.asm.AnnotationVisitor;

import net.fabricmc.tinyremapper.extension.mixin.common.data.AnnotationElement;
import net.fabricmc.tinyremapper.extension.mixin.common.data.CommonData;
import net.fabricmc.tinyremapper.extension.mixin.common.data.Constant;
import net.fabricmc.tinyremapper.extension.mixin.common.data.MxMember;
import net.fabricmc.tinyremapper.extension.mixin.hard.util.ConvertedMappable;

/**
 * In case of multi-target, if a remap conflict is detected,
 * an error message will show up and the behaviour is undefined.
 */
public class ShadowAnnotationVisitor extends AnnotationVisitor {
	private final List<Consumer<CommonData>> tasks;
	private final MxMember member;
	private final List<String> targets;

	private boolean remap;
	private String prefix;

	public ShadowAnnotationVisitor(List<Consumer<CommonData>> tasks, AnnotationVisitor delegate, MxMember member, boolean remap, List<String> targets) {
		super(Constant.ASM_VERSION, delegate);
		this.tasks = Objects.requireNonNull(tasks);
		this.member = Objects.requireNonNull(member);

		this.targets = Objects.requireNonNull(targets);
		this.remap = remap;
		this.prefix = "shadow$";
	}

	@Override
	public void visit(String name, Object value) {
		if (name.equals(AnnotationElement.REMAP)) {
			remap = Objects.requireNonNull((Boolean) value);
		} else if (name.equals(AnnotationElement.PREFIX)) {
			prefix = Objects.requireNonNull((String) value);
		}

		super.visit(name, value);
	}

	@Override
	public void visitEnd() {
		if (remap) {
			tasks.add(data -> new ShadowPrefixMappable(data, member, targets, prefix).result());
		}

		super.visitEnd();
	}

	private static class ShadowPrefixMappable extends ConvertedMappable {
		private final String prefix;

		ShadowPrefixMappable(CommonData data, MxMember self, Collection<String> targets, String prefix) {
			super(data, self, targets);
			Objects.requireNonNull(prefix);

			this.prefix = self.getName().startsWith(prefix) ? prefix : "";
		}

		@Override
		protected String getConvertedName() {
			return self.getName().substring(prefix.length());
		}

		@Override
		protected String getConvertedDesc() {
			return self.getDesc();
		}

		@Override
		protected String revertName(String name) {
			return prefix + name;
		}
	}
}
