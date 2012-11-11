package jk.querynex;

import groovy.util.GroovyTestCase;

class PlayerUtilsTest extends GroovyTestCase {

	public void testXonoticColorsToHtml() {
		assertEquals "<span style='color:#aaaaaa'>Pank<span style='color:#ffff00'>y</span></span>",
			PlayerUtils.xonoticColorsToHtml('^xaaaPank^xff0y')
	}

	public void testDecolorName() {
		assertEquals 'testName', PlayerUtils.decolorName('^xccctest^xA90Name')
	}

	public void testSanitizeName() {
		byte[] bytes = [200, 201]
		def name = '^x12ftestName' + new String(bytes, 'ISO-8859-1')
		assertEquals 'testNameHI', PlayerUtils.sanitizeName(name)
	}
}
