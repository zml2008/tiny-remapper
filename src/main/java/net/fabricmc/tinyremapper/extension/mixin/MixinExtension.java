/*
 * Copyright (c) 2016, 2018, Player, asie
 * Copyright (c) 2021, FabricMC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.fabricmc.tinyremapper.extension.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.objectweb.asm.ClassVisitor;

import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyRemapper.Builder;
import net.fabricmc.tinyremapper.api.TrClass;
import net.fabricmc.tinyremapper.api.TrEnvironment;
import net.fabricmc.tinyremapper.extension.mixin.common.Logger;
import net.fabricmc.tinyremapper.extension.mixin.common.data.CommonData;
import net.fabricmc.tinyremapper.extension.mixin.hard.HardTargetMixinClassVisitor;
import net.fabricmc.tinyremapper.extension.mixin.soft.SoftTargetMixinClassVisitor;

/**
 * A extension for remapping mixin annotation.
 */
public class MixinExtension implements TinyRemapper.Extension {
	private final Logger logger;
	private final Map<Integer, List<Consumer<CommonData>>> tasks;

	/**
	 * Remap mixin annotation.
	 * <p>Soft-target: Mixin, Invoker, Accessor, Inject, ModifyArg, ModifyArgs, Redirect, ModifyVariable, ModifyConstant, At, Slice.</p>
	 * <p>Hard-target: Shadow, Overwrite, Accessor, Invoker, Implements.</p>
	 */
	public MixinExtension() {
		this.logger = new Logger();
		this.tasks = new HashMap<>();
	}

	public MixinExtension(Logger.Level logLevel) {
		this.logger = new Logger(logLevel);
		this.tasks = new HashMap<>();
	}

	@Override
	public void attach(Builder builder) {
		builder.extraAnalyzeVisitor(this::analyzeVisitor)
				.extraStateProcessor(this::stateProcessor)
				.extraPreApplyVisitor(this::preApplyVisitor);
	}

	/**
	 * Hard-target: Shadow, Overwrite, Accessor, Invoker, Implements.
	 */
	private ClassVisitor analyzeVisitor(int mrjVersion, String className, ClassVisitor next) {
		tasks.putIfAbsent(mrjVersion, new ArrayList<>());
		return new HardTargetMixinClassVisitor(tasks.get(mrjVersion), next);
	}

	private void stateProcessor(TrEnvironment environment) {
		CommonData data = new CommonData(environment, logger);
		tasks.get(environment.getMrjVersion()).forEach(task -> {
			try {
				task.accept(data);
			} catch (RuntimeException e) {
				logger.error(e.getMessage());
			}
		});
	}

	/**
	 * Soft-target: Mixin, Invoker, Accessor, Inject, ModifyArg, ModifyArgs, Redirect, ModifyVariable, ModifyConstant, At, Slice.
	 */
	public ClassVisitor preApplyVisitor(TrClass cls, ClassVisitor next) {
		return new SoftTargetMixinClassVisitor(new CommonData(cls.getEnvironment(), logger), next);
	}
}

