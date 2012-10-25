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
    boolean isBot() { ping == 0 }

    /**
     * Whether this player is spectating.
     * @return true if player is spectating.
     */
    boolean isSpec() { score == -666 }

    void setName(newName) {
        def plainName = PlayerUtils.sanitizeName(PlayerUtils.decolorName(newName));		
		coloredName = plainName == newName ? newName : PlayerUtils.xonoticColorsToHtml(PlayerUtils.sanitizeName(newName))
        name = plainName;
    }
}
