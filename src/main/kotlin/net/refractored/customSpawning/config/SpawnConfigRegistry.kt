package net.refractored.customSpawning.config

import net.refractored.customSpawning.CustomSpawningExtension
import org.bukkit.World

object SpawnConfigRegistry {
    @JvmStatic
    private val registeredConfigs = mutableListOf<SpawnConfig>()

    /**
     * Gets a preset from the loaded presets
     * @param name The name of the preset
     * @return The itemstack for the preset, or null if it does not exist
     */
    @JvmStatic
    fun getHordeConfig(world: World): SpawnConfig? = registeredConfigs.find { it.worlds.contains(world) }

    /**
     * Gets a read-only map of all the presets loaded.
     * @return The mutable preset list
     */
    @JvmStatic
    fun getHordeConfigs() = registeredConfigs.toList()

    /**
     * Create a new preset and adds it to the config & map
     * @param name The name of the preset
     * @param item The itemstack for the preset
     */
    @JvmStatic
    fun createPreset(config: SpawnConfig) {
        for (world in config.worlds) {
            if (registeredConfigs.any { it.worlds.contains(world) }) {
                throw IllegalArgumentException("Multiple presets cannot be applied to the same world!")
            }
        }
        registeredConfigs.add(config)
    }

    /**
     * Removes a preset from the config & map
     * @return The itemstack that was created
     * @param name The name of the preset
     */
    @JvmStatic
    fun removePreset(config: SpawnConfig) {
        registeredConfigs.remove(config)
    }

    /**
     * Deletes all presets in the map and populates it with the presets in the config.
     */
    @JvmStatic
    fun refreshHordeConfigs() {
        registeredConfigs.clear()
        val config = CustomSpawningExtension.instance.spawnConfigYml
        val section = config.getConfigurationSection("Presets")
        val keys = section!!.getKeys(false)
        if (keys.isEmpty()) return
        for (key in keys) {
            config.getConfigurationSection(key)?.let { sectionKey ->
                createPreset(SpawnConfig(sectionKey))
            } ?: continue
        }
    }
}
