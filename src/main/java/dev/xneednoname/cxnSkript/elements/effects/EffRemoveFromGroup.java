package dev.xneednoname.cxnSkript.elements.effects;

import de.cytooxien.realms.api.RealmPermissionProvider;
import de.cytooxien.realms.api.model.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffRemoveFromGroup extends Effect {

    static {
        Skript.registerEffect(EffAddToGroup.class, "remove %player% from %group%");
    }

    private Expression<Player> player;
    private Expression<Group> group;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        this.player = (Expression<Player>) expressions[0];
        this.group = (Expression<Group>) expressions[1];
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "add player to group";
    }

    @Override
    protected void execute(Event event) {
        if (player == null)  return;
        Player p = player.getSingle(event);
        Group g = group.getSingle(event);
        for (Player user : player.getAll(event)) {
            RealmPermissionProvider provider = Bukkit.getServicesManager().load(RealmPermissionProvider.class);
            if (provider == null) {
                Skript.warning("Failed to get RealmPermissionProvider Service" );
            } else {
                provider.removePlayerFromGroup(p.getUniqueId(),g.uniqueId());
            }

        }
    }
}