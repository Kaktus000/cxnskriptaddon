package dev.xneednoname.cxnSkript.elements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import de.cytooxien.realms.api.DisplayProvider;
import de.cytooxien.realms.api.RealmInformationProvider;
import de.cytooxien.realms.api.Action;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

public class ExprPlayerTabSuffix extends SimpleExpression<String> {

    // Erstelle einen universellen Serializer, der alle drei Formate unterstützt
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .hexColors() // Support für <#RRGGBB> Hex-Farben
            .character('&') // Support für '&'-Farbcodes
            .build();
    static {
        Skript.registerExpression(
                ExprPlayerTabSuffix.class,
                String.class,
                ExpressionType.PROPERTY,
                "%player%'s [custom] tab[list] suffix",
                "[the] [custom] tab[list] suffix of %player%"
        );
    }
    private Expression<Player> player;

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
        return "tab suffix of player";
    }

    @Override
    @Nullable
    protected String[] get(Event event) {
        DisplayProvider provider = Bukkit.getServicesManager().load(DisplayProvider.class);
        if (provider == null) {
            Skript.warning("DisplayProvider service not available");
            return null;
        }
        Player p = player.getSingle(event);
        // Verwende den universellen Serializer, um die Komponente in einen String zu konvertieren
        // Der String kann jetzt Hex-Farben, Ampersand-Zeichen und Sektionszeichen enthalten.
        String name = SERIALIZER.serialize(provider.getTabSuffix(p));

        // Ersetze § durch &, da Skript meistens '&' erwartet.
        // Dies stellt sicher, dass das Ergebnis für Skript-Benutzer intuitiv ist.
        name = name.replace('§', '&');

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
        DisplayProvider provider = Bukkit.getServicesManager().load(DisplayProvider.class);
        if (provider == null) {
            Skript.warning("DisplayProvider service not available");
            return;
        }

        String newName;
        if (mode == ChangeMode.SET) {
            newName = (String) delta[0];
        } else {
            newName = "CxnSkriptAddon-Default Name";
        }
        Player p = player.getSingle(event);
        // Ersetze '&' durch '§', um sicherzustellen, dass legacySection-Formatierung korrekt deserialisiert wird.
        // Die .hexColors()-Option behandelt die Hex-Codes automatisch.
        newName = newName.replace('&', '§');

        // Deserialisiere den String (mit Hex-Farben und §) in eine Komponente
        Component componentName = LegacyComponentSerializer.legacySection().deserialize(newName);
        provider.setTabSuffix(p, componentName);

    }
}