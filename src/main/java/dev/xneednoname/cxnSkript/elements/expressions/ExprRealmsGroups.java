package dev.xneednoname.cxnSkript.elements.expressions;

import ch.njol.skript.lang.Expression;
import de.cytooxien.realms.api.RealmInformationProvider;
import de.cytooxien.realms.api.RealmPermissionProvider;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.util.Kleenean;

import java.util.List;

public class ExprRealmsGroups extends SimpleExpression<List> {

    static {
        Skript.registerExpression(
                ExprRealmsGroups.class,
                List.class,
                ExpressionType.SIMPLE,
                "[all] [of] [the] realm['s] groups"
        );
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected List @Nullable [] get(Event event) {
        RealmPermissionProvider provider = Bukkit.getServicesManager().load(RealmPermissionProvider.class);
        if (provider == null) {
            Skript.warning("RealmPermissionProvider service not available");
            return null;
        }

        try {
            List groups = provider.groups();
            return new List[] { groups };
        } catch (Exception e) {
            Skript.warning("Failed to get realms groups: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends List> getReturnType() {
        return List.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "all of the realms groups";
    }
}