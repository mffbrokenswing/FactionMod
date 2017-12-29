package factionmod.manager.instanciation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import factionmod.manager.IChunkManager;
import factionmod.utils.ServerUtils;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

/**
 * Reprensents a type of {@link IChunkManager}. A zone should be added through a
 * file in the config directory, using the file "zones.json" or, creating your
 * own file and sending it's name with the {@link IMCMessage}. If you don't add
 * a zone using a file, it doesn't allow the owner of the server to custom it,
 * and it's not what I want. But if you really want to add a zone using the
 * code, it should be done during the pre-initialization phase to allow users to
 * override your zones through the default file of configuration. When a zone is
 * registered, it will override the zone with the same name which is already
 * registered. <br />
 * There is two types of zones, the first ones are the zone with a
 * pre-instanciated {@link IChunkManager}. The second ones are the zones which
 * require arguments to instanciate an {@link IChunkManager}. <br />
 * First type :
 * <ul>
 * <li>"name":"the name of the zone"</li>
 * <li>"class":"the class where the IChunkManager is instanciated"</li>
 * <li>"instance":"the name of the field holding the instance"</li>
 * </ul>
 * Second type :
 * <ul>
 * <li>"name":"the name of the zone"</li>
 * <li>"class":"the class of the IChunkManager to instanciate"</li>
 * </ul>
 *
 * Each zone has to be an Object and all the zones have to be listed in an Array
 * in a JSON file.
 *
 * @author BrokenSwing
 *
 */
public class Zone {

    private final String  name;
    private final boolean standalone;
    private IChunkManager instance;
    private Class<?>      clazz;
    private final String  parameters;

    public Zone(final String name, final String className, final String parameters) throws Exception {
        ServerUtils.getProfiler().startSection("zoneCreation");

        this.name = name;
        this.clazz = Class.forName(className);
        this.parameters = parameters;

        if (!IChunkManager.class.isAssignableFrom(this.clazz))
            throw new Exception("The class " + this.clazz.getName() + " doesn't implements " + IChunkManager.class.getName() + ".");
        if (this.clazz.getConstructors().length > 1)
            throw new Exception("The class " + this.clazz.getName() + " has multiple constructors.");
        this.standalone = false;

        ServerUtils.getProfiler().endSection();
    }

    public Zone(final String name, final String className, final String instanceField, final String parameters) throws Exception {
        ServerUtils.getProfiler().startSection("zoneCreation");

        this.name = name;
        this.parameters = parameters;
        final Class<?> c = Class.forName(className);
        final Field f = c.getField(instanceField);

        if (!IChunkManager.class.isAssignableFrom(f.getType()))
            throw new Exception("The class " + f.getType().getName() + " doesn't implements " + IChunkManager.class.getName() + ".");
        this.instance = (IChunkManager) f.get(null);
        this.standalone = true;

        this.instance.handleParameters(this.parameters);

        ServerUtils.getProfiler().endSection();
    }

    public String getName() {
        return this.name;
    }

    /**
     * Must be called only if {@link Zone#isStandAlone()} returns true.
     *
     * @return The pre-instanciated IChunkManager
     */
    public IChunkManager getInstance() {
        return this.instance;
    }

    /**
     * Creates an instance of {@link IChunkManager} with the given arguments. Must
     * be called only if {@link Zone#isStandAlone()} returns false.
     *
     * @param args
     *            The arguments to instanciate the IChunkManager
     * @return A instance of an IChunkManager
     * @throws Exception
     */
    public IChunkManager createInstance(final String[] args) throws Exception {
        final Constructor<?> constructor = this.clazz.getConstructor(Class.forName("[Ljava.lang.String;"));
        final IChunkManager instance = (IChunkManager) constructor.newInstance(new Object[] { args });
        instance.handleParameters(this.parameters);
        return instance;
    }

    /**
     * Indicates if the zone has a pre-instanciated {@link IChunkManager}.
     *
     * @return true if the IChunkManager is pre-instanciated
     */
    public boolean isStandAlone() {
        return this.standalone;
    }

}
