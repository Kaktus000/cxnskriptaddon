package dev.xneednoname.cxnSkript.elements.expressions;

import ch.njol.skript.lang.Expression;
import de.cytooxien.realms.api.RealmInformationProvider;
import de.cytooxien.realms.api.RealmPermissionProvider;
import de.cytooxien.realms.api.model.Group;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.util.Kleenean;

import java.util.List;

public class ExprOwnerGroup extends SimpleExpression<Group> {

    static {
        Skript.registerExpression(
                ExprOwnerGroup.class,
                Group.class,
                ExpressionType.SIMPLE,
                "[the] realm['s] owner group",
                "[the] owner group of the realm"
        );
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected Group @Nullable [] get(Event event) {
        RealmPermissionProvider provider = Bukkit.getServicesManager().load(RealmPermissionProvider.class);
        if (provider == null) {
            Skript.warning("RealmPermissionProvider service not available");
            return null;
        }

        try {
            Group group = provider.ownerGroup();
            return new Group[] { group };
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
    public Class<? extends Group> getReturnType() {
        return Group.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "the realm's owner group";
    }
}