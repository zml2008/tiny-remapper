package net.fabricmc.tinyremapper.extension.mixin.soft.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.fabricmc.tinyremapper.api.TrMember.MemberType;

class MemberInfoTest {
	@Test
	void isRegex() {
		assertTrue(MemberInfo.isRegex("/^foo/"));
		assertTrue(MemberInfo.isRegex("/bar$/ desc=/^\\(I\\)/"));
		assertTrue(MemberInfo.isRegex("name=/bar$/ desc=/^\\(I\\)/"));
		assertTrue(MemberInfo.isRegex("/Entity/"));
		assertTrue(MemberInfo.isRegex("owner=/\\/google\\//"));
		assertFalse(MemberInfo.isRegex("func_1234_a"));
		assertFalse(MemberInfo.isRegex("field_5678_z:Ljava/lang/String;"));
		assertFalse(MemberInfo.isRegex("Lfoo/bar/Baz;func_1234_a(DDD)V"));
	}

	@Test
	void parse() {
		MemberInfo info;

		info = MemberInfo.parse("{2}(Z)V");
		assertNotNull(info);
		assertEquals(info.getType(), MemberType.METHOD);
		assertEquals(info.getOwner(), "");
		assertEquals(info.getName(), "");
		assertEquals(info.getQuantifier(), "{2}");
		assertEquals(info.getDesc(), "(Z)V");
		assertEquals(info.toString(), "{2}(Z)V");

		info = MemberInfo.parse("field_5678_z:Ljava/lang/String;");
		assertNotNull(info);
		assertEquals(info.getType(), MemberType.FIELD);
		assertEquals(info.getOwner(), "");
		assertEquals(info.getName(), "field_5678_z");
		assertEquals(info.getQuantifier(), "");
		assertEquals(info.getDesc(), "Ljava/lang/String;");
		assertEquals(info.toString(), "field_5678_z:Ljava/lang/String;");

		info = MemberInfo.parse("Lfoo/bar/Baz;func_1234_a(DDD)V");
		assertNotNull(info);
		assertEquals(info.getType(), MemberType.METHOD);
		assertEquals(info.getOwner(), "foo/bar/Baz");
		assertEquals(info.getName(), "func_1234_a");
		assertEquals(info.getQuantifier(), "");
		assertEquals(info.getDesc(), "(DDD)V");
		assertEquals(info.toString(), "Lfoo/bar/Baz;func_1234_a(DDD)V");

		info = MemberInfo.parse("foo.bar.Baz.func_1234_a(DDD)V");
		assertNotNull(info);
		assertEquals(info.getType(), MemberType.METHOD);
		assertEquals(info.getOwner(), "foo/bar/Baz");
		assertEquals(info.getName(), "func_1234_a");
		assertEquals(info.getQuantifier(), "");
		assertEquals(info.getDesc(), "(DDD)V");
		assertEquals(info.toString(), "Lfoo/bar/Baz;func_1234_a(DDD)V");
	}
}
