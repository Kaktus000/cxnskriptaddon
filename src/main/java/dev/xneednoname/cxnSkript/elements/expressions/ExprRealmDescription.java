package dev.xneednoname.cxnSkript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import de.cytooxien.realms.api.Action;
import de.cytooxien.realms.api.RealmInformationProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprRealmDescription extends SimpleExpression<String> {

    /**
     * Ein universeller Serializer, der <#RRGGBB>-Hex-Farben und '&'-Zeichen erkennt.
     * Dies wird verwendet, um den Text zu deserialisieren, den ein Skript-Nutzer eingibt.
     * Das '§'-Zeichen wird von Minecrafts interner API erwartet.
     */
    private static final LegacyComponentSerializer SERIALIZER_TO_ADVENTURE = LegacyComponentSerializer.builder()
            .hexColors() // Support für <#RRGGBB>
            .character('&') // Support für '&'
            .build();

    /**
     * Ein einfacher Legacy-Serializer, um einen String mit '§'-Farbcodes zu erstellen.
     * Wird verwendet, wenn der Text an die Realms-API gesendet wird, da diese `§` erwartet.
     */
    private static final LegacyComponentSerializer SERIALIZER_TO_LEGACY_STRING = LegacyComponentSerializer.legacySection();

    static {
        Skript.registerExpression(
                ExprRealmDescription.class,
                String.class,
                ExpressionType.PROPERTY,
                "[the] realm['s] description"
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
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "the realm description";
    }

    @Override
    @Nullable
    protected String[] get(Event event) {
        RealmInformationProvider provider = Bukkit.getServicesManager().load(RealmInformationProvider.class);
        if (provider == null) {
            Skript.warning("RealmInformationProvider service not available");
            return null;
        }

        // Schritt 1: Hole den rohen String von der API (er enthält wahrscheinlich §-Farbcodes)
        String description = provider.description();

        // Schritt 2: Deserialisiere den rohen String in eine Adventure Component
        // Dies wandelt alle §-Codes und Hex-Codes korrekt in eine Komponente um.
        Component component = SERIALIZER_TO_LEGACY_STRING.deserialize(description);

        // Schritt 3: Serialisiere die Component zurück in einen String,
        // der für Skript-Benutzer gut lesbar ist (mit '&' und <#...>).
        String formattedDescription = SERIALIZER_TO_ADVENTURE.serialize(component);

        return new String[]{formattedDescription};
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        RealmInformationProvider provider = Bukkit.getServicesManager().load(RealmInformationProvider.class);
        if (provider == null) {
            Skript.warning("RealmInformationProvider service not available");
            return;
        }

        String newDescription;
        if (mode == Changer.ChangeMode.SET) {
            newDescription = (String) delta[0];
        } else {
            newDescription = "CxnSkriptAddon-Default Description";
        }

        // Schritt 1: Deserialisiere den Skript-Eingabe-String in eine Adventure Component.
        // Der SERIALIZER_TO_ADVENTURE verarbeitet dabei '&', '§' und Hex-Farben.
        Component component = SERIALIZER_TO_ADVENTURE.deserialize(newDescription);

        // Schritt 2: Serialisiere die Component zurück in einen einfachen String,
        // der ausschließlich '§'-Zeichen für die Farbcodierung verwendet.
        // Die Realms-API erwartet dieses Format in den meisten Fällen.
        String legacyDescription = SERIALIZER_TO_LEGACY_STRING.serialize(component);

        // Schritt 3: Sende den konvertierten, legacy-formatierten String an die API.
        Action<Void> action = provider.changeDescription(legacyDescription);

        handleAction(action, "Failed to change realm description");
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
