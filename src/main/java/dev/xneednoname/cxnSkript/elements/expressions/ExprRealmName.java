package dev.xneednoname.cxnSkript.elements.expressions;

import de.cytooxien.realms.api.RealmInformationProvider;
import de.cytooxien.realms.api.Action;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprRealmName extends SimpleExpression<String> {

    static {
        Skript.registerExpression(
                ExprRealmName.class,
                String.class,
                ExpressionType.PROPERTY,
                "[the] realm['s] name"
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
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
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
        String name = LegacyComponentSerializer.legacySection().serialize(provider.realmDisplayName());
        return new String[]{name};
    }

    @Override
    public Class<?>[] acceptChange(final ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }

    @Override
    public void change(Event event, Object[] delta, ChangeMode mode) {
        RealmInformationProvider provider = Bukkit.getServicesManager().load(RealmInformationProvider.class);
        if (provider == null) {
            Skript.warning("RealmInformationProvider service not available");
            return;
        }

        String newName;
        if (mode == ChangeMode.SET) {
            newName = (String) delta[0];
        } else {
            newName = "CxnSkriptAddon-Default Name";
        }

        Component componentName = LegacyComponentSerializer.legacySection().deserialize(newName);
        Action<Void> action = provider.changeName(componentName);

        handleAction(action, "Failed to change realm name");
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