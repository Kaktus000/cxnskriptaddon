package dev.xneednoname.cxnSkript.elements.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import de.cytooxien.realms.api.RealmInformationProvider;
import de.cytooxien.realms.api.model.Limits;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class CondCanHaveSubdomain extends Condition {

    static {
        Skript.registerCondition(CondCanHaveSubdomain.class,
                "[the] [realm] (1¦can|2¦can('t| not)) have [a] subdomain");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setNegated(parseResult.mark == 1);
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "whether the realm can have a subdomain or not";
    }

    @Override
    public boolean check(Event event) {
        RealmInformationProvider provider = Bukkit.getServicesManager().load(RealmInformationProvider.class);
        if (provider == null) {
            Skript.warning("RealmInformationProvider service not available");
            return !isNegated();
        }

        try {
            Limits limits = provider.limits();
            boolean canHaveCustomPlugins = limits.subdomain();
            return isNegated() ? !canHaveCustomPlugins : canHaveCustomPlugins;
        } catch (Exception e) {
            Skript.warning("Failed to get realm subdomain allowance: " + e.getMessage());
            return !isNegated();
        }
    }
}