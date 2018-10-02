package de.poiu.fez;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Very much inspired by https://stackoverflow.com/a/19381667/572645
 *
 * @author mherrn
 */
public class Trigger {
  private static final Logger LOGGER= Logger.getLogger(Trigger.class.getName());

  /////////////////////////////////////////////////////////////////////////////
  //
  // Attributes

  private final BlockingQueue latch= new ArrayBlockingQueue(1);

  //TODO: Give this object an (optional) name, otherwise refer to objectId in logging statements

  /////////////////////////////////////////////////////////////////////////////
  //
  // Constructors

  /////////////////////////////////////////////////////////////////////////////
  //
  // Methods

  public void trigger(){
    LOGGER.log(Level.FINE, "Triggered.");
    latch.offer(Boolean.TRUE);
  }

  public void waitForTrigger() throws InterruptedException{
    LOGGER.log(Level.FINE, "Waiting for trigger.");
    latch.take();
    LOGGER.log(Level.FINE, "Trigger received.");
  }
}
