package dev.xneednoname.cxnSkript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import de.cytooxien.realms.api.Action;
import de.cytooxien.realms.api.RealmPermissionProvider;
import de.cytooxien.realms.api.model.Group;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ExprPlayersGroups extends SimpleExpression<List> {

    static {
        Skript.registerExpression(
                ExprPlayersGroups.class,
                List.class,
                ExpressionType.PROPERTY,
                "[all] [of] %player%'s groups",
                "[all] [of] [the] groups of %player%"
        );
    }

    private Expression<OfflinePlayer> player;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return true;
    }

    @Override
    protected List[] get(Event event) {
        OfflinePlayer o = player.getSingle(event);
        if (o == null) return null;

        RealmPermissionProvider provider = Bukkit.getServicesManager().load(RealmPermissionProvider.class);
        if (provider == null) return null;

        Action<List<Group>> g = provider.groupsOfPlayer(o.getUniqueId());
        List<Group> groups = g.value(); // tatsächliche Liste aus dem Action-Objekt
        return new List[] { groups };
    }

    @Override
    public Class<? extends List> getReturnType() {
        return List.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "all groups of player";
    }
}