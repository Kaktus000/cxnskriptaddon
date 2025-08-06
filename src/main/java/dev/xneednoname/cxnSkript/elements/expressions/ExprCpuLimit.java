package dev.xneednoname.cxnSkript.elements.expressions;

import ch.njol.skript.lang.Expression;
import de.cytooxien.realms.api.RealmInformationProvider;
import de.cytooxien.realms.api.model.Limits;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.util.Kleenean;

public class ExprCpuLimit extends SimpleExpression<Long> {

    static {
        Skript.registerExpression(
                ExprCpuLimit.class,
                Long.class,
                ExpressionType.SIMPLE,
                "[the] [realm's] cpu limit"
        );
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected Long @Nullable [] get(Event event) {
        RealmInformationProvider provider = Bukkit.getServicesManager().load(RealmInformationProvider.class);
        if (provider == null) {
            Skript.warning("RealmInformationProvider service not available");
            return null;
        }

        try {
            Limits limits = provider.limits(); // Using the direct int method
            return new Long[] { limits.cpuLimit() };
        } catch (Exception e) {
            Skript.warning("Failed to get realm cpu limit: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Long> getReturnType() {
        return Long.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "the realm's cpu limit";
    }
}