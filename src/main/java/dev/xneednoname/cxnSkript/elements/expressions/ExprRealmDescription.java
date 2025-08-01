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
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprRealmDescription extends SimpleExpression<String> {
    static {
        Skript.registerExpression(
                ExprRealmDescription.class,
                String.class,
                ExpressionType.PROPERTY,
                "[the] realm['s] description"
        );
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
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
    protected String[] get(Event event) {
        RealmInformationProvider provider = Bukkit.getServicesManager().load(RealmInformationProvider.class);
        if (provider == null) {
            Skript.warning("RealmInformationProvider service not available");
            return null;
        }
        String name = provider.description();
        return new String[]{name};
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

        String newName;
        if (mode == Changer.ChangeMode.SET) {
            newName = (String) delta[0];
        } else {
            newName = "CxnSkriptAddon-Default Name";
        }

        Action<Void> action = provider.changeDescription(newName);
        handleAction(action, "Failed to change realm description");
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
