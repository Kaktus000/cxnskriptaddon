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


public class ExprGroupsMembers extends SimpleExpression<OfflinePlayer> {

    static {
        Skript.registerExpression(
                ExprGroupsMembers.class,
                OfflinePlayer.class,
                ExpressionType.PROPERTY,
                "[the] members of %group%",
                "%group%'s members"
        );
    }

    private Expression<Group> group;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        group = (Expression<Group>) exprs[0];
        return true;
    }

    @Override
    protected OfflinePlayer[] get(Event event) {
        Group g = group.getSingle(event);
        if (g == null) return null;

        RealmPermissionProvider provider = Bukkit.getServicesManager().load(RealmPermissionProvider.class);
        if (provider == null) return null;

        Set<UUID> memberUUIDs = provider.groupMembers(g.uniqueId()).value();
        if (memberUUIDs == null) return null;

        List<OfflinePlayer> members = new ArrayList<>();
        for (UUID uuid : memberUUIDs) {
            members.add(Bukkit.getOfflinePlayer(uuid));
        }

        return members.toArray(new OfflinePlayer[0]);
    }

    @Override
    public Class<? extends OfflinePlayer> getReturnType() {
        return OfflinePlayer.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "members of group " + group.toString(event, debug);
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) {
            return CollectionUtils.array(OfflinePlayer.class);
        }
        return null;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Group g = group.getSingle(event);
        if (g == null) return;

        RealmPermissionProvider provider = Bukkit.getServicesManager().load(RealmPermissionProvider.class);
        if (provider == null) return;

        for (Object o : delta) {
            if (!(o instanceof OfflinePlayer)) continue;
            UUID playerId = ((OfflinePlayer) o).getUniqueId();
            UUID groupId = g.uniqueId();

            if (mode == Changer.ChangeMode.ADD) {
                provider.addPlayerToGroup(playerId, groupId);
            } else if (mode == Changer.ChangeMode.REMOVE) {
                provider.removePlayerFromGroup(playerId, groupId);
            }
        }
    }
}