package dev.xneednoname.cxnSkript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import de.cytooxien.realms.api.RealmPermissionProvider;
import de.cytooxien.realms.api.model.Group;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprGroupByName extends SimpleExpression<Group> {

    static {
        Skript.registerExpression(ExprGroupByName.class, Group.class, ExpressionType.PROPERTY,
                "group named %string%",
                "group with name %string%",
                "%string%['s] group"
        );
    }

    private Expression<String> name;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        name = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected Group[] get(Event event) {
        String groupName = name.getSingle(event);
        if (groupName == null) return null;

        RealmPermissionProvider provider = Bukkit.getServicesManager().load(RealmPermissionProvider.class);
        if (provider == null) return null;

        return new Group[]{provider.firstGroupWithName(groupName)};
    }

    @Override
    public Class<? extends Group> getReturnType() {
        return Group.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "group named " + name.toString(event, debug);
    }
}