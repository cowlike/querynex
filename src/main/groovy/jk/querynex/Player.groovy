package jk.querynex;

public class Player {

	String name;
	String coloredName;
	int score;
	int ping;
	int team;

	/**
	 * Whether this player is a bot.
	 * @return True if player is a bot.
	 */
	public boolean isBot() {
		ping == 0
	}

	/**
	 * Whether this player is spectating.
	 * @return true if player is spectating.
	 */
	public boolean isSpec() {
		score == -666
	}

	public void setName(newName) {
		def plainName = PlayerUtils.sanitizeName(PlayerUtils.decolorName(newName));
		coloredName = plainName == newName ? newName : PlayerUtils.xonoticColorsToHtml(PlayerUtils.sanitizeName(newName))
		name = plainName;
	}

	@Override
	String toString() {
		"${isSpec()? '-': '+'}${isBot()? '(bot)': ''}${name}"
	}
}
