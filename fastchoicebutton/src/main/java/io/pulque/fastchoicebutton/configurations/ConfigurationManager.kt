package io.pulque.fastchoicebutton.configurations

/*
 * @author savirdev on 2019-12-02
 */

private const val DEFAULT__CONFIGURATION_TAG = "DEFAULT__CONFIGURATION_TAG"
private const val RIGHT__CONFIGURATION_TAG = "RIGHT__CONFIGURATION_TAG"
private const val LEFT__CONFIGURATION_TAG = "LEFT__CONFIGURATION_TAG"
class ConfigurationManager {

    private val configurations: HashMap<String, ChoiceConfiguration> = HashMap()

    var defaultConfiguration: ChoiceConfiguration = DefaultConfiguration()

    init {
        configurations[defaultConfiguration.getTag()] = defaultConfiguration
    }

    fun registerConfiguration(configuration: ChoiceConfiguration){
        configurations[configuration.getTag()] = configuration
    }

    fun getConfiguration(tag: String) = configurations[tag]
}