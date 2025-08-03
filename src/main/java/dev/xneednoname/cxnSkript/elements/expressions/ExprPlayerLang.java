package dev.xneednoname.cxnSkript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.ExpressionType;
import de.cytooxien.realms.api.PlayerInformationProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.eclipse.jdt.annotation.Nullable;

public class ExprPlayerLang extends SimplePropertyExpression<Player, String> {
    static {
        Skript.registerExpression(
                ExprPlayerLang.class,
                String.class,
                ExpressionType.PROPERTY,
                "[the] cytooxien lang[uage] of player",
                "%player%'s cytooxien lang[uage]"
        );
    }

    @Override
    public @Nullable String convert(Player player) {
        if (player == null) {
            Skript.warning("PlayerLang expression called with null player!");
            return null;
        }

        PlayerInformationProvider provider = Bukkit.getServicesManager().load(PlayerInformationProvider.class);
        if (provider == null) {
            Skript.warning("PlayerInformationProvider service not available");
            return null;
        }

        String lang = provider.language(player.getUniqueId());

        return lang;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected String getPropertyName() {
        return "cytooxien language";
    }
}
