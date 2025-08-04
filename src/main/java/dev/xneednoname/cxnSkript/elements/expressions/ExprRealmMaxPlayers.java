package dev.xneednoname.cxnSkript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import de.cytooxien.realms.api.Action;
import de.cytooxien.realms.api.RealmInformationProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprRealmMaxPlayers extends SimpleExpression<Integer> {
    static {
        Skript.registerExpression(
                ExprRealmMaxPlayers.class,
                Integer.class,
                ExpressionType.PROPERTY,
                "[the] realm['s] (max players|player limit)"
        );
    }

    @Override
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "the realm name";
    }

    @Override
    @Nullable
    protected Integer[] get(Event event) {
        RealmInformationProvider provider = Bukkit.getServicesManager().load(RealmInformationProvider.class);
        if (provider == null) {
            Skript.warning("RealmInformationProvider service not available");
            return null;
        }
        Integer maxplayers = provider.maxPlayers();
        return new Integer[]{maxplayers};
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        RealmInformationProvider provider = Bukkit.getServicesManager().load(RealmInformationProvider.class);
        if (provider == null) {
            Skript.warning("RealmInformationProvider service not available");
            return;
        }

        Integer newmaxplayers;
        if (mode == Changer.ChangeMode.SET) {
            newmaxplayers = (Integer) delta[0];
        } else {
            newmaxplayers = 5;
        }

        Action<Void> action = provider.updateMaximumPlayers(newmaxplayers);
        handleAction(action, "Failed to change realm max player count.");
    }

    private void handleAction(Action<Void> action, String errorPrefix) {
        if (action.rateLimited()) {
            Skript.warning(errorPrefix + " (rate limited)");
            return;
        }

        if (!action.success()) {
            Throwable error = action.throwable();
            Skript.warning(errorPrefix + (error != null ? ": " + error.getMessage() : ""));
        }
    }
}
