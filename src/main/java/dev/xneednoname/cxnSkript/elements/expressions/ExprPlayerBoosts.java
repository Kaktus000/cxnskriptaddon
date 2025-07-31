package dev.xneednoname.cxnSkript.elements.expressions;

import de.cytooxien.realms.api.RealmInformationProvider;
import de.cytooxien.realms.api.Action;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;

import java.util.UUID;

public class ExprPlayerBoosts extends SimplePropertyExpression<Player, Integer> {

    static {
        Skript.registerExpression(
                ExprPlayerBoosts.class,
                Integer.class,
                ExpressionType.PROPERTY,
                "[the] boost count of %player%",
                "%player%'s boost count",
                "boost count of %player%"
        );
    }

    @Override
    public @Nullable Integer convert(Player player) {
        if (player == null) {
            Skript.warning("Boost count expression called with null player!");
            return null;
        }

        RealmInformationProvider provider = Bukkit.getServicesManager().load(RealmInformationProvider.class);
        if (provider == null) {
            Skript.warning("RealmInformationProvider service not available");
            return null;
        }

        Action<Integer> action = provider.boostsByPlayer(player.getUniqueId());

        if (action.rateLimited()) {
            Skript.warning("Boost count request rate limited for player: " + player.getName());
            return null;
        }

        if (!action.success()) {
            Throwable error = action.throwable();
            Skript.warning("Failed to get boost count for " + player.getName() +
                    (error != null ? ": " + error.getMessage() : ""));
            return null;
        }

        Integer boosts = action.value();
        return boosts != null ? boosts : 0;
    }

    @Override
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Override
    protected String getPropertyName() {
        return "boost count";
    }
}