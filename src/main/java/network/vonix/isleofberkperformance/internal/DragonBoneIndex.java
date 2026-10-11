package network.vonix.isleofberkperformance.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import software.bernie.geckolib3.core.processor.IBone;

/**
 * Per-animation-processor index for Isle of Berk dragon bones.
 *
 * <p>The source GeckoLib list remains authoritative. Entries retain the bone's
 * list index and identity so direct list mutations cannot return a stale bone.
 * Misses are deliberately not cached because callers can append to the live
 * GeckoLib list through getModelRendererList().
 */
public final class DragonBoneIndex {
    private final Map<String, Entry> entries = new HashMap<>();

    /**
     * Returns a valid cached bone, or {@code null} when the caller must run the
     * original GeckoLib linear lookup.
     */
    public IBone lookup(String name, List<IBone> modelRendererList) {
        Entry entry = this.entries.get(name);
        if (entry == null) {
            return null;
        }
        if (entry.isCurrent(name, modelRendererList)
                && !entry.hasEarlierMatch(name, modelRendererList)) {
            return entry.bone;
        }
        this.entries.remove(name, entry);
        return null;
    }

    /**
     * Records an already-resolved bone only when it is present in the current
     * authoritative list. Existing valid first registrations win for duplicate
     * names, matching GeckoLib's first-match linear scan.
     */
    public void observe(String name, IBone bone, List<IBone> modelRendererList) {
        if (bone == null) {
            return;
        }
        int index = indexOfIdentity(bone, modelRendererList);
        if (index < 0) {
            return;
        }
        Entry existing = this.entries.get(name);
        if (existing == null || !existing.isCurrent(name, modelRendererList)) {
            this.entries.put(name, new Entry(bone, index));
        }
    }

    /** Records a newly appended registration without replacing a valid duplicate. */
    public void observeRegistered(IBone bone, List<IBone> modelRendererList) {
        if (bone == null || modelRendererList.isEmpty()) {
            return;
        }
        String name = bone.getName();
        Entry existing = this.entries.get(name);
        if (existing == null || !existing.isCurrent(name, modelRendererList)) {
            int index = modelRendererList.size() - 1;
            if (index >= 0 && modelRendererList.get(index) == bone) {
                this.entries.put(name, new Entry(bone, index));
            }
        }
    }

    public void clear() {
        this.entries.clear();
    }

    private static int indexOfIdentity(IBone bone, List<IBone> modelRendererList) {
        for (int i = 0; i < modelRendererList.size(); i++) {
            if (modelRendererList.get(i) == bone) {
                return i;
            }
        }
        return -1;
    }

    private static final class Entry {
        private final IBone bone;
        private final int index;

        private Entry(IBone bone, int index) {
            this.bone = bone;
            this.index = index;
        }

        private boolean hasEarlierMatch(String requestedName, List<IBone> modelRendererList) {
            for (int i = 0; i < this.index; i++) {
                IBone earlier = modelRendererList.get(i);
                if (earlier != null && Objects.equals(requestedName, earlier.getName())) {
                    return true;
                }
            }
            return false;
        }

        private boolean isCurrent(String requestedName, List<IBone> modelRendererList) {
            return this.index >= 0
                    && this.index < modelRendererList.size()
                    && modelRendererList.get(this.index) == this.bone
                    && Objects.equals(requestedName, this.bone.getName());
        }
    }
}
