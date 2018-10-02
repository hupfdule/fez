package de.poiu.fez;

import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;


/**
 * A ThreadFactory that requires a name-prefix for the created threads for easier identification.
 * By default the created threads are daemon-Threads. This can be changed by using the constructor that allows
 * specifying the daemon-Property.
 * <p>
 * This class is intended to be used with ExecutorServices, e.g.:
 * <p>
 * <code>
 * private final ExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("ResettableTimer.executorService"));
 * </code>
 *
 * @author mherrn
 */
public class NamedThreadFactory implements ThreadFactory {
  private static final Logger LOGGER= Logger.getLogger(NamedThreadFactory.class.getName());

  /////////////////////////////////////////////////////////////////////////////
  //
  // Attributes

  private final String namePrefix;
  private long number= 1;
  private final boolean asDaemon;


  /////////////////////////////////////////////////////////////////////////////
  //
  // Constructors

  /**
   * Creates a new NamedThreadFactory.
   * The name of the created threads will be the <code>namePrefix</code> plus the object hashcode of this factory plus an additional counter starting with 1;
   * The threads created by this factory will be daemon-Threads. If non-daemon-Threads are required use
   * {@link #NamedThreadFactory(java.lang.String, boolean) } instead.
   *
   * @param namePrefix the prefix to be used for the name
   */
  public NamedThreadFactory(final String namePrefix) {
    this(namePrefix, true);
  }


  /**
   * Creates a new NamedThreadFactory.
   * The name of the created threads will be the <code>namePrefix</code> plus the object hashcode of this factory plus an additional counter starting with 1;
   *
   * @param namePrefix the prefix to be used for the name
   * @param asDaemon whether the created threads are daemon-Threads
   */
  public NamedThreadFactory(final String namePrefix, final boolean asDaemon) {
    this.namePrefix= namePrefix;
    this.asDaemon= asDaemon;
  }


  /////////////////////////////////////////////////////////////////////////////
  //
  // Methods

  @Override
  public Thread newThread(Runnable r) {
    final String objId= Integer.toHexString(System.identityHashCode(this));
    final Thread t= new Thread(r, namePrefix+"@"+objId+"-"+number++);
    t.setDaemon(asDaemon);
    return t;
  }

}
