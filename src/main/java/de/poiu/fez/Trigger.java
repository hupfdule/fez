package de.poiu.fez;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A simple Trigger class that allows waiting for some trigger event to occur.
 * <p>
 * Wait for a trigger event by calling {@link #waitForTrigger()}. This method blocks until
 * {@link #trigger()} is called. If <code>trigger()</code> is called without calling
 * <code>waitForTrigger()</code> before, nothing happens.
 * <p>
 * Internally this class uses an {@link ArrayBlockingQueue} with a size of 1.
 * <p>
 * It was very much inspired by
 * <a href="https://stackoverflow.com/a/19381667/572645" target="_top">https://stackoverflow.com/a/19381667/572645</a>.
 *
 * @author mherrn
 */
public class Trigger {
  private static final Logger LOGGER= Logger.getLogger(Trigger.class.getName());

  /////////////////////////////////////////////////////////////////////////////
  //
  // Attributes

  private final BlockingQueue latch= new ArrayBlockingQueue(1);

  private final String name;


  /////////////////////////////////////////////////////////////////////////////
  //
  // Constructors

  /**
   * Creates a new trigger.
   */
  public Trigger() {
    this.name= "Trigger@"+Integer.toHexString(System.identityHashCode(this));
  }


  /**
   * Creates a new trigger with the given name.This name is only used in logging statements and
   * always prepended by <code>"Trigger#"</code>.
   *
   * @param name the name of this Trigger
   */
  public Trigger(final String name) {
    this.name= "Trigger#"+name;
  }


  /////////////////////////////////////////////////////////////////////////////
  //
  // Methods

  /**
   * Executes a trigger event.
   */
  public void trigger(){
    LOGGER.log(Level.FINE, "[{0}] Triggered.", this.name);
    latch.offer(Boolean.TRUE);
  }


  /**
   * Waits for the next trigger event to occur.
   *
   * @throws InterruptedException
   */
  public void waitForTrigger() throws InterruptedException{
    LOGGER.log(Level.FINE, "[{0}] Waiting for trigger.", this.name);
    latch.take();
    LOGGER.log(Level.FINE, "[{0}] Trigger received.", this.name);
  }


  @Override
  public String toString() {
    return this.name;
  }
}
