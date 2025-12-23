package com.masterworks.masterworks.transfer.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.item.ConstructItem;
import com.masterworks.masterworks.location.CompositionReferenceLocation;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class ConstructForgeHandler extends ItemStacksResourceHandler {
    public static final int TEMPLATE_COUNT = 1, RESULT_COUNT = 1;
    public static final int TEMPLATE_INDEX = 0, RESULT_INDEX = 1, COMPONENTS_INDEX = 2;

    public record Constraint(Construct.Component.Key key, RoleReferenceLocation role) {
        public static Constraint of(
                Map.Entry<Construct.Component.Key, RoleReferenceLocation> entry) {
            return new Constraint(entry.getKey(), entry.getValue());
        }
    }

    private final Map<CompositionReferenceLocation, List<Constraint>> constraints;
    private final List<Optional<Construct.Component>> components;

    public ConstructForgeHandler(int componentCount) {
        super(TEMPLATE_COUNT + RESULT_COUNT + componentCount);

        this.constraints = new HashMap<>();
        this.components = Stream.generate(Optional::<Construct.Component>empty)
                .limit(componentCount).collect(Collectors.toCollection(ArrayList::new));
    }


    @Override
    protected void onContentsChanged(int index, @Nonnull ItemStack previousContents) {
        if (index == TEMPLATE_INDEX) {
            setTemplate(getResource(index).toStack());
        } else if (index == RESULT_INDEX) {
        } else {
            setComponent(getResource(index).toStack(), index - COMPONENTS_INDEX);
        }
    }

    private void setTemplate(@Nonnull ItemStack stack) {
        Stream<CompositionReferenceLocation> compositions =
                Optional.ofNullable(stack.get(MasterworksDataComponents.TEMPLATE)).stream()
                        .flatMap(template -> template.compositions().stream());

        constraints.clear();
        compositions.forEachOrdered(reference -> {
            var entries = reference.registered().value().components().entrySet();

            if (entries.size() > components.size()) {
                MasterworksMod.LOGGER.warn(
                        "Skipping composition {} as it has more components than the construct forge can handle ({} > {})",
                        reference, entries.size(), components.size());
                return;
            }

            constraints.put(reference, entries.stream().map(Constraint::of).toList());
        });

        setResult();
    }

    private void setComponent(@Nonnull ItemStack stack, int componentIndex) {
        if (componentIndex < 0 || componentIndex >= components.size()) {
            return;
        }

        components.set(componentIndex, Construct.Component.of(stack));
        setResult();
    }

    private void setResult() {
        constraints.entrySet().stream().flatMap(entry -> {
            CompositionReferenceLocation composition = entry.getKey();
            List<Constraint> constraints = entry.getValue();

            Map<Construct.Component.Key, Construct.Component> constructed = new HashMap<>();

            for (int index = 0; index < constraints.size(); index++) {
                Constraint constraint = constraints.get(index);
                Optional<Construct.Component> optionalComponent = components.get(index);

                if (optionalComponent.isEmpty()) {
                    return Stream.of();
                }

                Construct.Component component = optionalComponent.orElseThrow();

                if (component.roles().noneMatch(role -> role.equals(constraint.role))) {
                    return Stream.of();
                }

                constructed.put(constraint.key, component);
            }

            return Stream.of(new Construct(composition, constructed));
        }).findFirst().ifPresentOrElse(construct -> {
            set(RESULT_INDEX, ItemResource.of(ConstructItem.stack(construct)), 1);
        }, () -> {
            set(RESULT_INDEX, ItemResource.EMPTY, 0);
        });
    }


    @Override
    public boolean isValid(int index, @Nonnull ItemResource resource) {
        if (index == TEMPLATE_INDEX) {
            return canSetTemplate(resource.toStack());
        } else if (index == RESULT_INDEX) {
            return canSetResult();
        } else {
            return canSetComponent(resource.toStack(), index - COMPONENTS_INDEX);
        }
    }

    private boolean canSetTemplate(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && stack.get(MasterworksDataComponents.TEMPLATE) != null;
    }

    private boolean canSetComponent(@Nonnull ItemStack stack, int componentIndex) {
        if (componentIndex < 0 || componentIndex >= components.size()) {
            return false;
        }

        return Construct.Component.of(stack).map(component -> {
            Set<RoleReferenceLocation> roles =
                    getComponentRoles(componentIndex).collect(Collectors.toSet());

            return component.roles().anyMatch(roles::contains);
        }).orElse(false);
    }

    public Stream<RoleReferenceLocation> getComponentRoles(int componentIndex) {
        return constraints.values().stream().filter(
                list -> list.size() > componentIndex && validateConstraints(list, componentIndex))
                .map(list -> list.get(componentIndex).role);
    }

    private boolean validateConstraints(List<Constraint> constraints, int exceptComponentIndex) {
        for (int index = 0; index < constraints.size(); index++) {
            if (index == exceptComponentIndex) {
                continue;
            }

            if (components.size() <= index) {
                continue;
            }

            Constraint constraint = constraints.get(index);

            boolean emptyOrMatching = components.get(index).map(
                    component -> component.roles().anyMatch(role -> role.equals(constraint.role)))
                    .orElse(true);

            if (!emptyOrMatching) {
                return false;
            }
        }

        return true;
    }

    private boolean canSetResult() {
        return false;
    }
}
